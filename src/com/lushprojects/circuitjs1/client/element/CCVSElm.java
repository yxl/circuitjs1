/*    
    Copyright (C) Paul Falstad
    
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

import com.lushprojects.circuitjs1.client.ui.EditInfo;
import com.lushprojects.circuitjs1.client.util.ExprState;
import com.lushprojects.circuitjs1.client.util.StringTokenizer;

public class CCVSElm extends VCCSElm {
    double lastCurrent;

    public CCVSElm(int xa, int ya, int xb, int yb, int f,
                   StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
//	    exprString = CustomLogicModel.unescape(st.nextToken());
        inputCount = 2;
//	    parseExpr();
        setupPins();
    }

    public CCVSElm(int xx, int yy) {
        super(xx, yy);
        exprString = "2*i";
        parseExpr();
//	    setupPins();
    }

    @Override
    public void setupPins() {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[3];
        pins[0] = new Pin(0, SIDE_W, "C+");
        pins[1] = new Pin(1, SIDE_W, "C-");
        pins[1].output = true;
        pins[2] = new Pin(0, SIDE_E, "V+");
        pins[2].output = true;
        pins[3] = new Pin(1, SIDE_E, "V-");
        exprState = new ExprState(1);
    }

    @Override
    public String getChipName() {
        return "CCVS";
    }

    @Override
    public void stamp() {
        // voltage source (0V) between C+ and C- so we can measure current
        int vn1 = pins[1].voltSource;
        sim.stampVoltageSource(nodes[0], nodes[1], vn1, 0);

        // voltage source for outputs
        int vn2 = pins[2].voltSource;
        sim.stampNonLinear(vn2 + sim.nodeList.size());
        sim.stampVoltageSource(nodes[3], nodes[2], vn2);
    }

    @Override
    public void doStep() {
        // converged yet?
//            double limitStep = getLimitStep()*.1;
        double convergeLimit = getConvergeLimit() * .1;

        double cur = pins[1].current;
        if (Math.abs(cur - lastCurrent) > convergeLimit) {
            sim.converged = false;
//        	if (Math.abs(cur-lastCurrent) > limitStep)
//        	    volts[i] = lastVolts[i] + sign(volts[i]-lastVolts[i], limitStep);
        }
        int vn1 = pins[1].voltSource + sim.nodeList.size();
        int vn2 = pins[2].voltSource + sim.nodeList.size();
        if (expr != null) {
            // calculate output
            exprState.values[8] = cur;  // I = current
            exprState.t = sim.t;
            double rs = expr.eval(exprState);

            double dv = 1e-6;
            exprState.values[8] = cur + dv;
            double v = expr.eval(exprState);
            exprState.values[8] = cur - dv;
            double v2 = expr.eval(exprState);
            double dx = (v - v2) / (dv * 2);
            if (Math.abs(dx) < 1e-6)
                dx = sign(dx, 1e-6);
            sim.stampMatrix(vn2, vn1, -dx);
            // adjust right side
            rs -= dx * cur;
            //                	sim.console("ccedx " + cur + " " + dx + " " + rs);
            sim.stampRightSide(vn2, rs);
        }

        lastCurrent = cur;
    }

    @Override
    public int getPostCount() {
        return 4;
    }

    @Override
    public int getVoltageSourceCount() {
        return 2;
    }

    @Override
    public int getDumpType() {
        return 214;
    }

    @Override
    public boolean getConnection(int n1, int n2) {
        if (comparePair(0, 1, n1, n2))
            return true;
        return comparePair(2, 3, n1, n2);
    }

    @Override
    public boolean hasCurrentOutput() {
        return false;
    }

    @Override
    public void setCurrent(int vn, double c) {
        if (pins[1].voltSource == vn) {
            pins[0].current = -c;
            pins[1].current = c;
        }
        if (pins[2].voltSource == vn) {
            pins[2].current = c;
            pins[3].current = -c;
        }
    }

    @Override
    public EditInfo getEditInfo(int n) {
        // can't set number of inputs
        if (n == 1)
            return null;
        return super.getEditInfo(n);
    }
}

