package ryancheng.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ryancheng.bus.event.FavoriteLineEvent;
import ryancheng.bus.model.Bus;
import ryancheng.bus.retrofit.APIManager;
import ryancheng.bus.util.Util;


public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.til_search_bus)
    TextInputLayout textInputLayout;
    @InjectView(R.id.et_search_bus)
    AutoCompleteTextView searchEditText;
    @InjectView(R.id.ib_search_bus)
    ImageButton searchBtn;
    @InjectView(R.id.rv_my_favorites)
    RecyclerView myFavList;
    @InjectView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @InjectView(R.id.tv_empty)
    TextView emptyText;

    @OnClick(R.id.ib_search_bus)
    void searchBus() {
        String name = searchEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            textInputLayout.setError(getString(R.string.empty_search));
            textInputLayout.setErrorEnabled(true);
        } else {
            textInputLayout.setErrorEnabled(false);
            rxSid(name);
        }
    }

    private void rxSid(final String name) {
        Observable<Bus> busObservable = APIManager.getInstance().getSid(name);
        busObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bus>() {
                    @Override
                    public void call(Bus bus) {
                        if (bus == null) {
                            textInputLayout.setError(getString(R.string.line_error));
                            textInputLayout.setErrorEnabled(true);
                        } else if (!TextUtils.isEmpty(bus.mes)) {
                            textInputLayout.setError(bus.mes);
                            textInputLayout.setErrorEnabled(true);
                        } else {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            intent.putExtra(DetailActivity.EXTRA_SID, bus.sid);
                            intent.putExtra(DetailActivity.EXTRA_NAME, name);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        searchBtn.setImageDrawable(Util.getDrawableTint(this, R.drawable.ic_action_search));
        textInputLayout.setHint(getString(R.string.search_bus_hint));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Constant.BUS);
        searchEditText.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(FavoriteLineEvent event) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
