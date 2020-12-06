package com.lushprojects.circuitjs1.client.ui.menu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.lushprojects.circuitjs1.client.element.CircuitElm;
import com.lushprojects.circuitjs1.client.ui.*;

import java.util.Objects;
import java.util.Vector;

import static com.lushprojects.circuitjs1.client.CirSim.*;

/**
 * Top menu bar
 */
public class TopMenuBar {
    public final CheckboxMenuItem dotsCheckItem;
    public final CheckboxMenuItem voltsCheckItem;
    public final CheckboxMenuItem powerCheckItem;
    public final CheckboxMenuItem smallGridCheckItem;
    public final CheckboxMenuItem crossHairCheckItem;
    public final CheckboxMenuItem showValuesCheckItem;
    public final CheckboxMenuItem euroResistorCheckItem;
    public final CheckboxMenuItem euroGatesCheckItem;
    public final CheckboxMenuItem printableCheckItem;
    public final CheckboxMenuItem alternativeColorCheckItem;
    public final CheckboxMenuItem conventionCheckItem;
    public final Vector<CheckboxMenuItem> mainMenuItems = new Vector<>();
    public final Vector<String> mainMenuItemNames = new Vector<>();
    public final MenuBar menuBar;
    public final MenuItem pasteItem;
    public final MenuItem recoverItem;
    public final MenuItem saveFileItem;
    private final MenuItem undoItem;
    private final MenuItem redoItem;
    private final MenuBar drawMenuBar;


