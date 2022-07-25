package com.example.application.views.icons;

import java.util.Locale;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;

public enum CustomIcon implements IconFactory {
    LOGO;
    @Override
    public Icon create() {
        return new Icon("custom", name().toLowerCase(Locale.ENGLISH));
    }
}
