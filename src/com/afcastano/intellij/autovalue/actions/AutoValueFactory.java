package com.afcastano.intellij.autovalue.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoValueFactory {

    private static Logger LOG = Logger.getInstance(AutoValueFactory.class);
    private static final String AUTOVALUE_CLASS_NAME = "com.google.auto.value.AutoValue";


    private PsiType builderType;
    private PsiClass builderClass;
    private PsiType targetType;
    private PsiClass targetClass;
    private PsiElementFactory factory;
    private PsiJavaFile javaFile;
    private Project project;
    private PsiClass autoValueAnnotationClass;
    private PsiMethod buildMehtod;

    public AutoValueFactory(AnActionEvent e) {

        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if (file instanceof PsiJavaFile) {
            this.javaFile = (PsiJavaFile) file;
        } else {
            LOG.debug("Not a java file");
            throw new RuntimeException("Not a java file");
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null || javaFile == null) {
            throw new RuntimeException("No class selected");
        }

        this.targetClass = loadTargetClass(editor, javaFile);
        this.project = e.getProject();
        factory = JavaPsiFacade.getElementFactory(project);

    }

    public PsiClass getTargetClass() {
        return targetClass;
    }

    public Project getProject() {
        return project;
    }

    public PsiType getTargetType() {
        if(targetType == null) {
            targetType = factory.createType(targetClass);
        }

        return targetType;
    }

    public PsiClass getBuilderClass() {

        if(builderClass == null) {
            PsiClass existingBuilder = findExistingBuilderClass(targetClass);

            if (existingBuilder != null) {
                builderClass = existingBuilder;

            } else {

                final PsiClass newBuilderClass = factory.createClass("Builder");
                PsiModifierList modifierList = newBuilderClass.getModifierList();
                modifierList.setModifierProperty("public", true);
                modifierList.setModifierProperty("static", true);
                modifierList.setModifierProperty("abstract", true);
                modifierList.addAnnotation("AutoValue.Builder");

                this.builderClass = newBuilderClass;

            }

        }

        return builderClass;
    }

    public PsiMethod newBuilderSetter(PsiMethod getterMethod) {
        final PsiMethod method = factory.createMethod(getterMethod.getName(), getBuilderType());

        PsiParameter parameter = factory.createParameter(getterMethod.getName(), getterMethod.getReturnType());
        method.getParameterList().add(parameter);
        method.getBody().delete();
        method.getModifierList().setModifierProperty("public", true);
        method.getModifierList().setModifierProperty("abstract", true);
        return method;
    }

    public PsiMethod getBuildMethod() {
        PsiMethod[] buildMethods = builderClass.findMethodsByName("build", false);

        if (buildMethods.length > 0) {
            this.buildMehtod = buildMethods[0];

        } else {
            final PsiMethod newBuildMethod = factory.createMethod("build", getTargetType());
            newBuildMethod.getBody().delete();
            newBuildMethod.getModifierList().setModifierProperty("public", true);
            newBuildMethod.getModifierList().setModifierProperty("abstract", true);
            this.buildMehtod = newBuildMethod;

        }

        return this.buildMehtod;

    }

    public PsiMethod newBuilderFactoryMethod() {
        final PsiMethod builderMethod = factory.createMethod("builder", getBuilderType());
        builderMethod.getModifierList().setModifierProperty("public", true);
        builderMethod.getModifierList().setModifierProperty("static", true);

        String generatedName = "";

        for(PsiClass parent: findAllParents(getTargetClass())) {
            generatedName = generatedName + parent.getName() + "_";
        }

        generatedName = generatedName + getTargetClass().getName();

        PsiStatement returnStatement = factory
                .createStatementFromText("return new AutoValue_" + generatedName + ".Builder();", getTargetClass());

        builderMethod.getBody().add(returnStatement);
        return builderMethod;
    }

    public PsiType getBuilderType() {

        if (builderType == null) {
            this.builderType = factory.createType(getBuilderClass());
        }

        return builderType;
    }

    public PsiClass getAutoValueAnnotationClass() {
        if (autoValueAnnotationClass == null) {
            autoValueAnnotationClass = JavaPsiFacade.getInstance(getProject())
                    .findClass(AUTOVALUE_CLASS_NAME, GlobalSearchScope.allScope(project));
        }

        return autoValueAnnotationClass;
    }

    public boolean containsBuilderClass() {
        return findExistingBuilderClass(getTargetClass()) != null;
    }

    public boolean containsAutoValueAnnotation() {
        return getTargetClass().getModifierList().findAnnotation(AUTOVALUE_CLASS_NAME) != null;
    }

    private PsiClass loadTargetClass(Editor editor, PsiJavaFile javaFile) {
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = javaFile.findElementAt(caretOffset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    private PsiClass findExistingBuilderClass(PsiClass targetClass) {
        for ( PsiClass clz: targetClass.getInnerClasses() ) {
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

    private List<PsiClass> findAllParents(PsiClass childClass) {
        List<PsiClass> parents = new ArrayList<>();
        PsiClass parent = PsiTreeUtil.getParentOfType(childClass, PsiClass.class);

        if(parent == null) {
            return parents;
        }

        parents.addAll(findAllParents(parent));
        parents.add(parent);
        return parents;
    }

}
