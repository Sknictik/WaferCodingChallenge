package sknictik.wafercodingchallenge;

import android.app.Application;

import sknictik.wafercodingchallenge.data.network.NetworkManager;
import sknictik.wafercodingchallenge.domain.CommandFactory;
import sknictik.wafercodingchallenge.presentation.utils.ResourceMessageFormatter;

public class WaferApplication extends Application {

    private CommandFactory commandFactory;
    private ResourceMessageFormatter resourceMessageFormatter;

    @Override
    public void onCreate() {
        super.onCreate();

        this.commandFactory = new CommandFactory(new NetworkManager(BuildConfig.BASE_URL));
        this.resourceMessageFormatter = new ResourceMessageFormatter(this);
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public ResourceMessageFormatter getResourceMessageFormatter() {
        return resourceMessageFormatter;
    }
}
