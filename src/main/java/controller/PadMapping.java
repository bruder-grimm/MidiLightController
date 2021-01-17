package controller;

import util.Tuple;

import java.util.HashMap;
import java.util.Map;

public class PadMapping {
    private static final Map<Tuple<Integer, Integer>, Integer> pads = new HashMap<>();
    static {
        pads.put(Tuple.apply(0,0), 40);
        pads.put(Tuple.apply(0,1), 41);
        pads.put(Tuple.apply(0,2), 42);
        pads.put(Tuple.apply(0,3), 43);
        pads.put(Tuple.apply(0,4), 48);
        pads.put(Tuple.apply(0,5), 49);
        pads.put(Tuple.apply(0,6), 50);

        pads.put(Tuple.apply(0,7), 51);
        pads.put(Tuple.apply(1,0), 36);
        pads.put(Tuple.apply(1,1), 37);
        pads.put(Tuple.apply(1,2), 38);
        pads.put(Tuple.apply(1,3), 39);
        pads.put(Tuple.apply(1,4), 44);
        pads.put(Tuple.apply(1,5), 45);
        pads.put(Tuple.apply(1,6), 46);
        pads.put(Tuple.apply(1,7), 47);
    }

    public static Map<Tuple<Integer, Integer>, Integer> getPadMapping() {
        return pads;
    }
}
