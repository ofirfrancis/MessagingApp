package com.example.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ShowMessagesActivity extends AppCompatActivity implements RecyclerViewAdapterShowMessages.ItemClickListener {


    EditText message;
    Button sendButton;
    String userPhone;
    String[] user2Details;
    String[][] data;
    public RecyclerViewAdapterShowMessages recyclerViewAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_messages);
        message = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v->sendMessage());
        Bundle extras = getIntent().getExtras();
        userPhone = extras.getString("userPhone");
        user2Details = extras.getStringArray("user2Details");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        retrieveMessages();
    }

    public void setUpAdapter(){
        recyclerViewAdapter = new RecyclerViewAdapterShowMessages(this, data);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    public void onItemClick(View view, int position) {
        Log.i("clicked", "you clicked on " + Arrays.toString(recyclerViewAdapter.getItem(position)) + "at cell position " + position);
    }
    void sendMessage(){
        String messageString = message.getText().toString();
        sendButton.setEnabled(false);

        class SendMessageClass  extends AsyncTask<Void, Void, String> {
            Context context;
            public SendMessageClass(Context context){
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
//                GO TO CHAT
                if (result.equals("1")){
                    Toast.makeText(getApplicationContext(),"message sent", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(),"error sending message", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true);
                }

            }

            @Override
            protected String doInBackground(Void... params){
                ShowMessagesActivity.DataProcessClass dataProcessClass = new ShowMessagesActivity.DataProcessClass();
                HashMap<String, String> hashMapParams = new HashMap<String, String>();
                hashMapParams.put("message", messageString);
                hashMapParams.put("sender", userPhone);
                hashMapParams.put("chatID",user2Details[3]);
                String response = dataProcessClass.sendHTTPRequest("https://messagingapp1.000webhostapp.com/sendMessage.php", hashMapParams);
                return response;
            }

        }
        SendMessageClass sendMessageClass = new SendMessageClass(this);
        sendMessageClass.execute();
    }


    void retrieveMessages(){

        class RetrieveMessageClass  extends AsyncTask<Void, Void, String> {
            Context context;
            public RetrieveMessageClass(Context context){
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
                if (result.length() >= 5 && result.substring(0,5).equals("works")){
                    Toast.makeText(getApplicationContext(),"messages received", Toast.LENGTH_SHORT).show();
                    String[] temp = result.split("/");
                    String[] byMessage = Arrays.copyOfRange(temp,1,temp.length);
                    data = new String[byMessage.length][3];
                    for (int i = 0; i < byMessage.length; i++){
                        data[i] = byMessage[i].split(",");
                    }
                    setUpAdapter();

                }else{
                    Toast.makeText(getApplicationContext(),"error receiving message", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Void... params){
                ShowMessagesActivity.DataProcessClass dataProcessClass = new ShowMessagesActivity.DataProcessClass();
                HashMap<String, String> hashMapParams = new HashMap<String, String>();
                hashMapParams.put("id",user2Details[3]);
                String response = dataProcessClass.sendHTTPRequest("https://messagingapp1.000webhostapp.com/retrieveMessages.php", hashMapParams);
                return response;
            }

        }
        RetrieveMessageClass retrieveMessageClass = new RetrieveMessageClass(this);
        retrieveMessageClass.execute();
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
