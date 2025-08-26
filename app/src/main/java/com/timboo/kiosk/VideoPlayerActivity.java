package com.timboo.kiosk;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.timboo.kiosk.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBar;
    private Button playPauseButton;
    private Handler handler = new Handler();
    private boolean isPlaying = false;
    private Runnable hideControlsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.seekBar);
        playPauseButton = findViewById(R.id.playPauseButton);

        View controlLayout = findViewById(R.id.controlLayout);

        hideControlsRunnable = () -> controlLayout.animate().alpha(0f).setDuration(500).withEndAction(() -> controlLayout.setVisibility(View.GONE)).start();

        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener((v, event) -> {
            if (controlLayout.getVisibility() != View.VISIBLE) {
                controlLayout.setAlpha(0f);
                controlLayout.setVisibility(View.VISIBLE);
                controlLayout.animate().alpha(1f).setDuration(500).start();
            }
            handler.removeCallbacks(hideControlsRunnable);
            handler.postDelayed(hideControlsRunnable, 3000);
            return false;
        });

        // Start auto-hide timer
        handler.postDelayed(hideControlsRunnable, 3000);

        String uriString = getIntent().getStringExtra("videoUri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            videoView.setVideoURI(uri);
        } else {
            Toast.makeText(this, "Video bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            videoView.start();
            isPlaying = true;
            playPauseButton.setText("Duraklat");

            // SeekBar güncellemesi
            handler.post(updateSeekBar);
        });

        playPauseButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playPauseButton.setText("Devam");
                isPlaying = false;
            } else {
                videoView.start();
                playPauseButton.setText("Duraklat");
                isPlaying = true;
                handler.post(updateSeekBar);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) videoView.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        // Enable immersive mode
        enableImmersiveMode();
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && isPlaying) {
                seekBar.setProgress(videoView.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        handler.removeCallbacks(hideControlsRunnable);
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