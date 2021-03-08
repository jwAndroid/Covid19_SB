package com.example.covid_sb_kr.DataActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_ACTIVE;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_CONFIRMED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_DECEASED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NAME;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NEW_CONFIRMED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_NEW_DECEASED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_RECOVERED;
import static com.example.covid_sb_kr.Constants.Constants.COUNTRY_TESTS;

public class EachCountryDataActivity extends AppCompatActivity {

    private static final String TAG = "EachCountryDataActivity" ;

    private TextView tv_confirmed, tv_active, tv_death, tv_recovered, tv_tests;
    private String str_confirmed , str_active ,str_death, str_recovered , str_tests;

    String str_countryName;


    private final MainActivity activity = new MainActivity();

    BarChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_each_country_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        GetIntent();
        Init();
        LoadCountryData();

    }

    private void LoadCountryData(){
        //Show dialog
        activity.ShowDialog(this);
        chart2.clearChart();

        Handler handler = new Handler();

        handler.postDelayed(() -> runOnUiThread(() -> {

            tv_confirmed.setText(numberFormat(str_confirmed));
            tv_active.setText(numberFormat(str_active));
            tv_death.setText(numberFormat(str_death));
            tv_recovered.setText(numberFormat(str_recovered));
            tv_tests.setText(numberFormat(str_tests));
            activity.DismissDialog();

            setBarChart(
                    Integer.parseInt(str_confirmed),
                    Integer.parseInt(str_recovered),
                    Integer.parseInt(str_death),
                    Integer.parseInt(str_active)
            );

        }),1000);
    }

    private void GetIntent() {

        Intent intent = getIntent();
        str_confirmed = intent.getStringExtra(COUNTRY_CONFIRMED);
        str_active = intent.getStringExtra(COUNTRY_ACTIVE);
        str_death = intent.getStringExtra(COUNTRY_DECEASED);
        str_recovered = intent.getStringExtra(COUNTRY_RECOVERED);
        str_tests = intent.getStringExtra(COUNTRY_TESTS);
        str_countryName = intent.getStringExtra(COUNTRY_NAME);

        TextView conName_tv = findViewById(R.id.conName_tv);
        System.out.println("나라이름은 ? : "+ str_countryName);
        conName_tv.setText(str_countryName.trim().replace(" " ,""));

    }

    private void Init(){
        tv_confirmed = findViewById(R.id.activity_each_country_data_confirmed_textView);
        tv_active = findViewById(R.id.activity_each_country_data_active_textView);
        tv_recovered = findViewById(R.id.activity_each_country_data_recovered_textView);
        tv_death = findViewById(R.id.activity_each_country_data_death_textView);
        tv_tests = findViewById(R.id.activity_each_country_data_tests_textView);
        chart2 = findViewById(R.id.tab1_chart_2);
    }

    private String numberFormat(String str){
        return NumberFormat.getInstance(Locale.KOREA).format(Integer.parseInt(str));
    }

    private void setBarChart(int confirmed , int recovered , int deaths , int active) {

        chart2.addBar(new BarModel("+Confirmed", confirmed, Color.RED));
        chart2.addBar(new BarModel("+Recovered", recovered, 0xFF56B7F1));
        chart2.addBar(new BarModel("+Deaths", deaths, Color.RED));
        chart2.addBar(new BarModel("+Deaths", active, Color.BLACK));

        chart2.startAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}