package com.example.covid_sb_kr.SideNavigation.SearchClick;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.SideNavigation.SearchHosActivity;

import java.util.List;

public class AdapterSearchItem extends RecyclerView.Adapter<AdapterSearchItem.ViewHoler> {

    private static final String TAG = "AdapterSearchItem" ;
    public Context mContext;
    public List<Model> list;

    String code , apiRowData;

    public AdapterSearchItem(Context mContext, List<Model> list , String code , String apiRowData) {
        this.mContext = mContext;
        this.list = list;
        this.code = code;
        this.apiRowData = apiRowData;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_item , parent , false);
        return new AdapterSearchItem.ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler viewHoler, int position) {
        Model model = list.get(position);
        viewHoler.sidoName.setText(model.getName());

        viewHoler.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext , SearchHosActivity.class);
                intent.putExtra("search" ,  model.getName() );
                intent.putExtra("code" , code );
                intent.putExtra("apiRowData" , apiRowData );
                System.out.println("클릭 어댑터에서 code는 ??:"+code);
                System.out.println("클릭 어댑터에서 apiRowData ??:"+apiRowData);
                mContext.startActivity(intent);
//                ((Activity)mContext).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHoler extends RecyclerView.ViewHolder{

        TextView sidoName;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            sidoName = itemView.findViewById(R.id.textView);


        }
    }


}