    public TopMenuBar(String recovery, boolean euroSetting, boolean euroGates, boolean convention, boolean printable) {
        MenuBar m;
        MenuBar fileMenuBar = new MenuBar(true);
        if (isElectron()) {
            fileMenuBar.addItem(iconMenuItem("clone", "New Window...", new MyCommand("file", "newwindow")));
        }
        MenuItem importFromLocalFileItem = iconMenuItem("folder", "Open File...", new MyCommand("file", "importfromlocalfile"));
        importFromLocalFileItem.setEnabled(LoadFile.isSupported());
        fileMenuBar.addItem(importFromLocalFileItem);
        MenuItem importFromTextItem = iconMenuItem("doc-text", "Import From Text...", new MyCommand("file", "importfromtext"));
        fileMenuBar.addItem(importFromTextItem);
        MenuItem importFromDropboxItem = iconMenuItem("dropbox", "Import From Dropbox...", new MyCommand("file", "importfromdropbox"));
        fileMenuBar.addItem(importFromDropboxItem);
        if (isElectron()) {
            saveFileItem = fileMenuBar.addItem(iconMenuItem("floppy", "Save", new MyCommand("file", "save")));
            fileMenuBar.addItem(iconMenuItem("floppy", "Save As...", new MyCommand("file", "saveas")));
        } else {
            saveFileItem = null;
            MenuItem exportAsLocalFileItem = iconMenuItem("floppy", "Save As...", new MyCommand("file", "exportaslocalfile"));
            exportAsLocalFileItem.setEnabled(ExportAsLocalFileDialog.downloadIsSupported());
            fileMenuBar.addItem(exportAsLocalFileItem);
        }
        MenuItem exportAsUrlItem = iconMenuItem("export", "Export As Link...", new MyCommand("file", "exportasurl"));
        fileMenuBar.addItem(exportAsUrlItem);
        MenuItem exportAsTextItem = iconMenuItem("export", "Export As Text...", new MyCommand("file", "exportastext"));
        fileMenuBar.addItem(exportAsTextItem);
        fileMenuBar.addItem(iconMenuItem("export", "Export As Image...", new MyCommand("file", "exportasimage")));
        fileMenuBar.addItem(iconMenuItem("microchip", "Create Subcircuit...", new MyCommand("file", "createsubcircuit")));
        fileMenuBar.addItem(iconMenuItem("magic", "Find DC Operating Point", new MyCommand("file", "dcanalysis")));
        recoverItem = iconMenuItem("back-in-time", "Recover Auto-Save", new MyCommand("file", "recover"));
        recoverItem.setEnabled(recovery != null);
        fileMenuBar.addItem(recoverItem);
        MenuItem printItem = iconMenuItem("print", "Print...", new MyCommand("file", "print"));
        fileMenuBar.addItem(printItem);
        fileMenuBar.addSeparator();
        MenuItem aboutItem = iconMenuItem("info-circled", "About...", null);
        fileMenuBar.addItem(aboutItem);
        aboutItem.setScheduledCommand(new MyCommand("file", "about"));

        menuBar = new MenuBar();
        menuBar.addItem(LS("File"), fileMenuBar);

        m = new MenuBar(true);
        undoItem = menuItemWithShortcut("ccw", LS("Undo"), LS("Ctrl-Z"), new MyCommand("edit", "undo"));
        m.addItem(undoItem);
        redoItem = menuItemWithShortcut("cw", LS("Redo"), LS("Ctrl-Y"), new MyCommand("edit", "redo"));
        m.addItem(redoItem);
        m.addSeparator();
        m.addItem(menuItemWithShortcut("scissors", LS("Cut"), LS("Ctrl-X"), new MyCommand("edit", "cut")));
        m.addItem(menuItemWithShortcut("copy", LS("Copy"), LS("Ctrl-C"), new MyCommand("edit", "copy")));
        pasteItem = menuItemWithShortcut("paste", LS("Paste"), LS("Ctrl-V"), new MyCommand("edit", "paste"));
        m.addItem(pasteItem);
        pasteItem.setEnabled(false);

        m.addItem(menuItemWithShortcut("clone", LS("Duplicate"), LS("Ctrl-D"), new MyCommand("edit", "duplicate")));

        m.addSeparator();
        m.addItem(menuItemWithShortcut("select-all", LS("Select All"), LS("Ctrl-A"), new MyCommand("edit", "selectAll")));
        m.addSeparator();
        m.addItem(iconMenuItem("target", weAreInUS() ? "Center Circuit" : "Centre Circuit", new MyCommand("edit", "centrecircuit")));
        m.addItem(menuItemWithShortcut("zoom-11", LS("Zoom 100%"), "0", new MyCommand("edit", "zoom100")));
        m.addItem(menuItemWithShortcut("zoom-in", LS("Zoom In"), "+", new MyCommand("edit", "zoomin")));
        m.addItem(menuItemWithShortcut("zoom-out", LS("Zoom Out"), "-", new MyCommand("edit", "zoomout")));
        menuBar.addItem(LS("Edit"), m);

        drawMenuBar = new MenuBar(true);
        drawMenuBar.setAutoOpen(true);

        menuBar.addItem(LS("Draw"), drawMenuBar);

        m = new MenuBar(true);
        m.addItem(iconMenuItem("lines", "Stack All", new MyCommand("scopes", "stackAll")));
        m.addItem(iconMenuItem("columns", "Unstack All", new MyCommand("scopes", "unstackAll")));
        m.addItem(iconMenuItem("object-group", "Combine All", new MyCommand("scopes", "combineAll")));
        m.addItem(iconMenuItem("object-ungroup", "Separate All", new MyCommand("scopes", "separateAll")));
        menuBar.addItem(LS("Scopes"), m);

        MenuBar optionsMenuBar = m = new MenuBar(true);
        menuBar.addItem(LS("Options"), optionsMenuBar);
        m.addItem(dotsCheckItem = new CheckboxMenuItem(LS("Show Current")));
        dotsCheckItem.setState(true);
        voltsCheckItem = new CheckboxMenuItem(LS("Show Voltage"));
        powerCheckItem = new CheckboxMenuItem(LS("Show Power"));
        voltsCheckItem.setScheduledCommand(
                () -> {
                    if (voltsCheckItem.getState()) {
                        powerCheckItem.setState(false);
                    }
                    theSim.setPowerBarEnable(powerCheckItem.getState());
                });
        powerCheckItem.setScheduledCommand(
                () -> {
                    if (powerCheckItem.getState()) {
                        voltsCheckItem.setState(false);
                    }
                    theSim.setPowerBarEnable(powerCheckItem.getState());
                });
        m.addItem(voltsCheckItem);
        voltsCheckItem.setState(true);
        m.addItem(powerCheckItem);
        m.addItem(showValuesCheckItem = new CheckboxMenuItem(LS("Show Values")));
        showValuesCheckItem.setState(true);
        m.addItem(smallGridCheckItem = new CheckboxMenuItem(LS("Small Grid"),
                theSim::setGrid));
        crossHairCheckItem = new CheckboxMenuItem(LS("Show Cursor Cross Hairs"));
        crossHairCheckItem.setScheduledCommand(() -> theSim.setOptionInStorage("crossHair", crossHairCheckItem.getState()));
        m.addItem(crossHairCheckItem);
        crossHairCheckItem.setState(theSim.getOptionFromStorage("crossHair", false));
        euroResistorCheckItem = new CheckboxMenuItem(LS("European Resistors"));
        euroResistorCheckItem.setScheduledCommand(() -> theSim.setOptionInStorage("euroResistors", euroResistorCheckItem.getState()));
        m.addItem(euroResistorCheckItem);
        euroResistorCheckItem.setState(euroSetting);
        euroGatesCheckItem = new CheckboxMenuItem(LS("IEC Gates"));
        euroGatesCheckItem.setScheduledCommand(() -> theSim.setIecGates(euroGatesCheckItem.getState()));
        m.addItem(euroGatesCheckItem);
        euroGatesCheckItem.setState(euroGates);
        printableCheckItem = new CheckboxMenuItem(LS("White Background"));
        printableCheckItem.setScheduledCommand(() -> theSim.setWhiteBackground(printableCheckItem.getState()));
        m.addItem(printableCheckItem);
        printableCheckItem.setState(printable);
        alternativeColorCheckItem = new CheckboxMenuItem(LS("Alt Color for Volts & Pwr"));
        alternativeColorCheckItem.setScheduledCommand(() -> theSim.setAlternativeColor(alternativeColorCheckItem.getState()));
        m.addItem(alternativeColorCheckItem);
        alternativeColorCheckItem.setState(theSim.getOptionFromStorage("alternativeColor", false));
        conventionCheckItem = new CheckboxMenuItem(LS("Conventional Current Motion"));
        conventionCheckItem.setScheduledCommand(() -> theSim.setOptionInStorage("conventionalCurrent", conventionCheckItem.getState()));
        m.addItem(conventionCheckItem);
        conventionCheckItem.setState(convention);

        m.addItem(new CheckboxAlignedMenuItem(LS("Shortcuts..."), new MyCommand("options", "shortcuts")));
        m.addItem(new CheckboxAlignedMenuItem(LS("Other Options..."), new MyCommand("options", "other")));
        if (isElectron()) {
            m.addItem(new CheckboxAlignedMenuItem(LS("Toggle Dev Tools"), new MyCommand("options", "devtools")));
        }
    }

