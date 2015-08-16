package ryancheng.bus;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Bind(R.id.rv_line_detail)
    RecyclerView detailView;
    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;
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
        setTitle(title);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailView.setLayoutManager(linearLayoutManager);
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
                        progressWheel.setVisibility(View.GONE);
                        showErrorDialog(R.string.line_error);
                    }

                    @Override
                    public void onNext(List<Station> stations) {
                        progressWheel.setVisibility(View.GONE);
                        if (stations == null) {
                            showErrorDialog(R.string.line_error);
                        } else {
                            List<ParentObject> data = new ArrayList<>();
                            data.addAll(stations);
                            detailAdapter = new DetailAdapter(DetailActivity.this, data);
                            detailAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
                            detailAdapter.setParentClickableViewAnimationDefaultDuration();
                            detailAdapter.setParentAndIconExpandOnClick(true);
                            detailView.setAdapter(detailAdapter);
                        }
                    }
                });
    }

    private void showErrorDialog(int message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.line_error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

    public void onEvent(GetStationDetailEvent event) {
        rxStationDetail(event.position, event.station);
    }

    private void rxStationDetail(final int position, final Station station) {
        Observable<List<Line>> listObservable = APIManager.getInstance()
                .getStationDetail(stoptype, station.stopid, sid);
        listObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Line>>() {
                    @Override
                    public void call(List<Line> lines) {
                        if (lines != null) {
                            detailAdapter.updateItem(position, station, lines);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
