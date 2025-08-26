package com.timboo.kiosk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView batteryText, loadingText;
    private WebView loaderView;
    private int tapCount = 0;
    private Handler handler = new Handler();
    private static final String PASSWORD = "1234";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasStoragePermission()) {
            requestStoragePermission();
        }

        batteryText = findViewById(R.id.battery_text);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContentList.class);
            startActivity(intent);
        });

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

    }

    private void showAdminDialog() {
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
            }
        });

        builder.setNegativeButton("İptal", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setOnDismissListener(dialog -> {});
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
            dpm.setLockTaskPackages(adminComponent, new String[]{getPackageName()});
        }
    }

    private void startKioskMode() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null && dpm.isLockTaskPermitted(getPackageName())) {
            startLockTask();
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "İzin verildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "İzin reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}