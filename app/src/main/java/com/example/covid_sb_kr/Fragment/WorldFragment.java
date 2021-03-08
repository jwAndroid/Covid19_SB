package com.example.covid_sb_kr.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid_sb_kr.DataActivity.CountryWiseDataActivity;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.R;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class WorldFragment extends Fragment {

    //https://github.com/blackfizz/EazeGraph << 차트 출처

    TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new,
            tv_recovered, tv_recovered_new, tv_death, tv_death_new, tv_tests;

    SwipeRefreshLayout swipeRefreshLayout;

    String str_confirmed, str_confirmed_new, str_active, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests;

    LinearLayout lin_countrywise;
    BarChart chart2;

    private int int_active_new = 0;
    private final MainActivity mainActivity = new MainActivity();

    View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_world, container, false);

//        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle("World");

        Init();
        FetchWorldData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            FetchWorldData();
            swipeRefreshLayout.setRefreshing(false);
        });

        lin_countrywise.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CountryWiseDataActivity.class)));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    private void FetchWorldData() {
        //show dialog
        mainActivity.ShowDialog(requireActivity());
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        String apiUrl = "https://corona.lmao.ninja/v2/all";

        /*D/VolleyError: error: javax.net.ssl.SSLHandshakeException: Chain validation failed
        * 가 간혹 발생할 수 있는데 avd 날짜 설정오류 때문에 발생.*/

        chart2.clearChart();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Fetching data from API and storing into string
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            str_confirmed = response.getString("cases");
                            str_confirmed_new = response.getString("todayCases");
                            str_active = response.getString("active");
                            str_recovered = response.getString("recovered");
                            str_recovered_new = response.getString("todayRecovered");
                            str_death = response.getString("deaths");
                            str_death_new = response.getString("todayDeaths");
                            str_tests = response.getString("tests");

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {

                                            tv_confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(str_confirmed)));
                                            tv_confirmed_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_confirmed_new)));
                                            tv_active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));

                                            int_active_new = Integer.parseInt(str_confirmed_new) - (Integer.parseInt(str_recovered_new)
                                                    + Integer.parseInt(str_death_new));

                                            System.out.println("int 액티브 : " + int_active_new);

                                            if (int_active_new < 0 ){
                                                tv_active_new.setText(NumberFormat.getInstance().format(int_active_new));
                                            }else{
                                                tv_active_new.setText("+"+NumberFormat.getInstance().format(int_active_new));
                                            }

                                            tv_recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                                            tv_recovered_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_recovered_new)));
                                            tv_death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                                            tv_death_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_death_new)));
                                            tv_tests.setText(NumberFormat.getInstance().format(Integer.parseInt(str_tests)));

                                            setBarChart(
                                                    Integer.parseInt(str_confirmed_new) ,
                                                    Integer.parseInt(str_recovered_new) ,
                                                    Integer.parseInt(str_death_new) );

                                            mainActivity.DismissDialog();

                                        }
                                    },1000);

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });thread.start();
            }
        }, error -> Log.d("VolleyError " , "error: " +error.getMessage()));
        requestQueue.add(jsonObjectRequest);

    }

    private void Init() {
        tv_confirmed = view.findViewById(R.id.activity_world_data_confirmed_textView);
        tv_confirmed_new = view.findViewById(R.id.activity_world_data_confirmed_new_textView);
        tv_active = view.findViewById(R.id.activity_world_data_active_textView);
        tv_active_new = view.findViewById(R.id.activity_world_data_active_new_textView);
        tv_recovered = view.findViewById(R.id.activity_world_data_recovered_textView);
        tv_recovered_new = view.findViewById(R.id.activity_world_data_recovered_new_textView);
        tv_death = view.findViewById(R.id.activity_world_data_death_textView);
        tv_death_new = view.findViewById(R.id.activity_world_data_death_new_textView);
        tv_tests = view.findViewById(R.id.activity_world_data_tests_textView);
        swipeRefreshLayout = view.findViewById(R.id.activity_world_data_swipe_refresh_layout);
        lin_countrywise = view.findViewById(R.id.activity_world_data_countrywise_lin);
        chart2 = view.findViewById(R.id.tab1_chart_2);
    }

    private String numberFormat(String str){
        return NumberFormat.getInstance(Locale.KOREA).format(Integer.parseInt(str));
    }

    private void setBarChart(int confirmed , int recovered , int deaths) {

        chart2.addBar(new BarModel("+Confirmed", confirmed, 0xFF123456));
        chart2.addBar(new BarModel("+Recovered", recovered, 0xFF343456));
        chart2.addBar(new BarModel("+Deaths", deaths, 0xFF563456));
        chart2.startAnimation();
    }

}