package com.example.covid_sb_kr.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covid_sb_kr.Adapter.AdapterSido;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.Model.Sido;
import com.example.covid_sb_kr.Model.SidoCardModel;
import com.example.covid_sb_kr.R;
import com.google.firebase.messaging.FirebaseMessaging;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


/* getXmlData ( xml 파싱 ) => startOpenApi */
public class HomeFragment extends Fragment {

    private static final String TAG ="MainActivity";
    //요청변수
    private static String key;
    private static int PAGENO;
    private static int NUMOFROWS;
    private static String STARTCREATEDT;
    private static String ENDCREATEDT;

    //프래그먼트 view
    View view;

    //ui
    SwipeRefreshLayout swipeRefreshLayout;
    TextView decideCnt_tv , clearCnt_tv , examCnt_tv ,careCnt_tv, deathCnt_tv , createDt_tv;
    String decideCnt_str , clearCnt_str , examCnt_str , careCnt_str, deathCnt_str , createDt_str;
    String createDt_check;

    MainActivity mainActivity;
    private Activity activity;

    //StringBuilder 리턴값
    String data;
    String dataSido;

    //필드 선언
    BufferedReader bufferedReader;

    //시도 data , ui 필드선언

    private RecyclerView recyclerView;
    private AdapterSido adapter;
    List<SidoCardModel> list;
    String[] planets;

    //----------------------
    Sido model;
    String[] planets2;
    boolean flag = false;

    //---------sort-------------
    TextView max_tv,min_tv;
    int maxData , minData;

    //------------------

    BarChart chart2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        inIt();

