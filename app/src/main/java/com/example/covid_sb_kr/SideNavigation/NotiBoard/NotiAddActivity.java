package com.example.covid_sb_kr.SideNavigation.NotiBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.covid_sb_kr.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NotiAddActivity extends AppCompatActivity {

    /*FireBase 서버쪽에서 직접적으로 테이블에 데이터를 넣어서 > 리스트에 뿌려줄수도 있고 ,
    * 버튼 able = true 로 지정하여 ui로 뿌려줄수도 있다*/

    EditText title_et ,  description_et , createDate_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_noti_add);

        title_et = findViewById(R.id.title_et);
        description_et = findViewById(R.id.description_et);
        createDate_et = findViewById(R.id.createDate_et);

        Button server_save = findViewById(R.id.server_save);
        server_save.setOnClickListener(v -> upload());

    }

    private void upload(){
        EditText chlid_tv = findViewById(R.id.child_tv);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(chlid_tv.getText().toString().trim());

        HashMap<String , Object > hashMap = new HashMap<>();
        hashMap.put("title" , title_et.getText().toString());
        hashMap.put("description" , description_et.getText().toString());
        hashMap.put("createDate" , createDate_et.getText().toString());

        reference.setValue(hashMap)
                .addOnCompleteListener(task -> {

                    System.out.println("레퍼런스 서버전송 선공");
                    Intent intent = new Intent(NotiAddActivity.this , NotificationBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }).addOnFailureListener(e -> System.out.println("레퍼런스 서버전송 실패" + e.getMessage()));

    }

}