package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.generator.AutoValueHandler;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddCreateMethodIntention extends BaseIntentionHandler {

    public AddCreateMethodIntention() {
        super(AutoValueHandler.newGenerateCreateMethodHandler());
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add AutoValue create method";
    }

}
