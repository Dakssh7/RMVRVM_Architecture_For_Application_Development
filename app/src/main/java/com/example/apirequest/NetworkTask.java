package com.example.apirequest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Void, String> {

    private NetworkCallback callback;

    public NetworkTask(NetworkCallback callback) {
        this.callback = callback;
    }
    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String result = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.d("network", "doInBackground: HTTP OK");
                InputStream inputStream = urlConnection.getInputStream();
                result = readInputStream(inputStream);
            } else {
                // Handle HTTP error response
                result = "HTTP error status: " + statusCode;
                Log.d("network", "doInBackground: error http status code" + statusCode);
            }
        } catch (IOException e) {
            // Handle IO exception
            result = "Error making HTTP request: " + e.getMessage();
            Log.e("network", "error making http request: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        // Process the response in the UI thread
        if (result != null && callback != null) {
            // Response received successfully
            // Process the result
            callback.onResultReceived(result); // Pass the result to the callback interface

        } else {
            // Error occurred during the network request
            // Handle the error
        }
    }
    public interface NetworkCallback {
        void onResultReceived(String result);
    }

}
