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

import com.lushprojects.circuitjs1.client.CustomLogicModel;
import com.lushprojects.circuitjs1.client.ui.EditInfo;
import com.lushprojects.circuitjs1.client.ui.canvas.Graphics;
import com.lushprojects.circuitjs1.client.util.Expr;
import com.lushprojects.circuitjs1.client.util.ExprParser;
import com.lushprojects.circuitjs1.client.util.ExprState;
import com.lushprojects.circuitjs1.client.util.StringTokenizer;

public class VCCSElm extends ChipElm {
    public boolean broken;
    double gain;
    int inputCount;
    Expr expr;
    ExprState exprState;
    String exprString;
    double[] lastVolts;
    double lastvd;

    public VCCSElm(int xa, int ya, int xb, int yb, int f,
                   StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        inputCount = Integer.parseInt(st.nextToken());
        exprString = CustomLogicModel.unescape(st.nextToken());
        parseExpr();
        setupPins();
    }

    public VCCSElm(int xx, int yy) {
        super(xx, yy);
        inputCount = 2;
        exprString = ".1*(a-b)";
        parseExpr();
        setupPins();
    }

    @Override
    public String dump() {
        return super.dump() + " " + inputCount + " " + CustomLogicModel.escape(exprString);
    }

    @Override
    public void setupPins() {
        sizeX = 2;
        sizeY = inputCount > 2 ? inputCount : 2;
        pins = new Pin[inputCount + 2];
        int i;
        for (i = 0; i != inputCount; i++)
            pins[i] = new Pin(i, SIDE_W, Character.toString((char) ('A' + i)));
        pins[inputCount] = new Pin(0, SIDE_E, "C+");
        pins[inputCount + 1] = new Pin(1, SIDE_E, "C-");
        lastVolts = new double[inputCount];
        exprState = new ExprState(inputCount);
    }

    @Override
    public String getChipName() {
        return "VCCS~";
    } // ~ is for localization

    @Override
    public boolean nonLinear() {
        return true;
    }

    @Override
    public void stamp() {
        sim.stampNonLinear(nodes[inputCount]);
        sim.stampNonLinear(nodes[inputCount + 1]);
    }

    double sign(double a, double b) {
        return a > 0 ? b : -b;
    }

    double getLimitStep() {
        // get limit on changes in voltage per step.  be more lenient the more iterations we do
        if (sim.subIterations < 4)
            return 10;
        if (sim.subIterations < 10)
            return 1;
        if (sim.subIterations < 20)
            return .1;
        if (sim.subIterations < 40)
            return .01;
        return .001;
    }

    double getConvergeLimit() {
        // get maximum change in voltage per step when testing for convergence.  be more lenient over time
        if (sim.subIterations < 10)
            return .001;
        if (sim.subIterations < 200)
            return .01;
        return .1;
    }

    public boolean hasCurrentOutput() {
        return true;
    }

    public int getOutputNode(int n) {
        return nodes[n + inputCount];
    }

    @Override
    public void doStep() {
        int i;

        // no current path?  give up
        if (broken) {
            pins[inputCount].current = 0;
            pins[inputCount + 1].current = 0;
            // avoid singular matrix errors
            sim.stampResistor(nodes[inputCount], nodes[inputCount + 1], 1e8);
            return;
        }

        // converged yet?
        double limitStep = getLimitStep();
        double convergeLimit = getConvergeLimit();
        for (i = 0; i != inputCount; i++) {
            if (Math.abs(volts[i] - lastVolts[i]) > convergeLimit)
                sim.converged = false;
            if (Double.isNaN(volts[i]))
                volts[i] = 0;
            if (Math.abs(volts[i] - lastVolts[i]) > limitStep)
                volts[i] = lastVolts[i] + sign(volts[i] - lastVolts[i], limitStep);
        }
        if (expr != null) {
            // calculate output
            for (i = 0; i != inputCount; i++)
                exprState.values[i] = volts[i];
            exprState.t = sim.t;
            double v0 = -expr.eval(exprState);
//        	if (Math.abs(volts[inputCount]-v0) > Math.abs(v0)*.01 && sim.subIterations < 100)
//        	    sim.converged = false;
            double rs = v0;

            // calculate and stamp output derivatives
            for (i = 0; i != inputCount; i++) {
                double dv = 1e-6;
                exprState.values[i] = volts[i] + dv;
                double v = -expr.eval(exprState);
                exprState.values[i] = volts[i] - dv;
                double v2 = -expr.eval(exprState);
                double dx = (v - v2) / (dv * 2);
                if (Math.abs(dx) < 1e-6)
                    dx = sign(dx, 1e-6);
                sim.stampVCCurrentSource(nodes[inputCount], nodes[inputCount + 1], nodes[i], 0, dx);
//            	sim.console("ccedx " + i + " " + dx);
                // adjust right side
                rs -= dx * volts[i];
                exprState.values[i] = volts[i];
            }
//        	sim.console("ccers " + rs);
            sim.stampCurrentSource(nodes[inputCount], nodes[inputCount + 1], rs);
            pins[inputCount].current = -v0;
            pins[inputCount + 1].current = v0;
        }

        for (i = 0; i != inputCount; i++)
            lastVolts[i] = volts[i];
    }

    @Override
    public void draw(Graphics g) {
        drawChip(g);
    }

    @Override
    public int getPostCount() {
        return inputCount + 2;
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public int getDumpType() {
        return 213;
    }

    @Override
    public boolean getConnection(int n1, int n2) {
        return comparePair(inputCount, inputCount + 1, n1, n2);
    }

    @Override
    public boolean hasGroundConnection(int n1) {
        return false;
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            EditInfo ei = new EditInfo(EditInfo.makeLink("customfunction.html", "Output Function"), 0, -1, -1);
            ei.text = exprString;
            ei.disallowSliders();
            return ei;
        }
        if (n == 1)
            return new EditInfo("# of Inputs", inputCount, 1, 8).
                    setDimensionless();
        return null;
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 0) {
            exprString = ei.textf.getText();
            parseExpr();
            return;
        }
        if (n == 1) {
            if (ei.value < 0 || ei.value > 8)
                return;
            inputCount = (int) ei.value;
            setupPins();
            allocNodes();
            setPoints();
        }
    }

    void setExpr(String expr) {
        exprString = expr;
        parseExpr();
    }

    void parseExpr() {
        ExprParser parser = new ExprParser(exprString);
        expr = parser.parseExpression();
    }

    @Override
    public void getInfo(String[] arr) {
        super.getInfo(arr);
        int i;
        for (i = 0; arr[i] != null; i++) ;
        arr[i] = "I = " + getCurrentText(pins[inputCount].current);
    }
}

