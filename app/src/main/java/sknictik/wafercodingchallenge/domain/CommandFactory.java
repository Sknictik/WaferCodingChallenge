package sknictik.wafercodingchallenge.domain;

import sknictik.wafercodingchallenge.data.network.NetworkManager;

//Command factory is a nice way to make application more maintainable and extensible.
public class CommandFactory {

    private NetworkManager networkManager;

    public CommandFactory(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public IInfoCommand getInfoCommand() {
        return new InfoCommand(networkManager);
    }

}
