package com.example.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CreateAccountActivity extends AppCompatActivity {
    Button submitButton;
    EditText name;
    EditText nameLast;
    EditText userName;
    EditText password;
    EditText phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);
        submitButton = findViewById(R.id.button);
        name = findViewById(R.id.Name);
        userName = findViewById(R.id.UserName);
        password = findViewById(R.id.Password);
        nameLast = findViewById(R.id.NameLast);
        phone = findViewById(R.id.Phone);
        submitButton.setOnClickListener(v->sendRequest());


    }

    void sendRequest(){
        String nameString = name.getText().toString();
        String userNameString = userName.getText().toString();
        String passwordString = password.getText().toString();
        String nameLastString = nameLast.getText().toString();
        String phoneString = phone.getText().toString();
        submitButton.setEnabled(false);

        class UploadCreateAccountClass  extends AsyncTask<Void, Void, String> {
            Context context;
            public UploadCreateAccountClass(Context context){
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
                if (result == "success"){
                    Toast.makeText(getApplicationContext(),"Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("phoneNumber", phoneString);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(),"Nope!", Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true);
                }
            }

            @Override
            protected String doInBackground(Void... params){
                DataProcessClass dataProcessClass = new DataProcessClass();
                HashMap<String, String> hashMapParams = new HashMap<String, String>();
                hashMapParams.put("name",nameString);
                hashMapParams.put("userName",userNameString);
                hashMapParams.put("password",passwordString);
                hashMapParams.put("nameLast", nameLastString);
                hashMapParams.put("phone", phoneString);
                String response = dataProcessClass.sendHTTPRequest("https://messagingapp1.000webhostapp.com/createAccount.php", hashMapParams);
                return response;
            }

        }
        UploadCreateAccountClass uploadCreateAccountClass = new UploadCreateAccountClass(this);
        uploadCreateAccountClass.execute();
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
