package com.lushprojects.circuitjs1.client.ui;

import com.lushprojects.circuitjs1.client.element.ChipElm;

public class ExtListEntry {
    public String name;
    public int node, pos, side;

    public ExtListEntry(String s, int n) {
        name = s;
        node = n;
        side = ChipElm.SIDE_W;
    }
    public ExtListEntry(String s, int n, int p, int sd) {
        name = s;
        node = n;
        pos = p;
        side = sd;
    }
}
