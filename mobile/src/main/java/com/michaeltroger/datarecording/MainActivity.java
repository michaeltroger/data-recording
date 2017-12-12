package com.michaeltroger.datarecording;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.michaeltroger.datarecording.controller.ClickHandlers;
import com.michaeltroger.datarecording.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setHandlers(new ClickHandlers());
    }
}
