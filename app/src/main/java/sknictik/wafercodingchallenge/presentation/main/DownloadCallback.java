package sknictik.wafercodingchallenge.presentation.main;

import android.net.NetworkInfo;

import sknictik.wafercodingchallenge.presentation.utils.ResourceMessage;

public interface DownloadCallback<T> {
    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void onSuccess(T result);

    /**
     * Called on error result
     */
    void onError(ResourceMessage errorMsg);

    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishDownloading();

}
