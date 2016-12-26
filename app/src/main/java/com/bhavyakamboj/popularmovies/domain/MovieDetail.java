package com.bhavyakamboj.popularmovies.domain;

/**
 * Created by bkambo on 12/25/2016.
 */

public class MovieDetail extends Movie {
    private String homepage;
    private String imdb_id;
    private String backdrop_path;
    private int budget;
    private String original_title;
    private String overview;
    private String popularity;
    private String release_date;
    private String revenue;
    private String runtime;
    private String tagline;
    private boolean video;
    public MovieDetail(String movieId, String posterPath, String title,String homepage, String
            imdb_id,
                 String backdrop_path, int budget, String original_title, String overview,
                 String popularity, String release_date, String revenue, String runtime,
                 String tagline, boolean video){
        super(movieId,posterPath,title);
        this.homepage = homepage;
        this.imdb_id = imdb_id;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.original_title = original_title;
        this.overview = overview;
        this.popularity = popularity;
        this.release_date = release_date;
        this.revenue = revenue;
        this.runtime = runtime;
        this.tagline = tagline;
        this.video = video;
    }
    public String getHomepage() {
        return homepage;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int getBudget() {
        return budget;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public boolean isVideo() {
        return video;
    }

}
