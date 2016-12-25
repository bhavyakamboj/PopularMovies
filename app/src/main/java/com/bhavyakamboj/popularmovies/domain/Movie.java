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

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
