package com.bhavyakamboj.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


final class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.SimpleViewHolder> {
    private String[] mDataSet;
    private Context mContext;
    MoviesAdapter(String[] dataSet, Context context){
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
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textLine.setText(mDataSet[position]);
        holder.textLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Item is clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;

    }

    final static class SimpleViewHolder extends RecyclerView.ViewHolder{
        TextView textLine;
         SimpleViewHolder(View itemView){
            super(itemView);
            textLine = (TextView) itemView.findViewById(R.id.movies_list_item_text);
        }
    }
}
