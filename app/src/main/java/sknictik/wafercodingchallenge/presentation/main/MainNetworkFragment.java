package sknictik.wafercodingchallenge.presentation.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.R;
import sknictik.wafercodingchallenge.domain.IInfoCommand;
import sknictik.wafercodingchallenge.domain.model.Info;
import sknictik.wafercodingchallenge.presentation.utils.ResourceMessage;

public class MainNetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";

    private DownloadCallback<List<Info>> downloadCallback;
    private DownloadTask downloadTask;

    public static MainNetworkFragment getInstance(final FragmentManager fragmentManager) {
        MainNetworkFragment networkFragment = (MainNetworkFragment) fragmentManager
                .findFragmentByTag(MainNetworkFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new MainNetworkFragment();
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        // Host Activity will handle callbacks from task.
        downloadCallback = (DownloadCallback<List<Info>>) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        downloadCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();

        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload(IInfoCommand infoCommand) {
        cancelDownload();
        downloadTask = new DownloadTask(downloadCallback, infoCommand);
        downloadTask.execute();
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private static class DownloadTask extends AsyncTask<Void, Integer, DownloadTask.Result> {

        private DownloadCallback<List<Info>> downloadCallback;
        private IInfoCommand infoCommand;

        DownloadTask(final DownloadCallback<List<Info>> callback, IInfoCommand infoCommand) {
            downloadCallback = callback;
            this.infoCommand = infoCommand;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (downloadCallback != null) {
                final NetworkInfo networkInfo = downloadCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
                    // If no connectivity, cancel task and update Callback with null data.
                    downloadCallback.onDownloadError(new ResourceMessage(R.string.no_connection_error));
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(final Void... irrelevant) {
            Result result = null;
            if (!isCancelled()) {
                try {
                    final List<Info> modelList = infoCommand.loadInfoList();
                    if (modelList != null && !modelList.isEmpty()) {
                        result = new Result(modelList);
                    } else {
                        result = new Result(new IOException("No response received."));
                    }
                } catch(final Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
            if (result != null && downloadCallback != null) {
                if (result.errorMsg != null) {
                    downloadCallback.onDownloadError(result.errorMsg);
                } else if (result.resultValue != null) {
                    downloadCallback.onDownloadSuccess(result.resultValue);
                }
                downloadCallback.finishDownloading();
            }

            clean();
        }

        @Override
        protected void onCancelled(Result result) {
            clean();
        }

        private void clean() {
            infoCommand = null;
            downloadCallback = null;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        static class Result {
            List<Info> resultValue;
            //The reason why I'm using resource message here is to prevent providing context
            //to classes with low level logic only to be able to decipher string resources.
            ResourceMessage errorMsg;

            Result(final List<Info> resultValue) {
                //To prevent accidentally changing result field from outside of this object
                this.resultValue = new ArrayList<>(resultValue);
            }

            Result(final Exception exception) {
                this.errorMsg = new ResourceMessage(exception.getMessage());
            }
        }

    }

}
