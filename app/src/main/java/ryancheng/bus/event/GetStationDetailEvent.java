package ryancheng.bus.event;

import ryancheng.bus.model.Station;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class GetStationDetailEvent {
    public Station station;

    public GetStationDetailEvent(Station station) {
        this.station = station;
    }
}
