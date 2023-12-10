package com.example.recordexample2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private PermissionManager permissionManager;
    private AudioRecorderManager audioRecorderManager;
    private AudioPlayerManager audioPlayerManager;
    private Button recordToggleButton;
    private Button playToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new PermissionManager(this);
        audioRecorderManager = new AudioRecorderManager(this);
        audioPlayerManager = new AudioPlayerManager(this);

        permissionManager.requestPermissions();
        setupUI();
    }

    private void setupUI() {
        recordToggleButton = findViewById(R.id.recordToggleButton);
        playToggleButton = findViewById(R.id.playToggleButton);

        recordToggleButton.setOnClickListener(v -> {
            if (!audioRecorderManager.isRecording()) {
                audioRecorderManager.startRecording();
                recordToggleButton.setText("Stop Recording");
                playToggleButton.setEnabled(false);
            } else {
                audioRecorderManager.stopRecording();
                recordToggleButton.setText("Start Recording");
                playToggleButton.setEnabled(true);
            }
        });

        playToggleButton.setOnClickListener(v -> {
            if (!audioPlayerManager.isPlaying()) {
                playToggleButton.setText("Stop Recording");
                audioPlayerManager.playLastRecordedAudio();
                recordToggleButton.setEnabled(false);
            } else {
                audioPlayerManager.stopPlaying();
                playToggleButton.setText("Start Recording");
                recordToggleButton.setEnabled(true);
            }
        });

        audioPlayerManager.setOnCompletionListener(() -> runOnUiThread(() -> {
            playToggleButton.setText("Play Last Recorded");
            playToggleButton.setEnabled(true);
            recordToggleButton.setEnabled(true);
        }));
    }
}
