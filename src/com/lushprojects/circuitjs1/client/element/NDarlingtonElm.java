package com.lushprojects.circuitjs1.client.element;

public class NDarlingtonElm extends DarlingtonElm {


    public NDarlingtonElm(int xx, int yy) {
        super(xx, yy, false);
    }


    @Override
    public Class<?> getDumpClass() {
        return DarlingtonElm.class;
    }
}
