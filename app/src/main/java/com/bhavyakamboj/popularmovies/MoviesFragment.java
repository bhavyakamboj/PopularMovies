package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bhavyakamboj.popularmovies.domain.Movie;
import com.roger.catloadinglibrary.CatLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MoviesFragment extends Fragment {
    private final String TOP_RATED = "top_rated";
    private final String POPULAR = "popular";
    private final int DEFAULT_PAGE = 1;
    private RecyclerView mRecyclerView;
    private GridLayoutManager  mLayoutManager;
    private MoviesAdapter mAdapter;
    private List<Movie> mDataSet = new ArrayList<>();
    private CatLoadingView mCatLoadingView;
    private String movieFilter = POPULAR;
    private SharedPreferences mPreferences;
    private int mCurrentPage;
    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private OnMovieSelectedListener mListener;
    public MoviesFragment() {
        // Required empty public constructor
    }
    public interface OnMovieSelectedListener {
        void onMovieSelection(String movieId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieSelectedListener) {
            mListener = (OnMovieSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCatLoadingView = new CatLoadingView();
        mCatLoadingView.show(getFragmentManager(), "");
        // set initial preferences to 1st item on filter
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.movies_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        final int GRID_COLUMN_COUNT = 2;
        mLayoutManager = new GridLayoutManager(getActivity(),GRID_COLUMN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener
                (mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                updateMovies(movieFilter,current_page+1);
            }
        };
        mRecyclerView.addOnScrollListener(endlessScrollListener);
        mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(isAdded()){
                if(key.equals(getString(R.string.movies_filter_spinner))){
                    // if the mfsouovie filter is same as in shared preferences
                    String prefFilter = mPreferences.getString(getString(R.string
                            .movies_filter_spinner),null);
                    if(movieFilter.equals(prefFilter)){
                        if(!mDataSet.isEmpty()) mAdapter.clear();
                        updateMovies(mPreferences.getString(getString(R.string
                                .movies_filter_spinner),null),DEFAULT_PAGE);
                        movieFilter = mPreferences.getString(getString(R.string
                                .movies_filter_spinner),null);
                    }
                }}
            }
        };
        mPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(mPrefListener);
        MoviesAdapter.OnMovieClickListener movieClickListener
                = new MoviesAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(String movieId) {
                onMovieSelected(movieId);
            }

            @Override
            public void onClick(View v) {

            }
        };
        mAdapter = new MoviesAdapter(mDataSet,this.getContext(),movieClickListener);
        mRecyclerView.setAdapter(mAdapter);
        updateMovies(movieFilter,DEFAULT_PAGE);
        return view;
    }
    
    public void onMovieSelected(String movieId) {
        if (mListener != null) {
            mListener.onMovieSelection(movieId);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_list, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mPreferences.edit();
                if(position==0){
                    editor.putString(getString(R.string.movies_filter_spinner),POPULAR);
                    movieFilter = POPULAR;
                } else if (position==1){
                    editor.putString(getString(R.string.movies_filter_spinner),TOP_RATED);
                    movieFilter = TOP_RATED;
                }
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

// passing page as argument will help in making functionality of load more
    // used shared preferences to execute task
    private void updateMovies(String movieFilter, int page){
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(movieFilter,Integer.toString(page));
    }
    private class FetchMovieTask extends AsyncTask<String,Void,List<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        // params[0] is for filter, params[1] for page, params[2] is for movie ID
        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String language = "en-US";
            String moviesJsonStr = null;
            String filter = params[0];
            String page = params[1];

            try {
                final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
                // TODO: move api key to build config
                final String API_KEY = "api_key";
                final String LANGUAGE = "language";
                final String PAGE = "page";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(filter).appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(LANGUAGE, language)
                        .appendQueryParameter(PAGE, page).build();
                URL url = new URL(builtUri.toString());

                // create connection to moviedb and open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                //                Log.d(LOG_TAG,moviesJsonStr);


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesListFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }



        private List<Movie> getMoviesListFromJson(String moviesJsonStr) throws JSONException{
            String TMD_RESULTS = "results";
            String TMD_CURRENT_PAGE = "page";
            String TMD_POSTER_PATH = "poster_path";
            String TMD_ID = "id";
            String TMD_ORIGINAL_TITLE = "original_title";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            int currentPage = moviesJson.getInt(TMD_CURRENT_PAGE);
            List<Movie> moviesList = new ArrayList<>();

            JSONArray jsonArray = moviesJson.getJSONArray(TMD_RESULTS);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject currentMovie = jsonArray.getJSONObject(i);
                moviesList.add(new Movie(currentMovie.getString(TMD_ID),currentMovie.getString(TMD_POSTER_PATH),
                        currentMovie.getString(TMD_ORIGINAL_TITLE)));
            }

            return moviesList;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if(isAdded()){
                if(movies != null){
                    for(Movie movie: movies) {
                        mAdapter.add(movie);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if(null != mCatLoadingView) mCatLoadingView.dismiss();

            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
