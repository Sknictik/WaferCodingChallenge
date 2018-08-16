package sknictik.wafercodingchallenge.presentation.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.R;
import sknictik.wafercodingchallenge.WaferApplication;
import sknictik.wafercodingchallenge.domain.model.Info;
import sknictik.wafercodingchallenge.presentation.utils.ResourceMessage;

/**
 * Normally screen logic should be divided in three parts: UI logic (Activity class), Presenter and StateModel.
 */
public class MainActivity extends AppCompatActivity implements DownloadCallback<List<Info>>, InfoListAdapter.OnDeleteButtonClickedListener {

    private static final String INFO_LIST_KEY = "infoList";

    private MainNetworkFragment mainNetworkFragment;

    //By default set to false
    private boolean isDownloading;

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView errorText;
    private FrameLayout progressContainer;

    //ArrayList instead of List for serialization in bundle
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
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
        //To prevent conflicts between horizontal and vertical scroll inside recycler view
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new InfoListAdapter(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        progress = findViewById(R.id.progress);
        errorText = findViewById(R.id.error_text);
        progressContainer = findViewById(R.id.progress_container);
    }

    private void startDownload() {
        if (!isDownloading && mainNetworkFragment != null) {
            isDownloading = true;
            setProgressState(ProgressState.LOADING);
            // Execute the async download.
            mainNetworkFragment.startDownload(getWaferApplication().getCommandFactory().getInfoCommand());
        }
    }

    @Override
    public void onDownloadSuccess(final List<Info> result) {
        infoList = new ArrayList<>(result);
        fillListAdapter(infoList);
        setProgressState(ProgressState.SUCCESS);
    }

    @Override
    public void onDownloadError(final ResourceMessage errorMsg) {
        setProgressState(ProgressState.ERROR);
        errorText.setText(getWaferApplication().getResourceMessageFormatter().format(errorMsg));
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

        if (mainNetworkFragment != null) {
            mainNetworkFragment.cancelDownload();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable(INFO_LIST_KEY, infoList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDeleteButtonClick(final int position) {
        deleteItem(position);
    }

    private void deleteItem(final int position) {
        infoList.remove(position);
        fillListAdapter(infoList);
    }

    private void fillListAdapter(final List<Info> infoList) {
        final InfoListAdapter adapter = (InfoListAdapter) recyclerView.getAdapter();

        if (adapter != null) {
            adapter.setItems(infoList);
        }
    }

    private WaferApplication getWaferApplication() {
        return (WaferApplication) getApplication();
    }

    private void setProgressState(ProgressState progressState) {
        switch (progressState) {
            case SUCCESS: {
                progressContainer.setVisibility(View.GONE);
                errorText.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            }
            case LOADING: {
                progressContainer.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                break;
            }
            case ERROR: {
                progressContainer.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                break;
            }
        }
    }

    private enum ProgressState {
        SUCCESS, LOADING, ERROR
    }
}
