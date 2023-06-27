package com.example.measureenergyconsumptionmultithreaded;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class MainActivity extends AppCompatActivity {
    private  int finalI;
    private static final String TAG = "MainActivity";
    private static final int BATTERY_SAMPLE_INTERVAL_MS = 1000 * 60 * 60; // 1 hour
    private TextView mBatteryStatusTextView;
    private TextView mBatteryInfoTextView;
    private ProgressBar progressBar;
    private int isThreadRunning = 0;
    String countsString;
    String batteryStatus;
    String batteryConsumption, batteryInfo,runDuration;
    private  TextView mRunDuration;
    private TextView mcount_of_iterations;
    private TextView mBatteryConsumptionTextView;
    private int mStartBatteryLevel;
    private int mEndBatteryLevel;
    private long mStartTime;
    private long mEndTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcount_of_iterations = findViewById(R.id.count_of_iterations);
        mRunDuration = findViewById(R.id.run_duration_text_view);
        mBatteryStatusTextView = findViewById(R.id.battery_status_text_view);
        mBatteryInfoTextView = findViewById(R.id.battery_info_text_view);
        mBatteryConsumptionTextView = findViewById(R.id.battery_consumption_text_view);
        progressBar = findViewById(R.id.progress_bar);

        // Register battery level receiver
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryLevelReceiver, batteryLevelFilter);

    }
    private Runnable mBatterySampleRunnable = new Runnable() {
        @Override
        public void run() {
            loadFunction();
        }

    };
    Thread thread = new Thread(mBatterySampleRunnable);
    private void loadFunction() {
        mStartBatteryLevel = getBatteryLevel();
        mStartTime = System.currentTimeMillis();

        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
        });

        Log.d(TAG, "loadFunction: started");

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        final double[] x = {1.0};
        ExecutorService executor = Executors.newCachedThreadPool();

        int chunkSize = 1000;
        long startTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = 0;
        int i = 0;


        while (elapsedTimeMillis < 5* 60 * 1000) {

            final int start = i;
            final int end = i + chunkSize;

            activityManager.getMemoryInfo(memoryInfo);

            long availableMemory = memoryInfo.availMem;
            long totalMemory = memoryInfo.totalMem;

            double percentAvailable = (double) availableMemory / (double) totalMemory * 100.0;

            Log.d(TAG, "Percentage memory: " + percentAvailable + "\n");

            if (percentAvailable < 2){
                finalI = i;
                break;
            }

            // Submit a new task to the thread pool
            executor.submit(() -> {
                for (int j = start; j < end; j++) {
                    x[0] = Math.tan(Math.atan(x[0]));
                }
                Log.d(TAG, "Completed Task [" + start + "-" + (end - 1) + "]");
            });

            i += chunkSize;
            elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        }

        executor.shutdown();

        finalI = i;

        Log.d(TAG, "Result: " + x[0]);
    }

    private void updateUIInformation(int i){
        Log.d(TAG, "updateUIInformation: updating started");

        // Show count_of_iterations to the screen
        countsString = "Load function ran for : " + i +" number of times\n\n";

        // Update end battery level and end time
        mEndBatteryLevel = getBatteryLevel();
        mEndTime = System.currentTimeMillis();

        // Update battery status text view
        batteryStatus = "Battery status: " + getBatteryStatus() + "\t(" + mEndBatteryLevel + "%)\n\n";

        // Calculate battery consumption per hour
        long timeDifferenceMs = mEndTime - mStartTime;
        int batteryDifference = mStartBatteryLevel - mEndBatteryLevel;
        double batteryConsumptionPerHour = (double) batteryDifference / (timeDifferenceMs / (1000 * 60 * 60.0));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        batteryConsumption = "Battery Difference: "+ batteryDifference + "% \nTime Duration: " +timeDifferenceMs + "ms \nBattery consumption per hour: " + decimalFormat.format(batteryConsumptionPerHour) + "%\n\n";


        // Update battery info text view
        batteryInfo = "current Battery Level: "+mEndBatteryLevel + "%\n" + "Battery information: " + getBatteryInfo() + "\n\n";

        // update run duration text view
        runDuration = "Run Duration: " + timeDifferenceMs / (1000.0) + " seconds\n\n";

        Log.d(TAG, "run in update in UI via main Thread");

        mcount_of_iterations.setText(countsString);
        mBatteryStatusTextView.setText(batteryStatus);
        mRunDuration.setText(runDuration);
        mBatteryInfoTextView.setText(batteryInfo);
        mBatteryConsumptionTextView.setText(batteryConsumption);
        progressBar.setVisibility(View.GONE);
    }

    private BroadcastReceiver mBatteryLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private int getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level != -1 && scale != -1) {
                return (int) ((level / (float) scale) * 100);
            }
        }
        return -1;
    }

    private String getBatteryStatus() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not charging";
            default:
                return "Unknown";
        }
    }

    private String getBatteryInfo() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int health = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) : -1;
        String technology = batteryIntent != null ? batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) : "Unknown";
        int temperature = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) : -1;
        int voltage = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) : -1;

        String batteryInfo = "";
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                batteryInfo += "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                batteryInfo += "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                batteryInfo += "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                batteryInfo += "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                batteryInfo += "Over voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                batteryInfo += "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                batteryInfo += "Unspecified failure";
                break;
        }
        batteryInfo += " (" + technology + ")";
        if (temperature != -1) {
            batteryInfo += ", Temperature: " + temperature / 10.0 + "°C";
        }
        if (voltage != -1) {
            batteryInfo += ", Voltage: " + voltage / 1000.0 + "V";
        }
        return batteryInfo;
    }

    public void onStartButtonClick(View v)
    {
        Log.d(TAG, "run: progressbar");
        if (isThreadRunning == 0){
            Log.d(TAG, "onStartButtonClick: MAIN THREAD START");
            thread.start();
            isThreadRunning = 1;
        }
        try{
            thread.join();
            Log.d(TAG, "onStartButtonClick: MAIN THREAD STOPPED");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateUIInformation(finalI);
    }
}





