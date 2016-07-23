package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.generator.AutoValueHandler;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UpdateGeneratedMethodsIntention extends BaseIntentionHandler {

    public UpdateGeneratedMethodsIntention() {
        super(AutoValueHandler.newUpdateBuilderHandler());
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Update AutoValue generated methods";
    }

}
