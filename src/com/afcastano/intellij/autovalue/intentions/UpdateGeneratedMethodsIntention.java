package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.constants.ActionType;
import com.afcastano.intellij.autovalue.generator.AutoValueHandlerFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UpdateGeneratedMethodsIntention extends BaseIntentionHandler {

    public UpdateGeneratedMethodsIntention() {
        super(AutoValueHandlerFactory.make(ActionType.UPDATE_GENERATED_METHODS));
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Update AutoValue generated methods";
    }

}
