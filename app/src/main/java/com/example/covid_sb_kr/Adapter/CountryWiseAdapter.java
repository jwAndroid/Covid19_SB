package com.example.covid_sb_kr.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.covid_sb_kr.DataActivity.EachCountryDataActivity;
import com.example.covid_sb_kr.Model.CountryWiseModel;
import com.example.covid_sb_kr.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_ACTIVE;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_CONFIRMED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_DECEASED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_FLAGURL;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NAME;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NEW_CONFIRMED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NEW_DECEASED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_RECOVERED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_TESTS;

public class CountryWiseAdapter extends RecyclerView.Adapter<CountryWiseAdapter.MyViewHolder> {

    private final Context mContext;
    private ArrayList<CountryWiseModel> countryWiseModelArrayList;
    private String searchText="";
    private SpannableStringBuilder sb;

    public CountryWiseAdapter(Context mContext, ArrayList<CountryWiseModel> countryWiseModelArrayList) {
        this.mContext = mContext;
        this.countryWiseModelArrayList = countryWiseModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_country_wise, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CountryWiseModel currentItem = countryWiseModelArrayList.get(position);
        String countryName = currentItem.getCountry();
        String countryTotal = currentItem.getConfirmed();
        String countryFlag = currentItem.getFlag();
        String countryRank = String.valueOf(position+1);

        int countryTotalInt = Integer.parseInt(countryTotal);
        Log.d("country rank", countryRank);
        holder.tv_rankTextView.setText(countryRank+".");
        Log.d("country total cases int", String.valueOf(countryTotalInt));
        holder.tv_countryTotalCases.setText(NumberFormat.getInstance().format(countryTotalInt));
        //holder.tv_countryName.setText(countryName);

        if(searchText.length()>0){
            //color your text here
            int index = countryName.indexOf(searchText);
            sb = new SpannableStringBuilder(countryName);

            Pattern word = Pattern.compile(searchText.toLowerCase());
            Matcher match = word.matcher(countryName.toLowerCase());
            while(match.find()){
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(52, 195, 235)); //specify color here
                sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //index = stateName.indexOf(searchText,index+1);
            }
            holder.tv_countryName.setText(sb);

        }else{
            holder.tv_countryName.setText(countryName);
        }

        Glide.with(mContext).load(countryFlag).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.iv_flagImage);

        holder.lin_country.setOnClickListener(v -> {

            CountryWiseModel model = countryWiseModelArrayList.get(position);
            Intent perCountryIntent = new Intent(mContext, EachCountryDataActivity.class);

            /*new 하는부분 삭제 , view객체가 너무 많아져서 .. */
            //로그로 데이터 확인후 메인쪽에서 이값을 토대로 찍어주기.
            perCountryIntent.putExtra(COUNTRY_NAME, model.getCountry());
            perCountryIntent.putExtra(COUNTRY_CONFIRMED, model.getConfirmed());
            perCountryIntent.putExtra(COUNTRY_ACTIVE, model.getActive());
            perCountryIntent.putExtra(COUNTRY_RECOVERED, model.getRecovered());
            perCountryIntent.putExtra(COUNTRY_DECEASED, model.getDeceased());
            perCountryIntent.putExtra(COUNTRY_TESTS, model.getTests());
            perCountryIntent.putExtra(COUNTRY_FLAGURL, model.getFlag());

            perCountryIntent.putExtra(COUNTRY_NEW_CONFIRMED, model.getNewConfirmed());
            perCountryIntent.putExtra(COUNTRY_NEW_DECEASED, model.getNewDeceased());

            mContext.startActivity(perCountryIntent);

        });

    }

    @Override
    public int getItemCount() {
        return countryWiseModelArrayList == null || countryWiseModelArrayList.isEmpty() ?
                0 : countryWiseModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_countryName, tv_countryTotalCases, tv_rankTextView;
        ImageView iv_flagImage;
        LinearLayout lin_country;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_countryName = itemView.findViewById(R.id.layout_country_wise_country_name_textview);
            tv_countryTotalCases = itemView.findViewById(R.id.layout_country_wise_confirmed_textview);
            iv_flagImage = itemView.findViewById(R.id.layout_country_wise_flag_imageview);
            tv_rankTextView = itemView.findViewById(R.id.layout_country_wise_country_rank);
            lin_country = itemView.findViewById(R.id.layout_country_wise_lin);

        }
    }

    public void filterList(ArrayList<CountryWiseModel> filteredList, String text) {
        countryWiseModelArrayList = filteredList;
        this.searchText = text;
        notifyDataSetChanged();
    }
}
