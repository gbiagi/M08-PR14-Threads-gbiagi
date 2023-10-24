package com.example.m08_pr14_threads_gbiagi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        TextView textoVerso = findViewById(R.id.textoVerso);
        ImageView imagen = findViewById(R.id.imagen1);
        Button buttonVerso = findViewById(R.id.buttonVerso);
        buttonVerso.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Tasques en background (xarxa)
                    String dataWeb = getDataFromUrl("https://bible-api.com/?random=verse");
                    Log.i("INFO", dataWeb);
                    //textoVerso.setText(dataWeb);
                    JSONObject jsonData = null;
                    try {
                        jsonData = new JSONObject(dataWeb);

                        String reference = jsonData.optString("reference", "");
                        String text = jsonData.optString("text", "");

                        // Now you have the reference and text, you can use them in your UI.
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView textoVerso = findViewById(R.id.textoVerso);
                    String reference = jsonData.optString("reference", "");
                    String text = jsonData.optString("text", "");
                    String verseText = reference + "\n" + text;

                    String urldisplay = "https://lorempokemon.fakerapi.it/pokemon";
                    Bitmap bitmap = null;
                    try {
                        InputStream in = new java.net.URL(urldisplay).openStream();
                        bitmap = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    Bitmap finalBitmap = bitmap;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Tasques a la interfície gràfica (GUI)
                            textoVerso.setText(verseText);
                            imagen.setImageBitmap(finalBitmap);
                        }
                    });
                }
            });
        }
    });
    }
    String error = ""; // string field
    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}