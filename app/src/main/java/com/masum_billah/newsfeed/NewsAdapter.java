package com.masum_billah.newsfeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String dateFormat(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat fromUser = new SimpleDateFormat("LLL MM, yyyy");
        return fromUser.format(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        News currentNews = getItem(position);

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent,false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        /* populate list view by array list data */
        assert currentNews != null;
        holder.newsSection.setText(currentNews.getSectionName());
        holder.newsTitle.setText(currentNews.getNewsTitle());

        holder.newsAuthor.setText(currentNews.getNewsAuthor());

        try {
            holder.newsDate.setText(dateFormat(currentNews.getDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentNews.getImage() != null) {
            holder.newsImage.setImageBitmap(currentNews.getImage());
        } else {
            holder.newsImage.setImageResource(R.mipmap.ic_launcher);
        }


        return listItemView;
    }
    static class ViewHolder{
        final TextView newsSection;
        final TextView newsTitle;
        final TextView newsAuthor;
        final TextView newsDate;
        final ImageView newsImage;

        private ViewHolder(View itemView) {
            newsSection = itemView.findViewById(R.id.news_section);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsAuthor = itemView.findViewById(R.id.news_author);
            newsDate = itemView.findViewById(R.id.news_date);
            newsImage = itemView.findViewById(R.id.news_image);
        }
    }
}
