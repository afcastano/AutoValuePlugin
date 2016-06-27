package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.generator.AutoValueFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UpdateBuilderIntention extends BaseIntentionHandler {

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
