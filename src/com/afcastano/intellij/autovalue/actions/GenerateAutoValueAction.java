package com.afcastano.intellij.autovalue.actions;

import com.afcastano.intellij.autovalue.generator.GenerateAutoValueHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

//TODO eventually remove the right click action.
public class GenerateAutoValueAction extends BaseGenerateAction {

    public GenerateAutoValueAction() {
        super(new GenerateAutoValueHandler());
    }

}
