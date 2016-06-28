package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.generator.AutoValueFactory;
import com.afcastano.intellij.autovalue.generator.AutoValueHandler;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddBuilderIntention extends BaseIntentionHandler {

    public AddBuilderIntention() {
        super(AutoValueHandler.newGenerateBuilderHandler());
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add AutoValue Builder";
    }

}
