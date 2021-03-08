package com.example.covid_sb_kr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.covid_sb_kr.Fragment.HomeFragment;
import com.example.covid_sb_kr.Fragment.NewsFragment;
import com.example.covid_sb_kr.Fragment.WorldFragment;
import com.example.covid_sb_kr.SideNavigation.AboutActivity;
import com.example.covid_sb_kr.SideNavigation.Copyright;
import com.example.covid_sb_kr.SideNavigation.CovidAboutActivity;
import com.example.covid_sb_kr.SideNavigation.HospitalActivity;
import com.example.covid_sb_kr.SideNavigation.NotiBoard.NotificationBoard;
import com.example.covid_sb_kr.SideNavigation.NotificationSetting.FCMSetting;
import com.example.covid_sb_kr.SideNavigation.Terms;
import com.example.covid_sb_kr.SideNavigation.WorldNews.LoadJsonActivity;
import com.example.covid_sb_kr.Utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //repo
    //commit test

    private static final String TAG ="MainActivity" ;
    static final int REQUEST_CODE = 123;

    private String version;
    private String appUrl;
    private ProgressDialog progressDialog;
    private boolean doubleBackToExitPressedOnce = false;
    private Toast backPressToast;

    private FragmentManager fragmentManager;
    private Fragment home, world, news;
    ImageButton moveHome , moveWorld , moveNews ;

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //버전 테스트 마시멜로우 이상에서만 기능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            getTokenFCM(); //fcm토큰 알람처리를 위해서 진행

            ConnectivityManager cm = (ConnectivityManager)
                    getApplicationContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            /*기기 네트워크 환경 제어, 와이파이 or 데이터 or DisConnect */
            if (activeNetwork != null && activeNetwork.isConnected()){

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                    System.out.println("3G,4G,LTE Connect" );
                    if (isConnected){

                        startApplications();

                    }else{
                        System.out.println("Network DisConnect" );
                    }

                }else if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                    System.out.println("TYPE_WIFI Connect" );
                    if (isConnected){

                        startApplications();

                    }else{
                        System.out.println("Network DisConnect" );
                    }
                }
            }else{
                System.out.println("Network DisConnect" );
                Utils.toast(this , "인터넷 연결을 확인해주세요!");
            }
        }else{
            Utils.toast(this , "안드로이드 버전을 확인해주세요");
        }

    }//..................onCreate..............

    private void getTokenFCM(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        System.out.println("Fetching FCM registration token failed L:" + task.getException());
                        return;
                    }
                    String token = task.getResult();
                    System.out.println("Fetching FCM registration token S:" + token);

                });

    }

    private void startApplications(){
        Init(); //Initialise

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //왼쪽상단 drawerLayout 구현
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        startDrawNavi();

        checkPermission(); //멀티퍼미션
        CheckForUpdate(); //앱 버전 제어 (firebase db연동)
        startFragment(); //앱 하단 프래그먼트

    }

    @SuppressLint("NonConstantResourceId")
    private void startDrawNavi(){

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                //Notification
                case R.id.app_notification_menu :
                    /*02 10 핸들러 이용 . 먼저 close 해주고 , 딜레이 줘서 intent진행 */
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , NotificationBoard.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },500);

                    break;
                case R.id.notification_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , FCMSetting.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },500);
                    break;

                //Hospital,News
                case R.id.hosptital_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler4 = new Handler();
                    handler4.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , HospitalActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },600);
                    break;

                case R.id.world_news_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler7 = new Handler();
                    handler7.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , LoadJsonActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },600);
                    break;

                    //Copyright
                case R.id.feedback_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler5 = new Handler();
                    handler5.postDelayed(() -> {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("plain/text");
                        String[] address = {"cjd9408er@naver.com"};
                        email.putExtra(Intent.EXTRA_EMAIL, address);
                        email.putExtra(Intent.EXTRA_SUBJECT, "피드백 신청합니다.");
                        email.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(email);
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },500);
                    break;

                case R.id.sh_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler6 = new Handler();
                    handler6.postDelayed(() -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://velog.io/@cjd9408er");
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                        overridePendingTransition(R.anim.slide_up , R.anim.slide_down);
                    },500);
                    break;

                case R.id.copyright_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler8 = new Handler();
                    handler8.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , Copyright.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },400);
                    break;

                case R.id.terms_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler9 = new Handler();
                    handler9.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this , Terms.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },400);
                    break;

                case R.id.dev_page_menu :
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    },500);
                    break;
            }

            return true;
        });
    }

    private void ch(Activity activity , Class mClass , long postDelay){
        Handler handler = new Handler();
        handler.postDelayed(() -> startActivity(new Intent(activity , mClass)),postDelay);
    }

    /* 프래그먼트 beginTransaction()은 replace 대신에 add로 진행 , add는 메모리를 올려놓고서
    * 앱이 기능하는데, replace는 프래그먼트를 바꾸면(버튼 클릭하면) 계속 앱이 기능함.
    * 하단버튼(프래그먼트)을 계속 누를때마다 진행되면 안되니까 add로 진행했다. */
    private void startFragment(){

        fragmentManager = getSupportFragmentManager();
        home = new HomeFragment(); //초기화면 home프래그먼트
        fragmentManager.beginTransaction().replace(R.id.container, home).commit();

        moveHome.setOnClickListener(v -> {

            if(home == null) {
                home = new HomeFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.container, home)
                        .commit();
            }

            if(home != null) fragmentManager.beginTransaction()
                    .show(home)
                    .commit();

            if(world != null) fragmentManager.beginTransaction()
                    .hide(world)
                    .commit();

            if(news != null) fragmentManager.beginTransaction()
                    .hide(news)
                    .commit();
        });

        moveWorld.setOnClickListener(v -> {

            if(world == null) {
                world = new WorldFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.container, world)
                        .commit();
            }

            if(home != null) fragmentManager.beginTransaction()
                    .hide(home)
                    .commit();

            if(world != null) fragmentManager.beginTransaction()
                    .show(world)
                    .commit();

            if(news != null) fragmentManager.beginTransaction()
                    .hide(news)
                    .commit();
        });

        moveNews.setOnClickListener(v -> {

            if(news == null) {
                news = new NewsFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.container, news)
                        .commit();
            }

            if(home != null) fragmentManager.beginTransaction()
                    .hide(home)
                    .commit();

            if(world != null) fragmentManager.beginTransaction()
                    .hide(world)
                    .commit();

            if(news != null) fragmentManager.beginTransaction()
                    .show(news)
                    .commit();

        });
    }
    /*n개의 권한여부 */
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this ,
                Manifest.permission.ACCESS_FINE_LOCATION)
                +ContextCompat.checkSelfPermission(MainActivity.this ,
                Manifest.permission.READ_CONTACTS)
                +ContextCompat.checkSelfPermission(MainActivity.this ,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
        ){
            //when permission not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)
            ){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("권한을 허용해주세요.");
                builder.setMessage("위치,전화기록부,전화걸기");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
                                        Manifest.permission.READ_CONTACTS ,
                                        Manifest.permission.CALL_PHONE}
                                , REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel" , null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
                                Manifest.permission.READ_CONTACTS ,
                                Manifest.permission.CALL_PHONE}
                        , REQUEST_CODE
                );
            }
        }else{
//            Toast.makeText(MainActivity.this , "이미 퍼미션을 받았습니다 " , Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if ((grantResults.length>0) && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)){
                //위 조건을 만족시킨다면 퍼미션 승인!
                Toast.makeText(MainActivity.this , "권한 승인", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this , "권한 거절", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void Init() {
        moveHome = findViewById(R.id.btn_main_a);
        moveWorld = findViewById(R.id.btn_main_b);
        moveNews = findViewById(R.id.btn_main_c);

        navigationView = findViewById(R.id.navmenu);
        drawerLayout = findViewById(R.id.drawer);

//        net_tv = findViewById(R.id.net_tv);

    }

    /*TODO : 내부 APP버전 꼭 올리기(ver up) */
    private void CheckForUpdate() {
        try{
            //버전 먼저 가져와서 현재버전 할당.
            version = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView ver_tv = findViewById(R.id.ver_tv);
            ver_tv.setText(version);
            //레퍼런스 생성
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase
                    .getReference("Version")
                    .child("versionNumber");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String versionName = (String) dataSnapshot.getValue(); //db 내부에서 가져옴
                    Log.d(TAG, "db ver ? : " + versionName);
                    assert versionName != null;
                    if(!versionName.equals(version)){//프로젝트버전 != db버전 인지? 버전이 올라갔는지
                        androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("New Version")
                                .setMessage("새로운 버전이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton("UPDATE", (dialog, which) -> {

                                    DatabaseReference myRef = FirebaseDatabase.getInstance()
                                            .getReference("Version")
                                            .child("appUrl");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            //dataSnapshot.getValue() >> 앱 업데이트 설치 url
                                            appUrl = Objects.requireNonNull(dataSnapshot1.getValue()).toString();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            System.out.println(databaseError.getMessage());
                                        }
                                    });

                                }).setNegativeButton("EXIT", (dialog, which) -> {
                                    finish();
                                    Utils.toast(getApplicationContext() , "구글 플레이스토어에서 업데이트가 가능합니다.");
                                }).create();

                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }else{
                        System.out.println("ver:" + version);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ShowDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
    public void DismissDialog() {
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_about){
            startActivity(new Intent(MainActivity.this , CovidAboutActivity.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);
    }
    //onBackPressed 오버라이딩 >> 더블클릭시 앱 종료할껀지 제어
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            backPressToast.cancel();
            super.onBackPressed();

            //프로세스 종료
            ActivityCompat.finishAffinity(this);
            System.exit(0);

            return;
        }

        doubleBackToExitPressedOnce = true;
        backPressToast = Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);
        backPressToast.show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }




}