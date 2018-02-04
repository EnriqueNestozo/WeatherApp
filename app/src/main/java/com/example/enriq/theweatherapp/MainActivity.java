package com.example.enriq.theweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button checkWeather;
    EditText cityText;
    TextView description;
    String appid = "34b4a2afa3f980604fe5731cfd4b862c";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityText = findViewById(R.id.CityEditText);
        checkWeather = findViewById(R.id.CheckWeatherButton);
        description = findViewById(R.id.InfoTextView);

    }

    public void check(View view){
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityText.getWindowToken(),0);
        DownLoadWeatherInformation task = new DownLoadWeatherInformation();
        try {
            String encodedcity = URLEncoder.encode(cityText.getText().toString(),"UTF-8");
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedcity + "&appid=" + appid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class DownLoadWeatherInformation extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int dato = reader.read();
                String consulta = "";
                while(dato!=-1){
                    char current = (char)dato;
                    consulta += current;
                    dato = reader.read();
                }
                return consulta;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s != null){
                    JSONObject jsonObject = new JSONObject(s);
                    String weather = jsonObject.getString("weather");
                    JSONArray jsonArray = new JSONArray(weather);
                    ArrayList<String> info = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonpart = jsonArray.getJSONObject(i);
                        String main=jsonpart.getString("main");
                        String description = jsonpart.getString("description");
                        info.add(main + ": " + description);
                    }
                    String texto = "";
                    for (int i=0; i<info.size();i++){
                        texto += info.get(i) + "\n";
                    }
                    description.setText(texto);
                }else{
                    Toast.makeText(getApplicationContext(),"Insert a valid city",Toast.LENGTH_SHORT).show();
                    description.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
