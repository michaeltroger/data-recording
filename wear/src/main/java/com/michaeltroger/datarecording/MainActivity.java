package com.michaeltroger.datarecording;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.michaeltroger.datarecording.controller.ClickHandlers;
import com.michaeltroger.datarecording.databinding.ActivityMainBinding;
import com.michaeltroger.datarecording.messaging.Messaging;


public class MainActivity extends WearableActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Messaging messaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        messaging = new Messaging(this);
        binding.setHandlers(new ClickHandlers(messaging));

        setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        messaging.cancel();
        super.onDestroy();
    }
}
