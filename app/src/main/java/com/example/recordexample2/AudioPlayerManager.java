package com.example.recordexample2;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.database.Cursor;

import java.io.IOException;
import java.io.InputStream;

public class AudioPlayerManager {
    private Context context;
    private AudioTrack audioTrack;
    private boolean isPlaying = false;
    private InputStream inputStream;
    private Runnable onCompletionListener;

    public AudioPlayerManager(Context context) {
        this.context = context;
    }

    public void playLastRecordedAudio() {
        Uri lastAudioUri = getLastAudioUri();
        if (lastAudioUri != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(lastAudioUri);
                int minBufferSize = AudioTrack.getMinBufferSize(44100,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
                        AudioTrack.MODE_STREAM);

                byte[] buffer = new byte[minBufferSize];
                audioTrack.play();
                isPlaying = true;

                new Thread(() -> {
                    int read;
                    try {
                        while (isPlaying && (read = inputStream.read(buffer)) != -1) {
                            audioTrack.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("AudioPlayerManager", "Error in playback thread", e);
                    } finally {
                        stopAudioTrack();
                    }
                }).start();

            } catch (IOException e) {
                Log.e("AudioPlayerManager", "Error playing audio file", e);
            }
        } else {
            Log.d("AudioPlayerManager", "No last recorded audio found");
        }
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopPlaying() {
        stopAudioTrack();
    }

    public void setOnCompletionListener(Runnable listener) {
        this.onCompletionListener = listener;
    }

    private void stopAudioTrack() {
        if (audioTrack != null) {
            if (audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
            }
            audioTrack.release();
            isPlaying = false;

            if (onCompletionListener != null) {
                onCompletionListener.run();
            }
        }
    }
    private Uri getLastAudioUri() {
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Audio.Media._ID};
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        try (Cursor cursor = context.getContentResolver().query(audioUri, projection, null, null, sortOrder)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                long id = cursor.getLong(idColumn);
                return Uri.withAppendedPath(audioUri, Long.toString(id));
            }
        }
        return null;
    }
}
