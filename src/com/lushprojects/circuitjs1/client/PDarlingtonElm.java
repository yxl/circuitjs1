package com.lushprojects.circuitjs1.client;


public class PDarlingtonElm extends DarlingtonElm {


    public PDarlingtonElm(int xx, int yy) {
        super(xx, yy, true);
    }


    @Override
    public Class<?> getDumpClass() {
        return DarlingtonElm.class;
    }
}

