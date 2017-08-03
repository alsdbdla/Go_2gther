package app.gotogether.com.calendertest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class BossPage extends AppCompatActivity {

    EditText et;
    ArrayAdapter addressAdapter;
    GridView monthView;
    MonthAdapter monthViewAdapter;
    TextView monthText;
    public int curYear;
    public int curMonth;
    public int curDay;
    String content;
    EditText edit;
    private int startYear, startMonth, startDay;
    public MonthItem curItem;

    ArrayAdapter<String> adapter;
    ArrayList<String> as;
    //ArrayList<DayData> dayData;
    ListView lv;

    // 서버코드
    private static String TAG = "schedule_test";
    private TextView mTextViewResult; //insert 성공시 보여주는것 - 앱에서 불필요함

    // 서버코드
    private static String TAG2 = "schedule_json";

    private static final String TAG_JSON="schedule_json";
    private static final String TAG_ID = "id";
    private static final String TAG_GroupID = "groupId";
    private static final String TAG_Date = "date";
    private static final String TAG_Plan="plan";

    //ArrayList<HashMap<String, String>> mArrayList;
    //ListView mlistView;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boss_page);


        // 월별 캘린더 뷰 객체 참조
        // 어댑터 생성
        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new MonthAdapter(this); // 어댑터
        monthView.setAdapter(monthViewAdapter);


        // 달력 누르면 아래에 일정 뜨게

        //dayData = new ArrayList<DayData>();

        lv = (ListView)findViewById(R.id.listView);



        // 리스너 설정
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // 서버 - 정보 갖고오기

                // 현재 선택한 일자 정보 표시
                curItem = (MonthItem) monthViewAdapter.getItem(position);
                //curDay = curItem.getDay();

                //String day = String.valueOf(curDay);

                //Toast.makeText(BossPage.this, day, Toast.LENGTH_SHORT).show();

                GetData task = new GetData();
                task.execute("http://211.253.9.84/getschedule.php");

            }
        });

        /*
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                // 이미 참석했을 경우, 아직 참석하지 않은 경우로 나누어야함 - 일일이 비교
                // 참석한 경우는 롱클릭시 참석 취소
                // 각각의 스케쥴 마다

                AlertDialog.Builder builder = new AlertDialog.Builder(BossPage.this);
                builder.setTitle("참석여부");
                builder.setMessage("참석하시겠습니까?");


                me = "최예슬"; // 현재 접속자로 가정

                part = new ArrayList<participant>(); // 예비 리스트

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 현재 접속하고 있는 사람의 이름을 리스트에 넣어준다

                        part.add(new participant(2017, 7, 25, me)); // 참석자 목록에 나를 넣는다

                        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                        View customView = inflater.inflate(R.layout.activity_information, null);

                        // 다이얼로그에 리스트뷰 나타내기
                        information = (ListView)customView.findViewById(R.id.listView2);
                        adapter2 = new ArrayAdapter(BossPage.this, R.layout.activity_information, part);
                        information.setAdapter(adapter2);


                    }});
                builder.setNegativeButton("아니요", null);


                builder.create().show();


                return true;
            }

        });

        */

        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        Button monthPrevious = (Button) findViewById(R.id.monthPrevious);
        monthPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setPreviousMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });

        // 다음 월로 넘어가는 이벤트 처리
        Button monthNext = (Button) findViewById(R.id.monthNext);
        monthNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setNextMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });


    }

    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    public void onClick(View v){
        switch(v.getId()){
            // 일정 추가 코드
            case R.id.plus :

                // Inflate your custom layout containing 2 DatePickers
                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View customView = inflater.inflate(R.layout.activity_datetimepicker, null);

                // Define your date pickers
                final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(customView); // Set the view of the dialog to your custom layout
                builder.setTitle("일정추가");

                View view = (View) getLayoutInflater().inflate(R.layout.activity_datetimepicker, null);
                edit = (EditText)view.findViewById(R.id.editText2);

                // 서버
                mTextViewResult = (TextView)findViewById(R.id.result_text); // 성공하면 insert

                builder.setPositiveButton("추가", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startYear = dpStartDate.getYear();
                        startMonth = dpStartDate.getMonth() + 1;
                        startDay = dpStartDate.getDayOfMonth();
                        content = edit.getText().toString(); // 일정내용
                        String y = String.valueOf(startYear);
                        String m = String.valueOf(startMonth);
                        String d = String.valueOf(startDay);

                        //dayData = new ArrayList<DayData>();
                        // 우선은 일정 하나만 추가 가능하도록
                        // editText 수정
                        // 일정을 리스트뷰에 추가
                        //dayData.add(new DayData(startYear, startMonth, startDay, content));


                        String date = y + m + d;

                        InsertData task = new InsertData();
                        task.execute(date, "하");

                        // 후에 추가하면 동그라미 버튼 나타나게 or 색이 바뀌게
                        dialog.dismiss();
                    }});

                builder.setNegativeButton("취소", null);
                builder.create().show();

                break;

            case R.id.btnTitle :

                AlertDialog.Builder ad = new AlertDialog.Builder(BossPage.this);

                ad.setTitle("이름변경");       // 제목 설정
                ad.setMessage("변경할 이름을 입력해주세요");   // 내용 설정

                et = new EditText(this);
                ad.setView(et);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TextView text = (TextView) findViewById(R.id.title);
                        String title = et.getText().toString();

                        text.setText(title);

                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }


                });

                ad.show();

                break;

            case R.id.member :

                AlertDialog.Builder member = new AlertDialog.Builder(BossPage.this);

                member.setTitle("팀원관리");       // 제목 설정
                member.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }

                });
                member.setAdapter(addressAdapter, null);

                member.show();

                break;

        }
    }


    // 리스트뷰 업데이트
    public void updateLv(){
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,as);
        lv.setAdapter(adapter);
    }


    // 서버코드1

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String date = (String)params[0];
            String plan = (String)params[1];
            int groupId = 123;

            String serverURL = "http://211.253.9.84/schedule.php";
            String postParameters = "groupId=" + groupId + "&date=" + date + "&plan=" + plan;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    // 서버코드2

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG2, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG2, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG2, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString(TAG_ID);
                String groupId = item.getString(TAG_GroupID);
                String date = item.getString(TAG_Date);
                String plan = item.getString(TAG_Plan);

                as = new ArrayList<String>(); // 170231    2017  7  5
                String day = String.valueOf(curItem.getDay()); // 현재 날짜와 같으면
                String year = String.valueOf(monthViewAdapter.getCurYear());
                String month = String.valueOf(monthViewAdapter.getCurMonth() + 1);
                String d = year + month + day ;

                //Toast.makeText(BossPage.this, date, Toast.LENGTH_SHORT).show();
                Toast.makeText(BossPage.this, d, Toast.LENGTH_SHORT).show();

                    if(date.equals(d)){
                        as.add(plan); // 스케쥴 가져오기
                }

                updateLv();

            }

        } catch (JSONException e) {

            Log.d(TAG2, "showResult : ", e);
        }

    }

}









