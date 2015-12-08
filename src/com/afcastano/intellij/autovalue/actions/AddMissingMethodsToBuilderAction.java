package com.afcastano.intellij.autovalue.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddMissingMethodsToBuilderAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        AutoValueFactory factory;

        try {
            factory = new AutoValueFactory(e);
        } catch (Exception e1) {
            e1.printStackTrace();
            e.getPresentation().setEnabled(false);
            return;
        }

        processClass(factory);

    }

    @Override
    public void update(AnActionEvent e) {

        AutoValueFactory factory;

        try {
            factory = new AutoValueFactory(e);
        } catch (RuntimeException ex) {
            e.getPresentation().setEnabled(false);
            return;
        }

        PsiClass targetClass = factory.getTargetClass();

        if(targetClass == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        boolean isAnnotated = factory.containsAutoValueAnnotation();
        e.getPresentation().setEnabled(isAnnotated);

    }

    private void processClass(final AutoValueFactory factory) {

        Project project = factory.getProject();

        final PsiClass targetClass = factory.getTargetClass();

        final PsiClass builderClass = factory.getBuilderClass();

        final List<PsiMethod> pendingBuilderMethods = generateMissingMethods(factory, targetClass, builderClass);

        final PsiMethod builderFactoryMethod = factory.newBuilderFactoryMethod();

        final PsiMethod lastMethod = targetClass.getMethods()[targetClass.getMethods().length -1];

        Runnable runnable =  new Runnable() {
            @Override
            public void run() {

                boolean containsBuildMethod = containsBuildMethod(builderClass);

                for(PsiMethod method: pendingBuilderMethods) {

                    if(containsBuildMethod) {
                        builderClass.addBefore(method, factory.getBuildMethod());

                    } else {
                        builderClass.add(method);

                    }

                }

                if (!containsBuildMethod) {
                    builderClass.add(factory.getBuildMethod());
                }

                if(!factory.containsBuilderClass()) {
                    targetClass.add(builderClass);
                }

                if(!containsBuilderFactoryMethod(targetClass)) {
                    targetClass.addAfter(builderFactoryMethod, lastMethod);
                }

            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    @NotNull
    private List<PsiMethod> generateMissingMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod: targetClass.getMethods()) {
            if (isAbstractGetter(psiMethod) && !alreadyInBuilder(builderClass, psiMethod)) {
                pendingBuilderMethods.add(factory.newBuilderSetter(psiMethod));
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

    private boolean alreadyInBuilder(PsiClass builderClass, PsiMethod psiMethod) {
        for(PsiMethod method: builderClass.getMethods()) {

            String methodReturnName = method.getReturnType().getPresentableText();

            if(method.getName().equals(psiMethod.getName()) && methodReturnName.equals("Builder")) {
                return true;
            }
        }

        return false;
    }

    private boolean isAbstractGetter(PsiMethod psiMethod) {
        boolean isAbstract = psiMethod.getModifierList().hasExplicitModifier("abstract");
        boolean noParameters = psiMethod.getParameterList().getParametersCount() == 0;
        boolean returnsSomething = !psiMethod.getReturnType().equals(PsiType.VOID);
        boolean noBody = psiMethod.getBody() == null;
        return isAbstract && noParameters && returnsSomething && noBody;
    }


}
