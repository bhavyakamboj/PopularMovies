package com.bhavyakamboj.popularmovies.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bkambo on 12/24/2016.
 */

public class Movie {
    @SerializedName("id")
    private Integer id;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("budget")
    private Integer budget;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("revenue")
    private Integer revenue;
    @SerializedName("run_time")
    private Integer runtime;
    @SerializedName("vote_average")
    private Double voteAverage;
    public Movie(Integer id, String posterPath, String title, String backdropPath,
                 Integer budget, String overview, String releaseDate, Integer revenue,
                 Integer runtime, Double voteAverage){
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Integer getBudget() {
        return budget;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }


    public String getPosterPath() {
        return posterPath;
    }


    public Integer getMovieId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


}
