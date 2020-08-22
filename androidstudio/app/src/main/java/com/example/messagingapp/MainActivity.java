package com.example.messagingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    public static String phoneString;
    String[][] data;
    public RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        phoneString = extras.getString("phoneNumber");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        getChats();
    }


    public void setUpAdapter(){
        recyclerViewAdapter = new RecyclerViewAdapter(this, data);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public void onItemClick(View view, int position){
        Log.i("clicked", "you clicked on "+ Arrays.toString(recyclerViewAdapter.getItem(position)) + "at cell position " + position);
        Intent intent = new Intent(getApplicationContext(), ShowMessagesActivity.class);
        intent.putExtra("userPhone", phoneString);
        intent.putExtra("user2Details", recyclerViewAdapter.getItem(position));
        startActivity(intent);
    }

    void getChats(){

        class GetChatsClass  extends AsyncTask<Void, Void, String> {
            Context context;
            public GetChatsClass(Context context){
                this.context = context;
            }

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                Log.d("result", result);
                String[] byAccounts = result.split("/");
                data = new String[byAccounts.length][4];
                for (int i = 0; i < byAccounts.length; i++){
                    data[i] = byAccounts[i].split(",");
                }
                setUpAdapter();
//                Log.d("data", Arrays.deepToString(data));
            }

            @Override
            protected String doInBackground(Void... params){
                MainActivity.DataProcessClass dataProcessClass = new MainActivity.DataProcessClass();
                HashMap<String, String> hashMapParams = new HashMap<String, String>();
                hashMapParams.put("phone", phoneString);
                Log.d("phone", phoneString);
                String response = dataProcessClass.sendHTTPRequest("https://messagingapp1.000webhostapp.com/getChats.php", hashMapParams);
                Log.d("response",response);
                return response;
            }

        }
        GetChatsClass getChatsClass = new GetChatsClass(this);
        getChatsClass.execute();
    }


    public class DataProcessClass {
        public String sendHTTPRequest(String requestURL, HashMap<String,String> paramData){
            StringBuilder stringBuilder = new StringBuilder();
            try{
                URL url;
                HttpURLConnection httpURLConnection;
                OutputStream outputStream;
                BufferedWriter bufferedWriter;
                BufferedReader bufferedReader;
                int RC;

                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(buffferedWriterDataFN(paramData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                RC = httpURLConnection.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK){
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null){
                        stringBuilder.append(RC2);
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String buffferedWriterDataFN(HashMap<String ,String> hashMapParams) throws UnsupportedEncodingException {
            Boolean check = true;
            StringBuilder stringBuilder = new StringBuilder();
            for(Map.Entry<String ,String > key:hashMapParams.entrySet()){
                if(check){
                    check = false;
                }else{
                    stringBuilder.append("&");
                }
                stringBuilder.append(URLEncoder.encode(key.getKey(),"UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(key.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }
    }




}
