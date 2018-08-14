package sknictik.wafercodingchallenge.presentation.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.R;
import sknictik.wafercodingchallenge.WaferApplication;
import sknictik.wafercodingchallenge.domain.model.Info;
import sknictik.wafercodingchallenge.presentation.utils.ResourceMessage;

/**
 * Normally screen logic should be divided in three parts: UI logic (Activity class), Presenter and StateModel.
 * But writing my own MVP library would take too much time, so all presentation layer logic for this screen
 * will be written here.
 */
public class MainActivity extends FragmentActivity implements DownloadCallback<List<Info>> {

    private static final String INFO_LIST_KEY = "infoList";

    private MainNetworkFragment mainNetworkFragment;

    //By default set to false
    private boolean isDownloading;

    private RecyclerView recyclerView;
    private ProgressBar progress;
    //ArrayList required for serialization in bundle
    private ArrayList<Info> infoList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mainNetworkFragment = MainNetworkFragment.getInstance(getSupportFragmentManager());

        if (savedInstanceState != null) {
            infoList = (ArrayList<Info>) savedInstanceState.getSerializable(INFO_LIST_KEY);
            if (infoList != null) {
                fillListAdapter(infoList);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (infoList == null) {
            startDownload();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.info_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new InfoListAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        progress = findViewById(R.id.progress);
    }

    private void startDownload() {
        if (!isDownloading && mainNetworkFragment != null) {
            // Execute the async download.
            mainNetworkFragment.startDownload(getWaferApplication().getCommandFactory().getInfoCommand());
            isDownloading = true;
            setProgressState(true);
        }
    }

    @Override
    public void onSuccess(final List<Info> result) {
        infoList = new ArrayList<>(result);
        fillListAdapter(infoList);
    }

    @Override
    public void onError(ResourceMessage errorMsg) {
        Toast.makeText(this, getWaferApplication().getResourceMessageFormatter().format(errorMsg), Toast.LENGTH_SHORT).show();
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
    public void finishDownloading() {
        isDownloading = false;
        setProgressState(false);

        if (mainNetworkFragment != null) {
            mainNetworkFragment.cancelDownload();
        }
    }

    private void fillListAdapter(List<Info> infoList) {
        InfoListAdapter adapter = ((InfoListAdapter) recyclerView.getAdapter());

        if (adapter != null) {
            adapter.setItems(infoList);
        }
    }

    private WaferApplication getWaferApplication() {
        return (WaferApplication) getApplication();
    }

    private void setProgressState(boolean isDownloading) {
        progress.setVisibility(isDownloading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isDownloading ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(INFO_LIST_KEY, infoList);
        super.onSaveInstanceState(outState);
    }
}
