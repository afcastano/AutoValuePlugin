package com.afcastano.intellij.autovalue.generator;

import com.afcastano.intellij.autovalue.constants.ActionType;
import com.afcastano.intellij.autovalue.util.typeproperties.SetterProperties;
import com.afcastano.intellij.autovalue.util.PsiClassUtil;
import com.afcastano.intellij.autovalue.util.PsiMethodUtil;
import com.afcastano.intellij.autovalue.util.typeproperties.TargetClassProperties;
import com.afcastano.intellij.autovalue.util.validation.ValidationUtil;
import com.afcastano.intellij.autovalue.util.validation.HandlerValidator;
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

    private HandlerValidator validator ;

    AutoValueHandler(ActionType type) {
        this.type = type;
        this.validator = new HandlerValidator(type);
    }

    public HandlerValidator getValidator() {
        return validator;
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

        return validator.shouldHandle(factory);

    }

    private void processClass(final AutoValueFactory factory) {

        Project project = factory.getProject();

        final PsiClass targetClass = factory.getTargetClass();

        final PsiClass builderClass = factory.getBuilderClass();

        final List<PsiMethod> pendingAddBuilderMethods = generateMissingMethods(factory, targetClass, builderClass);
        final List<PsiMethod> pendingRemoveBuilderMethods = generateExtraMethods(targetClass, builderClass);

        final PsiMethod builderFactoryMethod = factory.newBuilderFactoryMethod();

        final boolean containsCreateMethod = ValidationUtil.containsCreateMethod(targetClass);

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
                    if(ValidationUtil.containsBuilderFactoryMethod(targetClass) || ValidationUtil.containsBuilderClass(factory.getTargetClass())) {
                        addBuilderElements();
                    }
                }

                //Delete the create method if exists.
                if(containsCreateMethod) {
                    targetClass.findMethodsByName("create", true)[0].delete();
                }


            }

            private void addBuilderElements() {
                boolean containsBuildMethod = ValidationUtil.containsBuildMethod(builderClass);

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

                if (!ValidationUtil.containsBuilderClass(factory.getTargetClass())) {
                    targetClass.add(builderClass);
                }

                if (!ValidationUtil.containsBuilderFactoryMethod(targetClass)) {
                    addAfterSafe(targetClass, builderFactoryMethod, lastMethod);
                }
            }
        };

        WriteCommandAction.runWriteCommandAction(project, generateBuilderRunnable);


        Runnable generateCreateMethodRunnable = new Runnable() {
            @Override
            public void run() {
                final PsiMethod createMethod = newCreateMethod(factory, targetClass);

                List<PsiMethod> allGetters = getThisClassGetters(targetClass);
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

        boolean containsBuilder = ValidationUtil.containsBuilderClass(factory.getTargetClass());

        if (containsBuilder) {
            createMethod = generateCreateMethodWithBuilder(factory, targetClass);

        } else {
            createMethod = generateCreateMethodWhenNoBuilder(factory, targetClass);

        }
        return createMethod;
    }

    private PsiMethod generateCreateMethodWithBuilder(AutoValueFactory factory, PsiClass targetClass) {
        TargetClassProperties targetClassProperties = TargetClassProperties.fromPsiClass(targetClass);
        return factory.newCreateMethodWithBuilder(targetClassProperties);
    }

    private PsiMethod generateCreateMethodWhenNoBuilder(AutoValueFactory factory, PsiClass targetClass) {
        TargetClassProperties targetClassProperties = TargetClassProperties.fromPsiClass(targetClass);
        return factory.newCreateMethodWhenNoBuilder(targetClassProperties);
    }



    private static List<PsiMethod> getThisClassGetters(PsiClass targetClass) {
        final List<PsiMethod> abstractGetters = new ArrayList<>();
        for (PsiMethod psiMethod : targetClass.getMethods()) {
            if (PsiMethodUtil.isGetter(psiMethod)) {
                abstractGetters.add(psiMethod);
            }
        }
        return abstractGetters;
    }

    @NotNull
    private static List<PsiMethod> generateMissingMethods(AutoValueFactory factory, PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();
        TargetClassProperties classProperties = TargetClassProperties.fromPsiClass(targetClass);

        for (SetterProperties setter : classProperties.getSettersFromGetters()) {
            if (!PsiClassUtil.alreadyInBuilder(builderClass, setter)) {
                pendingBuilderMethods.add(factory.newBuilderSetter(setter));
            }
        }
        return pendingBuilderMethods;
    }

    @NotNull
    private List<PsiMethod> generateExtraMethods(PsiClass targetClass, PsiClass builderClass) {
        final List<PsiMethod> pendingBuilderMethods = new ArrayList<>();

        for (PsiMethod psiMethod : builderClass.getAllMethods()) {
            if (PsiMethodUtil.isBuilderSetter(psiMethod) && !PsiMethodUtil.alreadyInClass(targetClass, psiMethod)) {
                pendingBuilderMethods.add(psiMethod);
            }
        }
        return pendingBuilderMethods;
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

}
