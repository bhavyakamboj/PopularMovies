package com.bhavyakamboj.popularmovies.domain;

/**
 * Created by bkambo on 12/25/2016.
 */

public class MovieDetail extends Movie {
    private String backdropPath;
    private String budget;
    private String overview;
    private String releaseDate;
    private String revenue;
    private String runtime;
    private String voteAverage;
    public MovieDetail(String movieId, String posterPath, String title, String backdropPath,
                       String budget, String overview, String releaseDate, String revenue, String runtime,
                       String voteAverage){
        super(movieId,posterPath,title);
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

}
