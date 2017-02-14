package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.constants.ActionType;
import com.afcastano.intellij.autovalue.generator.AutoValueHandlerFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddCreateMethodIntention extends BaseIntentionHandler {

    public AddCreateMethodIntention() {
        super(AutoValueHandlerFactory.make(ActionType.GENERATE_CREATE_METHOD));
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add AutoValue create method";
    }

}
