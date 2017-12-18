package com.michaeltroger.datarecording;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.michaeltroger.datarecording.controller.ClickHandlers;
import com.michaeltroger.datarecording.databinding.ActivityMainBinding;
import com.michaeltroger.datarecording.sensor.SensorUtilities;
import com.michaeltroger.settings.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.psdev.licensesdialog.LicensesDialog;


public class MainActivity extends AppCompatActivity implements IView {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private SharedPreferences preferences;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setHandlers(new ClickHandlers());

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        setupMetaData(binding.classLabel, R.string.pref_key_class_label);
        setupMetaData(binding.person, R.string.pref_key_person);
        setupMetaData(binding.location, R.string.pref_key_location);

        if (!SensorUtilities.hasWriteExternalStoragePermission(this)) {
            requestWriteExternalStoragePermission();
        }
    }

    private void setupMetaData(@NonNull final EditText editText, @StringRes final int prefKey) {
        if (preferences == null) throw new AssertionError("preference manager may not be null");

        editText.setText(preferences.getString(getString(prefKey), ""));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                preferences.edit().putString(getString(prefKey), charSequence.toString()).apply();
            }
            @Override public void afterTextChanged(Editable editable) {}
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        if(SensorUtilities.isRecordingActive(this)) {
            enableRecordMode();
        } else {
            enableStandbyMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final MessageEvent event) {
        Log.d(TAG, "received event:"+event.appState);
        switch(event.appState) {
            case STANDBY:
                enableStandbyMode();
                break;
            case RECORDING:
                enableRecordMode();
                break;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.license_info:
                showLicenseInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: enable recording
                } else {
                   // TODO: disable recording!
                }
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void showLicenseInfo() {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }

    @Override
    public void enableRecordMode() {
        binding.start.setEnabled(false);
        binding.stop.setEnabled(true);
        binding.classLabel.setEnabled(false);
        binding.person.setEnabled(false);
        binding.location.setEnabled(false);
    }

    @Override
    public void enableStandbyMode() {
        binding.start.setEnabled(true);
        binding.stop.setEnabled(false);
        binding.classLabel.setEnabled(true);
        binding.person.setEnabled(true);
        binding.location.setEnabled(true);
    }

    private void requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }
}
