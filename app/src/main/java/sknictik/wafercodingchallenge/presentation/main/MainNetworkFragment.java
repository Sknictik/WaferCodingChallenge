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
import java.util.Collections;
import java.util.List;

public class MainNetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";

    private static final String ARGS_URL_KEY = "args:urlKey";

    private DownloadCallback<List<InfoModel>> downloadCallback;
    private DownloadTask downloadTask;
    private String urlString;

    public static MainNetworkFragment getInstance(final FragmentManager fragmentManager, final String url) {
        final MainNetworkFragment networkFragment = new MainNetworkFragment();
        final Bundle args = new Bundle();
        args.putString(ARGS_URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Deliberately left this without null check as there should never be a null bundle.
        //If somehow there is - crash application since this behaviour is not expected.
        urlString = getArguments().getString(ARGS_URL_KEY);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        downloadCallback = (DownloadCallback<List<InfoModel>>) context;
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
    public void startDownload() {
        cancelDownload();
        downloadTask = new DownloadTask();
        downloadTask.execute(urlString);
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
    private static class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

        private DownloadCallback<List<InfoModel>> downloadCallback;

        DownloadTask(final DownloadCallback<List<InfoModel>> callback) {
            downloadCallback = callback;
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
                    downloadCallback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(final String... urls) {
            Result result = null;
            if (!isCancelled() && urls != null && urls.length > 0) {
                final String urlString = urls[0];
                try {
                    //URL url = new URL(urlString);
                    //TODO delegate to network manager from here
                    final String resultString = downloadUrl(url);
                    if (resultString != null) {
                        result = new Result(resultString);
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
                if (result.mException != null) {
                    downloadCallback.updateFromDownload(result.mException.getMessage());
                } else if (result.mResultValue != null) {
                    downloadCallback.updateFromDownload(result.mResultValue);
                }
                downloadCallback.finishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(Result result) {
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        static class Result {
            List<InfoModel> resultValue;
            Exception exception;

            Result(final List<InfoModel> resultValue) {
                //To prevent accidently changing result field from outside of this object
                this.resultValue = new ArrayList<>(resultValue);
            }

            Result(final Exception exception) {
                this.exception = exception;
            }
        }

    }

}
