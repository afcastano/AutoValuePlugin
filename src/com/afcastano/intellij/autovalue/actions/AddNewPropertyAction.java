package com.afcastano.intellij.autovalue.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class AddNewPropertyAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(false);
    }

}
