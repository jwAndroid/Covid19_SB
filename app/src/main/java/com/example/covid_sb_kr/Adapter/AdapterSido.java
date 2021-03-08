package com.example.covid_sb_kr.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.Model.ModelNews;
import com.example.covid_sb_kr.Model.SidoCardModel;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Sido.SidoActivity;

import java.util.List;

public class AdapterSido extends RecyclerView.Adapter<AdapterSido.ViewHolder> {

    private static final String TAG = "AdapterSido" ;
    public Context mContext;
    public List<SidoCardModel> list;
    boolean flag;

    public AdapterSido(Context mContext, List<SidoCardModel> list , boolean flag) {
        this.mContext = mContext;
        this.list = list;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sido_item , parent , false);
        return new AdapterSido.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        SidoCardModel model = list.get(position);

        if (!TextUtils.isEmpty(model.getSidoName()) & !TextUtils.isEmpty(model.getSidoCnt())){

            viewHolder.sido_tv.setText(model.getSidoName());
            viewHolder.sido_cnt.setText("+"+model.getSidoCnt());

        }else{
            viewHolder.sido_tv.setText("널값");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(mContext
//                        , "position:"+position+
//                                "\n"+"지역:"+model.getSidoName()
//                                +"\n"+"수:"+model.getSidoCnt()
//                        , Toast.LENGTH_SHORT).show();

                String parm = model.getSidoName();
                Intent intent = new Intent(mContext , SidoActivity.class);
                intent.putExtra("key" , parm);
                intent.putExtra("flag" , flag);
                mContext.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView sido_tv , sido_cnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sido_tv = itemView.findViewById(R.id.sido_tv);
            sido_cnt = itemView.findViewById(R.id.sido_cnt);
        }
    }

}
