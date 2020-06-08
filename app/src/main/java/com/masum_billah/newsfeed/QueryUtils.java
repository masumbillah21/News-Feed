package com.masum_billah.newsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    public QueryUtils() {
    }

    public static List<News> extractNews(String newsJSON){
        if(TextUtils.isEmpty(newsJSON)){
            return null;
        }

        List<News> newsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(newsJSON);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray newsArray = responseObject.getJSONArray("results");

            for(int i = 0; i < newsArray.length(); i++){
                JSONObject currentNews = newsArray.getJSONObject(i);
                String sectionName = currentNews.getString("sectionName");
                String webTitle = currentNews.getString("webTitle");
                String newsAuthor =
                        currentNews.getJSONArray("tags").getJSONObject(0).getString("webTitle");
                String webPublicationDate = currentNews.getString("webPublicationDate");
                String webUrl = currentNews.getString("webUrl");


                /* Getting News Image if news image not found then getting author image */
                Bitmap image;
                String imageUrl;
                imageUrl = currentNews.getJSONObject("fields").getString("thumbnail");

                if (!imageUrl.equals("empty")) {
                    image = getImage(imageUrl);
                } else {
                    image = getImage(currentNews.getJSONArray("tags").getJSONObject(0).getString(
                            "bylineLargeImageUrl"));
                }

                String newsDate =
                        webPublicationDate.replace("T", " ").replace("Z", " ").split(" ")[0];

                newsList.add(new News(sectionName, webTitle, newsAuthor, image, newsDate,
                        webUrl));

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return newsList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<News> fetchNewsFeed(String feedUrl){
        URL url = createUrl(feedUrl);
        String jsonResponse;


        jsonResponse = makeHttpRequest(url);

        return extractNews(jsonResponse);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String makeHttpRequest(URL url) {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (line != null) {
                output.append(line);
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return output.toString();
    }

    private static URL createUrl(String feedUrl) {
        URL url = null;
        try {
            url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // method to load thumbnail images
    private static Bitmap getImage(String url) {
        if (url == null) {
            return null;
        }
        URL link = createUrl(url);
        try {
            assert link != null;
            return BitmapFactory.decodeStream(link.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
