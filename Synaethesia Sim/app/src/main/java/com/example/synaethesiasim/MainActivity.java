package com.example.synaethesiasim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import static javax.xml.transform.OutputKeys.ENCODING;

public class MainActivity extends AppCompatActivity {
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private int SAMPLE_RATE = 44100; // Hz
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;

    private int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//       FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        int REQUEST_RECORD_AUDIO_PERMISSION = 200;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        startAudio();
    }

    private void startAudio() {
        AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);

        // Avoiding loop allocations
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        int read;
        String text = "";

        TextView testText = findViewById(R.id.testText);

        audioRecord.startRecording();
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            read = audioRecord.read(buffer, BUFFER_SIZE);
            for (int i = 0; i < BUFFER_SIZE; i++){
                text += String.valueOf(buffer.array()[i]);
            }
            testText.setText(text);
            text = "";
            audioRecord.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pause) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
