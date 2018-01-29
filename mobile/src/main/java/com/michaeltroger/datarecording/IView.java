package com.michaeltroger.datarecording;

import android.support.annotation.StringRes;

/**
 * Changes in the UI are made
 * through its methods
 */
public interface IView {
    /**
     * Sets the views representation
     * to record mode
     */
    void enableRecordMode();

    /**
     * Sets the view representation
     * to standby mode
     */
    void enableStandbyMode();

    /**
     * Updates the sample number
     * in the view
     * @param label The label to use. It can be "current" or "next"
     *              depending on the app's state
     */
    void setSampleNumber(@StringRes final int label);
    /**
     * Displays license information of open source software used
     * in this project
     */
    void showLicenseInfo();
}
