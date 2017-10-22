package org.macho.beforeandafter.graphe;

import java.util.List;
import java.util.Map;

/**
 * Created by yuukimatsushima on 2017/10/04.
 */

public class LineGrapheData {
    private long from;
    private long to;
    private List<Map<Long, Float>> dateValueMaps;
    private String unit;

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public List<Map<Long, Float>> getDateValueMaps() {
        return dateValueMaps;
    }

    public void setDateValueMaps(List<Map<Long, Float>> dateValueMaps) {
        this.dateValueMaps = dateValueMaps;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
