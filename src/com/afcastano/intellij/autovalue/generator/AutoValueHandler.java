package com.afcastano.intellij.autovalue.generator;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AutoValueHandler implements CodeInsightActionHandler, ContextAwareActionHandler {

    private ActionType type;

    private AutoValueHandler(ActionType type) {
        this.type = type;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        AutoValueFactory factory;
        try {
            factory = new AutoValueFactory(project, editor, file);
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        processClass(factory);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor,
                                           @NotNull PsiFile file, @NotNull DataContext dataContext) {
        Project project = editor.getProject();

        AutoValueFactory factory;
        try {
            factory = new AutoValueFactory(project, editor, file);

        } catch (RuntimeException e) {
            return false;
        }

        return shouldHandle(factory);

    }

    public boolean shouldHandle(AutoValueFactory factory) {
        switch (type) {
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

    private boolean shouldGenerateCreateMethod(AutoValueFactory factory) {
        return !factory.containsCreateMethod();
    }

    private boolean shouldUpdateMethods(AutoValueFactory factory) {
        boolean builderNotUpToDate = factory.containsBuilderClass() && !factory.isBuilderUpToDate();
        boolean createMethodNotUpToDate = factory.containsCreateMethod() && !factory.isCreateMethodUpToDate();
        return builderNotUpToDate || createMethodNotUpToDate;
    }

    private boolean shouldGenerateBuilder(AutoValueFactory factory) {
        return !factory.containsBuilderClass();
    }

    private void processClass(final AutoValueFactory factory) {

        Project project = factory.getProject();

        final PsiClass targetClass = factory.getTargetClass();

        final PsiClass builderClass = factory.getBuilderClass();

        final List<PsiMethod> pendingAddBuilderMethods = generateMissingMethods(factory, targetClass, builderClass);
        final List<PsiMethod> pendingRemoveBuilderMethods = generateExtraMethods(factory, targetClass, builderClass);

        final PsiMethod builderFactoryMethod = factory.newBuilderFactoryMethod();

        final boolean containsCreateMethod = containsCreateMethod(targetClass);

        PsiMethod[] targetClassMethods = targetClass.getMethods();
        final PsiMethod lastMethod = targetClassMethods.length > 0
                ? targetClassMethods[targetClassMethods.length - 1]
                : null;

        //Had to separate builder and crate method write commands. When I run the builder, it deletes the create method.
        //The create write command will generate it only if it existed before.
        Runnable generateBuilderRunnable = new Runnable() {
            @Override
            public void run() {

                if (type == ActionType.GENERATE_BUILDER) {

                    addBuilderElements();

                }

                if (type == ActionType.UPDATE_GENERATED_METHODS) {
                    //Update the builder only if it existed
                    if(containsBuilderFactoryMethod(targetClass) || factory.containsBuilderClass()) {
                        addBuilderElements();
                    }
                }

                //Delete the create method if exists.
                if(containsCreateMethod) {
                    targetClass.findMethodsByName("create", true)[0].delete();
                }


            }

            private void addBuilderElements() {
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
                    addAfterSafe(targetClass, builderFactoryMethod, lastMethod);
                }
            }
        };

        WriteCommandAction.runWriteCommandAction(project, generateBuilderRunnable);


        Runnable generateCreateMethodRunnable = new Runnable() {
            @Override
            public void run() {
                final PsiMethod createMethod = newCreateMethod(factory, targetClass);

                List<PsiMethod> allGetters = getThisClassGetters(factory, targetClass);
                PsiMethod lastMethod = allGetters.size() > 0 ? allGetters.get(allGetters.size() - 1) : null;

                if (type == ActionType.GENERATE_CREATE_METHOD) {
                    addAfterSafe(targetClass, createMethod, lastMethod);
                }

                if (type == ActionType.UPDATE_GENERATED_METHODS || type == ActionType.GENERATE_BUILDER) {
                    //Update only if create method exist
                    if (containsCreateMethod) {
                        addAfterSafe(targetClass, createMethod, lastMethod);
                    }

                }
            }
        };

        WriteCommandAction.runWriteCommandAction(project, generateCreateMethodRunnable);
    }

    private PsiMethod newCreateMethod(AutoValueFactory factory, PsiClass targetClass) {
        final PsiMethod createMethod;

        boolean containsBuilder = factory.containsBuilderClass();

        if (containsBuilder) {
            createMethod = generateCreateMethodWithBuilder(factory, targetClass);

        } else {
            createMethod = generateCreateMethodWhenNoBuilder(factory, targetClass);

        }
        return createMethod;
    }

    private PsiMethod generateCreateMethodWithBuilder(AutoValueFactory factory, PsiClass targetClass) {
        final List<PsiMethod> abstractGetters = factory.getAbstractGetters(targetClass);
        return factory.newCreateMethodWithBuilder(abstractGetters);
    }

    private PsiMethod generateCreateMethodWhenNoBuilder(AutoValueFactory factory, PsiClass targetClass) {
        final List<PsiMethod> abstractGetters = factory.getAbstractGetters(targetClass);
        return factory.newCreateMethodWhenNoBuilder(abstractGetters);
    }



    private List<PsiMethod> getThisClassGetters(AutoValueFactory factory, PsiClass targetClass) {
        final List<PsiMethod> abstractGetters = new ArrayList<>();
        for (PsiMethod psiMethod : targetClass.getMethods()) {
            if (factory.isGetter(psiMethod)) {
                abstractGetters.add(psiMethod);
            }
        }
        return abstractGetters;
    }

    @NotNull
    private List<PsiMethod> generateMissingMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod : factory.getAbstractGetters(targetClass)) {
            if (!factory.alreadyInBuilder(builderClass, psiMethod)) {
                pendingBuilderMethods.add(factory.newBuilderSetter(psiMethod));
            }
        }
        return pendingBuilderMethods;
    }

    @NotNull
    private List<PsiMethod> generateExtraMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod : builderClass.getAllMethods()) {
            if (factory.isBuilderSetter(psiMethod) && !factory.alreadyInClass(targetClass, psiMethod)) {
                pendingBuilderMethods.add(psiMethod);
            }
        }
        return pendingBuilderMethods;
    }

    //TODO Duplicated logic on factory
    private boolean containsBuilderFactoryMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("builder", true).length != 0;
    }

    private boolean containsBuildMethod(PsiClass builderClass) {
        return builderClass.findMethodsByName("build", true).length != 0;
    }

    private boolean containsCreateMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("create", true).length != 0;
    }

    private void addAfterSafe(PsiClass targetClass, PsiMethod toAdd, PsiMethod anchor ) {
        if(anchor != null) {
            //If the action is generate method, add it anyway.
            targetClass.addAfter(toAdd, anchor);

        } else {
            //If the action is generate method, add it anyway.
            targetClass.add(toAdd);

        }
    }

    private enum ActionType {
        GENERATE_BUILDER, GENERATE_CREATE_METHOD, UPDATE_GENERATED_METHODS
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
