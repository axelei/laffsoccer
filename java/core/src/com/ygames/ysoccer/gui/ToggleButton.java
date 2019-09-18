package com.ygames.ysoccer.gui;

abstract public class ToggleButton extends Button {

    @Override
    public void onFire1Down() {
        toggle();
    }

    @Override
    public void onFire2Down() {
        toggle();
    }

    abstract protected void toggle();
}
