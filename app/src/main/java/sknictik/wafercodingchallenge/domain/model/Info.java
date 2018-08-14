package sknictik.wafercodingchallenge.domain.model;

import java.io.Serializable;

/**
 * A simple data class. Normally I would have a separate POJO class for each layer
 * (data, domain and presentation) but this time there is no real need for that.
 */
public class Info implements Serializable {

    private String countryName;
    private String currencyName;
    private String languageName;

    public Info(String countryName, String currencyName, String languageName) {
        this.countryName = countryName;
        this.currencyName = currencyName;
        this.languageName = languageName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getLanguageName() {
        return languageName;
    }
}
