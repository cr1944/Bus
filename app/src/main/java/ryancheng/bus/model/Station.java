package ryancheng.bus.model;

import java.util.List;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class Station {
    public int stopid;
    public String name;
    public List<Line> lines;

    public Station(int stopid, String name) {
        this.stopid = stopid;
        this.name = name;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

}
