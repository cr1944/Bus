package ryancheng.bus.retrofit;

import java.util.List;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import ryancheng.bus.model.Bus;
import ryancheng.bus.model.Line;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public interface API {
    @FormUrlEncoded
    @POST("/public/bus/get")
    Observable<Bus> getSid(@Field("idnum") String idnum);

    @GET("/public/bus/mes/sid/{sid}/stoptype/{stoptype}")
    Object getStationFromSid(@Path("sid") String sid, @Path("stoptype") int stoptype);

    @FormUrlEncoded
    @POST("/public/bus/Getstop")
    Observable<List<Line>> getStationDetail(@Field("stoptype") int stoptype, @Field("stopid") String stopid, @Field("sid") String sid);
}
