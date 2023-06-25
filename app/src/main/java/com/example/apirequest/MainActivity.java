package com.example.apirequest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private  Button startButton;
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestInternetPermission();
        startButton = findViewById(R.id.start_button);
        textView = findViewById(R.id.text_view);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callAPI(5.0);
                String localhostUrl = "https://major-project-rmvrvm-study-server.vercel.app/calculate/5";
                NetworkTask networkTask = new NetworkTask(result -> {
                    if (result != null) {
                        textView.setText(result); // Update the TextView with the result
                    } else {
                        // Handle the error
                        Log.d("network", "onClick: result is null" );
                    }
                });
                networkTask.execute(localhostUrl);
            }
        });

    }
    private void requestInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            showToast("Permission Granted");
        } else {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                showToast("Permission Granted");
            } else {
                // Permission denied
                showToast("Permission Denied");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void callAPI(double number){
        String serverUrl = "http://10.200.16.126:8000/calculate/"+ number;
        // Make sure to perform the network operation on a background thread (e.g., using AsyncTask or a separate thread)
        String serverResponse;
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            // Handle the response based on the response code

            // Read the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Close the connections and readers
            reader.close();
            connection.disconnect();

            serverResponse = response.toString();
            textView.setText("Response: " + serverResponse);
            // Process the server response as needed
        } catch (IOException e) {
            textView.setText("Error: " + e);
            // Handle any exceptions that occurred during the request
        }


    }

}