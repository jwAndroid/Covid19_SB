package com.example.covid_sb_kr.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.Model.ModelNews;
import com.example.covid_sb_kr.Nnews.NaverNewsDetailActivity;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.SideNavigation.WorldNews.NewsDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.ViewHolder> {

    private static final String TAG = "MyAdapter" ;
    public Context mContext;
    public List<ModelNews> mPost;
    private Activity activity;

    public AdapterNews(Context mContext, List<ModelNews> mPost , Activity activity ) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item , parent , false);
        return new AdapterNews.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        ModelNews model = mPost.get(position);
        //제목과 링크 null처리
        String getJsonTitle = model.getTitle();
        String content = model.getDescription();
        String getJsonDate = model.getPubDate();
        String getJsonLink = model.getLink();

        if (getJsonTitle != null && content != null
                && getJsonDate != null && getJsonLink != null){

            viewHolder.title.setText(model.getTitle());
            viewHolder.pubDate.setText(model.getPubDate());
            viewHolder.link.setText(model.getLink());

        }else{
            Toast.makeText(mContext , "Some error!" , Toast.LENGTH_SHORT).show();
        }

        if(content != null && content.length() > 0) {
            viewHolder.description.setText(content);
        }
        else{
            viewHolder.description.setText("-");
        }

        viewHolder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(model.getLink()));
//            mContext.startActivity(intent);

            Intent intent = new Intent(mContext, NaverNewsDetailActivity.class);
            intent.putExtra("url", model.getLink());
            intent.putExtra("title", model.getTitle());
            intent.putExtra("date",  model.getPubDate());
            mContext.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

        });

    }

    @Override
    public int getItemCount() {
        return mPost == null ? 0 : mPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        TextView link;
        TextView pubDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            link = itemView.findViewById(R.id.link);
            pubDate = itemView.findViewById(R.id.pubDate);

        }
    }

}
