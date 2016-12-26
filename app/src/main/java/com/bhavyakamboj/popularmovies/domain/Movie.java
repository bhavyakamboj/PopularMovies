package com.bhavyakamboj.popularmovies.domain;

/**
 * Created by bkambo on 12/24/2016.
 */

public class Movie {
    private String movieId;
    private String posterPath;
    private String title;

    public Movie(String movieId, String posterPath,String title){
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }


    public String getMovieId() {
        return movieId;
    }


    public String getTitle() {
        return title;
    }


}
