package com.afcastano.intellij.autovalue.generator;

import com.afcastano.intellij.autovalue.constants.ActionType;

public class AutoValueHandlerFactory {

    public static AutoValueHandler make(ActionType actionType){

        if(ActionType.GENERATE_BUILDER == actionType){
            return newGenerateBuilderHandler();
        }else if (ActionType.UPDATE_GENERATED_METHODS == actionType){
            return newUpdateBuilderHandler();
        }else if (ActionType.GENERATE_CREATE_METHOD == actionType){
            return newGenerateCreateMethodHandler();
        }else {
            throw new RuntimeException("Invalid ActionType Provided.");
        }
    }


    public static AutoValueHandler newGenerateBuilderHandler() {
        return new AutoValueHandler(ActionType.GENERATE_BUILDER);
    }

    public static AutoValueHandler newUpdateBuilderHandler() {
        return new AutoValueHandler(ActionType.UPDATE_GENERATED_METHODS);
    }

    public static AutoValueHandler newGenerateCreateMethodHandler() {
        return new AutoValueHandler(ActionType.GENERATE_CREATE_METHOD);
    }
}


