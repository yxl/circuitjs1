package com.lushprojects.circuitjs1.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.lushprojects.circuitjs1.client.CirSim;

public class ImportFromDropboxDialog extends DialogBox {


    VerticalPanel vp;
    Button cancelButton;
    Button chooserButton;
    Button importButton;
    TextArea ta;
    Label la;
    HorizontalPanel hp;
    ImportFromDropbox importFromDropbox;
    static CirSim sim;


    static public void setSim(CirSim csim) {
        sim = csim;
    }

    static public void doLoadCallback(String s) {
        sim.pushUndo();
        sim.readCircuit(s);
        sim.allowSave(false);
    }


    static public final native void doDropboxImport(String link)  /*-{
        try {
            var xhr = new XMLHttpRequest();
            xhr.addEventListener("load", function reqListener() {
                //			console.log(xhr.responseText);
                var text = xhr.responseText;
                @com.lushprojects.circuitjs1.client.ui.ImportFromDropboxDialog::doLoadCallback(Ljava/lang/String;)(text);
            });
            xhr.open("GET", link, false);
            xhr.send();
        } catch (err) {

        }

    }-*/;

    static public void doImportDropboxLink(String link, Boolean validateIsDropbox) {
        if (validateIsDropbox && link.indexOf("https://www.dropbox.com/") != 0) {
            Window.alert("Dropbox links must start https://www.dropbox.com/");
            return;
        }
        // Work-around to allow CORS access to dropbox links - see
        // https://www.dropboxforum.com/t5/API-support/CORS-issue-when-trying-to-download-shared-file/m-p/82466
        link = link.replace("www.dropbox.com", "dl.dropboxusercontent.com");
        doDropboxImport(link);

    }

    public ImportFromDropboxDialog(CirSim csim) {
        super();
        setSim(csim);

        vp = new VerticalPanel();
        setWidget(vp);
        setText(CirSim.LS("Import from Dropbox"));
        if (ImportFromDropbox.isSupported()) {
            vp.add(new Label(CirSim.LS("To open a file in your dropbox account using the chooser click below.")));
            chooserButton = new Button(CirSim.LS("Open Dropbox Chooser"));
            vp.add(chooserButton);
            chooserButton.addClickHandler(event -> {
                closeDialog();
                importFromDropbox = new ImportFromDropbox(sim);
            });
            la = new Label(CirSim.LS("To open a shared Dropbox file from a Dropbox link paste the link below..."));
        } else {
            vp.add(new Label("This site, or your browser doesn't support the Dropbox chooser so you can't pick a file from your dropbox account."));
            la = new Label("You can open a shared Dropbox file if you have a link. Paste the Dropbox link below...");
            la.setStyleName("topSpace");
        }

        vp.add(la);
        ta = new TextArea();
        ta.setWidth("300px");
        ta.setHeight("200px");
        vp.add(ta);
        hp = new HorizontalPanel();
        hp.setWidth("100%");
        vp.add(hp);
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        importButton = new Button(CirSim.LS("Import From Dropbox Link"));
        importButton.addClickHandler(event -> {
            closeDialog();
            doImportDropboxLink(ta.getText(), true);
        });
        hp.add(importButton);
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        cancelButton = new Button(CirSim.LS("Cancel"));
        hp.add(cancelButton);
        cancelButton.addClickHandler(event -> closeDialog());
        this.center();
    }

    protected void closeDialog() {
        this.hide();
    }

}