    public static MenuItem menuItemWithShortcut(String icon, String text, String shortcut, MyCommand cmd) {
        final String edithtml = "<div style=\"display:inline-block;width:100px;\"><i class=\"cirjsicon-";
        String nbsp = "&nbsp;";
        if (Objects.equals(icon, "")) {
            nbsp = "";
        }
        String sn = edithtml + icon + "\"></i>" + nbsp + text + "</div>" + shortcut;
        return new MenuItem(SafeHtmlUtils.fromTrustedString(sn), cmd);
    }

    public void init() {
        composeMainMenu(drawMenuBar);
        menuBar.addDomHandler(event -> doMainMenuChecks(), ClickEvent.getType());
    }

    public void composeMainMenu(MenuBar mainMenuBar) {
        mainMenuBar.addItem(getClassCheckItem(LS("Add Wire"), "WireElm"));
        mainMenuBar.addItem(getClassCheckItem(LS("Add Resistor"), "ResistorElm"));

        MenuBar passMenuBar = new MenuBar(true);
        passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor"), "CapacitorElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor (polarized)"), "PolarCapacitorElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Inductor"), "InductorElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Switch"), "SwitchElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Push Switch"), "PushSwitchElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add SPDT Switch"), "Switch2Elm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Potentiometer"), "PotElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Transformer"), "TransformerElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Tapped Transformer"), "TappedTransformerElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Transmission Line"), "TransLineElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Relay"), "RelayElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Memristor"), "MemristorElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Spark Gap"), "SparkGapElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Fuse"), "FuseElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Custom Transformer"), "CustomTransformerElm"));
        passMenuBar.addItem(getClassCheckItem(LS("Add Crystal"), "CrystalElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Passive Components")), passMenuBar);

