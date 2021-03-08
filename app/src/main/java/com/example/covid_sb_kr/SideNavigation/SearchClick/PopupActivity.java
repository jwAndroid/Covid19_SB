package com.example.covid_sb_kr.SideNavigation.SearchClick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.covid_sb_kr.R;

import java.util.ArrayList;
import java.util.List;

public class PopupActivity extends AppCompatActivity {

    List<Model> list;
    String[] planets;


//    from hospitalActivity

//     intent.putExtra("code"  , codeName);
//     intent.putExtra("apiRowData" , apiRowData);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        popupSetting();
        recylerSetting();
        setList();

    }

    private void recylerSetting(){
        Intent intent = getIntent();
        list = new ArrayList<>();

        String code = intent.getStringExtra("code");
        String apiRowData = intent.getStringExtra("apiRowData");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this , 3));
        AdapterSearchItem adapter = new AdapterSearchItem(this, list , code , apiRowData );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void setList(){

        Resources res = getResources();
        planets = res.getStringArray(R.array.planets_array);
        for (String s : planets) {
            inItItem(s);
        }
    }

    private void inItItem(String text){
        Model model = new Model();
        model.setName(text);
        list.add(model);
    }

    private void popupSetting(){
        Button close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(v -> { finish(); });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}