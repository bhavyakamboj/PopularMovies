package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bhavyakamboj.popularmovies.domain.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.SimpleViewHolder> {
    private List<Movie> mDataSet;
    private Context mContext;
    MoviesAdapter(List<Movie> dataSet, Context context){
        if (dataSet == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_list_item,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final String BASE_URL = "http://image.tmdb.org/t/p/w500";
        Movie movie = mDataSet.get(position);
        Picasso.with(mContext).load(BASE_URL+movie.getPosterPath())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: return movie id to fragment
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    void clear(){
        mDataSet.clear();
    }

    void add(Movie movie){
        mDataSet.add(movie);
    }

    final static class SimpleViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
         SimpleViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movies_list_item_image);
        }
    }
}
