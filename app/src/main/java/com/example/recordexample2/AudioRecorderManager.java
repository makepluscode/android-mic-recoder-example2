// AudioRecorderManager.java
package com.example.recordexample2;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.content.Context;
import java.io.IOException;
import java.io.OutputStream;
import android.net.Uri; // Add this import statement
import android.util.Log;


public class AudioRecorderManager {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private AudioRecord recorder;
    private Thread recordingThread;
    private boolean isRecording = false;
    private FileManager fileManager;

    public AudioRecorderManager(Context context) {
        fileManager = new FileManager(context);
    }

    public void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        recorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(() -> {
            writeAudioDataToFile();
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte[] data = new byte[BUFFER_SIZE];
        OutputStream os = null;

        try {
            os = fileManager.createOutputStream();
            while (isRecording) {
                int read = recorder.read(data, 0, BUFFER_SIZE);
                if (read != AudioRecord.ERROR_INVALID_OPERATION && os != null) {
                    os.write(data, 0, read);
                    Log.d("AudioRecorderManager", "Writing audio data to file");
                }
            }
        } catch (IOException e) {
            Log.e("AudioRecorderManager", "Error writing audio data to file", e);
            e.printStackTrace();
        } finally {
            Uri fileUri = fileManager.getCurrentFileUri();
            fileManager.closeStreamQuietly(os, fileUri);
            Log.d("AudioRecorderManager", "Recording stopped, file saved: " + fileUri.toString());
        }
    }


    public void stopRecording() {
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
