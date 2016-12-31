package com.bhavyakamboj.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.movie_detail_container);
        Intent intent = getIntent();
        String movieId = intent.getStringExtra(getString(R.string.movie_id_key));
        if(fragment == null){
            fragment = new MovieDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.movie_id_key),movieId);
            fragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.movie_detail_container,fragment)
                    .commitAllowingStateLoss();
        }
    }
}
