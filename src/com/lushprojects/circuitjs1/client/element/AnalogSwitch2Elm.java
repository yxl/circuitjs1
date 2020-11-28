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

import com.lushprojects.circuitjs1.client.ui.canvas.Graphics;
import com.lushprojects.circuitjs1.client.ui.canvas.Point;
import com.lushprojects.circuitjs1.client.util.StringTokenizer;

public class AnalogSwitch2Elm extends AnalogSwitchElm {
    public AnalogSwitch2Elm(int xx, int yy) {
        super(xx, yy);
    }

    public AnalogSwitch2Elm(int xa, int ya, int xb, int yb, int f,
                            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    final int openhs = 16;
    Point[] swposts;
    Point[] swpoles;
    Point ctlPoint;

    @Override
    public void setPoints() {
        super.setPoints();
        calcLeads(32);
        swposts = newPointArray(2);
        swpoles = newPointArray(2);
        interpPoint2(lead1, lead2, swpoles[0], swpoles[1], 1, openhs);
        interpPoint2(point1, point2, swposts[0], swposts[1], 1, openhs);
        ctlPoint = interpPoint(point1, point2, .5, openhs);
    }

    @Override
    public int getPostCount() {
        return 4;
    }

    @Override
    public void draw(Graphics g) {
        setBbox(point1, point2, openhs);

        // draw first lead
        setVoltageColor(g, volts[0]);
        CircuitElm.drawThickLine(g, point1, lead1);

        // draw second lead
        setVoltageColor(g, volts[1]);
        CircuitElm.drawThickLine(g, swpoles[0], swposts[0]);

        // draw third lead
        setVoltageColor(g, volts[2]);
        CircuitElm.drawThickLine(g, swpoles[1], swposts[1]);

        // draw switch
        g.setColor(CircuitElm.lightGrayColor);
        int position = (open) ? 1 : 0;
        CircuitElm.drawThickLine(g, lead1, swpoles[position]);

        updateDotCount();
        drawDots(g, point1, lead1, curcount);
        drawDots(g, swpoles[position], swposts[position], curcount);
        drawPosts(g);
    }

    @Override
    public Point getPost(int n) {
        return (n == 0) ? point1 : (n == 3) ? ctlPoint : swposts[n - 1];
    }

    @Override
    public int getDumpType() {
        return 160;
    }

    @Override
    public void calculateCurrent() {
        if (open)
            current = (volts[0] - volts[2]) / r_on;
        else
            current = (volts[0] - volts[1]) / r_on;
    }

    @Override
    public void stamp() {
        CircuitElm.sim.stampNonLinear(nodes[0]);
        CircuitElm.sim.stampNonLinear(nodes[1]);
        CircuitElm.sim.stampNonLinear(nodes[2]);
    }

    @Override
    public void doStep() {
        open = (volts[3] < 2.5);
        if ((flags & FLAG_INVERT) != 0)
            open = !open;
        if (open) {
            CircuitElm.sim.stampResistor(nodes[0], nodes[2], r_on);
            CircuitElm.sim.stampResistor(nodes[0], nodes[1], r_off);
        } else {
            CircuitElm.sim.stampResistor(nodes[0], nodes[1], r_on);
            CircuitElm.sim.stampResistor(nodes[0], nodes[2], r_off);
        }
    }

    @Override
    public boolean getConnection(int n1, int n2) {
        return n1 != 3 && n2 != 3;
    }

    @Override
    public void getInfo(String[] arr) {
        arr[0] = "analog switch (SPDT)";
        arr[1] = "I = " + CircuitElm.getCurrentDText(getCurrent());
    }

    @Override
    public double getCurrentIntoNode(int n) {
        if (n == 0)
            return -current;
        int position = (open) ? 1 : 0;
        if (n == position + 1)
            return current;
        return 0;
    }
}

