package dynoapps.exchange_rates.network;

import java.util.List;

import dynoapps.exchange_rates.model.rates.BigparaRate;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by erdemmac on 25/11/2016.
 */

public interface BigparaService {
    @Headers({
            "Content-Type:text/html; charset=utf-8"
    })
    @GET("doviz/dolar/")
    Observable<List<BigparaRate>> rates();
}
