package com.afcastano.intellij.autovalue.actions;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UpdateBuilderAction extends BuilderAction {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Update AutoValue Builder";
    }

    @Override
    public boolean isAvailable(AutoValueFactory factory) {
        return factory.containsBuilderClass() && !factory.isBuilderUpToDate();
    }
}
