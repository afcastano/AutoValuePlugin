package com.afcastano.intellij.autovalue.generator;

import com.afcastano.intellij.autovalue.util.typeproperties.SetterProperties;
import com.afcastano.intellij.autovalue.util.PsiClassUtil;
import com.afcastano.intellij.autovalue.util.PsiMethodUtil;
import com.afcastano.intellij.autovalue.util.typeproperties.TargetClassProperties;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;

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

                final PsiClass newBuilderClass = factory.createClass("Builder");
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
        final PsiMethod method = factory.createMethod("create", getTargetType());
        method.getModifierList().setModifierProperty("public", true);
        method.getModifierList().setModifierProperty("static", true);

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

        String returnStatementText = "return builder()\n" + builderChain + ".build();";

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        method.getBody().add(returnStatement);
        return method;
    }


    public PsiMethod newCreateMethodWhenNoBuilder(TargetClassProperties targetClassProperties) {
        final PsiMethod method = factory.createMethod("create", getTargetType());
        method.getModifierList().setModifierProperty("public", true);
        method.getModifierList().setModifierProperty("static", true);

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

        String returnStatementText = "return new " + getAutoValueClassName() + "(" + paramList + ");";

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        method.getBody().add(returnStatement);
        return method;
    }

    public PsiMethod newBuilderSetter(SetterProperties setterMethod) {
        final PsiMethod method = factory.createMethod(setterMethod.getName(), getBuilderType());
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

        String autoValueClassName = getAutoValueClassName();
        String returnStatementText = "return new " + autoValueClassName + ".Builder();";

        PsiStatement returnStatement = factory.createStatementFromText(returnStatementText, getTargetClass());

        builderMethod.getBody().add(returnStatement);
        return builderMethod;
    }

    @NotNull
    private String getAutoValueClassName() {
        String generatedName = "";

        for (PsiClass parent : PsiClassUtil.findAllParents(getTargetClass())) {
            generatedName = generatedName + parent.getName() + "_";
        }

        generatedName = generatedName + getTargetClass().getName();

        String autoValueAnnotationName = StringUtil.getShortName(autoValueAnnotation.getQualifiedName());
        return autoValueAnnotationName + "_" + generatedName;
    }

    public PsiType getBuilderType() {

        if (builderType == null) {
            this.builderType = factory.createType(getBuilderClass());
        }

        return builderType;
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