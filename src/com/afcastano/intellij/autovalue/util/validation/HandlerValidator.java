package com.afcastano.intellij.autovalue.util.validation;

import com.afcastano.intellij.autovalue.constants.ActionType;
import com.afcastano.intellij.autovalue.generator.AutoValueFactory;

public class HandlerValidator {

    private ActionType actionType;

    public HandlerValidator(ActionType actionType) {
        this.actionType = actionType;
    }

    public boolean shouldHandle(AutoValueFactory factory) {
        switch (actionType) {
            case GENERATE_BUILDER:
                return shouldGenerateBuilder(factory);
            case UPDATE_GENERATED_METHODS:
                return shouldUpdateMethods(factory);
            case GENERATE_CREATE_METHOD:
                return shouldGenerateCreateMethod(factory);
            default:
                return false;
        }
    }

    private static boolean shouldGenerateBuilder(AutoValueFactory factory) {
        return !ValidationUtil.containsBuilderClass(factory.getTargetClass());
    }

    private static boolean shouldUpdateMethods(AutoValueFactory factory) {
        boolean builderNotUpToDate = ValidationUtil.containsBuilderClass(factory.getTargetClass()) && !ValidationUtil.isBuilderUpToDate(factory);
        boolean createMethodNotUpToDate = ValidationUtil.containsCreateMethod(factory.getTargetClass()) && !ValidationUtil.isCreateMethodUpToDate(factory.getTargetClass());
        return builderNotUpToDate || createMethodNotUpToDate;
    }

    private static boolean shouldGenerateCreateMethod(AutoValueFactory factory) {
        return !ValidationUtil.containsCreateMethod(factory.getTargetClass());
    }

}
