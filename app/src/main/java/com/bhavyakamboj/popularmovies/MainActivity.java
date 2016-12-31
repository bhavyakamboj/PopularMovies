package com.bhavyakamboj.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MoviesFragment
        .OnMovieSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.container);
            if(fragment == null){
                fragment = new MoviesFragment();
                fm.beginTransaction()
                        .add(R.id.container,fragment)
                        .commit();
            }
        }
    }

    @Override
    public void onMovieSelection(String movieId) {
        if(!movieId.isEmpty()){
            Intent intent = new Intent(this,MovieDetailActivity.class);
            intent.putExtra(getString(R.string.movie_id_key),movieId);
            startActivity(intent);
        }
    }










}
