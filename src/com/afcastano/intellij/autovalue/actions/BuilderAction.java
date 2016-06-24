package com.afcastano.intellij.autovalue.actions;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BuilderAction extends AbstractIntentionAction implements HighPriorityAction {

    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        AutoValueFactory factory;
        try {
            factory = new AutoValueFactory(project, editor, psiFile);
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        processClass(factory);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        try {
            AutoValueFactory factory = new AutoValueFactory(project, editor, file);
            return isAvailable(factory);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public abstract boolean isAvailable(AutoValueFactory factory);

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private void processClass(final AutoValueFactory factory) {

        Project project = factory.getProject();

        final PsiClass targetClass = factory.getTargetClass();

        final PsiClass builderClass = factory.getBuilderClass();

        final List<PsiMethod> pendingAddBuilderMethods = generateMissingMethods(factory, targetClass, builderClass);
        final List<PsiMethod> pendingRemoveBuilderMethods = generateExtraMethods(factory, targetClass, builderClass);

        final PsiMethod builderFactoryMethod = factory.newBuilderFactoryMethod();

        final PsiMethod lastMethod = targetClass.getMethods()[targetClass.getMethods().length - 1];

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                boolean containsBuildMethod = containsBuildMethod(builderClass);

                for (PsiMethod method : pendingAddBuilderMethods) {

                    if (containsBuildMethod) {
                        builderClass.addBefore(method, factory.getBuildMethod());

                    } else {
                        builderClass.add(method);

                    }

                }

                for (PsiMethod method : pendingRemoveBuilderMethods) {
                    method.delete();
                }

                if (!containsBuildMethod) {
                    builderClass.add(factory.getBuildMethod());
                }

                if (!factory.containsBuilderClass()) {
                    targetClass.add(builderClass);
                }

                if (!containsBuilderFactoryMethod(targetClass)) {
                    targetClass.addAfter(builderFactoryMethod, lastMethod);
                }

            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    @NotNull
    private List<PsiMethod> generateMissingMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod : targetClass.getMethods()) {
            if (factory.isAbstractGetter(psiMethod) && !factory.alreadyInBuilder(builderClass, psiMethod)) {
                pendingBuilderMethods.add(factory.newBuilderSetter(psiMethod));
            }
        }
        return pendingBuilderMethods;
    }

    @NotNull
    private List<PsiMethod> generateExtraMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod : builderClass.getMethods()) {
            if (factory.isBuilderSetter(psiMethod) && !factory.alreadyInClass(targetClass, psiMethod)) {
                pendingBuilderMethods.add(psiMethod);
            }
        }
        return pendingBuilderMethods;
    }

    private boolean containsBuilderFactoryMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("builder", true).length != 0;
    }

    private boolean containsBuildMethod(PsiClass builderClass) {
        return builderClass.findMethodsByName("build", true).length != 0;
    }
}
