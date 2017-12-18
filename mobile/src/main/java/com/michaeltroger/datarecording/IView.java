package com.michaeltroger.datarecording;

import android.support.annotation.StringRes;

public interface IView {
    void enableRecordMode();
    void enableStandbyMode();
    void setSampleNumber(@StringRes final int label);
    void showLicenseInfo();
}
