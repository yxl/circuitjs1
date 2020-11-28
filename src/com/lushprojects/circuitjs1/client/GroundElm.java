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

public class GroundElm extends CircuitElm {
    public GroundElm(int xx, int yy) {
        super(xx, yy);
    }

    public GroundElm(int xa, int ya, int xb, int yb, int f,
                     StringTokenizer st) {
        super(xa, ya, xb, yb, f);
    }

    @Override
    public int getDumpType() {
        return 'g';
    }

    @Override
    public int getPostCount() {
        return 1;
    }

    @Override
    public void draw(Graphics g) {
        setVoltageColor(g, 0);
        drawThickLine(g, point1, point2);
        int i;
        for (i = 0; i != 3; i++) {
            int a = 10 - i * 4;
            int b = i * 5; // -10;
            interpPoint2(point1, point2, ps1, ps2, 1 + b / dn, a);
            drawThickLine(g, ps1, ps2);
        }
        doDots(g);
        interpPoint(point1, point2, ps2, 1 + 11. / dn);
        setBbox(point1, ps2, 11);
        drawPosts(g);
    }

    @Override
    public void setCurrent(int x, double c) {
        current = -c;
    }

    @Override
    public void stamp() {
        sim.stampVoltageSource(0, nodes[0], voltSource, 0);
    }

    @Override
    public double getVoltageDiff() {
        return 0;
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public void getInfo(String[] arr) {
        arr[0] = "ground";
        arr[1] = "I = " + getCurrentText(getCurrent());
    }

    @Override
    public boolean hasGroundConnection(int n1) {
        return true;
    }

    @Override
    public int getShortcut() {
        return 'g';
    }

    @Override
    public double getCurrentIntoNode(int n) {
        return -current;
    }
}
