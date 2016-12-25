package com.bhavyakamboj.popularmovies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

 abstract class EndlessScrollListener extends RecyclerView.OnScrollListener{
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean stillLoading = true; // True if we are still waiting for the last set of data to load.
    private int current_page = 1;
    private GridLayoutManager mGridLayoutManager;

    EndlessScrollListener(GridLayoutManager gridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        final int numItemsToPreload = 5;
        int firstVisibleItem, numVisibleItems, numTotalItems;
        numVisibleItems = recyclerView.getChildCount();
        numTotalItems = mGridLayoutManager.getItemCount();
        firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

        if (stillLoading) {
            if (numTotalItems > previousTotal) {
                stillLoading = false;
                previousTotal = numTotalItems;
            }
        }
        if (!stillLoading && (numTotalItems - numVisibleItems)
                <= (firstVisibleItem + numItemsToPreload)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            stillLoading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}