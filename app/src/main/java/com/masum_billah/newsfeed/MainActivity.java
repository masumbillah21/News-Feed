package com.masum_billah.newsfeed;


import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    public static final String apiUrl = "https://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mNewsAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsList = findViewById(R.id.news_list);
        mEmptyStateTextView = findViewById(R.id.empty_view);

        ConnectivityManager connMsr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connMsr != null;
        NetworkInfo networkInfo = connMsr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }else{
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        final ArrayList<News> news = new ArrayList<>();
        mNewsAdapter = new NewsAdapter(this, news);
        newsList.setEmptyView(mEmptyStateTextView);
        newsList.setAdapter(mNewsAdapter);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News singleNews = news.get(position);
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(singleNews.getWebUrl()));
                startActivity(webIntent);
            }
        });

    }


    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String keyword = sharedPreferences.getString(
                getString(R.string.settings_keyword_key),
                getString(R.string.settings_keyword_default)
        );
        String orderBy  = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(apiUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.format_key), getString(R.string.format_value));
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value));
        uriBuilder.appendQueryParameter(getString(R.string.query_key), keyword);
        uriBuilder.appendQueryParameter(getString(R.string.show_fields_key), getString(R.string.show_fields_value));
        uriBuilder.appendQueryParameter(getString(R.string.show_tag_key), getString(R.string.show_tag_value));
        uriBuilder.appendQueryParameter(getString(R.string.order_by_key), orderBy);

        return new NewsFeedLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news);
        mNewsAdapter.clear();
        if(data != null && !data.isEmpty()){
            mNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mNewsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();
        if(menuId == R.id.action_settings){
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}