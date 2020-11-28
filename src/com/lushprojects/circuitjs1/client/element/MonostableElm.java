/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client.element;

import com.lushprojects.circuitjs1.client.ui.Checkbox;
import com.lushprojects.circuitjs1.client.ui.EditInfo;
import com.lushprojects.circuitjs1.client.util.StringTokenizer;

public class MonostableElm extends ChipElm {

    //Used to detect rising edge
    private boolean prevInputValue = false;
    private boolean retriggerable = false;
    private boolean triggered = false;
    private double lastRisingEdge = 0;
    private double delay = 0.01;

    public MonostableElm(int xx, int yy) {
        super(xx, yy);
    }

    public MonostableElm(int xa, int ya, int xb, int yb, int f,
                         StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        retriggerable = new Boolean(st.nextToken()).booleanValue();
        delay = new Double(st.nextToken()).doubleValue();
    }

    @Override
    public String getChipName() {
        return "Monostable";
    }

    @Override
    public void setupPins() {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_W, "");
        pins[0].clock = true;
        pins[1] = new Pin(0, SIDE_E, "Q");
        pins[1].output = true;
        pins[2] = new Pin(1, SIDE_E, "Q");
        pins[2].output = true;
        pins[2].lineOver = true;
    }

    @Override
    public int getPostCount() {
        return 3;
    }

    @Override
    public int getVoltageSourceCount() {
        return 2;
    }

    @Override
    public void execute() {

        if (pins[0].value && prevInputValue != pins[0].value && (retriggerable || !triggered)) {
            lastRisingEdge = sim.t;
            pins[1].value = true;
            pins[2].value = false;
            triggered = true;
        }

        if (triggered && sim.t > lastRisingEdge + delay) {
            pins[1].value = false;
            pins[2].value = true;
            triggered = false;
        }
        prevInputValue = pins[0].value;
    }

    @Override
    public String dump() {
        return super.dump() + " " + retriggerable + " " + delay;
    }

    @Override
    public int getDumpType() {
        return 194;
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Retriggerable", retriggerable);
            return ei;
        }
        if (n == 3) {
            return new EditInfo("Period (s)", delay, 0.001, 0.1);
        }
        return super.getEditInfo(n);
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            retriggerable = ei.checkbox.getState();
        }
        if (n == 3) {
            delay = ei.value;
        }
        super.setEditValue(n, ei);
    }
}
