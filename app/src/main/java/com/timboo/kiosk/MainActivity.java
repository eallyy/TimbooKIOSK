package com.timboo.kiosk;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.http.SslError;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView batteryText, loadingText;
    private WebView loaderView;
    private int tapCount = 0;
    private Handler handler = new Handler();
    private static final String PASSWORD = "1234";
    private static final String TIMBOO_PACKAGE = "com.example.timboo";
    private static final String TIMBOO_ACTIVITY = "com.example.timboo.MainActivity";
    private boolean allowLaunch = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryText = findViewById(R.id.battery_text);
        loadingText = findViewById(R.id.loading_text);

        Handler dotHandler = new Handler();
        Runnable dotRunnable = new Runnable() {
            int dotCount = 0;
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4;
                String dots = new String(new char[dotCount]).replace("\0", ".");
                loadingText.setText("Timboo Cafe Yükleniyor" + dots);
                dotHandler.postDelayed(this, 300);
            }
        };
        dotHandler.post(dotRunnable);

        setupKioskPermissions();
        startKioskMode();
        updateBatteryLevel();

        batteryText.setOnClickListener(v -> {
            tapCount++;
            handler.removeCallbacksAndMessages(null);
            if (tapCount >= 3) {
                tapCount = 0;
                showAdminDialog();
            } else {
                handler.postDelayed(() -> tapCount = 0, 2500);
            }
        });

        // Launch Timboo Cafe in 3 seconds
        handler.postDelayed(this::launchTimbooCafe, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allowLaunch) {
            handler.postDelayed(this::launchTimbooCafe, 3000);
        }
    }

    private void launchTimbooCafe() {
        try {
            Intent intent = new Intent();
            intent.setClassName(TIMBOO_PACKAGE, TIMBOO_ACTIVITY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startLockTask();
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Timboo Cafe başlatılamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAdminDialog() {
        allowLaunch = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yetkili Girişi");

        final EditText input = new EditText(this);
        input.setHint("Şifre");
        builder.setView(input);

        builder.setPositiveButton("Uygulamadan Çık", (dialog, which) -> {
            if (input.getText().toString().equals(PASSWORD)) {
                stopLockTask();
                finishAffinity(); // Exit app
            } else {
                Toast.makeText(this, "Şifre hatalı!", Toast.LENGTH_SHORT).show();
                allowLaunch = true;
            }
        });

        builder.setNegativeButton("İptal", (dialog, which) -> {
            allowLaunch = true;
            dialog.dismiss();
        });

        builder.setOnDismissListener(dialog -> allowLaunch = true);
        builder.show();
    }

    private void updateBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);
            batteryText.setText(batteryPct + "%");
        }
    }

    private void setupKioskPermissions() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (dpm != null && dpm.isDeviceOwnerApp(getPackageName())) {
            dpm.setLockTaskPackages(adminComponent, new String[]{getPackageName(), TIMBOO_PACKAGE});
        }
    }

    private void startKioskMode() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null && dpm.isLockTaskPermitted(getPackageName())) {
            startLockTask();
        }
    }
}