        startOpenApi(currentDate());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            startOpenApi(currentDate());
            swipeRefreshLayout.setRefreshing(false);
        });

        Resources res = getResources();
        planets = res.getStringArray(R.array.planets_array);
        planets2 = new String[planets.length];

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;

    }
    private void inIt(){
        mainActivity = new MainActivity();
        decideCnt_tv = view.findViewById(R.id.decideCnt_tv);
        clearCnt_tv = view.findViewById(R.id.clearCnt_tv);
        examCnt_tv = view.findViewById(R.id.examCnt_tv);
        careCnt_tv = view.findViewById(R.id.careCnt_tv);
        deathCnt_tv = view.findViewById(R.id.deathCnt_tv);
        createDt_tv = view.findViewById(R.id.createDt_tv);
        max_tv = view.findViewById(R.id.max_tv);
        min_tv = view.findViewById(R.id.min_tv);
        swipeRefreshLayout = view.findViewById(R.id.activity_world_data_swipe_refresh_layout);
        chart2 = view.findViewById(R.id.tab1_chart_2);
    }

    private void setBarChart(int confirmed , int deaths , int exam , int isoClear) {

        chart2.addBar(new BarModel("확진자", confirmed, 0xFF123456));
        chart2.addBar(new BarModel("격리해제", isoClear, 0xFF563456));
        chart2.addBar(new BarModel("사망자", deaths, 0xFF873F56));
        chart2.addBar(new BarModel("검사중", exam, 0xFF56B7F1));
        chart2.startAnimation();


    }

    private void startOpenApi(String date){

        new Thread(() -> {
            try {

                data = getXmlData(date);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.ShowDialog(getContext());
                    chart2.clearChart();
                    //TODO : createDt를 기준으로 NULL일때 else 일때로 나눠서 업데이트확인. createDt를 두개의 변수에 할당.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                        @Override
                        public void run() {

                            if(createDt_check == null){

                                Log.d("createDt_str" , "createDt_str null? :" + createDt_check);
//                                    Toast.makeText(getContext() , "업데이트 되지 않았습니다!" , Toast.LENGTH_SHORT).show();

                                createDt_tv.setText("아직 업데이트 되지 않았습니다.");
                                mainActivity.DismissDialog();

                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle("알림")
                                        .setMessage("전날의 상황판을 보시겠습니까?")
                                        .setIcon(R.drawable.icon1);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO : 만약에 날짜별로 확진자현황을 보고싶다면 >> 다이얼로그 띄워주고 변수값받아서 그대로 메소드 호출
                                        flag = true;
                                        System.out.println("플래그는?" + flag);
                                        startOpenApi(yesterdayForDialog());
                                        startOpenApi_sido(yesterdayForDialog()); //시 도 데이터 어제자

                                        mainActivity.DismissDialog();

                                    }
                                }).setCancelable(false);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        mainActivity.DismissDialog();

                                    }
                                }).setCancelable(false);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }else{
                                startOpenApi_sido(currentDate()); //시 도 데이터 오늘자
                                //TODO : 각 UI객체 VIEW 셋팅.
                                Toast.makeText(getContext() , "업데이트 일시 : " + createDt_check , Toast.LENGTH_SHORT).show();
                                createDt_tv.setText(createDt_str);

                                if (!TextUtils.isEmpty(decideCnt_str)){
                                    decideCnt_tv.setText(numberFormat(decideCnt_str));
                                }else{
                                    decideCnt_tv.setText("-");
                                }

                                if (!TextUtils.isEmpty(clearCnt_str)){
                                    clearCnt_tv.setText(numberFormat(clearCnt_str));
                                }else{
                                    clearCnt_tv.setText("-");
                                }

                                if (!TextUtils.isEmpty(examCnt_str)){
                                    examCnt_tv.setText(numberFormat(examCnt_str));
                                }else{
                                    examCnt_tv.setText("-");
                                }

                                if (!TextUtils.isEmpty(deathCnt_str)){
                                    deathCnt_tv.setText(numberFormat(deathCnt_str));
                                }else{
                                    deathCnt_tv.setText("-");
                                }

                                if (!TextUtils.isEmpty(careCnt_str)){
                                    careCnt_tv.setText(numberFormat(careCnt_str));
                                }else{
                                    careCnt_tv.setText("-");
                                }

                                setBarChart(
                                        Integer.parseInt(decideCnt_str),
                                        Integer.parseInt(deathCnt_str),
                                        Integer.parseInt(examCnt_str),
                                        Integer.parseInt(clearCnt_str)
                                );
                                mainActivity.DismissDialog();
                            }

                        }
                    }, 1000);

                }
            });
        }).start();
    }
    private String getXmlData(String date) throws UnsupportedEncodingException {

        StringBuilder stringBuffer = new StringBuilder();

        key = getString(R.string.API_KEY);
        PAGENO =1;
        NUMOFROWS = 10;

//        STARTCREATEDT = "20210129";
//        ENDCREATEDT = "20210129";

        STARTCREATEDT = date;
        ENDCREATEDT = date;

        Log.d("STARTCREATEDT", "날짜함수:"+STARTCREATEDT);
        Log.d(TAG, "OPEN_API_KEY:"+key);

        StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson");
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

            for(int i=0; i < nodeList.getLength(); i++){

                NodeList child = nodeList.item(i).getChildNodes();
                Log.d("NodeList:", "NodeList"+child.toString());

                for(int j=0; j < child.getLength(); j++){
                    Node node = child.item(j);

                    if (node.getNodeName().equals("item")){
                        Log.d("OPEN_API_NODEDATA:", "item"+node.getTextContent());

                    }
                    //else if 로 돌렸었음
                    if (node.getNodeName().equals("createDt")){

                        stringBuffer.append(node.getTextContent());
                        createDt_str = node.getTextContent();
                        createDt_check = node.getTextContent();

                    }
                    if(node.getNodeName().equals("decideCnt")){

                        stringBuffer.append(node.getTextContent());
                        decideCnt_str = node.getTextContent();

                    }
                    if(node.getNodeName().equals("clearCnt")){

                        stringBuffer.append(node.getTextContent());
                        clearCnt_str = node.getTextContent();

                    }
                    if(node.getNodeName().equals("examCnt")){

                        stringBuffer.append(node.getTextContent());
                        examCnt_str = node.getTextContent();

                    }
                    if(node.getNodeName().equals("deathCnt")){

                        stringBuffer.append(node.getTextContent());
                        deathCnt_str = node.getTextContent();

                    }
                    if(node.getNodeName().equals("careCnt")){

                        stringBuffer.append(node.getTextContent());
                        careCnt_str = node.getTextContent();
                    }

                }
            }
        } catch(Exception e){
            Log.d("xml e catch", e.getMessage());
        }

        return stringBuffer.toString();
    }

    private void startOpenApi_sido(String date){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    dataSido = getXmlData_sido(date);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                //TODO : 리사이클러뷰로 진행
                                recyclerView = view.findViewById(R.id.sido_recyclerView);
                                list = new ArrayList<>();
                                System.out.println("어댑터 플래그는?" + flag);
                                adapter = new AdapterSido(getContext(), list , flag);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 4));
                                recyclerView.setAdapter(adapter);

                                for (int i = 0 ; i <  planets.length; i++){
                                    addItem(planets[i] , planets2[i]);
                                    System.out.println("테스트값: " + planets2[i]);
                                    //TODO : 지금은 1로 고정인데
                                    // sidoCnt의 값은 먼저 홈 프레그먼트쪽에서 json을 풀어준뒤 , 지역의 특정값을 따로따로 받아서 이값을 배열로 묶어서 똑같이 뿌려주기.
                                    // 그냥 , 리스트데이터 얻고 넣어주면 끝인데 , 이 리스트의 인덱스(지역)에 주의해서 넣어준다.
                                }
                                adapter.notifyDataSetChanged();

                                forMaxandMin(planets2);
                                System.out.println("스코프에서 최소값은 : " +minData);
                                System.out.println("스코프에서 최대값은 : " +maxData);

                                max_tv.setText(String.valueOf(maxData));
                                min_tv.setText(String.valueOf(minData));

                            }
                        }, 500);

                    }
                });
            }
        }).start();
    }
    private String getXmlData_sido(String date) throws UnsupportedEncodingException {

        StringBuilder stringBuffer = new StringBuilder();

        key = getString(R.string.API_KEY);
        PAGENO =1;
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

                    if(node.getNodeName().equals("gubun")) {
                        model.setGubun(node.getTextContent());
                    }

                    if(node.getNodeName().equals("incDec")) {
                        model.setIncDec(node.getTextContent());
                    }

                }

