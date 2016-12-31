package com.bhavyakamboj.popularmovies;

import com.bhavyakamboj.popularmovies.domain.MovieResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by bkambo on 12/31/2016.
 */

public interface MovieInterface {

    @GET("movie/{filter}")
    Call<MovieResponse> getMovies(@Path("filter") String filter,@Query("api_key") String
            apiKey,@Query("page") int page);

    @GET("movie/{id}")
    Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
