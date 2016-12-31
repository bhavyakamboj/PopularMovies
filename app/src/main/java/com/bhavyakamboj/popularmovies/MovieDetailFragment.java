package com.bhavyakamboj.popularmovies;


import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.bhavyakamboj.popularmovies.domain.MovieDetail;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;


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
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(movieId);
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

    private class FetchMovieTask extends AsyncTask<String,Void,MovieDetail> {

        private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
        // params[0] is for filter, params[1] for page, params[2] is for movie ID
        @Override
        protected MovieDetail doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String language = "en-US";
            String singleMovieJsonStr = null;
            String movieID = params[0];

            try {
                // TODO: move api key to build config
                final String API_KEY = "api_key";
                final String LANGUAGE = "language";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(movieID)
                        .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(LANGUAGE, language)
                        .build();
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
                singleMovieJsonStr = buffer.toString();
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
                return getSingleMovieFromJSON(singleMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }



        private MovieDetail getSingleMovieFromJSON(String singleMovieJsonStr) throws JSONException {
            String MOVIE_ID = "id";
            String POSTER_PATH = "poster_path";
            String TITLE = "title";
            String BACKDROP_PATH = "backdrop_path";
            String BUDGET = "budget";
            String OVERVIEW = "overview";
            String RELEASE_DATE = "release_date";
            String REVENUE = "revenue";
            String RUNTIME = "runtime";
            String VOTE_AVERAGE = "vote_average";

            JSONObject movieJson = new JSONObject(singleMovieJsonStr);
           return new com.bhavyakamboj.popularmovies.domain.MovieDetail(movieJson.getString
                   (MOVIE_ID),movieJson.getString(POSTER_PATH),movieJson.getString(TITLE),
                   movieJson.getString(BACKDROP_PATH),movieJson.getString(BUDGET),
                   movieJson.getString(OVERVIEW),movieJson.getString(RELEASE_DATE),movieJson.getString(REVENUE),
                   movieJson.getString(RUNTIME),movieJson.getString(VOTE_AVERAGE));

        }

        @Override
        protected void onPostExecute(MovieDetail movie) {
            super.onPostExecute(movie);
            if(movie != null){
                    updateDetailFragmentFromTask(movie);
            }

        }

    }
    private void updateDetailFragmentFromTask(final MovieDetail movie){
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

            String runtime = movie.getRuntime();
            TextView title = (TextView) getView().findViewById(R.id.title_textview);
            title.setText(movie.getTitle()+"("+runtime+" min)");


            ExpandableTextView expTv1 = (ExpandableTextView) getView().findViewById(R.id
                    .expandable_text_view);
            expTv1.setText(movie.getOverview());


            TextView voteAverage = (TextView) getView().findViewById(R.id.vote_average);
            voteAverage.setText(movie.getVoteAverage());
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
            budget.setText(numberFormat.format(Double.parseDouble(movie.getBudget())));

            TextView revenue = (TextView) getView().findViewById(R.id.revenue_textview_value);
            revenue.setText(numberFormat.format(Double.parseDouble(movie.getRevenue())));
    }
}

