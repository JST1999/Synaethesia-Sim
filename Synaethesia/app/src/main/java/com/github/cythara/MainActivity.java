package com.github.cythara;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;

import com.github.cythara.ListenerFragment.TaskCallbacks;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinner.OnItemSelectedListener;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import com.shawnlin.numberpicker.NumberPicker;
import com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements TaskCallbacks,
        OnItemSelectedListener, OnValueChangeListener {

    public static final int RECORD_AUDIO_PERMISSION = 0;
    public static final String PREFS_FILE = "prefs_file";
    public static final String USE_SCIENTIFIC_NOTATION = "use_scientific_notation";
    public static final String CURRENT_TUNING = "current_tuning";
    protected static final String REFERENCE_PITCH = "reference_pitch";
    private static final String TAG_LISTENER_FRAGMENT = "listener_fragment";
    private static final String USE_DARK_MODE = "use_dark_mode";
    private static int tuningPosition = 0;
    private static boolean isDarkModeEnabled;
    private static PitchAdjuster pitchAdjuster;

    public static Tuning getCurrentTuning() {
        return TuningMapper.getTuningFromPosition(tuningPosition);
    }

    public static boolean isDarkModeEnabled() {
        return isDarkModeEnabled;
    }

    public static float adjustPitch(float pitch) {
        return pitchAdjuster.adjustPitch(pitch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestRecordAudioPermission();
        } else {
            startRecording();
        }

        enableTheme();

        setContentView(R.layout.activity_main);

        setTuning();
        setPitchAdjuster();

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.app_name);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_privacy_policy: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.privacy_policy_link)));
                startActivity(browserIntent);

                break;
            }
            case R.id.toggle_dark_mode: {
                final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                        MODE_PRIVATE);
                boolean currentlyUsingDarkMode = preferences.getBoolean(USE_DARK_MODE, true);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(USE_DARK_MODE, !currentlyUsingDarkMode);
                editor.apply();

                recreate();

                break;
            }
            case R.id.set_reference_pitch: {
                final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                        MODE_PRIVATE);
                int referencePitch = preferences.getInt(REFERENCE_PITCH, 440);

                NumberPickerDialog dialog = new NumberPickerDialog();

                Bundle bundle = new Bundle();
                bundle.putInt("current_value", referencePitch);
                dialog.setArguments(bundle);

                dialog.setValueChangeListener(this);
                dialog.show(getSupportFragmentManager(), "number_picker");

                break;
            }
        }

        return false;
    }

    @Override
    public void onProgressUpdate(PitchDifference pitchDifference) {
        TunerView tunerView = this.findViewById(R.id.pitch);

        tunerView.setPitchDifference(pitchDifference);
        tunerView.invalidate();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startRecording();
                    } else {
                        AlertDialog alertDialog = new Builder(MainActivity.this).create();
                        alertDialog.setTitle(R.string.permission_required);
                        alertDialog.setMessage(getString(R.string.microphone_permission_required));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        finishAffinity();
                                    } else {
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_TUNING, position);
        editor.apply();

        tuningPosition = position;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
        final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(REFERENCE_PITCH, newValue);
        editor.apply();

        setPitchAdjuster();

        TunerView tunerView = this.findViewById(R.id.pitch);
        tunerView.invalidate();
    }

    private void startRecording() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListenerFragment listenerFragment = (ListenerFragment)
                fragmentManager.findFragmentByTag(TAG_LISTENER_FRAGMENT);

        if (listenerFragment == null) {
            listenerFragment = new ListenerFragment();
            fragmentManager
                    .beginTransaction()
                    .add(listenerFragment, TAG_LISTENER_FRAGMENT)
                    .commit();
        }
    }

    private void setTuning() {
        final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);
        tuningPosition = preferences.getInt(CURRENT_TUNING, 0);

        int textColorDark = getResources().getColor(R.color.colorTextDark);
    }

    private void enableTheme() {
        final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);
        isDarkModeEnabled = preferences.getBoolean(USE_DARK_MODE, true);

        int mode = AppCompatDelegate.MODE_NIGHT_NO;
        if (isDarkModeEnabled) {
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        }

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void setPitchAdjuster() {
        final SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);
        int referencePitch = preferences.getInt(REFERENCE_PITCH, 440);

        pitchAdjuster = new PitchAdjuster(referencePitch);
    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_AUDIO_PERMISSION);
    }
}