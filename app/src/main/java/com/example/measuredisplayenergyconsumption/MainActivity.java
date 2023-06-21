package com.example.measuredisplayenergyconsumption;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PowerManager powerManager;
    private WakeLock wakeLock;
    private TextView batteryPercentageTextView;
    private TextView timeTextView;
    private Button startButton;

    private Handler handler;
    private Runnable batteryRunnable;
    private int startBatteryPercentage;
    private int  lastBatteryPercentage;
    private int consumedPercentage;

    private Button stopButton;
    private long startTime;

    private int finalEplasedSeconds;
    private TextView startBatteryPercentageTextView;
    private TextView differenceTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Display:BatteryMonitoringWakeLock");
        batteryPercentageTextView = findViewById(R.id.battery_percentage_textview);
        timeTextView = findViewById(R.id.time_textview);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        differenceTextView = findViewById(R.id.difference_textview);
        startBatteryPercentageTextView = findViewById(R.id.start_battery_percentage_textview);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBatteryMonitoring();
                startButton.setEnabled(false);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopBatteryMonitoring();
            }
        });

        handler = new Handler();
        batteryRunnable = new Runnable() {
            @Override
            public void run() {
                checkBatteryPercentage();
            }
        };
    }

    private void startBatteryMonitoring() {
        startButton.setEnabled(false);
        wakeLock.acquire();
        startBatteryPercentage = getBatteryPercentage();

        startBatteryPercentageTextView.setText("Start Battery Percentage: " + startBatteryPercentage);
        consumedPercentage = 0;
        startTime = System.currentTimeMillis();
        handler.postDelayed(batteryRunnable, 15000); // Initial delay of 15 seconds
    }

    private void checkBatteryPercentage() {
        int currentBatteryPercentage = getBatteryPercentage();
        Log.d("CHECK", "checkBatteryPercentage ----- " + currentBatteryPercentage);

        batteryPercentageTextView.setText(getString(R.string.battery_percentage, currentBatteryPercentage));
        long elapsedTime = System.currentTimeMillis() - startTime;
        int elapsedSeconds = (int) (elapsedTime / 1000);
        timeTextView.setText(getString(R.string.consumed_time, elapsedSeconds));
        int difference = currentBatteryPercentage - startBatteryPercentage;
        differenceTextView.setText("Difference in percentage: " + difference);
        Log.d("CHECK", "Difference ----- " + difference);

        if (difference >= 5) {
//            lastBatteryPercentage = currentBatteryPercentage;
            finalEplasedSeconds = elapsedSeconds;
            stopBatteryMonitoring();
        }
//        else {
//            consumedPercentage += difference;
//        }

        handler.postDelayed(batteryRunnable, 15000); // Schedule the next execution after 15 seconds
    }


    private void stopBatteryMonitoring() {
        handler.removeCallbacks(batteryRunnable);
        startButton.setEnabled(true);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        batteryPercentageTextView.setText("Starting Percentage: " + startBatteryPercentage + "\nEnding Battery Percentage: " + lastBatteryPercentage);
        timeTextView.setText( "Time Taken: " + finalEplasedSeconds);
    }

    private int getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (int) ((level / (float) scale) * 100);
    }
}
