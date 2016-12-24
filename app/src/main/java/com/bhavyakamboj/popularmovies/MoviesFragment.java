package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieSelectedListener} interface
 * to handle interaction events.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TOP_RATED = "top_rated?";
    private final String POPULAR = "popular?";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String[] mDataSet = {"1","2","3","4","5","6","7","8","9","10","1",
            "2","3","4","5","6","7","8","9","10","1","2","3","4","5","6","7",
            "8","9","10","1","2","3","4","5","6","7","8","9","10",
            "1","2","3","4","5","6","7","8","9","10","1",
            "2","3","4","5","6","7","8","9","10","1","2","3","4","5","6","7",
            "8","9","10","1","2","3","4","5","6","7","8","9","10",};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnMovieSelectedListener mListener;

    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        //TODO: figure out what to do with params in moviesFragment new instance
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        // TODO: execute fetch movie task and populate recycler view
        // TODO: get preferences from shared preferences
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for th`is fragment
        View view = inflater.inflate(R.layout.fragment_movies,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.movies_recycler_view);
//        mRecyclerView = new RecyclerView(getContext());
        if(mRecyclerView == null){
            Log.e(LOG_TAG,"null recycler view");
        }
//        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(mDataSet,this.getContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // TODO: hook method into UI event
    // TODO: link on item click to onMovieSelected method
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
                // TODO: link with shared preferences
                Log.d(LOG_TAG,position+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO: set to most popular in shared preferences by default
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        // page is optional but default value is 1
        updateMovies("1");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMovieSelectedListener {
        // TODO: Update argument type and name
        void onMovieSelection(String movieId);
    }
    // passing page as argument will help in making functionality of load more
    private void updateMovies(String page){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String movieFilter = prefs.getString(getString(R.string.pref_movies_filter_key),
                getString(R.string.pref_movies_filter_default));
        movieTask.execute(movieFilter,page);
    }
    // TODO: add load more button at end of recyclerview
    // pass params[0]-> most_popular or top-rated
    public class FetchMovieTask extends AsyncTask<String,Void,Void> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        // params[0] for top_rated/popular params[1] for page number
        @Override
        protected Void doInBackground(String... params) {

            if(params.length == 0){
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
                    .appendPath(filter).appendQueryParameter(API_KEY,BuildConfig.THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE,language)
                    .appendQueryParameter(PAGE,page).build();
                Log.d(LOG_TAG,builtUri.toString());
                URL url = new URL(builtUri.toString());

                // create connection to moviedb and open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG,moviesJsonStr);


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
            return null;
        }





    }
}
