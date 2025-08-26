package com.timboo.kiosk;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class CartoonDetailActivity extends AppCompatActivity {

    private String title, description, thumbnailPath, folderPath;
    private LinearLayout episodeListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon_detail);

        ImageView thumbnailView = findViewById(R.id.thumbnail);
        TextView titleView = findViewById(R.id.title);
        TextView descriptionView = findViewById(R.id.description);
        episodeListLayout = findViewById(R.id.episode_list);

        // Intent ile verileri al
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        thumbnailPath = intent.getStringExtra("thumbnail");
        folderPath = intent.getStringExtra("folder");

        if (thumbnailPath != null)
            thumbnailView.setImageURI(Uri.fromFile(new File(thumbnailPath)));

        titleView.setText(title);
        descriptionView.setText(description);

        loadEpisodes();

        enableImmersiveMode();
    }

    private void loadEpisodes() {
        File cartoonFolder = new File(folderPath);
        File episodesDir = new File(cartoonFolder, "episodes");
        if (!episodesDir.exists() || !episodesDir.isDirectory()) return;

        File[] videos = episodesDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".mp4") && !name.startsWith(".")
        );
        if (videos == null) return;

        for (File episodeFile : videos) {
            String episodeTitle = episodeFile.getName().replace(".mp4", "");

            TextView episodeTextView = new TextView(this);
            episodeTextView.setText(episodeTitle);
            episodeTextView.setTextSize(18);
            episodeTextView.setPadding(16, 16, 16, 16);
            episodeTextView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            // Add margin to the bottom of each episode item
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20); // Add 20px bottom margin
            episodeTextView.setLayoutParams(params);

            // Set click listener to play video using internal activity
            episodeTextView.setOnClickListener(v -> {
                String episodePath = cartoonFolder.getAbsolutePath() + "/episodes/" + episodeFile.getName();
                File videoFile = new File(episodePath);
                Uri videoUri = Uri.fromFile(videoFile);

                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra("videoUri", videoUri.toString());
                startActivity(intent);
            });

            episodeListLayout.addView(episodeTextView);
        }
    }

    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}