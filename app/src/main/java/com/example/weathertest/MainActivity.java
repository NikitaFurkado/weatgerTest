package com.example.weathertest;

import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView descriptionTextView;

    private static final String API_KEY = "baa1fcaa04bfeda6f21baed67fbac2e5";
    private static final String CITY_NAME = "Moscow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new FetchWeatherTask());
    }

    private class FetchWeatherTask implements Runnable {


        @Override
        public void run() {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY_NAME + "&appid=" + API_KEY;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    handleWeatherResponse(response.toString());
                } else {
                    Log.e("WeatherApp", "Error: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleWeatherResponse(String response) {
            if (response != null) {
                try {
                    JSONObject weatherData = new JSONObject(response);
                    double temperature = weatherData.getJSONObject("main").getDouble("temp");
                    String weatherDescription = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");

                    runOnUiThread(() -> {
                        temperatureTextView.setText("Temperature: " + temperature + " Kelvin");
                        descriptionTextView.setText("Weather Description: " + weatherDescription);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}