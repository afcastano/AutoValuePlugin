package com.afcastano.intellij.autovalue.actions;

import com.afcastano.intellij.autovalue.generator.GenerateAutoValueHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

public class GenerateAutoValueAction extends BaseGenerateAction {

    public GenerateAutoValueAction() {
        super(new GenerateAutoValueHandler());
    }

}
