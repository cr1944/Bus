package ryancheng.bus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import ryancheng.bus.R;
import ryancheng.bus.event.GetStationDetailEvent;
import ryancheng.bus.model.Line;
import ryancheng.bus.model.Station;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.StationVH> {
    private List<Station> data;
    private Context context;

    public DetailAdapter(Context context, List<Station> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public StationVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.layout_station, parent, false);
        return new StationVH(rootView);
    }

    @Override
    public void onBindViewHolder(StationVH holder, int position) {
        holder.station = data.get(position);
        holder.stationNameView.setText(holder.station.name);
        if (holder.station.lines != null) {
            holder.stationDetailView.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (Line line : holder.station.lines) {
                String time;
                if (TextUtils.isDigitsOnly(line.time)) {
                    time = String.valueOf(Integer.valueOf(line.time) / 60);
                } else {
                    time = line.time;
                }
                String s = context.getString(R.string.line_detail, line.terminal, line.stopdis, time);
                sb.append(s).append('\n');
            }
            holder.stationDetailView.setText(sb.toString());
        } else {
            holder.stationDetailView.setVisibility(View.GONE);
            holder.stationDetailView.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateItem(int position, List<Line> lines) {
        data.get(position).lines = lines;
        notifyItemChanged(position);
    }

    static class StationVH extends RecyclerView.ViewHolder {
        Station station;
        @InjectView(R.id.tv_station_name)
        TextView stationNameView;
        @InjectView(R.id.tv_station_detail)
        TextView stationDetailView;

        @OnClick(R.id.rl_station)
        void viewStation() {
            if (stationDetailView.getVisibility() == View.VISIBLE) {
                stationDetailView.setVisibility(View.GONE);
            } else {
                EventBus.getDefault().post(new GetStationDetailEvent(station));
            }
        }

        public StationVH(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
