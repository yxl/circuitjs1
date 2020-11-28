package com.lushprojects.circuitjs1.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.lushprojects.circuitjs1.client.*;
import com.lushprojects.circuitjs1.client.element.CircuitElm;

// values with sliders
public class Adjustable implements Command {
    public CircuitElm elm;
    public double minValue, maxValue;
    public String sliderText;

    // index of value in getEditInfo() list that this slider controls
    public int editItem;

    public Label label;
    public Scrollbar slider;
    boolean settingValue;

    Adjustable(CircuitElm ce, int item) {
        minValue = 1;
        maxValue = 1000;
        elm = ce;
        editItem = item;
    }

    // undump
    public Adjustable(StringTokenizer st, CirSim sim) {
        int e = new Integer(st.nextToken()).intValue();
        if (e == -1)
            return;
        elm = sim.getElm(e);
        editItem = new Integer(st.nextToken()).intValue();
        minValue = new Double(st.nextToken()).doubleValue();
        maxValue = new Double(st.nextToken()).doubleValue();
        sliderText = CustomLogicModel.unescape(st.nextToken());
    }

    public void createSlider(CirSim sim) {
        double value = elm.getEditInfo(editItem).value;
        createSlider(sim, value);
    }

    void createSlider(CirSim sim, double value) {
        sim.addWidgetToVerticalPanel(label = new Label(CirSim.LS(sliderText)));
        label.addStyleName("topSpace");
        int intValue = (int) ((value - minValue) * 100 / (maxValue - minValue));
        sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, intValue, 1, 0, 101, this, elm));
    }

    void setSliderValue(double value) {
        int intValue = (int) ((value - minValue) * 100 / (maxValue - minValue));
        settingValue = true; // don't recursively set value again in execute()
        slider.setValue(intValue);
        settingValue = false;
    }

    @Override
    public void execute() {
        CircuitElm.sim.analyzeFlag = true;
        if (settingValue)
            return;
        EditInfo ei = elm.getEditInfo(editItem);
        ei.value = getSliderValue();
        elm.setEditValue(editItem, ei);
        CircuitElm.sim.repaint();
    }

    double getSliderValue() {
        return minValue + (maxValue - minValue) * slider.getValue() / 100;
    }

    public void deleteSlider(CirSim sim) {
        sim.removeWidgetFromVerticalPanel(label);
        sim.removeWidgetFromVerticalPanel(slider);
    }

    public String dump() {
        return CircuitElm.sim.locateElm(elm) + " " + editItem + " " + minValue + " " + maxValue + " " + CustomLogicModel.escape(sliderText);
    }
}
