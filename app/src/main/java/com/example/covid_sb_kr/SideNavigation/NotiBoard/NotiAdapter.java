package com.example.covid_sb_kr.SideNavigation.NotiBoard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.R;

import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.ViewHoler> {

    private static final String TAG = "NotiAdapter" ;
    public Context mContext;
    public List<NotiModel> mNotiList;
    Activity activity;

    public NotiAdapter(Activity activity , Context mContext, List<NotiModel> mNotiList) {
        this.mContext = mContext;
        this.mNotiList = mNotiList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notiitem , parent , false);
        return new NotiAdapter.ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler viewHoler, int position) {

        NotiModel model = mNotiList.get(position);

        if (!mNotiList.isEmpty()){
            viewHoler.title.setText(model.getTitle());
            viewHoler.description.setText(model.getDescription());
            viewHoler.createDate.setText(model.getCreateDate());

        }else{
            System.out.println("어댑터의 리스트가 널");
        }

        viewHoler.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.toast(mContext , "제목은?:" + model.getTitle());
                Intent intent = new Intent(mContext , DetailNotiActivity.class);
                intent.putExtra("title" , model.getTitle());
                intent.putExtra("description" , model.getDescription());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mNotiList != null ? mNotiList.size() : 0;
    }

    public class ViewHoler extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        TextView createDate;


        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            title  = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            createDate = itemView.findViewById(R.id.createDate);

        }
    }
}
