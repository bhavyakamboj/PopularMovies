package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
}
