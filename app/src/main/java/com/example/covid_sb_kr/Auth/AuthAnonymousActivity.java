package com.example.covid_sb_kr.Auth;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/* 사용x Splash 에서 한번에 처리 */
public class AuthAnonymousActivity extends AppCompatActivity {

    private static final String TAG = "AnonymousAuth";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_auth_anonymous);

        mAuth = FirebaseAuth.getInstance();

    }

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
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.w(TAG, "DatabaseReference addOnCompleteListener:"+userid);
                                //인텐트 메인
                                Intent intent = new Intent(AuthAnonymousActivity.this , MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.w(TAG, "DatabaseReference addOnFailureListener:"+e.getMessage());
                            }
                        });
                        updateUI(user);

                    } else {

                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(AuthAnonymousActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);

                    }

                });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

//    private void signOut() {
//        mAuth.signOut();
//        updateUI(null);
//    }

    private void updateUI(FirebaseUser user) {

        boolean isSignedIn = (user != null);

        if (isSignedIn) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AuthAnonymousActivity.this , MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    finish();
                }
            },1000);


        } else {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(AuthAnonymousActivity.this ,  "환영합니다.");
                    signInAnonymously();
                }
            },1000);


        }

    }


}