package demoapps.exchange_rates.converters;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import demoapps.exchange_rates.data.BuySellRate;
import demoapps.exchange_rates.data.Rate;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by erdemmac on 24/11/2016.
 */

public class BigparaConverter implements Converter<ResponseBody, List<Rate>> {

    /**
     * Factory for creating converter. We only care about decoding responses.
     **/
    public static final class Factory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                                Annotation[] annotations,
                                                                Retrofit retrofit) {
            return INSTANCE;
        }

    }

    private static final String HOST = "http://www.bigpara.com";

    private BigparaConverter() {
    }

    static final BigparaConverter INSTANCE = new BigparaConverter();


    @Override
    public List<Rate> convert(ResponseBody value) throws IOException {

        ArrayList<Rate> rates = new ArrayList<>();
        String responseBody = value != null ? value.string() : null;

        ArrayList<Element> elements =  Jsoup.parse(responseBody, HOST).select("#content").select(".kurdetail").select(".kurbox");

        String val_sell = elements.get(1).select(".value").text();
        String val_buy = elements.get(2).select(".value").text();

        BuySellRate buySellRate = new BuySellRate();
        buySellRate.value_sell =val_sell;
        buySellRate.value_buy = val_buy;
        buySellRate.setRealValues();
        rates.add(buySellRate);

        return rates;
    }


}