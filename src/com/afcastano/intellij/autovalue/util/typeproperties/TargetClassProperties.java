package com.afcastano.intellij.autovalue.util.typeproperties;

import com.afcastano.intellij.autovalue.util.PsiClassUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class TargetClassProperties {
    private List<SetterProperties> settersFromGetters;
    private boolean usesJavaBeansStyle = true;

    private TargetClassProperties(List<SetterProperties> settersFromGetters) {
        this.settersFromGetters = settersFromGetters;
        this.usesJavaBeansStyle = shouldUseJavaBeansStyle(settersFromGetters);
        overrideJavaBeansStyle(settersFromGetters);

    }

    private void overrideJavaBeansStyle(List<SetterProperties> setters) {
        for(SetterProperties properties: setters) {
            properties.overrideJavaBeansStyle(this.usesJavaBeansStyle);
        }
    }

    private boolean shouldUseJavaBeansStyle(List<SetterProperties> getters) {
        //Only one property not using java beans is enough.
        for (SetterProperties props: getters) {
            if (!props.isJavaBeanStyle()){
                return false;
            }
        }

        return true;
    }

    public List<SetterProperties> getSettersFromGetters() {
        return settersFromGetters;
    }

    public static TargetClassProperties fromPsiClass(PsiClass autoVClass) {
        List<PsiMethod> getterMethods = PsiClassUtil.getAbstractGetters(autoVClass);
        List<SetterProperties> getterProps = new ArrayList<>();

        for (PsiMethod psiMethod : getterMethods) {
            getterProps.add(SetterProperties.fromGetter(psiMethod));
        }

        return new TargetClassProperties(getterProps);
    }
}
