package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;

public interface IView {
    void enableRecordMode();
    void enableStandbyMode();
    void setSampleNumber(@NonNull final String text);
    void showLicenseInfo();
}
