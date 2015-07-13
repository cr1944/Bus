package ryancheng.bus;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ryancheng.bus.adapter.DetailAdapter;
import ryancheng.bus.event.GetStationDetailEvent;
import ryancheng.bus.model.Line;
import ryancheng.bus.model.Station;
import ryancheng.bus.retrofit.APIManager;

public class DetailActivity extends AppCompatActivity {
    static final String TAG = "DetailActivity";
    public static final String EXTRA_SID = "sid";
    public static final String EXTRA_NAME = "name";

    @InjectView(R.id.rv_line_detail)
    RecyclerView detailView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.toolbarlayout)
    CollapsingToolbarLayout toolbarLayout;
    @InjectView(R.id.line_info)
    TextView lineInfoView;
    private DetailAdapter detailAdapter;
    private int stoptype;
    private String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        sid = intent.getStringExtra(EXTRA_SID);
        String title = intent.getStringExtra(EXTRA_NAME);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailView.setLayoutManager(linearLayoutManager);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbarLayout.setTitle(title);
        setSupportActionBar(toolbar);
        rxStations();
    }

    private void rxStations() {
        Observable<List<Station>> listObservable = APIManager.getInstance().getStations(sid, stoptype);
        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Station>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, R.string.line_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Station> stations) {
                        if (stations == null) {
                            Toast.makeText(DetailActivity.this, R.string.line_error, Toast.LENGTH_SHORT).show();
                        } else {
                            detailAdapter = new DetailAdapter(DetailActivity.this, stations);
                            detailView.setAdapter(detailAdapter);
                            lineInfoView.setText(stations.get(0).name + "->"
                                    + stations.get(stations.size() - 1).name);
                        }
                    }
                });
    }

    public void onEvent(GetStationDetailEvent event) {
        rxStationDetail(event.station.stopid);
    }

    private void rxStationDetail(final int stopid) {
        Observable<List<Line>> listObservable = APIManager.getInstance()
                .getStationDetail(stoptype, stopid, sid);
        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Line>>() {
                    @Override
                    public void call(List<Line> lines) {
                        if (lines != null) {
                            detailAdapter.updateItem(stopid - 1, lines);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "error:" + throwable);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
