package com.example.covid_sb_kr.SideNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covid_sb_kr.Adapter.HospitalAdapter;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.Hospital;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;
import com.example.covid_sb_kr.SideNavigation.SearchClick.PopupActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class HospitalActivity extends AppCompatActivity {

    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final String TAG = "HospitalActivity";
    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Hospital> list = new ArrayList<>();
    ArrayList<Hospital> list2 = new ArrayList<>();
    Hospital hospital = null;
    RecyclerView recyclerView;
    HospitalAdapter adapter;

    boolean isLoading = false;
    MainActivity mainActivity;

    String[] hospitalDivisionCode;
    String apiRowData;

    //로컬 db -- > 쿼리 -- > 리사이클러뷰
    // 문자열 자음 , 모음이 포함한다면 ?? -- > 테스트 진행

    BufferedReader bufferedReader;
    String totalCnt;

    TextView hosCode_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_hospital);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        hosCode_tv = findViewById(R.id.hosCode_tv);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.activity_country_wise_swipe_refresh_layout);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mainActivity = new MainActivity();

        startDialog();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            /*하나의 list를 가지고 3개의 xml데이터를 파싱하고있는데 .. 이렇게 들어가면 복잡해짐.*/
            list.clear();
            list2.clear();
            startDialog();

        });

