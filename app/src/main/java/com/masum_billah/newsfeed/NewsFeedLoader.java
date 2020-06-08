package com.masum_billah.newsfeed;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.content.AsyncTaskLoader;

import java.util.List;

public class NewsFeedLoader extends AsyncTaskLoader<List<News>> {
    private String mNewsFeedUrl;

    public NewsFeedLoader(@NonNull Context context, String newsFeedUrl) {
        super(context);
        this.mNewsFeedUrl = newsFeedUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public List<News> loadInBackground() {
        if(mNewsFeedUrl == null){
            return null;
        }

        List<News> news;
        news = QueryUtils.fetchNewsFeed(mNewsFeedUrl);
        return news;
    }
}
