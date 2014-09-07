package com.hoppersoft.android.sunshine;

/**
 * Created by ahopper on 8/9/2014.
 */
public interface IAsyncTaskNotificationRecipient<U,V> {
    void onProgressUpdate(U... progress);
    void onCompleted(V result);
}
