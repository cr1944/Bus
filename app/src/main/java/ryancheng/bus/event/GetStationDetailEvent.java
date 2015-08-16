package ryancheng.bus.event;

import ryancheng.bus.model.Station;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class GetStationDetailEvent {
    public Station station;
    public int position;

    public GetStationDetailEvent(int position, Station station) {
        this.position = position;
        this.station = station;
    }
}
