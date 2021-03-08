package com.example.covid_sb_kr.DataActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid_sb_kr.Adapter.CountryWiseAdapter;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.CountryWiseModel;
import com.example.covid_sb_kr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class CountryWiseDataActivity extends AppCompatActivity {

    private static final String TAG = "CountryWiseDataActivity" ;
    private CountryWiseAdapter countryWiseAdapter;
    private ArrayList<CountryWiseModel> countryWiseModelArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText et_search;

    private String
            str_country,
            str_confirmed,
            str_confirmed_new,
            str_active,
            str_recovered,
            str_death,
            str_death_new,
            str_tests;

    private final MainActivity activity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_country_wise_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        Init();
        FetchCountryWiseData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            FetchCountryWiseData();
            swipeRefreshLayout.setRefreshing(false);
        });

        //Search
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filtering(s.toString());
            }
        });

    }

    // https://documenter.getpostman.com/view/8854915/SzS7R6uu?version=latest

    private void FetchCountryWiseData() {
        //Show progress dialog
        activity.ShowDialog(this);
//        String apiURL_new = "https://corona.lmao.ninja/v2/countries?sort=country";
        String apiURL = "https://corona.lmao.ninja/v2/countries";
        Log.d(TAG, "FetchCountryWiseData: "+ apiURL );

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest
                = new JsonArrayRequest(Request.Method.GET, apiURL, null,
                response -> {
                    try {
                        countryWiseModelArrayList.clear();

                        for (int i = 0; i<response.length(); i++){
                            JSONObject countryJSONObject = response.getJSONObject(i);

                            str_country = countryJSONObject.getString("country");
                            str_confirmed = countryJSONObject.getString("cases");
                            str_confirmed_new = countryJSONObject.getString("todayCases");
                            str_active = countryJSONObject.getString("active");
                            str_recovered = countryJSONObject.getString("recovered");
                            str_death = countryJSONObject.getString("deaths");
                            str_death_new = countryJSONObject.getString("todayDeaths");
                            str_tests = countryJSONObject.getString("tests");

                            JSONObject flagObject = countryJSONObject.getJSONObject("countryInfo");
                            //국기이미지 가져오려고 , 배열안에 json객체형태로 되어있음.
                            String flagUrl = flagObject.getString("flag");


                            CountryWiseModel countryWiseModel
                                    = new CountryWiseModel(
                                            str_country,
                                            str_confirmed,
                                            str_confirmed_new,
                                            str_active,
                                            str_death,
                                            str_death_new,
                                            str_recovered,
                                            str_tests,
                                            flagUrl);

                            //adding data to our arraylist
                            countryWiseModelArrayList.add(countryWiseModel);
                        }
                        Collections.sort(countryWiseModelArrayList, (o1, o2) -> {
                            if (Integer.parseInt(o1.getConfirmed()) > Integer.parseInt(o2.getConfirmed())){
                                //getConfirmed 를 기준으로 sort
                                return -1;
                            } else {
                                return 1;
                            }
                        });

                        Handler makeDelay = new Handler();
                        makeDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                countryWiseAdapter.notifyDataSetChanged();
                                activity.DismissDialog();
                            }
                        }, 1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

                });
        requestQueue.add(jsonArrayRequest);

    }

    private void Init() {
        swipeRefreshLayout = findViewById(R.id.activity_country_wise_swipe_refresh_layout);
        et_search = findViewById(R.id.activity_country_wise_search_editText);
        RecyclerView rv_country_wise = findViewById(R.id.activity_country_wise_recyclerview);
        rv_country_wise.setHasFixedSize(true);
        rv_country_wise.setLayoutManager(new LinearLayoutManager(this));
        countryWiseModelArrayList = new ArrayList<>();
        countryWiseAdapter = new CountryWiseAdapter(CountryWiseDataActivity.this, countryWiseModelArrayList);
        rv_country_wise.setAdapter(countryWiseAdapter);

    }

    private void filtering(String text) {
        ArrayList<CountryWiseModel> filteredList = new ArrayList<>();
        for (CountryWiseModel item : countryWiseModelArrayList) {
            if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        countryWiseAdapter.filterList(filteredList, text);
    }


    //프래그먼트(MainActivity) --> 현재 액티비티로 인텐트하고 , 툴바의 백버튼을 눌렀을떄의 처리.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {//toolbar의 back키 눌렀을 때 동작
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}