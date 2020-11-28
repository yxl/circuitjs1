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

package com.lushprojects.circuitjs1.client;

public class CC2Elm extends ChipElm {
    double gain;

    public CC2Elm(int xx, int yy) {
        super(xx, yy);
        gain = 1;
    }

    public CC2Elm(int xx, int yy, int g) {
        super(xx, yy);
        gain = g;
    }

    public CC2Elm(int xa, int ya, int xb, int yb, int f,
                  StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        gain = new Double(st.nextToken()).doubleValue();
    }

    @Override
    public String dump() {
        return super.dump() + " " + gain;
    }

    @Override
    public String getChipName() {
        return "CC2";
    }

    @Override
    public void setupPins() {
        sizeX = 2;
        sizeY = 3;
        pins = new Pin[3];
        pins[0] = new Pin(0, SIDE_W, "X");
        pins[0].output = true;
        pins[1] = new Pin(2, SIDE_W, "Y");
        pins[2] = new Pin(1, SIDE_E, "Z");
    }

    @Override
    public void getInfo(String[] arr) {
        arr[0] = (gain == 1) ? "CCII+~" : "CCII-~"; // ~ is for localization
        arr[1] = "X,Y = " + getVoltageText(volts[0]);
        arr[2] = "Z = " + getVoltageText(volts[2]);
        arr[3] = "I = " + getCurrentText(pins[0].current);
    }

    //boolean nonLinear() { return true; }
    @Override
    public void stamp() {
        // X voltage = Y voltage
        sim.stampVoltageSource(0, nodes[0], pins[0].voltSource);
        sim.stampVCVS(0, nodes[1], 1, pins[0].voltSource);
        // Z current = gain * X current
        sim.stampCCCS(0, nodes[2], pins[0].voltSource, gain);
    }

    @Override
    public void draw(Graphics g) {
        pins[2].current = pins[0].current * gain;
        drawChip(g);
    }

    @Override
    public int getPostCount() {
        return 3;
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public int getDumpType() {
        return 179;
    }
}

