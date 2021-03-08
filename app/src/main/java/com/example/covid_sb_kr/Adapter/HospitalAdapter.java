package com.example.covid_sb_kr.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.Model.Hospital;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "HospitalAdapter" ;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<Hospital> mItemList;

    private Context context;
    private Activity activity;

    public HospitalAdapter(List<Hospital> itemList , Context context , Activity activity ) {
        mItemList = itemList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    private String replaceCode(String str){
        return str.replace("" , "");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Hospital hospital = mItemList.get(position);

        if (viewHolder instanceof ItemViewHolder) {

            String code = hospital.getSpclAdmTyCd();
            String hos = hospital.getHospTyTpCd();

            if (TextUtils.isEmpty(hos)){

                ((ItemViewHolder) viewHolder).hospTyTpCd_tv.setVisibility(View.GONE);
                ((ItemViewHolder) viewHolder).sgguNm_tv.setText(hospital.getSgguNm());
                ((ItemViewHolder) viewHolder).sidoNm_tv.setText(hospital.getSidoNm());
                ((ItemViewHolder) viewHolder).telno_tv.setText(hospital.getTelno());
                ((ItemViewHolder) viewHolder).yadmNm_tv.setText(hospital.getYadmNm());

            }else{

                ((ItemViewHolder) viewHolder).hospTyTpCd_tv.setVisibility(View.VISIBLE);
                ((ItemViewHolder) viewHolder).hospTyTpCd_tv.setText(hospital.getHospTyTpCd());
                ((ItemViewHolder) viewHolder).sgguNm_tv.setText(hospital.getSgguNm());
                ((ItemViewHolder) viewHolder).sidoNm_tv.setText(hospital.getSidoNm());
                ((ItemViewHolder) viewHolder).telno_tv.setText(hospital.getTelno());
                ((ItemViewHolder) viewHolder).yadmNm_tv.setText(hospital.getYadmNm());

            }

            if (code.equals("A0")){
                ((ItemViewHolder) viewHolder)
                        .spclAdmTyCd_tv
                        .setText(hospital.getSpclAdmTyCd()
                                .replace("A0" , "국민안심병원"));

            }else if(code.equals("97")){
                ((ItemViewHolder) viewHolder)
                        .spclAdmTyCd_tv
                        .setText(hospital.getSpclAdmTyCd()
                                .replace("97" , "코로나검사실시기관"));

            }else if(code.equals("99")){
                ((ItemViewHolder) viewHolder)
                        .spclAdmTyCd_tv
                        .setText(hospital.getSpclAdmTyCd()
                                .replace("99" , "선별진료소운영기관"));
            }


        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("전화")
                        .setMessage(hospital.getYadmNm()+" 에 전화를 거시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Uri uri = Uri.parse("tel:"+hospital.getTelno());

                        try {

                            activity.startActivity(new Intent(Intent.ACTION_DIAL, uri));

                        }catch (Exception e){
                            Log.d(TAG , e.getMessage());
                        }

                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Utils.toast(context , "test2");
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView hospTyTpCd_tv;
        TextView sgguNm_tv;
        TextView sidoNm_tv;
        TextView spclAdmTyCd_tv;
        TextView telno_tv;
        TextView yadmNm_tv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            hospTyTpCd_tv = itemView.findViewById(R.id.hospTyTpCd_tv);
            sgguNm_tv = itemView.findViewById(R.id.sgguNm_tv);
            sidoNm_tv = itemView.findViewById(R.id.sidoNm_tv);
            spclAdmTyCd_tv = itemView.findViewById(R.id.spclAdmTyCd_tv);
            telno_tv = itemView.findViewById(R.id.telno_tv);
            yadmNm_tv = itemView.findViewById(R.id.yadmNm_tv);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //
    }

//    private void populateItemRows(ItemViewHolder viewHolder, int position) {
//
//        Hospital item = mItemList.get(position);
//        viewHolder.tvItem.setText(item);
//
//    }

}
