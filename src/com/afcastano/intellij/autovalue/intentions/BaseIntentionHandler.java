package com.afcastano.intellij.autovalue.intentions;

import com.afcastano.intellij.autovalue.generator.AutoValueFactory;
import com.afcastano.intellij.autovalue.generator.AutoValueHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public abstract class BaseIntentionHandler extends AbstractIntentionAction implements HighPriorityAction {

    private AutoValueHandler handler;

    public BaseIntentionHandler(AutoValueHandler handler) {
        this.handler = handler;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        handler.invoke(project, editor, psiFile);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        try {
            AutoValueFactory factory = new AutoValueFactory(project, editor, file);
            return handler.getValidator().shouldHandle(factory);

        } catch (RuntimeException ex) {
            return false;
        }
    }

}
