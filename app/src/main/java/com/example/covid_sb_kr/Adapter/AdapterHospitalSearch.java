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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.Model.Hospital;
import com.example.covid_sb_kr.R;

import java.util.ArrayList;

public class AdapterHospitalSearch extends RecyclerView.Adapter<AdapterHospitalSearch.MyViewHolder > implements Filterable {

    private static final String TAG = "AdapterHospitalSearch";
    private ArrayList<Hospital> mList;
    private ArrayList<Hospital> unFilteredlist;
    private LayoutInflater mInflate;
    Context mContext;
    private Activity activity;

    public AdapterHospitalSearch(Context context , Activity activity, ArrayList<Hospital> mList  ) {
        this.mList = mList;
        this.unFilteredlist = mList;
        this.mInflate = LayoutInflater.from(context);
        this.mContext = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.hospital_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder  viewHolder, int position) {

        try{
            Hospital hospital = mList.get(position);
            String code = hospital.getSpclAdmTyCd();
            String hos = hospital.getHospTyTpCd();

                if (TextUtils.isEmpty(hos)){

                    viewHolder.hospTyTpCd_tv.setVisibility(View.GONE);
                    viewHolder.sgguNm_tv.setText(hospital.getSgguNm());
                    viewHolder.sidoNm_tv.setText(hospital.getSidoNm());
                    viewHolder.telno_tv.setText(hospital.getTelno());
                    viewHolder.yadmNm_tv.setText(hospital.getYadmNm());

                }else{

                    viewHolder.hospTyTpCd_tv.setVisibility(View.VISIBLE);
                    viewHolder.hospTyTpCd_tv.setText(hospital.getHospTyTpCd());
                    viewHolder.sgguNm_tv.setText(hospital.getSgguNm());
                    viewHolder.sidoNm_tv.setText(hospital.getSidoNm());
                    viewHolder.telno_tv.setText(hospital.getTelno());
                    viewHolder.yadmNm_tv.setText(hospital.getYadmNm());
                }

                if (code.equals("A0")){
                    viewHolder.
                            spclAdmTyCd_tv
                            .setText(hospital.getSpclAdmTyCd()
                                    .replace("A0" , "국민안심병원"));

                }else if(code.equals("97")){
                    viewHolder.
                            spclAdmTyCd_tv
                            .setText(hospital.getSpclAdmTyCd()
                                    .replace("97" , "코로나검사실시기관"));

                }else if(code.equals("99")){
                    viewHolder.
                            spclAdmTyCd_tv
                            .setText(hospital.getSpclAdmTyCd()
                                    .replace("99" , "선별진료소운영기관"));
                }


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("전화")
                                .setMessage(hospital.getYadmNm()+"에 전화를 거시겠습니까?");

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

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if(charString.isEmpty()) {
                mList = unFilteredlist;
            } else {
                ArrayList<Hospital> filteringList = new ArrayList<>();
                for(Hospital name : unFilteredlist) {
                    if(name.getSidoNm().toLowerCase().contains(charString.toLowerCase()) ||
                            name.getSgguNm().toLowerCase().contains(charString.toLowerCase()) ||
                                name.getYadmNm().toLowerCase().contains(charString.toLowerCase())
                    ) {
                        filteringList.add(name);
                    }

                }
                mList = filteringList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = mList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mList = (ArrayList<Hospital>)results.values;
            notifyDataSetChanged();
        }
    };


    public static class MyViewHolder  extends RecyclerView.ViewHolder {

        TextView hospTyTpCd_tv;
        TextView sgguNm_tv;
        TextView sidoNm_tv;
        TextView spclAdmTyCd_tv;
        TextView telno_tv;
        TextView yadmNm_tv;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);

            hospTyTpCd_tv = itemView.findViewById(R.id.hospTyTpCd_tv);
            sgguNm_tv = itemView.findViewById(R.id.sgguNm_tv);
            sidoNm_tv = itemView.findViewById(R.id.sidoNm_tv);
            spclAdmTyCd_tv = itemView.findViewById(R.id.spclAdmTyCd_tv);
            telno_tv = itemView.findViewById(R.id.telno_tv);
            yadmNm_tv = itemView.findViewById(R.id.yadmNm_tv);
        }
    }



}
