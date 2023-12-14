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

    int diff;

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
                final float pitchInHz = res.getPitch();
                runOnUiThread(() -> {
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
    private void setMarginAndText(int diff) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < abs(diff); i++)
            text.append(String.valueOf((char) 124));

        if (diff > 0) {
            //params.setMargins(diff * (-15), 0, 0, 0);
            params.setMargins(0, 0, diff * 10, 0);
            pitch.setText(text + "-" + diff);
            pitch.setGravity(Gravity.START);
        } else if (diff < 0) {
            params.setMargins(diff * (-10), 0, 0, 0);
            //params.setMargins(0, 0, diff * 15, 0);
            pitch.setText("+" + abs(diff) + text);
            pitch.setGravity(Gravity.END);
        } else {
            params.setMargins(0, 0, 0, 0);
            pitch.setText("0");
            pitch.setGravity(Gravity.CENTER);
        }
        pitch.setLayoutParams(params);
    }

    private void standardTuning(float pitchInHz) {
        currentPitch.setText(pitchInHz == -1.0 ? "0" : Float.toString(pitchInHz));
        if (pitchInHz >= 72.4 && pitchInHz < 92.4) {
            diff = 82 - (int) Math.ceil(pitchInHz);
            string.setText("e");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 100 && pitchInHz < 120) {
            diff = 110 - (int) Math.ceil(pitchInHz);
            string.setText("A");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 137 && pitchInHz < 157) {
            diff = 146 - (int) Math.ceil(pitchInHz);
            string.setText("D");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 186 && pitchInHz < 206) {
            diff = 196 - (int) Math.ceil(pitchInHz);
            string.setText("G");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 237 && pitchInHz <= 257) {
            diff = 246 - (int) Math.ceil(pitchInHz);
            string.setText("B");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 320 && pitchInHz < 340) {
            diff = 330 - (int) Math.ceil(pitchInHz);
            string.setText("E");
            setMarginAndText(diff);
        }
    }

    private void dropDTuning(float pitchInHz) {
        currentPitch.setText(pitchInHz == -1.0 ? "0" : Float.toString(pitchInHz));
        if (pitchInHz >= 63.4 && pitchInHz < 83.4) {
            diff = 73 - (int) Math.ceil(pitchInHz);
            string.setText("e");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 100 && pitchInHz < 120) {
            diff = 110 - (int) Math.ceil(pitchInHz);
            string.setText("A");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 137 && pitchInHz < 157) {
            diff = 146 - (int) Math.ceil(pitchInHz);
            string.setText("D");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 186 && pitchInHz < 206) {
            diff = 196 - (int) Math.ceil(pitchInHz);
            string.setText("G");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 237 && pitchInHz <= 257) {
            diff = 246 - (int) Math.ceil(pitchInHz);
            string.setText("B");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 320 && pitchInHz < 340) {
            diff = 330 - (int) Math.ceil(pitchInHz);
            string.setText("E");
            setMarginAndText(diff);
        }
    }

    private void lowETuning(float pitchInHz) {
        currentPitch.setText(pitchInHz == -1.0 ? "0" : Float.toString(pitchInHz));
        if (pitchInHz >= 70 && pitchInHz < 86) {
            diff = 78 - (int) Math.ceil(pitchInHz);
            string.setText("e");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 97 && pitchInHz < 113) {
            diff = 105 - (int) Math.ceil(pitchInHz);
            string.setText("A");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 129 && pitchInHz < 148) {
            diff = 139 - (int) Math.ceil(pitchInHz);
            string.setText("D");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 175 && pitchInHz < 195) {
            diff = 185 - (int) Math.ceil(pitchInHz);
            string.setText("G");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 223 && pitchInHz <= 243) {
            diff = 233 - (int) Math.ceil(pitchInHz);
            string.setText("B");
            setMarginAndText(diff);
        }
        else if (pitchInHz >= 302 && pitchInHz < 322) {
            diff = 312 - (int) Math.ceil(pitchInHz);
            string.setText("E");
            setMarginAndText(diff);
        }
    }

    private int checkPermissions() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
    }
}
