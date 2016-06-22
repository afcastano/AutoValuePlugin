package com.afcastano.intellij.autovalue.actions;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddBuilderAction extends BuilderAction {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add AutoValue Builder";
    }

    @Override
    public boolean isAvailable(AutoValueFactory factory) {
        return !factory.containsBuilderClass();
    }
}
