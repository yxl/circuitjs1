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

// contributed by Edward Calver

import com.lushprojects.circuitjs1.client.ui.EditInfo;
import com.lushprojects.circuitjs1.client.ui.canvas.Graphics;
import com.lushprojects.circuitjs1.client.ui.canvas.Point;
import com.lushprojects.circuitjs1.client.ui.canvas.Polygon;
import com.lushprojects.circuitjs1.client.util.StringTokenizer;

public class InvertingSchmittElm extends CircuitElm {
    double slewRate; // V/ns
    double lowerTrigger;
    double upperTrigger;
    boolean state;
    double logicOnLevel;
    double logicOffLevel;
    Polygon gatePoly;
    Polygon symbolPoly;
    Point pcircle;
    double dlt;
    double dut;

    public InvertingSchmittElm(int xx, int yy) {
        super(xx, yy);
        noDiagonal = true;
        slewRate = .5;
        state = false;
        lowerTrigger = 1.66;
        upperTrigger = 3.33;
        logicOnLevel = 5;
        logicOffLevel = 0;
    }
    public InvertingSchmittElm(int xa, int ya, int xb, int yb, int f,
                               StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        noDiagonal = true;
        slewRate = .5;
        lowerTrigger = 1.66;
        upperTrigger = 3.33;
        logicOnLevel = 5;
        logicOffLevel = 0;
        try {
            slewRate = new Double(st.nextToken()).doubleValue();
            lowerTrigger = new Double(st.nextToken()).doubleValue();
            upperTrigger = new Double(st.nextToken()).doubleValue();
            logicOnLevel = new Double(st.nextToken()).doubleValue();
            logicOffLevel = new Double(st.nextToken()).doubleValue();
        } catch (Exception e) {
        }
    }

    @Override
    public String dump() {
        return super.dump() + " " + slewRate + " " + lowerTrigger + " " + upperTrigger + " " + logicOnLevel + " " + logicOffLevel;
    }

    @Override
    public int getDumpType() {
        return 183;
    }//Trying to find unused type

    @Override
    public void draw(Graphics g) {
        drawPosts(g);
        draw2Leads(g);
        g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        drawThickPolygon(g, gatePoly);
        g.setLineWidth(2);
        drawPolygon(g, symbolPoly);
        g.setLineWidth(1);
        drawThickCircle(g, pcircle.x, pcircle.y, 3);
        curcount = updateDotCount(current, curcount);
        drawDots(g, lead2, point2, curcount);
    }

    @Override
    public void setPoints() {
        super.setPoints();
        int hs = 16;
        int ww = 16;
        if (ww > dn / 2)
            ww = (int) (dn / 2);
        lead1 = interpPoint(point1, point2, .5 - ww / dn);
        lead2 = interpPoint(point1, point2, .5 + (ww + 2) / dn);
        pcircle = interpPoint(point1, point2, .5 + (ww - 2) / dn);
        Point[] triPoints = newPointArray(3);
        interpPoint2(lead1, lead2, triPoints[0], triPoints[1], 0, hs);
        triPoints[2] = interpPoint(point1, point2, .5 + (ww - 5) / dn);

        gatePoly = createPolygon(triPoints);
        symbolPoly = getSchmittPolygon(1, .3f);
        setBbox(point1, point2, hs);
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public void stamp() {
        sim.stampVoltageSource(0, nodes[1], voltSource);
    }

    @Override
    public void doStep() {
        double v0 = volts[1];
        double out;
        if (state) {//Output is high
            if (volts[0] > upperTrigger)//Input voltage high enough to set output low
            {
                state = false;
                out = logicOffLevel;
            } else {
                out = logicOnLevel;
            }
        } else {//Output is low
            if (volts[0] < lowerTrigger)//Input voltage low enough to set output high
            {
                state = true;
                out = logicOnLevel;
            } else {
                out = logicOffLevel;
            }
        }

        double maxStep = slewRate * sim.timeStep * 1e9;
        out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
        sim.updateVoltageSource(0, nodes[1], voltSource, out);
    }

    @Override
    public double getVoltageDiff() {
        return volts[0];
    }

    @Override
    public void getInfo(String[] arr) {
        arr[0] = "inverting Schmitt trigger";
        arr[1] = "Vi = " + getVoltageText(volts[0]);
        arr[2] = "Vo = " + getVoltageText(volts[1]);
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            dlt = lowerTrigger;
            return new EditInfo("Lower threshold (V)", lowerTrigger, 0.01, 5);
        }
        if (n == 1) {
            dut = upperTrigger;
            return new EditInfo("Upper threshold (V)", upperTrigger, 0.01, 5);
        }
        if (n == 2)
            return new EditInfo("Slew Rate (V/ns)", slewRate, 0, 0);
        if (n == 3)
            return new EditInfo("High Voltage (V)", logicOnLevel, 0, 0);
        if (n == 4)
            return new EditInfo("Low Voltage (V)", logicOffLevel, 0, 0);

        return null;
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 0)
            dlt = ei.value;
        if (n == 1)
            dut = ei.value;
        if (n == 2)
            slewRate = ei.value;
        if (n == 3)
            logicOnLevel = ei.value;
        if (n == 4)
            logicOffLevel = ei.value;


        if (dlt > dut) {
            upperTrigger = dlt;
            lowerTrigger = dut;
        } else {
            upperTrigger = dut;
            lowerTrigger = dlt;
        }

    }

    // there is no current path through the InvertingSchmitt input, but there
    // is an indirect path through the output to ground.
    @Override
    public boolean getConnection(int n1, int n2) {
        return false;
    }

    @Override
    public boolean hasGroundConnection(int n1) {
        return (n1 == 1);
    }

    @Override
    public double getCurrentIntoNode(int n) {
        if (n == 1)
            return current;
        return 0;
    }

}