        MenuBar inputMenuBar = new MenuBar(true);
        inputMenuBar.addItem(getClassCheckItem(LS("Add Ground"), "GroundElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (2-terminal)"), "DCVoltageElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (2-terminal)"), "ACVoltageElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (1-terminal)"), "RailElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (1-terminal)"), "ACRailElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Square Wave Source (1-terminal)"), "SquareRailElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Clock"), "ClockElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Sweep"), "SweepElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Variable Voltage"), "VarRailElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Antenna"), "AntennaElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add AM Source"), "AMElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add FM Source"), "FMElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Current Source"), "CurrentElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Noise Generator"), "NoiseElm"));
        inputMenuBar.addItem(getClassCheckItem(LS("Add Audio Input"), "AudioInputElm"));

        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Inputs and Sources")), inputMenuBar);

        MenuBar outputMenuBar = new MenuBar(true);
        outputMenuBar.addItem(getClassCheckItem(LS("Add Analog Output"), "OutputElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add LED"), "LEDElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Lamp"), "LampElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Text"), "TextElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Box"), "BoxElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Voltmeter/Scobe Probe"), "ProbeElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Ohmmeter"), "OhmMeterElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Labeled Node"), "LabeledNodeElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Test Point"), "TestPointElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Ammeter"), "AmmeterElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Data Export"), "DataRecorderElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Audio Output"), "AudioOutputElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add LED Array"), "LEDArrayElm"));
        outputMenuBar.addItem(getClassCheckItem(LS("Add Stop Trigger"), "StopTriggerElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Outputs and Labels")), outputMenuBar);

        MenuBar activeMenuBar = new MenuBar(true);
        activeMenuBar.addItem(getClassCheckItem(LS("Add Diode"), "DiodeElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Zener Diode"), "ZenerElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, NPN)"), "NTransistorElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, PNP)"), "PTransistorElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (N-Channel)"), "NMosfetElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (P-Channel)"), "PMosfetElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (N-Channel)"), "NJfetElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (P-Channel)"), "PJfetElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add SCR"), "SCRElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add DIAC"), "DiacElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add TRIAC"), "TriacElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (NPN)"), "NDarlingtonElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (PNP)"), "PDarlingtonElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Varactor/Varicap"), "VaractorElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Tunnel Diode"), "TunnelDiodeElm"));
        activeMenuBar.addItem(getClassCheckItem(LS("Add Triode"), "TriodeElm"));
        //    	activeMenuBar.addItem(getClassCheckItem("Add Photoresistor", "PhotoResistorElm"));
        //    	activeMenuBar.addItem(getClassCheckItem("Add Thermistor", "ThermistorElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Active Components")), activeMenuBar);

        MenuBar activeBlocMenuBar = new MenuBar(true);
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (ideal, - on top)"), "OpAmpElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (ideal, + on top)"), "OpAmpSwapElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (real)"), "OpAmpRealElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPST)"), "AnalogSwitchElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPDT)"), "AnalogSwitch2Elm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Tristate Buffer"), "TriStateElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger"), "SchmittElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger (Inverting)"), "InvertingSchmittElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII+"), "CC2Elm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII-"), "CC2NegElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Comparator (Hi-Z/GND output)"), "ComparatorElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add OTA (LM13700 style)"), "OTAElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Voltage Source"), "VCVSElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Current Source"), "VCCSElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Voltage Source"), "CCVSElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Current Source"), "CCCSElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Optocoupler"), "OptocouplerElm"));
        activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Subcircuit Instance"), "CustomCompositeElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Active Building Blocks")), activeBlocMenuBar);

        MenuBar gateMenuBar = new MenuBar(true);
        gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Input"), "LogicInputElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Output"), "LogicOutputElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add Inverter"), "InverterElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add NAND Gate"), "NandGateElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add NOR Gate"), "NorGateElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add AND Gate"), "AndGateElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add OR Gate"), "OrGateElm"));
        gateMenuBar.addItem(getClassCheckItem(LS("Add XOR Gate"), "XorGateElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Logic Gates, Input and Output")), gateMenuBar);

        MenuBar chipMenuBar = new MenuBar(true);
        chipMenuBar.addItem(getClassCheckItem(LS("Add D Flip-Flop"), "DFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add JK Flip-Flop"), "JKFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add T Flip-Flop"), "TFlipFlopElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment LED"), "SevenSegElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment Decoder"), "SevenSegDecoderElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Multiplexer"), "MultiplexerElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Demultiplexer"), "DeMultiplexerElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add SIPO shift register"), "SipoShiftElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add PISO shift register"), "PisoShiftElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Counter"), "CounterElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Ring Counter"), "DecadeElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Latch"), "LatchElm"));
        //chipMenuBar.addItem(getClassCheckItem("Add Static RAM", "SRAMElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Sequence generator"), "SeqGenElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Full Adder"), "FullAdderElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Half Adder"), "HalfAdderElm"));
        chipMenuBar.addItem(getClassCheckItem(LS("Add Custom Logic"), "UserDefinedLogicElm")); // don't change this, it will break people's saved shortcuts
        chipMenuBar.addItem(getClassCheckItem(LS("Add Static RAM"), "SRAMElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Digital Chips")), chipMenuBar);

        MenuBar achipMenuBar = new MenuBar(true);
        achipMenuBar.addItem(getClassCheckItem(LS("Add 555 Timer"), "TimerElm"));
        achipMenuBar.addItem(getClassCheckItem(LS("Add Phase Comparator"), "PhaseCompElm"));
        achipMenuBar.addItem(getClassCheckItem(LS("Add DAC"), "DACElm"));
        achipMenuBar.addItem(getClassCheckItem(LS("Add ADC"), "ADCElm"));
        achipMenuBar.addItem(getClassCheckItem(LS("Add VCO"), "VCOElm"));
        achipMenuBar.addItem(getClassCheckItem(LS("Add Monostable"), "MonostableElm"));
        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Analog and Hybrid Chips")), achipMenuBar);

        MenuBar otherMenuBar = new MenuBar(true);
        CheckboxMenuItem mi;
        otherMenuBar.addItem(mi = getClassCheckItem(LS("Drag All"), "DragAll"));
        mi.setShortcut(LS("(Alt-drag)"));
        otherMenuBar.addItem(mi = getClassCheckItem(LS("Drag Row"), "DragRow"));
        mi.setShortcut(LS("(A-S-drag)"));
        otherMenuBar.addItem(mi = getClassCheckItem(LS("Drag Column"), "DragColumn"));
        mi.setShortcut(theSim.isMac ? LS("(A-Cmd-drag)") : LS("(A-M-drag)"));
        otherMenuBar.addItem(getClassCheckItem(LS("Drag Selected"), "DragSelected"));
        otherMenuBar.addItem(mi = getClassCheckItem(LS("Drag Post"), "DragPost"));
        mi.setShortcut("(" + theSim.ctrlMetaKey + "-drag)");

        mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml + LS("&nbsp;</div>Drag")), otherMenuBar);

        mainMenuBar.addItem(mi = getClassCheckItem(LS("Select/Drag Sel"), "Select"));
        mi.setShortcut(LS("(space or Shift-drag)"));
    }

    public void enableUndoRedo(boolean redo, boolean undo) {
        redoItem.setEnabled(redo);
        undoItem.setEnabled(undo);
    }

    private CheckboxMenuItem getClassCheckItem(String s, String t) {
        // try {
        //   Class c = Class.forName(t);
        String shortcut = "";
        CircuitElm elm = constructElement(t, 0, 0);
        CheckboxMenuItem mi;
        //  register(c, elm);
        if (elm != null) {
            if (elm.needsShortcut()) {
                shortcut += (char) elm.getShortcut();
                theSim.shortcuts[elm.getShortcut()] = t;
            }
            elm.delete();
        }
//    	else
//    		GWT.log("Coudn't create class: "+t);
        //	} catch (Exception ee) {
        //	    ee.printStackTrace();
        //	}
        if (Objects.equals(shortcut, "")) {
            mi = new CheckboxMenuItem(s);
        } else {
            mi = new CheckboxMenuItem(s, shortcut);
        }
        mi.setScheduledCommand(new MyCommand("main", t));
        mainMenuItems.add(mi);
        mainMenuItemNames.add(t);
        return mi;
    }

    // load shortcuts from local storage
    public void loadShortcuts() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null) {
            return;
        }
        String str = stor.getItem("shortcuts");
        if (str == null) {
            return;
        }
        String[] keys = str.split(";");

        // clear existing shortcuts
        int i;
        for (i = 0; i != theSim.shortcuts.length; i++) {
            theSim.shortcuts[i] = null;
        }

        // clear shortcuts from menu
        for (i = 0; i != mainMenuItems.size(); i++) {
            CheckboxMenuItem item = mainMenuItems.get(i);
            // stop when we get to drag menu items
            if (item.getShortcut().length() > 1) {
                break;
            }
            item.setShortcut("");
        }

        // go through keys (skipping version at start)
        for (i = 1; i < keys.length; i++) {
            String[] arr = keys[i].split("=");
            if (arr.length != 2) {
                continue;
            }
            int c = Integer.parseInt(arr[0]);
            String className = arr[1];
            theSim.shortcuts[c] = className;

            // find menu item and fix it
            int j;
            for (j = 0; j != mainMenuItems.size(); j++) {
                if (Objects.equals(mainMenuItemNames.get(j), className)) {
                    CheckboxMenuItem item = mainMenuItems.get(j);
                    item.setShortcut(Character.toString((char) c));
                    break;
                }
            }
        }
    }

    private MenuItem iconMenuItem(String icon, String text, Command cmd) {
        String icoStr = "<i class=\"cirjsicon-" + icon + "\"></i>&nbsp;" + LS(text); //<i class="cirjsicon-"></i>&nbsp;
        return new MenuItem(SafeHtmlUtils.fromTrustedString(icoStr), cmd);
    }

    public void doMainMenuChecks() {
        int c = mainMenuItems.size();
        for (int i = 0; i < c; i++) {
            mainMenuItems.get(i).setState(Objects.equals(mainMenuItemNames.get(i), theSim.mouseModeStr));
        }
    }
}
