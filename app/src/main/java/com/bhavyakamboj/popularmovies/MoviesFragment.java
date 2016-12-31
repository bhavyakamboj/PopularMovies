package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.bhavyakamboj.popularmovies.domain.Movie;
import com.bhavyakamboj.popularmovies.domain.MovieResponse;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.bhavyakamboj.popularmovies.R.id.spinner;


public class MoviesFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private final String TOP_RATED = "top_rated";
    private final String POPULAR = "popular";
    private final int DEFAULT_PAGE = 1;
    private RecyclerView mRecyclerView;
    private GridLayoutManager  mLayoutManager;
    private MoviesAdapter mAdapter;
    private List<Movie> mDataSet = new ArrayList<>();
    private CatLoadingView mCatLoadingView;
    private String movieFilter;
    private SharedPreferences mPreferences;
    private int mCurrentPage;
    private int mSelectedSpinner=0;
    private Spinner mSpinner;
    SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private OnMovieSelectedListener mListener;
    private boolean loaderVisible = false;
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
        setRetainInstance(true);
        mCatLoadingView = new CatLoadingView();
        mCatLoadingView.show(getFragmentManager(), "");
        loaderVisible = true;
        // set initial preferences to 1st item on filter
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner",mSelectedSpinner);
        outState.putString("filter",movieFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies,container,false);

        if(savedInstanceState != null){
            if(mSpinner!=null) mSpinner.setSelection(savedInstanceState.getInt("spinner"));
            movieFilter = savedInstanceState.getString("filter");
        } else {
            movieFilter = POPULAR;
        }


        mRecyclerView = (RecyclerView) view.findViewById(R.id.movies_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(getActivity(),2);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(),4);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener
                (mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                    updateMovies(movieFilter, current_page + 1);
            }
        };
        mRecyclerView.addOnScrollListener(endlessScrollListener);
        mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(isAdded()){
                    if (!checkConnection()) {
                        Toast.makeText(getActivity(),"Not connected to internet",Toast
                                .LENGTH_SHORT).show();
                    } else if(key.equals(getString(R.string.movies_filter_spinner)) ) {
                        // if the mfsouovie filter is same as in shared preferences
                        String prefFilter = mPreferences.getString(getString(R.string
                                .movies_filter_spinner), null);
                        //                    mSpinner.setSelection(mSelectedSpinner);
                        if (movieFilter.equals(prefFilter)) {
                            if (!mDataSet.isEmpty()) mAdapter.clear();
                            movieFilter = mPreferences.getString(getString(R.string
                                    .movies_filter_spinner), null);
                            updateMovies(movieFilter, DEFAULT_PAGE);
                        }
                    }
                }
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
        MyApplication.getInstance().setConnectivityListener(this);
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

        MenuItem item = menu.findItem(spinner);
        mSpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_list, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(mSelectedSpinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mPreferences.edit();
                if(position==0){
                    editor.putString(getString(R.string.movies_filter_spinner),POPULAR);
                    movieFilter = POPULAR;
                    mSelectedSpinner = 0;
                } else if (position==1){
                    editor.putString(getString(R.string.movies_filter_spinner),TOP_RATED);
                    movieFilter = TOP_RATED;
                    mSelectedSpinner = 1;
                }
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            Toast.makeText(getActivity(),"Not connected to internet",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(),"Connected to internet",Toast.LENGTH_SHORT).show();
        }
    }
    // Method to manually check connection status
    private boolean checkConnection() {
        return  ConnectivityReceiver.isConnected();
    }

    public void updateMovies(String movieFilter,int page){
        if(!checkConnection()){
            Toast.makeText(getActivity(),"Not connected to internet",Toast.LENGTH_SHORT).show();
        } else {
        MovieInterface apiService =
                ApiClient.getClient().create(MovieInterface.class);

        Call<MovieResponse> call = apiService.getMovies(movieFilter,BuildConfig
                .THE_MOVIE_DB_API_KEY,page);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Response<MovieResponse> response, Retrofit retrofit) {
                if (isAdded()) {
                    for (Movie movie : response.body().getResults()) {
                        mDataSet.add(movie);
                    }
                    mAdapter.notifyDataSetChanged();

                    if (loaderVisible) {
                        mCatLoadingView.dismiss();
                        loaderVisible = false;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TAG", t.toString());

                if (loaderVisible) {
                    mCatLoadingView.dismiss();
                    loaderVisible = false;
                }
            }
        });
        }
    }
}