//                list.add(model);

                for(int k = 0 ; k < planets.length; k++){

                    if (model.getGubun().equals(planets[k])){ // << 받은값 넘겨주기.

                        planets2[k] = model.getIncDec();
                        System.out.println("지역:"+planets[k]
                                            +"\n인덱스값:"+planets2[k]);

                    }
                }
            }
        } catch(Exception e){
            Log.d("xml e catch", e.getMessage());
        }
        return stringBuffer.toString();
    }

    private void addItem(String mainText , String sidoCnt){
        SidoCardModel item = new SidoCardModel();
        item.setSidoName(mainText);
        item.setSidoCnt(sidoCnt);
        list.add(item);
    }

    private String currentDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd" , Locale.KOREAN);
        return mFormat.format(date);

        /*TODO : 리턴값은 현재날짜 yyyyMMdd 값을 반환함. 중요한건 24시가 지나면 다음날을 반환하는데 현재 API는 오전10시쯤 업데이트가 된다는것.
           그래서 보면 새벽12시부터 ~ 대략 아침10시까지 node.getTextContent() = null 이다. 이부분을 제어해주는 역할이 필요함. */
    }
    private String yesterdayForDialog(){
        int yesterday = (Integer.parseInt(currentDate()) - 1);
        return String.valueOf(yesterday);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void forMaxandMin(String[] arr){

        String[] strings = {"1", "2", "3","4","5"};

        int[] nums = Arrays.stream(arr).mapToInt(Integer::parseInt).toArray();

        int max = nums[0];
        int min = nums[0];

        Arrays.sort(nums);

        System.out.println("최소값은 : " +nums[0]);
        System.out.println("최대값은 : " +nums[nums.length - 1]);
        maxData = nums[nums.length - 1];
        minData = nums[0];
    }

    private String numberFormat(String str){
        return NumberFormat.getInstance(Locale.KOREA).format(Integer.parseInt(str));
    }

}