package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhavyakamboj.popularmovies.domain.Movie;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.SimpleViewHolder> {
    private List<Movie> mDataSet;
    private Context mContext;
    private OnMovieClickListener movieClickListener;
    MoviesAdapter(List<Movie> dataSet, Context context, OnMovieClickListener listener){
        if (dataSet == null) {
            throw new IllegalArgumentException(
                    "modelData must not be null");
        }
        this.mDataSet = dataSet;
        this.mContext = context;
        this.movieClickListener = listener;

    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_list_item,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        String widthPath;
        if (width <= 92)
            widthPath = "/w92";
        else if (width <= 154)
            widthPath = "/w154";
        else if (width <= 185)
            widthPath = "/w185";
        else if (width <= 342)
            widthPath = "/w342";
        else if (width <= 500)
            widthPath = "/w500";
        else
            widthPath = "/w780";
        final String BASE_URL = "http://image.tmdb.org/t/p"+widthPath;
        Movie movie = mDataSet.get(position);
        Picasso.with(mContext).load(BASE_URL+movie.getPosterPath()).networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String movieId = holder.textView.getText().toString();
                onMovieClicked(movieId);
            }
        });
        holder.textView.setText(movie.getMovieId().toString());

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    void clear(){
        mDataSet.clear();
    }


    final static class SimpleViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
         SimpleViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movies_list_item_image);
             textView = (TextView) itemView.findViewById(R.id.movies_list_item_textview);
        }
    }
    public interface OnMovieClickListener extends View.OnClickListener {
        void onMovieClick(String movieId);
    }
    public void onMovieClicked(String movieId) {
            movieClickListener.onMovieClick(movieId);
    }
}
