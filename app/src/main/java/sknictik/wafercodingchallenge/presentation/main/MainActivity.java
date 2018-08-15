package sknictik.wafercodingchallenge.presentation.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
 */
public class MainActivity extends AppCompatActivity implements DownloadCallback<List<Info>>,
        SwipeToDeleteHelperCallback.OnItemDeletedListener, InfoListAdapter.OnDeleteButtonClickedListener {

    private static final String INFO_LIST_KEY = "infoList";

    private MainNetworkFragment mainNetworkFragment;

    //By default set to false
    private boolean isDownloading;

    private RecyclerView recyclerView;
    private ProgressBar progress;
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

        //new ItemTouchHelper(new SwipeToDeleteHelperCallback(this)).attachToRecyclerView(recyclerView);

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
    public void onError(final ResourceMessage errorMsg) {
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

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable(INFO_LIST_KEY, infoList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemDeleted(final int positionOfDeletedItem) {
        deleteItem(positionOfDeletedItem);
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

    private void setProgressState(final boolean isDownloading) {
        progress.setVisibility(isDownloading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isDownloading ? View.GONE : View.VISIBLE);
    }
}
