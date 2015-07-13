package ryancheng.bus.retrofit;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import ryancheng.bus.model.Bus;
import ryancheng.bus.model.Line;
import ryancheng.bus.model.Station;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class APIManager {
    static final String HOST = "http://shanghaicity.openservice.kankanews.com";
    static final String XPATH = "//div[@class=\"station\"]";
    static final String WEIXIN_UA = "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 MicroMessenger/6.0.2.56_r958800.520 NetType/WIFI";
    private static APIManager instance;

    private APIManager() {
    }

    public static APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
        }
        return instance;
    }

    public Observable<Bus> getSid(String idnum) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HOST)
                .build();
        API api = restAdapter.create(API.class);
        return api.getSid(idnum);
    }

    public Observable<List<Station>> getStations(final String sid, final int stoptype) {
        return Observable.create(new Observable.OnSubscribe<List<Station>>() {
            @Override
            public void call(Subscriber<? super List<Station>> subscriber) {
                try {
                    subscriber.onNext(getStationsFromSid(sid, stoptype));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private List<Station> getStationsFromSid(String sid, int stoptype) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", WEIXIN_UA);
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HOST)
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new HtmlConverter())
                .build();
        API api = restAdapter.create(API.class);
        TagNode tn = (TagNode) api.getStationFromSid(sid, stoptype);
        Object[] objects;
        try {
            objects = tn.evaluateXPath(XPATH);
            List<Station> stations = new ArrayList<>();
            int index = 1;
            for (Object o : objects) {
                TagNode n = (TagNode) o;
                String text = n.getText().toString().replaceAll("\r\n", "").replaceAll(" ", "");
                stations.add(new Station(index++, text));
            }
            return stations;
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Observable<List<Line>> getStationDetail(final int stoptype, int stopid, final String sid) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Referer", HOST + "/public/bus/mes/sid/" + sid + "/stoptype/" + stoptype);
                request.addHeader("User-Agent", WEIXIN_UA);
                request.addHeader("X-Requested-With", "XMLHttpRequest");
                request.addHeader("Content-Type", "application/x-www-form-urlencoded");
                request.addHeader("Cookie", "_gscu_1404343399=36792790evxq6v41; _gscbrs_1404343399=1; Hm_lvt_ba907373475281ec79b64ad73e7c9a36=1436792791; Hm_lpvt_ba907373475281ec79b64ad73e7c9a36=1436792918; _gat=1; _ga=GA1.2.585713480.1433042162");
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HOST)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                //.setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        API api = restAdapter.create(API.class);
        return api.getStationDetail(stoptype, stopid + "", sid);
    }

}
