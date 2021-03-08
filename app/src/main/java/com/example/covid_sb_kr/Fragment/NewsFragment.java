package com.example.covid_sb_kr.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covid_sb_kr.Adapter.AdapterNews;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.ModelNews;
import com.example.covid_sb_kr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFragment extends Fragment {

    //TODO : 프래그먼트의 getContext? => read 확인 **중요**
    private final MainActivity mainActivity = new MainActivity();
    private static final String TAG = "NewsFragment" ;
    private Context mContext;
    private Activity activity;
    static public String clientId;
    static public String clientSecret;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;

    private RecyclerView recyclerView;
    private AdapterNews adapter;
    List<ModelNews> list;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;

    int first = 10;
    String display;
    int x = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        inIt();
        running(limit(first));

        //리사이클러뷰 LoadMore 10개씩 받아와서 진행.
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){

                    x += 10;
                    System.out.println("엑스는 ? : " + x);
                    display = limit(x);
                    System.out.println("디스플레이는 ? : " + display);
                    running(display);

                    if (display.equals("100")){
                        System.out.println("마지막입니다.");
                        x = 10;
                    }

                }
            }
        });

        return view;

    }//........onCreateView..........

    private String limit(int dis){
        return Integer.toString(dis);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }

    private void inIt(){

        CircleImageView naver_api_logo = view.findViewById(R.id.naver_api_logo);
        nestedScrollView = view.findViewById(R.id.scroll_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        naver_api_logo.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("알림!")
                    .setMessage("네이버로 이동하시겠습니까?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("http://developers.naver.com"));
                    startActivity(intent);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

    }

    private void running(String dis){
        mainActivity.ShowDialog(requireActivity());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    main(dis);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                        adapter = new AdapterNews(getContext(), list ,activity);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
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

    public void main(String dis){

        String text = null;
        clientId = getString(R.string.NAVER_API_clientId);
        clientSecret = getString(R.string.NAVER_API_clientSecret);
        String searchText = "코로나";

        try {
            text = URLEncoder.encode(searchText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        // json 결과
        String apiURL =
                "https://openapi.naver.com/v1/search/news?" +
                "query="
                +text+
                "&display="+dis+
                "&start=1" +
                "&sort=sim";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);
        parseData(responseBody);
    }
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);

        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());

            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }

        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
//            lineReader.close();

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private void parseData(String responseBody) {

        String title , description , link , pubDate;

        list = new ArrayList<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseBody.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            list.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                title = item.getString("title");
                description = item.getString("description");
                link = item.getString("link");
                pubDate = item.getString("pubDate");

                String reTitle = title
                        .replaceAll("<b>", "")
                        .replaceAll("</b>", "")
                        .replaceAll("&quot;", "\\\"");

                String reDescription = description
                        .replaceAll("<b>", "")
                        .replaceAll("</b>", "")
                        .replaceAll("&quot;", "\\\""); //큰따옴표

                ModelNews model = new ModelNews();
                model.setTitle(reTitle);
                model.setDescription(reDescription);
                model.setLink(link);
                model.setPubDate(pubDate);
                list.add(model);
            }

            System.out.println("리스트 ? : " + list.size());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




}