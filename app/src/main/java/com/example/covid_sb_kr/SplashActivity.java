package com.example.covid_sb_kr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.covid_sb_kr.Auth.AuthAnonymousActivity;
import com.example.covid_sb_kr.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {


    private static final String TAG = "SplashActivity" ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        //하단바 컬러변경
//        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //익명 로그인을 위한 파이어베이스 어스객체
        mAuth = FirebaseAuth.getInstance();

    }

    //익명로그인 함수구현
    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userid = user.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("AnonymousUsers")
                                .child(userid);
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("id" , userid);
                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            Log.w(TAG, "db ref addOnCompleteListener:"+userid);
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                Intent intent = new Intent(SplashActivity.this , MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                                finish();
                            },1200);

                        }).addOnFailureListener(e -> Log.w(TAG, "DatabaseReference addOnFailureListener:"+e.getMessage()));

                    }else {
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(SplashActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);

                    }
                });

    }

    /* Activity의 생명주기에 따라 onCreate -> onStart 이니까 auth객체 먼저 할당받고서 진행
    *  이미 존재하는 유저인지 새로운유저인지 구분하기위해 제일먼저 updateUI 먼저 실행  */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser); //해당유저 받아와서 매개변수로 던져주고
    }

//    private void signOut() {
//        mAuth.signOut();
//        updateUI(null);
//    }

    private void updateUI(FirebaseUser user) {

        boolean isSignedIn = (user != null);
        Handler handler = new Handler();
        if (isSignedIn) {
            System.out.println("isSignedIn TRUE:" + true);
            //존재한다면 바로 넘기고
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this , MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                finish();
            },1200);

        }else{
            //처음이라면 익명가입
            System.out.println("isSignedIn FALSE NEW USER :" + false);
            signInAnonymously();
        }

    }

}