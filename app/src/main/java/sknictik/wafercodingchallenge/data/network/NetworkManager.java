package sknictik.wafercodingchallenge.data.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class contains low level logic related to network.
 * Normally i would use retrofit and okhttp3 here but they are 3-rd party so...
 * Should probably create an interface for this class too
 */
public class NetworkManager {

    private static final int TIMEOUT = 3000;
    private String baseUrl;

    public NetworkManager(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String loadDataFromNetwork() throws IOException {
        URL url = new URL(baseUrl);
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(TIMEOUT);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(TIMEOUT);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // Converts Stream to String with max length of MAX_READ_SIZE.
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        String json = buf.toString("UTF-8");
        Log.d(NetworkManager.class.getSimpleName(), json);
        return json;
    }

}
