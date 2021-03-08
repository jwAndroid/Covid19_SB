package com.example.covid_sb_kr.Sido;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.Sido;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class SidoActivity extends AppCompatActivity {

//    Activity에서 fragment로 데이터 전달 : Bundle 사용

    private static final String TAG ="MainActivity";

    //요청변수
    private static String key;
    private static int PAGENO;
    private static int NUMOFROWS;
    private static String STARTCREATEDT;
    private static String ENDCREATEDT;

    BufferedReader bufferedReader;
    Sido model;
    ArrayList<Sido> list ;

    //getExtra (getText )

    MainActivity mainActivity ;

    //UI필드 선언
    SwipeRefreshLayout swipeRefreshLayout;

    TextView getDeathCnt_tv,
             getDefCnt_tv,
             getIncDec_tv,
             getIsolClearCnt_tv,
             getIsolIngCnt_tv,
             getLocalOccCnt_tv,
             getOverFlowCnt_tv;

    TextView createDt_tv , sido_name_tv;

    //StringBuilder 리턴값
    String data;
    String  getDeathCnt_str,
            getDefCnt_str,
            getIncDec_str,
            getIsolClearCnt_str,
            getIsolIngCnt_str,
            getLocalOccCnt_str,
            getOverFlowCnt_str;

    String getCreateDt_str;
    BarChart chart2;

    Intent intent;
    String sido;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sido);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inIt();
        list = new ArrayList<>();
        startOpenApi(currentDate());
        intent = getIntent();
        sido  = intent.getStringExtra("key");
        flag = intent.getBooleanExtra("flag" , false);
        System.out.println("시도쪽에서 플래그는?" + flag);
        Log.d("시도군" , "지역:" + sido );


        swipeRefreshLayout.setOnRefreshListener(() -> {
            startOpenApi(currentDate());
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void inIt(){

        mainActivity = new MainActivity();

        getDeathCnt_tv = findViewById(R.id.getDeathCnt_tv);
        getDefCnt_tv = findViewById(R.id.getDefCnt_tv);
        getIncDec_tv = findViewById(R.id.getIncDec_tv);
        getIsolClearCnt_tv = findViewById(R.id.getIsolClearCnt_tv);
        getIsolIngCnt_tv = findViewById(R.id.getIsolIngCnt_tv);
        getLocalOccCnt_tv = findViewById(R.id.getLocalOccCnt_tv);
        getOverFlowCnt_tv = findViewById(R.id.getOverFlowCnt_tv);
        createDt_tv = findViewById(R.id.createDt_tv);
        sido_name_tv = findViewById(R.id.sido_name_tv);
        chart2 = findViewById(R.id.tab1_chart_2);

        swipeRefreshLayout = findViewById(R.id.activity_world_data_swipe_refresh_layout);

    }


    private void setBarChart(int confirmed , int isolClear , int isolIng , int death , int localOcc) {

        chart2.addBar(new BarModel("확진자", confirmed, 0xFF123456));
        chart2.addBar(new BarModel("격리해제", isolClear, 0xFF343456));
        chart2.addBar(new BarModel("격리중", isolIng, 0xFF563456));
        chart2.addBar(new BarModel("사망자", death, 0xFF873F56));
        chart2.addBar(new BarModel("지역발생", localOcc, 0xFF56B7F1));

        chart2.startAnimation();
    }

    private void startOpenApi(String date){
        mainActivity.ShowDialog(SidoActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    data = getXmlData(date);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (TextUtils.isEmpty(getCreateDt_str)){

                                    Log.d("createDt_str" , "createDt_str null? :" + getCreateDt_str);
                                    sido_name_tv.setText(sido);
                                    createDt_tv.setText("아직 업데이트 되지 않았습니다.");
                                    mainActivity.DismissDialog();

                                    if (flag){
                                        startOpenApi(yesterdayForDialog());

                                    }else{
                                        Toast.makeText(SidoActivity.this , "flag is false" , Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    sido_name_tv.setText(sido);
                                    createDt_tv.setText(getCreateDt_str);

                                    if(!TextUtils.isEmpty(getDeathCnt_str)){
                                        getDeathCnt_tv.setText(numberFormat(getDeathCnt_str));
                                    }else{
                                        getDeathCnt_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getDefCnt_str)){
                                        getDefCnt_tv.setText(numberFormat(getDefCnt_str));
                                    }else{
                                        getDefCnt_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getIncDec_str)){
                                        getIncDec_tv.setText("+"+numberFormat(getIncDec_str));
                                    }else{
                                        getIncDec_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getIsolClearCnt_str)){
                                        getIsolClearCnt_tv.setText(numberFormat(getIsolClearCnt_str));
                                    }else{
                                        getIsolClearCnt_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getIsolIngCnt_str)){
                                        getIsolIngCnt_tv.setText(numberFormat(getIsolIngCnt_str));
                                    }else{
                                        getIsolIngCnt_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getLocalOccCnt_str)){
                                        getLocalOccCnt_tv.setText(numberFormat(getLocalOccCnt_str));
                                    }else{
                                        getLocalOccCnt_tv.setText("-");
                                    }

                                    if(!TextUtils.isEmpty(getOverFlowCnt_str)){
                                        getOverFlowCnt_tv.setText(numberFormat(getOverFlowCnt_str));
                                    }else{
                                        getOverFlowCnt_tv.setText("-");
                                    }

//                                    setBarChart(int confirmed , int isolClear , int isolIng , int death , int localOcc)
                                    setBarChart(
                                            Integer.parseInt(getDefCnt_str),
                                            Integer.parseInt(getIsolClearCnt_str),
                                            Integer.parseInt(getIsolIngCnt_str),
                                            Integer.parseInt(getDeathCnt_str),
                                            Integer.parseInt(getLocalOccCnt_str)
                                    );

                                    mainActivity.DismissDialog();

                                }

                            }
                        }, 1000);

                    }
                });
            }
        }).start();
    }

    private String getXmlData(String date) throws UnsupportedEncodingException {

        StringBuilder stringBuffer = new StringBuilder();

        key = getString(R.string.API_KEY);
        PAGENO = 1;
        NUMOFROWS = 10;

//        STARTCREATEDT = "20210128";
//        ENDCREATEDT = "20210128";

        STARTCREATEDT = date;
        ENDCREATEDT = date;

        Log.d("STARTCREATEDT", "날짜함수:"+STARTCREATEDT);
        Log.d(TAG, "OPEN_API_KEY:"+key);

        StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson");

        urlBuilder.append("?").append(URLEncoder.encode("ServiceKey", "UTF-8")).append("=").append(key);
        urlBuilder.append("&").append(URLEncoder.encode("pageNo","UTF-8")).append("=").append(PAGENO);
        urlBuilder.append("&").append(URLEncoder.encode("numOfRows","UTF-8")).append("=").append(NUMOFROWS);
        urlBuilder.append("&").append(URLEncoder.encode("startCreateDt","UTF-8")).append("=").append(STARTCREATEDT);
        urlBuilder.append("&").append(URLEncoder.encode("endCreateDt","UTF-8")).append("=").append(ENDCREATEDT);
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
            XPathExpression xpe = xpath.compile("//items /item");
            NodeList nodeList = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);
            model = new Sido();

            for(int i=0; i < nodeList.getLength(); i++){

                NodeList child = nodeList.item(i).getChildNodes();
                Log.d("NodeList:", "NodeList"+child.toString());

                for(int j=0; j < child.getLength(); j++){
                    Node node = child.item(j);

                    if (node.getNodeName().equals("item")){
                        Log.d("OPEN_API_NODEDATA:", "item"+node.getTextContent());
                    }

                    else if (node.getNodeName().equals("createDt"))
                        { model.setCreateDt(node.getTextContent()); }

                    else if(node.getNodeName().equals("deathCnt"))
                        { model.setDeathCnt(node.getTextContent()); }

                    else if(node.getNodeName().equals("defCnt"))
                        { model.setDefCnt(node.getTextContent()); }

                    else if(node.getNodeName().equals("gubun"))
                        { model.setGubun(node.getTextContent()); }

                    else if(node.getNodeName().equals("incDec"))
                        { model.setIncDec(node.getTextContent()); }

                    else if(node.getNodeName().equals("isolClearCnt"))
                        { model.setIsolClearCnt(node.getTextContent()); }

                    else if(node.getNodeName().equals("isolIngCnt"))
                        { model.setIsolIngCnt(node.getTextContent()); }

                    else if(node.getNodeName().equals("localOccCnt"))
                        { model.setLocalOccCnt(node.getTextContent()); }

                    else if(node.getNodeName().equals("overFlowCnt"))
                        { model.setOverFlowCnt(node.getTextContent()); }

                }

                list.add(model);
                //sido
                if (model.getGubun().equals(sido)){ // << 받은값 넘겨주기.

                    getDeathCnt_str = model.getDeathCnt();
                    getDefCnt_str = model.getDefCnt();
                    getIncDec_str = model.getIncDec();
                    getIsolClearCnt_str = model.getIsolClearCnt();
                    getIsolIngCnt_str = model.getIsolIngCnt();
                    getLocalOccCnt_str = model.getLocalOccCnt();
                    getOverFlowCnt_str = model.getOverFlowCnt();
                    getCreateDt_str = model.getCreateDt();

                    System.out.println("테스트값:" + getDeathCnt_str );

                }
            }
        } catch(Exception e){
            Log.d("xml e catch", e.getMessage());
        }
        return stringBuffer.toString();
    }

    private String yesterdayForDialog(){
        int yesterday = (Integer.parseInt(currentDate()) - 1);
        return String.valueOf(yesterday);
    }
    private String currentDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd" , Locale.KOREAN);
        return mFormat.format(date);

        /*TODO : 리턴값은 현재날짜 yyyyMMdd 값을 반환함. 중요한건 24시가 지나면 다음날을 반환하는데 현재 API는 오전10시쯤 업데이트가 된다는것.
           그래서 보면 새벽12시부터 ~ 대략 아침10시까지 node.getTextContent() = null 이다. 이부분을 제어해주는 역할이 필요함. */
    }
    //통화형태의 콤마를 추가하기위해서
    private String numberFormat(String str){
        return NumberFormat.getInstance(Locale.KOREA).format(Integer.parseInt(str));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}