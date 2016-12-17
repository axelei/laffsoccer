package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.GLColor;

public class WidgetColor {

    public Integer body;
    Integer lightBorder;
    Integer darkBorder;

    public WidgetColor() {
    }

    public WidgetColor(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public void set(Integer body, Integer lightBorder, Integer darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public WidgetColor brighter() {
        return new WidgetColor(GLColor.brighter(body), GLColor.brighter(lightBorder), GLColor.brighter(darkBorder));
    }

    public WidgetColor darker() {
        return new WidgetColor(GLColor.darker(body), GLColor.darker(lightBorder), GLColor.darker(darkBorder));
    }
}
