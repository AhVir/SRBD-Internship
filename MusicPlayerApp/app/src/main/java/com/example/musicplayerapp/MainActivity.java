package com.example.musicplayerapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // Song class to store song details
    class Song {
        String title;
        int resourceId;

        Song(String title, int resourceId) {
            this.title = title;
            this.resourceId = resourceId;
        }
    }

    // Pre-defined music list (REQUIREMENT 1)
    private final List<Song> songList = new ArrayList<Song>() {{
        add(new Song("Song 1", R.raw.song1));
        add(new Song("Song 2", R.raw.song2));
        add(new Song("Song 3", R.raw.song3));
    }};

    private int currentSongIndex = 0;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView textCurrentTime, textTotalTime, textSongTitle;
    private ImageView buttonPlayPause, buttonStop, buttonNext, buttonPrevious;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                textCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initializeViews();

        // Initialize media player with first song
        initializeMediaPlayer();

        // Set up button click listeners
        setupButtonListeners();

        // Set up seek bar listener
        setupSeekBarListener();
    }

    private void initializeViews() {
        seekBar = findViewById(R.id.seekBar);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);
        textSongTitle = findViewById(R.id.textSongTitle);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
    }

    private void initializeMediaPlayer() {
        // Create media player with current song
        mediaPlayer = MediaPlayer.create(this, songList.get(currentSongIndex).resourceId);
        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mp.getDuration());
            textTotalTime.setText(formatTime(mp.getDuration()));
            textSongTitle.setText(songList.get(currentSongIndex).title);
        });
    }

    private void setupButtonListeners() {
        // Single Play/Pause button
        buttonPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                buttonPlayPause.setImageResource(R.drawable.pause);
                handler.post(updateSeekBar);
            }
        });

        // Stop button - FIXED
        buttonStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                initializeMediaPlayer(); // Create new MediaPlayer
                buttonPlayPause.setImageResource(R.drawable.play);
                seekBar.setProgress(0);
                textCurrentTime.setText("0:00");
                textTotalTime.setText("0:00");
            }
        });

        // Next button
        buttonNext.setOnClickListener(v -> playNextSong());

        // Previous button
        buttonPrevious.setOnClickListener(v -> playPreviousSong());
    }

    private void setupSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    textCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playNextSong() {
        // Move to next song (circular)
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        playSelectedSong();
    }

    private void playPreviousSong() {
        // Move to previous song (circular)
        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        playSelectedSong();
    }

    private void playSelectedSong() {
        // Stop current playback
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // Initialize with new song
        initializeMediaPlayer();

        // Start playing
        mediaPlayer.start();
        buttonPlayPause.setImageResource(R.drawable.pause);
        handler.post(updateSeekBar);
    }

    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}