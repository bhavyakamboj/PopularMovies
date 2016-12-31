package com.bhavyakamboj.popularmovies;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhavyakamboj.popularmovies.domain.Movie;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{
    private String imageBaseURL = "http://image.tmdb.org/t/p/w780/";
    private String posterBaseURL = "http://image.tmdb.org/t/p/w342/";
    private String posterBaseLargeUrl = "http://image.tmdb.org/t/p/w780/";
    private final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String movieId = bundle.getString(getString(R.string.movie_id_key));
        // Inflate the layout for this fragment
        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_movie_detail,
                container,false);

        Log.v("LOGS",movieId);
        fetchDataFromInternet(movieId);

        return view;
    }
    public void fetchDataFromInternet(String movieId){
        if(!checkConnection()){
            Toast.makeText(getActivity(),"Not connected to internet",Toast.LENGTH_SHORT).show();
        } else {
            fetchMovieDetails(movieId);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            Toast.makeText(getActivity(),"Not connected to internet",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkConnection() {
        return  ConnectivityReceiver.isConnected();
    }

    private void fetchMovieDetails(String movieId){
        if(!checkConnection()){
            Toast.makeText(getActivity(),"Not connected to internet",Toast.LENGTH_SHORT).show();
        } else {
            MovieInterface apiService =
                    ApiClient.getClient().create(MovieInterface.class);

            Call<Movie> call = apiService.getMovieDetails(Integer.parseInt(movieId),BuildConfig
                    .THE_MOVIE_DB_API_KEY);

            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Response<Movie> response, Retrofit retrofit) {
                    if (isAdded()) {
                        updateDetailFragmentUI(response.body());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("TAG", t.toString());
                }
            });
        }
    }

    private void updateDetailFragmentUI(final Movie movie){
        // TODO: fill the details of movie in fragment
            AVLoadingIndicatorView posterImageLoader = (AVLoadingIndicatorView) getView()
                    .findViewById(R.id.posterImageLoading);
        if(posterImageLoader!=null)  posterImageLoader.hide();
            ImageView imageView = (ImageView) getView().findViewById(R.id.backdrop);
            Picasso.with(getContext()).load(imageBaseURL+movie.getBackdropPath()).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView image = new ImageView(getContext());
                    Picasso.with(getContext()).load(imageBaseURL+movie
                            .getBackdropPath()
                    ).into(image);
                    image.setAdjustViewBounds(true);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getContext()).
                                    setPositiveButton("", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    AlertDialog dialog = builder.create();
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.dimAmount = 0.7f;
                    dialog.show();
                }
            });

            String runtime = movie.getRuntime().toString();
            TextView title = (TextView) getView().findViewById(R.id.title_textview);
            title.setText(movie.getTitle()+"("+runtime+" min)");


            ExpandableTextView expTv1 = (ExpandableTextView) getView().findViewById(R.id
                    .expandable_text_view);
            expTv1.setText(movie.getOverview());


            TextView voteAverage = (TextView) getView().findViewById(R.id.vote_average);
            voteAverage.setText(movie.getVoteAverage().toString());
            // TODO: setup zoom to image now
            final ImageView poster = (ImageView) getView().findViewById(R.id.movie_poster);
            Picasso.with(getContext()).load(posterBaseURL+movie.getPosterPath()).fetch();
            AVLoadingIndicatorView topImageLoader = (AVLoadingIndicatorView) getView().findViewById
                    (R.id
                    .topImageLoading);
            topImageLoader.hide();
            Picasso.with(getContext()).load(posterBaseURL+movie.getPosterPath()).into(poster);
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
              public void onClick(View view) {
                    ImageView image = new ImageView(getContext());
                        Picasso.with(getContext()).load(posterBaseLargeUrl+movie
                                .getPosterPath()
                        ).into(image);
                    image.setAdjustViewBounds(true);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getContext()).
                                    setPositiveButton("", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    AlertDialog dialog = builder.create();
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.dimAmount = 0.7f;
                    dialog.show();
                }
              });

        TextView releaseDate = (TextView) getView().findViewById(R.id.release_date_textview_value);
            releaseDate.setText(movie.getReleaseDate());

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            TextView budget = (TextView) getView().findViewById(R.id.budget_textview_value);
            budget.setText(numberFormat.format(movie.getBudget()));

            TextView revenue = (TextView) getView().findViewById(R.id.revenue_textview_value);
            revenue.setText(numberFormat.format(movie.getRevenue()));
    }
}

