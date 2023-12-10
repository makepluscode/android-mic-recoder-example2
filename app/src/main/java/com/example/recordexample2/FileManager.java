package com.example.recordexample2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class FileManager {

    private Context context;
    private Uri currentFileUri;

    public FileManager(Context context) {
        this.context = context;
    }

    public OutputStream createOutputStream() throws IOException {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Recording_" + System.currentTimeMillis() + ".pcm");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/x-wav");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);

        currentFileUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (currentFileUri == null) {
            throw new IOException("Failed to create new MediaStore record.");
        }

        return resolver.openOutputStream(currentFileUri);
    }

    public void closeStreamQuietly(OutputStream os, Uri currentFileUri) {
        try {
            if (os != null) {
                os.close();
                if (currentFileUri != null) {
                    long fileSize = calculateFileSize(currentFileUri);
                    String message = "Saving complete, URI: " + currentFileUri.toString();
                    new Handler(Looper.getMainLooper()).post(() ->
                            new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getCurrentFileUri() {
        return currentFileUri;
    }

    private long calculateFileSize(Uri fileUri) {
        try (Cursor cursor = context.getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
                return cursor.getLong(sizeIndex);
            }
        }
        return -1; // Return -1 or some other error value if size couldn't be determined
    }
}
