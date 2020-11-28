package com.lushprojects.circuitjs1.client.ui;

public interface Editable {
    EditInfo getEditInfo(int n);

    void setEditValue(int n, EditInfo ei);
}
