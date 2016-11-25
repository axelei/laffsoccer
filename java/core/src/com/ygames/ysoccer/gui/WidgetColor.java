package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.GlColor;

public class WidgetColor {

    public int body;
    int lightBorder;
    int darkBorder;

    public WidgetColor() {
    }

    public WidgetColor(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public void set(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public WidgetColor brighter() {
        return new WidgetColor(GlColor.brighter(body), GlColor.brighter(lightBorder), GlColor.brighter(darkBorder));
    }

    public WidgetColor darker() {
        return new WidgetColor(GlColor.darker(body), GlColor.darker(lightBorder), GlColor.darker(darkBorder));
    }
}
