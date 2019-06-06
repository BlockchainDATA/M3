package eco.data.m3.demo.netperf.android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * author: dai
 * date:   $date$
 * des:
 */
public interface ApiService {
    @POST("list_peers")
    Call<List<PeerEntity>> list();
}
