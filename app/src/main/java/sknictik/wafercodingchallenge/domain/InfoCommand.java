package sknictik.wafercodingchallenge.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.data.network.NetworkManager;
import sknictik.wafercodingchallenge.domain.model.Info;

/**
 * This class contains intermediate logic between data and presentation layers for handling information from remote host.
 * This is where data retrieved from network will be transformed to list of POJOs, which will be used in presentation layer.
 */
public class InfoCommand implements IInfoCommand {

    private NetworkManager networkManager;

    InfoCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public List<Info> loadInfoList() throws IOException, JSONException {
        String result = networkManager.loadDataFromNetwork();
        //Gson would have really helped me out here...
        return convertJsonToPOJO(result);
    }

    private List<Info> convertJsonToPOJO(String json) throws JSONException {
        List<Info> infoList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        final String countryNameKey = "name";
        final String currenicesArrayNameKey = "currencies";
        final String currencyNameKey = "name";
        final String languagesArrayNameKey = "languages";
        final String languageNameKey = "name";

        String countryName;
        String currencyName;
        String languageName;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            countryName = obj.getString(countryNameKey);
            currencyName = obj.getJSONArray(currenicesArrayNameKey).getJSONObject(0).getString(currencyNameKey);
            languageName = obj.getJSONArray(languagesArrayNameKey).getJSONObject(0).getString(languageNameKey);

            infoList.add(new Info(countryName, currencyName, languageName));
        }

        return infoList;
    }

}
