package com.timboo.kiosk;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

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
    }

    private void loadEpisodes() {
        File episodesDir = new File(folderPath, "episodes");
        if (!episodesDir.exists() || !episodesDir.isDirectory()) return;

        File[] videos = episodesDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".mp4") && !name.startsWith(".")
        );
        if (videos == null) return;

        for (File video : videos) {
            String episodeTitle = video.getName().replace(".mp4", "");

            TextView textView = new TextView(this);
            textView.setText(episodeTitle);
            textView.setTextSize(18);
            textView.setPadding(16, 16, 16, 16);
            textView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            // Add margin to the bottom of each episode item
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20); // Add 20px bottom margin
            textView.setLayoutParams(params);

            episodeListLayout.addView(textView);
        }
    }
}