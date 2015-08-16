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
                request.addHeader("Cookie", "Hm_p1vt_6f69830ae7173059e935b61372431b35=KnmCelXQG78dbwlaAyo1Ag==; _gscu_1404343399=36792790evxq6v41; _gscs_1404343399=397019526bwjdj20|pv:4; _gscbrs_1404343399=1; Hm_lvt_ba907373475281ec79b64ad73e7c9a36=1439701953; Hm_lpvt_ba907373475281ec79b64ad73e7c9a36=1439702471; Hm_1vt_6f69830ae7173059e935b61372431b35=KnmCelXQHzMeoAlfAyzdAg==; _gat=1; HH=637a61ebc0d2a7778465aa69c975ab1ce50d57ec; HK=2fb14cf4dfa9d154946334616083062b1cabf790; HG=30a1546d70073d1459e37145aac5d44934bcb978; HA=cf43e9cf10677c251ed1c6a85d17e98bde1eaf49; HB=Y2Y0M2U5Y2YxMDY3N2MyNTFlZDFjNmE4NWQxN2U5OGJkZTFlYWY0OQ==; HC=b74e985b69a48da1cc9390d9ed5a2974b923a195; HD=MjAxNTA4MTY=; HY=MjAxNTA4MTY=2fb14cf4dfa9d154946334616083062b1cabf79030a1546d70073d1459e37145aac5d44934bcb978df0d6945585447a9903919817dc023d67afd6f38; HO=TWpBeE5UQTRNVFk9MTNNakV4TVRZeDI3VFc5NmFXeHNZUzgxTGpBZ0tFeHBiblY0T3lCQmJtUnliMmxrSURVdU1Ec2dVMDB0VGpreE1EQWdRblZwYkdRdlRGSllNakZXS1NCQmNIQnNaVmRsWWt0cGRDODFNemN1TXpZZ0tFdElWRTFNTENCc2FXdGxJRWRsWTJ0dktTQldaWEp6YVc5dUx6UXVNQ0JEYUhKdmJXVXZNemN1TUM0d0xqQWdUVzlpYVd4bElGTmhabUZ5YVM4MU16Y3VNellnVFdsamNtOU5aWE56Wlc1blpYSXZOaTR3TGpJdU5UWmZjamsxT0Rnd01DNDFNakFnVG1WMFZIbHdaUzlYU1VaSmRmMGQ2OTQ1NTg1NDQ3YTk5MDM5MTk4MTdkYzAyM2Q2N2FmZDZmMzg=; Hm_lvt_6f69830ae7173059e935b61372431b35=1439702836; Hm_lpvt_6f69830ae7173059e935b61372431b35=1439702870; _ga=GA1.2.585713480.1433042162; _gali=check");
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
                request.addHeader("Cookie", "Hm_p1vt_6f69830ae7173059e935b61372431b35=KnmCelXQG78dbwlaAyo1Ag==; _gscu_1404343399=36792790evxq6v41; _gscs_1404343399=397019526bwjdj20|pv:4; _gscbrs_1404343399=1; Hm_lvt_ba907373475281ec79b64ad73e7c9a36=1439701953; Hm_lpvt_ba907373475281ec79b64ad73e7c9a36=1439702471; Hm_1vt_6f69830ae7173059e935b61372431b35=KnmCelXQHzMeoAlfAyzdAg==; _gat=1; HH=637a61ebc0d2a7778465aa69c975ab1ce50d57ec; HK=2fb14cf4dfa9d154946334616083062b1cabf790; HG=30a1546d70073d1459e37145aac5d44934bcb978; HA=cf43e9cf10677c251ed1c6a85d17e98bde1eaf49; HB=Y2Y0M2U5Y2YxMDY3N2MyNTFlZDFjNmE4NWQxN2U5OGJkZTFlYWY0OQ==; HC=b74e985b69a48da1cc9390d9ed5a2974b923a195; HD=MjAxNTA4MTY=; HY=MjAxNTA4MTY=2fb14cf4dfa9d154946334616083062b1cabf79030a1546d70073d1459e37145aac5d44934bcb978df0d6945585447a9903919817dc023d67afd6f38; HO=TWpBeE5UQTRNVFk9MTNNakV4TVRZeDI3VFc5NmFXeHNZUzgxTGpBZ0tFeHBiblY0T3lCQmJtUnliMmxrSURVdU1Ec2dVMDB0VGpreE1EQWdRblZwYkdRdlRGSllNakZXS1NCQmNIQnNaVmRsWWt0cGRDODFNemN1TXpZZ0tFdElWRTFNTENCc2FXdGxJRWRsWTJ0dktTQldaWEp6YVc5dUx6UXVNQ0JEYUhKdmJXVXZNemN1TUM0d0xqQWdUVzlpYVd4bElGTmhabUZ5YVM4MU16Y3VNellnVFdsamNtOU5aWE56Wlc1blpYSXZOaTR3TGpJdU5UWmZjamsxT0Rnd01DNDFNakFnVG1WMFZIbHdaUzlYU1VaSmRmMGQ2OTQ1NTg1NDQ3YTk5MDM5MTk4MTdkYzAyM2Q2N2FmZDZmMzg=; Hm_lvt_6f69830ae7173059e935b61372431b35=1439702836; Hm_lpvt_6f69830ae7173059e935b61372431b35=1439702870; _ga=GA1.2.585713480.1433042162; _gali=check");
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
