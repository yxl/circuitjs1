package com.lushprojects.circuitjs1.client.ui.menu;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.lushprojects.circuitjs1.client.ui.MyCommand;
import com.lushprojects.circuitjs1.client.ui.ScopePopupMenu;

import static com.lushprojects.circuitjs1.client.CirSim.LS;
import static com.lushprojects.circuitjs1.client.CirSim.theSim;

public class ContextMenu {
    public final MenuBar mainMenuBar;
    public final ScopePopupMenu scopePopupMenu;
    public final MenuBar elmMenuBar;
    public MenuItem elmEditMenuItem;
    public MenuItem elmCutMenuItem;
    public MenuItem elmCopyMenuItem;
    public MenuItem elmDeleteMenuItem;
    public MenuItem elmScopeMenuItem;
    public MenuItem elmFloatScopeMenuItem;
    public MenuItem elmFlipMenuItem;
    public MenuItem elmSplitMenuItem;
    public MenuItem elmSliderMenuItem;

    public ContextMenu(TopMenuBar topMenuBar) {
        mainMenuBar = new MenuBar(true);
        mainMenuBar.setAutoOpen(true);
        topMenuBar.composeMainMenu(mainMenuBar);

        elmMenuBar = createElmMenuBar();
        scopePopupMenu = new ScopePopupMenu();
    }

    private MenuBar createElmMenuBar() {
        MenuBar menuBar = new MenuBar(true);
        menuBar.addItem(elmEditMenuItem = new MenuItem(LS("Edit..."), new MyCommand("elm", "edit")));
        menuBar.addItem(elmScopeMenuItem = new MenuItem(LS("View in Scope"), new MyCommand("elm", "viewInScope")));
        menuBar.addItem(elmFloatScopeMenuItem = new MenuItem(LS("View in Undocked Scope"), new MyCommand("elm", "viewInFloatScope")));
        menuBar.addItem(elmCutMenuItem = new MenuItem(LS("Cut"), new MyCommand("elm", "cut")));
        menuBar.addItem(elmCopyMenuItem = new MenuItem(LS("Copy"), new MyCommand("elm", "copy")));
        menuBar.addItem(elmDeleteMenuItem = new MenuItem(LS("Delete"), new MyCommand("elm", "delete")));
        menuBar.addItem(new MenuItem(LS("Duplicate"), new MyCommand("elm", "duplicate")));
        menuBar.addItem(elmFlipMenuItem = new MenuItem(LS("Swap Terminals"), new MyCommand("elm", "flip")));
        menuBar.addItem(elmSplitMenuItem = TopMenuBar.menuItemWithShortcut("", LS("Split Wire"), LS(theSim.ctrlMetaKey + "-click"), new MyCommand("elm", "split")));
        menuBar.addItem(elmSliderMenuItem = new MenuItem(LS("Sliders..."), new MyCommand("elm", "sliders")));
        return menuBar;
    }
}
