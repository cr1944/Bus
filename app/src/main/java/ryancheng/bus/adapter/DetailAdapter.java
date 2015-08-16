package ryancheng.bus.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import ryancheng.bus.R;
import ryancheng.bus.event.GetStationDetailEvent;
import ryancheng.bus.model.Line;
import ryancheng.bus.model.Station;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class DetailAdapter extends ExpandableRecyclerAdapter<DetailAdapter.StationViewHolder,
        DetailAdapter.DetailViewHolder> {
    private Context context;

    public DetailAdapter(Context context, List<ParentObject> data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public StationViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.layout_detail_item_parent, viewGroup, false);
        return new StationViewHolder(rootView);
    }

    @Override
    public DetailViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.layout_detail_item_child, viewGroup, false);
        return new DetailViewHolder(rootView);
    }

    @Override
    public void onBindParentViewHolder(StationViewHolder stationViewHolder, int i, Object o) {
        stationViewHolder.station = (Station) o;
        stationViewHolder.mTitleTextView.setText(stationViewHolder.station.name);
    }

    @Override
    public void onBindChildViewHolder(DetailViewHolder detailViewHolder, int i, Object o) {
        Line line = (Line) o;
        String time;
        if (TextUtils.isDigitsOnly(line.time)) {
            time = String.valueOf(Integer.valueOf(line.time) / 60);
        } else {
            time = line.time;
        }
        String s = context.getString(R.string.line_detail, line.terminal, line.stopdis, time);
        detailViewHolder.mTextView.setText(s);
    }

    @Override
    public void onParentItemClickListener(int position) {
        if(mItemList.get(position) instanceof Station) {
            Station station = (Station)mItemList.get(position);
            EventBus.getDefault().post(new GetStationDetailEvent(position, station));
        }
    }

    public void updateItem(int position, Station station, List<Line> lines) {
        List<Object> data = new ArrayList<>();
        data.addAll(lines);
        station.setChildObjectList(data);
        super.onParentItemClickListener(position);
    }

    public static class StationViewHolder extends ParentViewHolder {
        @Bind(R.id.parent_list_item_title_text_view)
        TextView mTitleTextView;
        Station station;

        public StationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class DetailViewHolder extends ChildViewHolder {
        @Bind(R.id.child_list_item_text_view)
        TextView mTextView;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
