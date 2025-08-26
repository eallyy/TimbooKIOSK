package com.timboo.kiosk;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        String uriString = getIntent().getStringExtra("videoUri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            VideoView videoView = findViewById(R.id.videoView);
            videoView.setVideoURI(uri);
            videoView.start();
        } else {
            Toast.makeText(this, "Video yolu alınamadı", Toast.LENGTH_LONG).show();
            finish(); // veya hata yönetimi
        }
    }
}