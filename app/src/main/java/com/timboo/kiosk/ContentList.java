package com.timboo.kiosk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.io.File;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import com.timboo.kiosk.adapters.ContentAdapter;
import com.timboo.kiosk.models.CartoonItem;

public class ContentList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        checkPermissions();


    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            } else {
                loadCartoons();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                loadCartoons();
            }
        }
    }

    // SDCARD_PATH should be defined as the root folder for TimbooCartoons on the SD card
    // For example: private static final String SDCARD_PATH = "/storage/XXXX-XXXX/TimbooCartoons";
    private static final String SDCARD_PATH = "/storage/68A5-E201/TimbooCartoons";
    private List<CartoonItem> cartoonList;

    private void loadCartoons() {
        if (cartoonList == null) {
            cartoonList = new ArrayList<>();
        } else {
            cartoonList.clear();
        }

        File sdCard = new File(SDCARD_PATH);
        if (!sdCard.exists() || !sdCard.isDirectory()) {
            Toast.makeText(this, "SD kart bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] cartoonFolders = sdCard.listFiles();
        if (cartoonFolders == null) {
            Toast.makeText(this, "SD kartta içerik bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        // LinearLayout (contentContainer) içinde içerikleri göster
        LinearLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            contentContainer.removeAllViews();
        }

        for (File folder : cartoonFolders) {
            if (folder.isDirectory()) {
                String titleStr = folder.getName();
                String description = "Açıklama burada olabilir";
                File thumbnail = new File(folder, "thumbnail.jpg");
                String thumbnailPath = thumbnail.exists() ? thumbnail.getAbsolutePath() : null;

                CartoonItem item = new CartoonItem(titleStr, description, thumbnailPath);
                cartoonList.add(item);

                if (contentContainer != null) {
                    View itemView = LayoutInflater.from(this).inflate(R.layout.content_item, null);

                    ImageView thumbnailView = itemView.findViewById(R.id.cartoon_image);
                    TextView titleView = itemView.findViewById(R.id.cartoon_title);
                    TextView descriptionView = itemView.findViewById(R.id.cartoon_description);

                    if (thumbnailPath != null) {
                        thumbnailView.setImageURI(Uri.fromFile(new File(thumbnailPath)));
                    }
                    titleView.setText(titleStr);
                    descriptionView.setText(description);

                    contentContainer.addView(itemView);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCartoons();
            } else {
                Toast.makeText(this, "İzin verilmediği için içerikler gösterilemiyor.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadCartoons();
                } else {
                    Toast.makeText(this, "İzin verilmediği için içerikler gösterilemiyor.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}