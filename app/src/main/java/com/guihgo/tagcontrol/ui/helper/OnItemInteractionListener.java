package com.guihgo.tagcontrol.ui.helper;

import android.view.View;

public interface OnItemInteractionListener {
    public void onItemSwipedLeft(int position);
    public void onItemSwipedRight(int position);
    public void onItemClick(int position);
    public boolean onTemLongClick(int position, View v);
}