package ryancheng.bus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ryancheng.bus.model.Bus;
import ryancheng.bus.retrofit.APIManager;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @Bind(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator)
    StringSectionTitleIndicator sectionTitleIndicator;
    MainAdapter adapter;
    private List<Item> allData;

    private void rxSid(final String name) {
        Observable<Bus> busObservable = APIManager.getInstance().getSid(name);
        busObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bus>() {
                    @Override
                    public void call(Bus bus) {
                        if (bus == null) {
                            Toast.makeText(MainActivity.this, R.string.line_error, Toast.LENGTH_SHORT).show();
                        } else if (!TextUtils.isEmpty(bus.mes)) {
                            Toast.makeText(MainActivity.this, bus.mes, Toast.LENGTH_SHORT).show();
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
        ButterKnife.bind(this);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(sectionTitleIndicator);
        initData();
        adapter = new MainAdapter();
        adapter.reset();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressWheel.setVisibility(View.GONE);
    }

    private void initData() {
        allData = new ArrayList<>();
        for (String name : Constant.BUS) {
            int marked = getMarked(name);
            allData.add(new Item(name, marked));
        }
    }

    private int getMarked(String name) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getInt(name, 0);
    }

    private void setMarked(String name, int marked) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putInt(name, marked).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final MenuItem settings = menu.findItem(R.id.action_settings);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                settings.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                settings.setVisible(true);
                return true;
            }
        });
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

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            adapter.reset();
        } else {
            adapter.setFilter(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    static class Item implements Comparable<Item> {
        String name;
        int marked;

        public Item(String name, int marked) {
            this.name = name;
            this.marked = marked;
        }

        @Override
        public int compareTo(@NonNull Item another) {
            if (marked == another.marked) {
                return name.compareTo(another.name);
            }
            return marked > another.marked ? -1 : 1;
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<BusViewHolder> implements SectionIndexer {
        private List<Item> data;

        @Override
        public BusViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.layout_main_item, viewGroup, false);
            return new BusViewHolder(v, MainActivity.this);
        }

        @Override
        public void onBindViewHolder(BusViewHolder viewHolder, int i) {
            viewHolder.item = data.get(i);
            viewHolder.textView.setText(viewHolder.item.name);
            viewHolder.imageView.setImageResource(viewHolder.item.marked != 0 ?
                    R.drawable.ic_action_favorite : R.drawable.ic_action_favorite_outline);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public Object[] getSections() {
            return data.toArray();
        }


        @Override
        public int getPositionForSection(int sectionIndex) {
            return 0;
        }


        @Override
        public int getSectionForPosition(int position) {
            return position;
        }

        public void reOrder() {
            Collections.sort(data);
            notifyDataSetChanged();
        }

        public void reset(){
            data = new ArrayList<>();
            data.addAll(allData);
            Collections.sort(data);
            notifyDataSetChanged();
        }

        public void setFilter(String queryText) {
            data = new ArrayList<>();
            for (Item item: allData) {
                if (item.name.contains(queryText))
                    data.add(item);
            }
            Collections.sort(data);
            notifyDataSetChanged();
        }
    }

    static class BusViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView textView;
        @Bind(R.id.mark)
        ImageView imageView;
        Item item;

        public BusViewHolder(View itemView, final MainActivity activity) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.rxSid(item.name);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.marked == 0) {
                        item.marked = 1;
                        Toast.makeText(activity, activity.getString(R.string.add_favorite, item.name), Toast.LENGTH_SHORT).show();
                    } else {
                        item.marked = 0;
                        Toast.makeText(activity, activity.getString(R.string.remove_favorite, item.name), Toast.LENGTH_SHORT).show();
                    }
                    activity.setMarked(item.name, item.marked);
                    activity.adapter.reOrder();
                }
            });
        }
    }
}
