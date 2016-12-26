package com.bhavyakamboj.popularmovies;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhavyakamboj.popularmovies.domain.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {


    public MovieDetailFragment() {
        // Required empty public constructor
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
        return view;
    }



    private class FetchMovieTask extends AsyncTask<String,Void,Movie> {

        private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
        // params[0] is for filter, params[1] for page, params[2] is for movie ID
        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String language = "en-US";
            String singleMovieJsonStr = null;
            String movieID = params[0];

            try {
                final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
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



        private Movie getSingleMovieFromJSON(String singleMovieJsonStr) throws JSONException {
            String MOVIE_ID = "id";
            String POSTER_PATH = "poster_path";
            String TITLE = "title";
            String ORIGINAL_TITLE = "original_title";
            String HOMEPAGE = "homepage";
            String IMDB_ID = "imdb_id";
            String BACKDROP_PATH = "backdrop_path";
            String BUDGET = "budget";
            String OVERVIEW = "overview";
            String POPULARITY = "popularity";
            String RELEASE_DATE = "release_date";
            String REVENUE = "revenue";
            String RUNTIME = "runtime";
            String TAGLINE = "tagline";
            String VIDEO =  "video";

            JSONObject movieJson = new JSONObject(singleMovieJsonStr);
           return new com.bhavyakamboj.popularmovies.domain.MovieDetail(movieJson.getString
                    (MOVIE_ID),movieJson
                    .getString
                            (POSTER_PATH),
                    movieJson.getString(TITLE),movieJson.getString(HOMEPAGE),movieJson.getString(IMDB_ID),
                    movieJson.getString(BACKDROP_PATH),movieJson.getInt(BUDGET),movieJson
                    .getString(ORIGINAL_TITLE),movieJson.getString(OVERVIEW),movieJson.getString
                    (POPULARITY),movieJson.getString(RELEASE_DATE),movieJson.getString(REVENUE),
                    movieJson.getString(RUNTIME),movieJson.getString(TAGLINE),movieJson
                    .getBoolean(VIDEO));

        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            if(movie != null){
                    Log.d(LOG_TAG,"single movie");
            }

        }

    }

}