//        getString(HospitalActivity.this , "response");

    }

    private void dataMore() {
        Log.d(TAG, "dataMore: ");
        list2.add(null);

        /*Cannot call this method while RecyclerView is computing a layout or scrolling
           출처: https://gogorchg.tistory.com/entry/Android-Cannot-call-this-method-while-RecyclerView-is-computing-a-layout-or-scrolling */

        Handler handler2 = new Handler();
        final Runnable r = () -> adapter.notifyItemInserted(list2.size() -1 );
        handler2.post(r);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            list2.remove(list2.size() -1);
            int scrollPosition = list2.size();
            adapter.notifyItemRemoved(scrollPosition);
            int currentSize = scrollPosition;
            int nextLimit = currentSize + 10;

            for (int i=currentSize; i<nextLimit; i++) {

                if (i == list.size()) {
                    return;
                }

                list2.add(list.get(i));
            }

            adapter.notifyDataSetChanged();
            isLoading = false;
        }, 2000);

    }
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list2.size() -1 ) {
                        dataMore();
                        isLoading = true;
                    }
                }
            }
        });


    }
    private void firstData() {
        // 총 아이템에서 10개를 받아옴

        for (int i=0; i<10; i++) {

            if (list.size() !=0 && list.size() !=1){
                list2.add(list.get(i));
                System.out.println("firstData()에서의 list1의 사이즈는?:" + list.size());

            }else{

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(HospitalActivity.this , "아래로 스크롤하여 새로고침 해주세요");

                    }
                }, 0);
                break;
            }

        }

        System.out.println("리스트1 사이즈 : " + list.size());
        System.out.println("리스트2 사이즈 : " + list2.size());
    }

    private void startOpenApi(String data , String rowData){
        mainActivity.ShowDialog(HospitalActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    getXmlData(data , rowData);
                    firstData();
                    System.out.println("firstData()실행" + list.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                adapter = new HospitalAdapter(list2 , getApplicationContext() , HospitalActivity.this);
                                recyclerView.setAdapter(adapter);
                                initScrollListener();
                                adapter.notifyDataSetChanged();
                                mainActivity.DismissDialog();

                            }
                        }, 2000);

                    }
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

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item") && hospital != null) {
                            list.add(hospital);
                            System.out.println("getXML의 list.size() 카운트:" + list.size());
                            /*애초에 이쪽에서 리스트size가 1이다 .. xml파싱하는게 잘못된경우
                            * 페이징처리 로직이 문제가 아니라 (사실 문제되는게 하나 있었음 범위를 총9개만 받고있었다.) 애초에 xml데이터를 받아오는쪽에서 문제되었었다. */
                        }
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

                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.hospital, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_hospital){

                Intent intent = new Intent(HospitalActivity.this, PopupActivity.class);
                String codeName = hospitalDivisionCode[0];
                intent.putExtra("code"  , codeName);
                System.out.println("onOptionsItemSelected 의 apiRowData "+getString(HospitalActivity.this , "response"));
                intent.putExtra("apiRowData" , getString(HospitalActivity.this , "response"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

        } else if(item.getItemId() == R.id.menu_more){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("국민안심병원의 선정유형")
                    .setMessage(getString(R.string.HOS_A_CD)+"\n"
                            +getString(R.string.HOS_B_CD)+"\n"
                            +getString(R.string.exam_about));

            builder.setPositiveButton("OK", (dialog, which) -> {

            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (item.getItemId() == android.R.id.home){

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void startDialog(){

        hospitalDivisionCode = new String[]{null};

        final String[] words = new String[]{"국민안심병원", "코로나검사실시기관","선별진료소운영기관" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택")
                .setIcon(R.drawable.noti_icon8)
                .setNegativeButton("취소", null)
                .setItems(words, (dialogInterface, which) -> {

                    switch (words[which]) {

                        case "국민안심병원":
                            hospitalDivisionCode[0] = "A0";
                            hosCode_tv.setText(words[which]);
                            startResponseApi(hospitalDivisionCode[0]);
                            startOpenApi(hospitalDivisionCode[0], getString(HospitalActivity.this, "response"));
                            break;

                        case "코로나검사실시기관":
                            hospitalDivisionCode[0] = "97";
                            hosCode_tv.setText(words[which]);
                            startResponseApi(hospitalDivisionCode[0]);
                            startOpenApi(hospitalDivisionCode[0], getString(HospitalActivity.this, "response"));
                            break;

                        case "선별진료소운영기관":
                            hospitalDivisionCode[0] = "99";
                            hosCode_tv.setText(words[which]);
                            startResponseApi(hospitalDivisionCode[0]);
                            startOpenApi(hospitalDivisionCode[0], getString(HospitalActivity.this, "response"));
                            break;
                    }

                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void startResponseApi(String type ){

        new Thread(() -> {

            try {

                getResponse(type);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {

                System.out.println("레스폰즈의 값은??:" + totalCnt);
                String thisTotalCount = totalCnt;
                setString(HospitalActivity.this , "response" , thisTotalCount );
                System.out.println("getString:" + getString(HospitalActivity.this , "response"));

            });

        }).start();

    }
    private void getResponse(String type) throws UnsupportedEncodingException {

        StringBuilder stringBuffer = new StringBuilder();

        //요청변수
        String key = getString(R.string.API_KEY);
        int pageNo_int = 1;

//        String spclAdmTyCd_str = data;
//        &spclAdmTyCd=A0

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B551182/pubReliefHospService/getpubReliefHospList");
        urlBuilder.append("?").append(URLEncoder.encode("ServiceKey", "UTF-8")).append("=").append(key);
        urlBuilder.append("&").append(URLEncoder.encode("pageNo","UTF-8")).append("=").append(pageNo_int);
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows","UTF-8")).append("=").append("10");
        urlBuilder.append("&").append(URLEncoder.encode("spclAdmTyCd","UTF-8")).append("=").append(type);

        Log.d(TAG, "urlBuilder(append):" + urlBuilder);

        try {
            //DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = null;

            //URL
            URL url = new URL(urlBuilder.toString());
            Log.d("URL", "작업 위치의 URL 객체화 완료");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("urlConnection",urlConnection.getResponseMessage());

            //응답 읽기
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String result = "";
            String line;

            while((line = bufferedReader.readLine()) != null){
                result = result + line.trim();
                Log.d("line",result);
            }

            //xml 파싱
            InputSource is = new InputSource(new StringReader(result));
            doc = docBuilder.parse(is);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression xpe = xpath.compile("//response /body");

            NodeList nodeList = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

            for(int i=0; i < nodeList.getLength(); i++){

                NodeList child = nodeList.item(i).getChildNodes();
                Log.d("NodeList:", "NodeList"+child.toString());

                for(int j=0; j < child.getLength(); j++){
                    Node node = child.item(j);

//                    if (node.getNodeName().equals("body")){
//
//                    }
//                    if (node.getNodeName().equals("numOfRows")){
//                        stringBuffer.append(node.getTextContent());
//                        System.out.println("numOfRows 노드는? : " + node.getTextContent());
//                    }

                    if (node.getNodeName().equals("totalCount")){
                        stringBuffer.append(node.getTextContent());
                        System.out.println("totalCount 노드는? : " + node.getTextContent());
                        totalCnt = node.getTextContent();
                    }

                }
            }
        } catch(Exception e){
            Log.d("xml e catch", e.getMessage());
        }

//        data = stringBuffer.toString();
//        return stringBuffer.toString();

    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

}