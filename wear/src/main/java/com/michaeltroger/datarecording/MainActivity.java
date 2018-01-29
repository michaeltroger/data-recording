package com.michaeltroger.datarecording;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.wearable.activity.WearableActivity;
import android.widget.Toast;

import com.michaeltroger.datarecording.controller.ClickHandlers;
import com.michaeltroger.datarecording.databinding.ActivityMainBinding;
import com.michaeltroger.datarecording.messaging.Messaging;

/**
 * The core of the application and its starting point
 */
public class MainActivity extends WearableActivity implements IView {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Responsible for sending messages like a remote control
     */
    private Messaging messaging;
    /**
     * For displaying popup messages
     */
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        messaging = new Messaging(this, this);
        binding.setHandlers(new ClickHandlers(messaging));

        setAmbientEnabled(); // keep the screen on
    }

    @Override
    protected void onDestroy() {
        if(toast != null) {
            toast.cancel();
        }
        messaging.cancel();
        super.onDestroy();
    }

    @Override
    public void displayToast(final @StringRes int message) {
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
