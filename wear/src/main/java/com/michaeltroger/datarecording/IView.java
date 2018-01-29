package com.michaeltroger.datarecording;

import android.support.annotation.StringRes;

/**
 * Changes in the UI are made
 * through its methods
 */
public interface IView {
    /**
     * Display a popup message with the wished text
     * @param message the string resource to use as message
     */
    void displayToast(final @StringRes int message);
}
