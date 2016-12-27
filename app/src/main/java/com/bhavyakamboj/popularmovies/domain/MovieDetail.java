package com.bhavyakamboj.popularmovies.domain;

/**
 * Created by bkambo on 12/25/2016.
 */

public class MovieDetail extends Movie {
    private String homepage;
    private String imdbId;
    private String backdropPath;
    private String budget;
    private String overview;
    private String popularity;
    private String releaseDate;
    private String revenue;
    private String runtime;
    private String tagline;
    private boolean video;
    private String voteAverage;
    public MovieDetail(String movieId, String posterPath, String title, String homepage, String
            imdbId, String backdropPath, String budget, String overview, String popularity,
                       String releaseDate, String revenue, String runtime,
                       String tagline, boolean video, String voteAverage){
        super(movieId,posterPath,title);
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.overview = overview;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.tagline = tagline;
        this.video = video;
        this.voteAverage = voteAverage;
    }
    public String getHomepage() {
        return homepage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getBudget() {
        return budget;
    }

    public String getOverview() {
        return overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
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

    public String getVoteAverage() {
        return voteAverage;
    }

}
