package com.example.covid_sb_kr.SideNavigation;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_sb_kr.Adapter.AdapterHospitalSearch;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.Hospital;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class SearchHosActivity extends AppCompatActivity {

    ArrayList<Hospital> list = null;
    Hospital hospital = null;
    RecyclerView recyclerView;
    AdapterHospitalSearch adapter;
    MainActivity mainActivity;
    String hosSearch;

    String hasCode;
    
    /*TODO : 버그노트 02 05
    *  POPUP ACTIVITY 쪽에서 지역값 받아서 그값을 넘겨줘야함. 오타를 검색했을때 앱이 죽어버림.
    *  or 리사이클러뷰+페이징처리+검색 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_search_hos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        mainActivity = new MainActivity();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        hasCode = intent.getStringExtra("code");
        String apiRowData =  intent.getStringExtra("apiRowData");
        hosSearch = intent.getStringExtra("search");

        System.out.println("서치값은?" + hosSearch);

        if (!TextUtils.isEmpty(hasCode) || !TextUtils.isEmpty(apiRowData)){
            System.out.println("해스코드 :" + hasCode +" 로우데이타:" + apiRowData);
            startOpenApi(hasCode , apiRowData);
        }else{
            Utils.toast(this , "?");
        }

    }////////////////////onCreate/////////////////////////

    private void startOpenApi(String data , String rowData){

        mainActivity.ShowDialog(SearchHosActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    getXmlData(data , rowData);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            adapter = new AdapterHospitalSearch(getApplicationContext(), SearchHosActivity.this,  list );
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            /*Inconsistency detected >> java.lang.IndexOutOfBoundsException: Inconsistency detected. */

                            if (hasCode.equals("97")){
                                if (hosSearch.equals("세종")){
                                    Toast.makeText(SearchHosActivity.this , "검색값이 없습니다." , Toast.LENGTH_SHORT).show();
                                    mainActivity.DismissDialog();
                                }

                            }else{
                                adapter.getFilter().filter(hosSearch);
                                adapter.notifyDataSetChanged();
                                mainActivity.DismissDialog();
//                                adapter.notifyDataSetChanged();
                            }

                            mainActivity.DismissDialog();


                        }
                    }, 3000);

                });
            }
        }).start();

    }

    private void getXmlData(String hosCode , String rowData) throws UnsupportedEncodingException {

        String dataKey = getString(R.string.API_KEY);
        String pageNoData = "1";

        String urlPars = "http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList" + "?" +
                URLEncoder.encode("ServiceKey", "UTF-8") + "=" + dataKey + "&" +
                URLEncoder.encode("pageNo", "UTF-8") + "=" + pageNoData + "&" +
                URLEncoder.encode("numOfRows", "UTF-8") + "=" + rowData + "&" +
                URLEncoder.encode("spclAdmTyCd", "UTF-8") + "=" + hosCode;

        try {
            boolean b_hospTyTpCd = false;
            boolean b_sgguNm =false;
            boolean b_sidoNm = false;
            boolean b_spclAdmTyCd = false;
            boolean b_telno = false;
            boolean b_yadmNm = false;

            URL url = new URL(urlPars);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, StandardCharsets.UTF_8));

            String tag;
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){

                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("item")){
                            hospital = new Hospital();
                        }
                        if (parser.getName().equals("hospTyTpCd")) b_hospTyTpCd = true;
                        if (parser.getName().equals("sgguNm")) b_sgguNm = true;
                        if (parser.getName().equals("sidoNm")) b_sidoNm = true;
                        if (parser.getName().equals("spclAdmTyCd")) b_spclAdmTyCd = true;
                        if (parser.getName().equals("telno")) b_telno = true;
                        if (parser.getName().equals("yadmNm")) b_yadmNm = true;
                        break;
                    case XmlPullParser.TEXT:

                        if(b_hospTyTpCd){
                            hospital.setHospTyTpCd(parser.getText());
                            b_hospTyTpCd = false;

                        } else if(b_sgguNm) {

                            hospital.setSgguNm(parser.getText());
                            b_sgguNm = false;

                        } else if (b_sidoNm) {
                            hospital.setSidoNm(parser.getText());
                            b_sidoNm = false;

                        } else if(b_spclAdmTyCd) {
                            hospital.setSpclAdmTyCd(parser.getText());
                            b_spclAdmTyCd = false;

                        } else if(b_telno) {
                            hospital.setTelno(parser.getText());
                            b_telno = false;

                        } else if(b_yadmNm) {
                            hospital.setYadmNm(parser.getText());
                            b_yadmNm = false;
                        }
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item") && hospital != null) {
                            list.add(hospital);
//                            System.out.println("크기===========" +list.size());
                        }
                        break;
                }
                eventType = parser.next();
            }// while의 끝


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search , menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search here..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0){
                    adapter.getFilter().filter(newText);
                    System.out.println("무슨글자 쓰니??" + newText);

                }else{
                    adapter.getFilter().filter("NULL NULL");
                    //만약 newText >> 완전히 지울떄 아무값도 안나타나게 하려면
                    //adapter.getFilter().filter("null");
                    System.out.println("지울떄 ? " + newText);

                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}