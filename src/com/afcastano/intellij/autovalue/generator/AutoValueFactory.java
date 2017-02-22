package com.afcastano.intellij.autovalue.generator;

import com.afcastano.intellij.autovalue.util.typeproperties.SetterProperties;
import com.afcastano.intellij.autovalue.util.PsiClassUtil;
import com.afcastano.intellij.autovalue.util.PsiMethodUtil;
import com.afcastano.intellij.autovalue.util.typeproperties.TargetClassProperties;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoValueFactory {

    private PsiType builderType;
    private PsiClass builderClass;
    private PsiType targetType;
    private PsiClass targetClass;
    private PsiElementFactory factory;
    private PsiJavaFile javaFile;
    private Project project;
    private PsiAnnotation autoValueAnnotation;
    private PsiMethod buildMehtod;

    public AutoValueFactory(Project project, Editor editor, PsiFile file) {
        this.project = project;

        if (file instanceof PsiJavaFile) {
            this.javaFile = (PsiJavaFile) file;
        } else {
            PsiMethodUtil.LOG.debug("Not a java file");
            throw new RuntimeException("Not a java file");
        }

        if (editor == null || javaFile == null) {
            throw new RuntimeException("No class selected");
        }

        this.targetClass = PsiClassUtil.loadTargetClass(editor, javaFile);

        if (targetClass == null) {
            throw new RuntimeException("Target class not found");
        }

        this.autoValueAnnotation = findAutoValueAnnotationClass();

        if (autoValueAnnotation == null) {
            throw new RuntimeException("No auto value annotation detected");
        }

        factory = JavaPsiFacade.getElementFactory(project);
    }

    public PsiClass getTargetClass() {
        return targetClass;
    }

    public Project getProject() {
        return project;
    }

    public PsiType getTargetType() {
        if (targetType == null) {
            targetType = factory.createType(targetClass);
        }

        return targetType;
    }

    public PsiClass getBuilderClass() {

        if (builderClass == null) {
            PsiClass existingBuilder = PsiClassUtil.findExistingBuilderClass(targetClass);

            if (existingBuilder != null) {
                builderClass = existingBuilder;

            } else {

                PsiClass newBuilderClass = PsiClassUtil.createClass(project, "Builder",
                        targetClass.getTypeParameters());

                PsiModifierList modifierList = newBuilderClass.getModifierList();
                modifierList.setModifierProperty("public", true);
                modifierList.setModifierProperty("abstract", true);
                modifierList.setModifierProperty("static", true);
                modifierList.addAnnotation(autoValueAnnotation.getQualifiedName() + ".Builder");

                this.builderClass = newBuilderClass;

            }

        }

        return builderClass;
    }

    public PsiMethod newCreateMethodWithBuilder(TargetClassProperties targetClassProperties) {
        PsiMethod method = PsiMethodUtil.newStaticMethod("create", targetClass, factory);

        List<SetterProperties> properties = targetClassProperties.getSettersFromGetters();
        for (SetterProperties getter : properties) {
            PsiParameter parameter = factory.createParameter(getter.getParameterName(),
                    getter.getParameterType());

            method.getParameterList().add(parameter);
        }

        String builderChain = "";

        for (SetterProperties property : properties) {
            builderChain = builderChain + "." + property.getName() + "(" + property.getParameterName() + ")\n";
        }


        String builderFactoryName = "builder";

        if (targetClass.hasTypeParameters()) {
            String typeParams = PsiClassUtil.getTypeParameterString(targetClass.getTypeParameters());
            builderFactoryName = targetClass.getName() + "." + typeParams + "builder";
        }

        String returnStatementText = "return " + builderFactoryName + "()\n" + builderChain + ".build();";

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        method.getBody().add(returnStatement);
        return method;
    }


    public PsiMethod newCreateMethodWhenNoBuilder(TargetClassProperties targetClassProperties) {

        PsiMethod method = PsiMethodUtil.newStaticMethod("create", targetClass, factory);

        ArrayList<String> paramNames = new ArrayList<>();
        for (SetterProperties properties : targetClassProperties.getSettersFromGetters()) {
            PsiParameter parameter = factory.createParameter(properties.getParameterName(),
                    properties.getParameterType());

            method.getParameterList().add(parameter);
            paramNames.add(properties.getParameterName());

        }

        String paramList = "";

        for (String paramName : paramNames) {
            paramList = paramList + paramName + ", ";
        }

        if (!paramList.equals("")) {
            paramList = paramList.substring(0, paramList.length() - 2);
        }

        String returnStatementText = "return new " + getAutoValueClassName(false) + "(" + paramList + ");";

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        method.getBody().add(returnStatement);
        return method;
    }

    public PsiMethod newBuilderSetter(SetterProperties setterMethod) {
        String builderClassName = PsiClassUtil.getClassName(getBuilderClass());
        PsiMethod method = factory.createMethodFromText(builderClassName + " " + setterMethod.getName() + "(){}", null);
        PsiParameter parameter = factory.createParameter(setterMethod.getParameterName(),
                setterMethod.getParameterType());

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
            String className = PsiClassUtil.getClassName(targetClass);
            PsiMethod newBuildMethod = factory.createMethodFromText(className + " build(){}", null);
            newBuildMethod.getBody().delete();
            newBuildMethod.getModifierList().setModifierProperty("public", true);
            newBuildMethod.getModifierList().setModifierProperty("abstract", true);
            this.buildMehtod = newBuildMethod;

        }

        return this.buildMehtod;

    }

    public PsiMethod newBuilderFactoryMethod() {
        PsiMethod builderMethod = PsiMethodUtil.newStaticMethod("builder", getBuilderClass(), factory);

        String autoValueClassName = getAutoValueClassName(true);
        String builderPart = ".Builder();";

        if(getBuilderClass().hasTypeParameters()) {
            builderPart = ".Builder<>();";
        }

        String returnStatementText = "return new " + autoValueClassName + builderPart;

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        builderMethod.getBody().add(returnStatement);
        return builderMethod;
    }

    @NotNull
    private String getAutoValueClassName(boolean useAsQualifier) {
        String generatedName = "";

        for (PsiClass parent : PsiClassUtil.findAllParents(getTargetClass())) {
            generatedName = generatedName + parent.getName() + "_";
        }

        if(getTargetClass().hasTypeParameters() && !useAsQualifier) {
            generatedName = generatedName + getTargetClass().getName() + "<>";
        } else {
            generatedName = generatedName + getTargetClass().getName();
        }


        String autoValueAnnotationName = StringUtil.getShortName(autoValueAnnotation.getQualifiedName());
        return autoValueAnnotationName + "_" + generatedName;
    }

    @Nullable
    private PsiAnnotation findAutoValueAnnotationClass() {
        for (String autoValueAnnotationName : PsiMethodUtil.SUPPORTED_AUTOVALUE_LIBRARIES) {
            PsiAnnotation autoValueAnnotation = getTargetClass().getModifierList().findAnnotation(autoValueAnnotationName);
            if (autoValueAnnotation != null) {
                return autoValueAnnotation;
            }
        }

        return null;
    }


}