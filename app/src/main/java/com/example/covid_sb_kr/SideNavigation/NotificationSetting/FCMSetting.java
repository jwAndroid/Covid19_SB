package com.example.covid_sb_kr.SideNavigation.NotificationSetting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.SideNavigation.NotiBoard.NotiAddActivity;
import com.example.covid_sb_kr.SideNavigation.NotiBoard.NotiModel;
import com.example.covid_sb_kr.SideNavigation.NotiBoard.NotificationBoard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Objects;

public class FCMSetting extends AppCompatActivity {

    public static Context context_FCMSetting;
    boolean state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_f_c_m_setting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        context_FCMSetting  = this;

        SwitchButton th_button = findViewById(R.id.th_button);
        ImageView noti_state_img = findViewById(R.id.noti_state_img);

        boolean state = getPreferences("switchkey");
        th_button.setChecked(state);

        if(state){
            noti_state_img.setBackgroundResource(R.drawable.noti_fill);

        }else{
            noti_state_img.setBackgroundResource(R.drawable.noti_non);

        }

        th_button.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            if(isChecked){
                noti_state_img.setBackgroundResource(R.drawable.noti_fill);
            }else{
                noti_state_img.setBackgroundResource(R.drawable.noti_non);
            }
            setPreferences("switchkey", isChecked);

        });

        LinearLayout notification_go = findViewById(R.id.notification_go);
        notification_go.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알람설정")
                        .setIcon(R.drawable.icon1)
                        .setMessage("설정 화면으로 이동하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                startActivity(intent);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });
    }


    private void setPreferences(String key , Boolean value){
        SharedPreferences settings1 = getSharedPreferences("SPFS", 0);
        SharedPreferences.Editor editor = settings1.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean getPreferences(String key){
        SharedPreferences settings = getSharedPreferences("SPFS", 0);
        boolean value = settings.getBoolean(key, false);
        return value;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }




}



