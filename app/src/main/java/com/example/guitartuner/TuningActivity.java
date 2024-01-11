package com.example.guitartuner;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.ResultSetMetaData;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.*;
import be.tarsos.dsp.*;


public class TuningActivity extends AppCompatActivity {
    TextView tuning;
    FloatingActionButton fab;

    TextView string;
    TextView pitch;
    TextView currentPitch;

    double diff;

    private double round1dec(double value) {

        double tmp1 = value * 10;
        double tmp2 = Math.round(tmp1);
        return tmp2 / 10;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tuning = (TextView) findViewById(R.id.tuning_name);
            tuning.setText(extras.getString("tuning"));
        }

        string = (TextView) findViewById(R.id.string);
        pitch = (TextView) findViewById(R.id.pitch);
        currentPitch = (TextView) findViewById(R.id.currentPitch);

        if (checkPermissions() == PackageManager.PERMISSION_GRANTED) {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
            PitchDetectionHandler pdh = (res, e) -> {
                final double pitchInHz = res.getPitch();
                runOnUiThread(() -> {
                    // small delay
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException err) {
                        err.printStackTrace();
                    }
                    if (tuning.getText().toString().equals("Standard")) {
                        standardTuning(pitchInHz);
                    } else if (tuning.getText().toString().equals("Low E")) {
                        lowETuning(pitchInHz);
                    } else if (tuning.getText().toString().equals("Drop D")) {
                        dropDTuning(pitchInHz);
                    }
                });
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();
        } else {
            ActivityCompat.requestPermissions(
                    TuningActivity.this,
                    new String[]{ Manifest.permission.RECORD_AUDIO
                    }, 1);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void setMarginAndText(double diff) {

        double roundDiff = round1dec(diff);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        int numberOfLines = (int) Math.floor(abs(roundDiff));
        int margin = numberOfLines * 8;

        StringBuilder text = new StringBuilder();
        // only add lines if difference more than 1
        for (int i = 0; i < numberOfLines; i++)
            text.append(String.valueOf((char) 124));

        if (roundDiff > 0) {
            params.setMargins(0, 0, margin, 0);
            pitch.setText(text + "+" + roundDiff);
            pitch.setGravity(Gravity.START);
        } else if (roundDiff < 0) {
            params.setMargins(margin, 0, 0, 0);
            pitch.setText("-" + abs(roundDiff) + text);
            pitch.setGravity(Gravity.END);
        } else {
            params.setMargins(0, 0, 0, 0);
            pitch.setText("0");
            pitch.setGravity(Gravity.CENTER);
        }
        pitch.setLayoutParams(params);
    }

    private void processPitch(Double pitchInHz, Double desiredPitch, String note) {
        diff = pitchInHz - desiredPitch;
        string.setText(note);
        setMarginAndText(diff);
    }

    private void resetText(Double pitchInHz) {

        currentPitch.setText(pitchInHz == -1.0 ? "0" : Double.toString(pitchInHz));
        string.setText("");
        pitch.setText("");
    }

    private void standardTuning(double pitchInHz) {
        resetText(pitchInHz);
        if (pitchInHz >= 72.4 && pitchInHz < 92.4)
            processPitch(pitchInHz, 82.0, "e");
        else if (pitchInHz >= 100 && pitchInHz < 120)
            processPitch(pitchInHz, 110.0, "A");
        else if (pitchInHz >= 137 && pitchInHz < 157)
            processPitch(pitchInHz, 146.0, "D");
        else if (pitchInHz >= 186 && pitchInHz < 206)
            processPitch(pitchInHz, 196.0, "G");
        else if (pitchInHz >= 237 && pitchInHz <= 257)
            processPitch(pitchInHz, 246.0, "B");
        else if (pitchInHz >= 320 && pitchInHz < 340)
            processPitch(pitchInHz, 330.0, "E");
    }

    private void dropDTuning(double pitchInHz) {
        resetText(pitchInHz);
        if (pitchInHz >= 63.4 && pitchInHz < 83.4)
            processPitch(pitchInHz, 73.0, "e");
        else if (pitchInHz >= 100 && pitchInHz < 120)
            processPitch(pitchInHz, 110.0, "A");
        else if (pitchInHz >= 137 && pitchInHz < 157)
            processPitch(pitchInHz, 146.0, "D");
        else if (pitchInHz >= 186 && pitchInHz < 206)
            processPitch(pitchInHz, 196.0, "G");
        else if (pitchInHz >= 237 && pitchInHz <= 257)
            processPitch(pitchInHz, 246.0, "B");
        else if (pitchInHz >= 320 && pitchInHz < 340)
            processPitch(pitchInHz, 330.0, "E");
    }

    private void lowETuning(double pitchInHz) {
        resetText(pitchInHz);
        if (pitchInHz >= 70 && pitchInHz < 86)
            processPitch(pitchInHz, 78.0, "e");
        else if (pitchInHz >= 97 && pitchInHz < 113)
            processPitch(pitchInHz, 105.0, "A");
        else if (pitchInHz >= 129 && pitchInHz < 148)
            processPitch(pitchInHz, 139.0, "D");
        else if (pitchInHz >= 175 && pitchInHz < 195)
            processPitch(pitchInHz, 185.0, "G");
        else if (pitchInHz >= 223 && pitchInHz <= 243)
            processPitch(pitchInHz, 233.0, "B");
        else if (pitchInHz >= 302 && pitchInHz < 322)
            processPitch(pitchInHz, 312.0, "E");
    }

    private int checkPermissions() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
    }
}
