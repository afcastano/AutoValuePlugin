package com.afcastano.intellij.autovalue.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddMissingMethodsToBuilderAction extends AnAction {
    private final String autoValueName = "com.google.auto.value.AutoValue";

    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiJavaFile javaFile = (PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE);

        Project project = e.getProject();
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);

        PsiClass targetClass = findTargetClass(e, javaFile);
        processClass(javaFile, project, factory, targetClass);

    }

    @Override
    public void update(AnActionEvent e) {

        final PsiJavaFile javaFile = (PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE);

        if(javaFile == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        PsiClass targetClass = findTargetClass(e, javaFile);

        if(targetClass == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        boolean isAnnotated = targetClass.getModifierList().findAnnotation(autoValueName) != null;
        e.getPresentation().setEnabled(isAnnotated);

    }

    @Nullable
    private PsiClass findTargetClass(AnActionEvent e, PsiJavaFile javaFile) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        PsiElement element = javaFile.findElementAt(editor.getCaretModel().getOffset());

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    private void processClass(final PsiJavaFile javaFile, Project project, PsiElementFactory factory,
                              final PsiClass targetClass) {
        PsiType targetType = factory.createType(targetClass);

        final PsiClass builderClass = getBuilderClass(factory, targetClass);

        PsiType builderType = factory.createType(builderClass);

        final List<PsiMethod> methodsToAdd = new ArrayList<>();

        for (PsiMethod psiMethod: targetClass.getMethods()) {

            if (isAbstractGetter(psiMethod) && !alreadyInBuilder(builderClass, psiMethod, builderType)) {
                methodsToAdd.add(newBuilderPropertyMethod(factory, builderType, psiMethod));
            }

        }

        if(!containsBuildMethod(builderClass)) {
            builderClass.add(newBuildMethod(factory, targetType));
        }

        final PsiMethod builderFactoryMethod = newBuilderFactoryMethod(factory, targetClass, builderType);

        final PsiMethod lastMethod = targetClass.getMethods()[targetClass.getMethods().length -1];

        PsiClass autovalueClass = JavaPsiFacade.getInstance(project).findClass(autoValueName, GlobalSearchScope.allScope(project));


        final PsiImportStatement autovalueImport = factory.createImportStatement(autovalueClass);

        //New instance of Runnable to make a replacement
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if(methodsToAdd.size() > 0) {
                    for(PsiMethod method: methodsToAdd) {
                        builderClass.add(method);
                    }
                }

                if(findExistingBuilderClass(targetClass) == null) {
                    targetClass.add(builderClass);
                }

                if(!containsBuilderFactoryMethod(targetClass)) {
                    targetClass.addAfter(builderFactoryMethod, lastMethod);
                }

                if(javaFile.getImportList().findSingleClassImportStatement(autoValueName) == null){
                    javaFile.getImportList().add(autovalueImport);
                }

            }
        };

        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    private boolean containsBuilderFactoryMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("builder", true).length != 0;
    }

    private boolean containsBuildMethod(PsiClass builderClass) {
        return builderClass.findMethodsByName("build", true).length != 0;
    }

    private boolean alreadyInBuilder(PsiClass builderClass, PsiMethod psiMethod, PsiType builderType) {
        for(PsiMethod method: builderClass.getMethods()) {
            if(method.getName().equals(psiMethod.getName()) && method.getReturnType().equals(builderType)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    private PsiMethod newBuilderFactoryMethod(PsiElementFactory factory, PsiClass targetClass, PsiType builderType) {
        final PsiMethod builderMethod = factory.createMethod("builder", builderType);
        builderMethod.getModifierList().setModifierProperty("public", true);
        builderMethod.getModifierList().setModifierProperty("static", true);
        PsiStatement returnStatement = factory
                .createStatementFromText("return new AutoValue_" + targetClass.getName() + ".Builder();", targetClass);

        builderMethod.getBody().add(returnStatement);
        return builderMethod;
    }

    @NotNull
    private PsiMethod newBuildMethod(PsiElementFactory factory, PsiType targetType) {
        final PsiMethod buildMethod = factory.createMethod("build", targetType);
        buildMethod.getBody().delete();
        buildMethod.getModifierList().setModifierProperty("public", true);
        buildMethod.getModifierList().setModifierProperty("abstract", true);
        return buildMethod;
    }

    @NotNull
    private PsiMethod newBuilderPropertyMethod(PsiElementFactory factory, PsiType builderType, PsiMethod getterMethod) {
        final PsiMethod method = factory.createMethod(getterMethod.getName(), builderType);

        PsiParameter parameter = factory.createParameter(getterMethod.getName(), getterMethod.getReturnType());
        method.getParameterList().add(parameter);
        method.getBody().delete();
        method.getModifierList().setModifierProperty("public", true);
        method.getModifierList().setModifierProperty("abstract", true);
        return method;
    }

    private boolean isAbstractGetter(PsiMethod psiMethod) {
        boolean isAbstract = psiMethod.getModifierList().hasExplicitModifier("abstract");
        boolean noParameters = psiMethod.getParameterList().getParametersCount() == 0;
        boolean returnsSomething = !psiMethod.getReturnType().equals(PsiType.VOID);
        boolean noBody = psiMethod.getBody() == null;
        return isAbstract && noParameters && returnsSomething && noBody;
    }



    private PsiClass getBuilderClass(PsiElementFactory factory, PsiClass parentClass) {

        PsiClass clz = findExistingBuilderClass(parentClass);
        if (clz != null) return clz;

        final PsiClass builderClass = factory.createClass("Builder");
        PsiModifierList modifierList = builderClass.getModifierList();
        modifierList.setModifierProperty("public", true);
        modifierList.setModifierProperty("static", true);
        modifierList.setModifierProperty("abstract", true);
        modifierList.addAnnotation("AutoValue.Builder");

        return builderClass;
    }

    @Nullable
    private PsiClass findExistingBuilderClass(PsiClass parentClass) {
        for ( PsiClass clz: parentClass.getInnerClasses() ) {
            if(isBuilderClass(clz)) {
                return clz;
            }
        }
        return null;
    }

    private boolean isBuilderClass(PsiClass clz) {
        PsiAnnotation[] annotations = clz.getModifierList().getAnnotations();

        for (PsiAnnotation annotation: annotations) {
            if (annotation.getQualifiedName().contains("Builder")) {
                return true;
            }
        }

        return false;
    }

}
