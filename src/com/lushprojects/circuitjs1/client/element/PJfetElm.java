package com.lushprojects.circuitjs1.client.element;

public class PJfetElm extends JfetElm {
    public PJfetElm(int xx, int yy) {
        super(xx, yy, true);
    }

    @Override
    public Class<?> getDumpClass() {
        return JfetElm.class;
    }
}
