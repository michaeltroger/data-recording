package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;

public class MessageEvent {

    public final Mode mode;

    public MessageEvent(@NonNull final Mode mode) {
        this.mode = mode;
    }
}