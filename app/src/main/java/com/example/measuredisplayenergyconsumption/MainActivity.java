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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean flag = false;
    private PowerManager powerManager;
    private WakeLock wakeLock;
    private TextView batteryPercentageTextView;
    private TextView timeTextView;
    private Button startButton;

    private Handler handler;
    private Runnable batteryRunnable;
    private int startBatteryPercentage;
    private int  initialBatteryPercentage = -1;
    private int consumedPercentage;
    private long startTime;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Display:BatteryMonitoringWakeLock");
        progressBar = findViewById(R.id.progressBar);
        batteryPercentageTextView = findViewById(R.id.battery_percentage_textview);
        timeTextView = findViewById(R.id.time_textview);
        startButton = findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBatteryMonitoring();
                startButton.setEnabled(false);
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
        progressBar.setVisibility(View.VISIBLE);
        wakeLock.acquire();
        startBatteryPercentage = getBatteryPercentage();
        consumedPercentage = 0;
        startTime = System.currentTimeMillis();
        handler.postDelayed(batteryRunnable, 15000); // Initial delay of 15 seconds
    }

    private void checkBatteryPercentage() {
        int currentBatteryPercentage = getBatteryPercentage();
        Log.d("CHECK", "checkBatteryPercentage ----- " + currentBatteryPercentage);

        batteryPercentageTextView.setText(getString(R.string.battery_percentage, currentBatteryPercentage));

        int difference = currentBatteryPercentage - startBatteryPercentage - consumedPercentage;
        if (difference >= 5) {
            stopBatteryMonitoring();
            long elapsedTime = System.currentTimeMillis() - startTime;
            int elapsedSeconds = (int) (elapsedTime / 1000);
            timeTextView.setText(getString(R.string.consumed_time, elapsedSeconds));
        } else {
            consumedPercentage += difference;
        }

        handler.postDelayed(batteryRunnable, 15000); // Schedule the next execution after 15 seconds
    }


    private void stopBatteryMonitoring() {
        handler.removeCallbacks(batteryRunnable);
        progressBar.setVisibility(View.GONE);
        startButton.setEnabled(true);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private int getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (int) ((level / (float) scale) * 100);
    }
}
