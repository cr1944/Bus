package ryancheng.bus.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Administrator
 * 2015/5/31 0031.
 */
public class Station implements ParentObject {
    public int stopid;
    public String name;
    public List<Object> lines;

    public Station(int stopid, String name) {
        this.stopid = stopid;
        this.name = name;
    }

    @Override
    public List<Object> getChildObjectList() {
        return lines;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        lines = list;
    }
}
