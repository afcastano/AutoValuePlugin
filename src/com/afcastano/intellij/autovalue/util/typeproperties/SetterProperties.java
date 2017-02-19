package com.afcastano.intellij.autovalue.util.typeproperties;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

public class SetterProperties {
    private String methodName;
    private String noPrefixSetter;
    private String noPrefixParameterName;
    private String javaBeansSetter;
    private String javaBeansParameterName;
    private PsiType returnType;
    private boolean javaBeansStyle;

    private SetterProperties(PsiMethod getter) {
        this.methodName = getter.getName();
        this.noPrefixSetter = this.methodName;
        this.noPrefixParameterName = this.noPrefixSetter;
        this.returnType = getter.getReturnType();

        if (this.methodName.startsWith("get") && this.methodName.length() > 3) {
            if (Character.isUpperCase(this.methodName.charAt(3))) {
                this.javaBeansSetter = this.methodName.replaceFirst("get", "set");
                this.javaBeansParameterName = this.javaBeansSetter.replaceFirst("set", "new");
            }

        } else if (this.methodName.startsWith("is") && this.methodName.length() > 2) {
            if (Character.isUpperCase(this.methodName.charAt(2))) {
                this.javaBeansSetter = this.methodName.replaceFirst("is", "set");
                this.javaBeansParameterName = this.javaBeansSetter.replaceFirst("set", "new");
            }
        }

        this.javaBeansStyle = javaBeansSetter != null;

    }

    public boolean isJavaBeanStyle() {
        return javaBeansStyle;
    }

    public String getParameterName() {
        if (this.javaBeansStyle) {
            return javaBeansParameterName;
        }

        return noPrefixParameterName;
    }

    public String getName() {
        if (this.javaBeansStyle) {
            return javaBeansSetter;
        }

        return noPrefixSetter;
    }

    public PsiType getParameterType() {
        return returnType;
    }

    public static SetterProperties fromGetter(PsiMethod getter) {
        return new SetterProperties(getter);
    }

    void overrideJavaBeansStyle(boolean usesJavaBeansStyle) {
        this.javaBeansStyle = usesJavaBeansStyle;
    }
}