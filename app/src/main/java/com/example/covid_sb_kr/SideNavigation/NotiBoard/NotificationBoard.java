package com.example.covid_sb_kr.SideNavigation.NotiBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.covid_sb_kr.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationBoard extends AppCompatActivity {

    private static final String TAG = "NotificationBoard";
    private NotiAdapter notiAdapter;
    private List<NotiModel> notiLists;

    /*TODO :
    *  스택관리
    *  디자인
    *  공지 시간 DB , 모델 , 리사이클러뷰 추가
    *
    *  싱글벨류는 한번만 읽어들이고 버리는거고 , 에드벨류는 db가 변할때마다 순간적으로 계속 변경시키는차이임.
       직접 db에서 값을 바꿔보고 들어오는 값을 확인하면 구별하기가 쉽다.
       com.google.firebase.database.DatabaseException: Can't convert object of type
       위 에러는 레퍼런스 path에 문제있을떄 발생하는데 ..
       * 데이터베이스 path의 변수명이 다르다거나 , 모델의 변수명과 db의 테이블명 , 노드 명이 다르면 발생.
        * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_notification_board);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        ImageView addPost = findViewById(R.id.addPost);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        notiLists = new ArrayList<>();

        addPost.setOnClickListener(v -> startActivity(new Intent(NotificationBoard.this , NotiAddActivity.class)));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notiLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NotiModel model = snapshot.getValue(NotiModel.class);
                    notiLists.add(model);
                }
                notiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        notiAdapter = new NotiAdapter(NotificationBoard.this, getApplicationContext() , notiLists);
        recyclerView.setAdapter(notiAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}