package sknictik.wafercodingchallenge.presentation.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import java.util.List;

import sknictik.wafercodingchallenge.R;

/**
 * Normally screen logic should be divided in three parts: UI logic (Activity class), Presenter and StateModel.
 * But writing my own MVP library would take too much time, so all presentation layer logic for this screen
 * will be written here.
 */
public class MainActivity extends FragmentActivity implements DownloadCallback<List<InfoModel>> {

    private MainNetworkFragment mainNetworkFragment;

    //By default set to false
    private boolean isDownloading;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainNetworkFragment = MainNetworkFragment.getInstance(getSupportFragmentManager(), ???Url?);

    }

    private void startDownload() {
        if (!isDownloading && mainNetworkFragment != null) {
            // Execute the async download.
            mainNetworkFragment.startDownload();
            isDownloading = true;
        }

    }

    @Override
    public void updateFromDownload(final List<InfoModel> result) {
        //TODO update UI here based on download result
    }

    @Override
    @Nullable
    public NetworkInfo getActiveNetworkInfo() {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @Nullable final NetworkInfo networkInfo;
        //Connectivity manager should never be null in a real device but still...
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        } else {
            networkInfo = null;
        }
        return networkInfo;

    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR: {
                //TODO show error dialog or toast
                break;
            }
            case Progress.CONNECT_SUCCESS:
                //Do nothing
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                //TODO ??
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS: {
                //TODO show progress?
                break;
            }
            case Progress.PROCESS_INPUT_STREAM_SUCCESS: {
                //TODO remove progress
                break;
            }
            default:
                //TODO show unknown progress status error
        }

    }

    @Override
    public void finishDownloading() {
        isDownloading = false;
        if (mainNetworkFragment != null) {
            mainNetworkFragment.cancelDownload();
        }
    }

    //TODO onSaveInstanceState
}
