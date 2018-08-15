package sknictik.wafercodingchallenge.domain.model;

import java.io.Serializable;

/**
 * A simple data class. Normally I would have a separate POJO class for each layer
 * (data, domain and presentation) but this time there is no real need for that.
 */
public class Info implements Serializable {

    private final String countryName;
    private final String currencyName;
    private final String languageName;

    public Info(final String countryName, final String currencyName, final String languageName) {
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
