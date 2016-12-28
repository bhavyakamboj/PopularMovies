package com.bhavyakamboj.popularmovies;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhavyakamboj.popularmovies.domain.MovieDetail;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.roger.catloadinglibrary.CatLoadingView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {
    private String imageBaseURL = "http://image.tmdb.org/t/p/w1280/";
    private String posterBaseURL = "http://image.tmdb.org/t/p/w342/";
    private String posterBaseLargeUrl = "http://image.tmdb.org/t/p/w780/";
    private List<String> movieImages = new ArrayList<>();
    private CatLoadingView mCatLoadingView;
    private ImageView mExpandedImageView;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatLoadingView = new CatLoadingView();
        mCatLoadingView.show(getFragmentManager(), "");
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
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(movieId);
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



        private MovieDetail getSingleMovieFromJSON(String singleMovieJsonStr) throws JSONException {
            String MOVIE_ID = "id";
            String POSTER_PATH = "poster_path";
            String TITLE = "title";
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
            String VOTE_AVERAGE = "vote_average";

            JSONObject movieJson = new JSONObject(singleMovieJsonStr);
           return new com.bhavyakamboj.popularmovies.domain.MovieDetail(movieJson.getString
                   (MOVIE_ID),movieJson.getString(POSTER_PATH),movieJson.getString(TITLE),
                   movieJson.getString(HOMEPAGE),movieJson.getString(IMDB_ID),
                   movieJson.getString(BACKDROP_PATH),movieJson.getString(BUDGET),
                   movieJson.getString(OVERVIEW),movieJson.getString
                   (POPULARITY),movieJson.getString(RELEASE_DATE),movieJson.getString(REVENUE),
                   movieJson.getString(RUNTIME),movieJson.getString(TAGLINE),movieJson
                   .getBoolean(VIDEO),movieJson.getString(VOTE_AVERAGE));

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
            mCatLoadingView.dismiss();
            ImageView imageView = (ImageView) getView().findViewById(R.id.backdrop);
            Picasso.with(getContext()).load(imageBaseURL+movie.getBackdropPath()).into(imageView);

            TextView title = (TextView) getView().findViewById(R.id.title_textview);
            title.setText(movie.getTitle());

            ExpandableTextView expTv1 = (ExpandableTextView) getView().findViewById(R.id
                    .expandable_text_view);
            expTv1.setText(movie.getOverview());

            TextView popularity = (TextView) getView().findViewById(R.id.popularity);
            popularity.setText(movie.getPopularity());

            TextView voteAverage = (TextView) getView().findViewById(R.id.vote_average);
            voteAverage.setText(movie.getVoteAverage());
            // TODO: setup zoom to image now
            final ImageView poster = (ImageView) getView().findViewById(R.id.movie_poster);
            Picasso.with(getContext()).load(posterBaseURL+movie.getPosterPath()).into(poster);
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExpandedImageView = new ImageView(getContext());
                    Picasso.with(getContext()).load
                            (posterBaseLargeUrl+movie.getPosterPath()).into(mExpandedImageView);
                    zoomImageFromThumb(poster,mExpandedImageView.getId() );
                }
            });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        java.util.Date date = null;
        TextView releaseDate = (TextView) getView().findViewById(R.id.release_date_textview_value);
            releaseDate.setText(movie.getReleaseDate());

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            TextView budget = (TextView) getView().findViewById(R.id.budget_textview_value);
            budget.setText(numberFormat.format(Double.parseDouble(movie.getBudget())));

            TextView revenue = (TextView) getView().findViewById(R.id.revenue_textview_value);
            revenue.setText(numberFormat.format(Double.parseDouble(movie.getRevenue())));
    }
    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) getView().findViewById(
                mExpandedImageView.getId());
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getView().findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}

