package com.afcastano.intellij.autovalue.util.validation;

import com.afcastano.intellij.autovalue.generator.AutoValueFactory;
import com.afcastano.intellij.autovalue.util.PsiClassUtil;
import com.afcastano.intellij.autovalue.util.PsiMethodUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static boolean isBuilderUpToDate(AutoValueFactory factory) {
        PsiClass builderClass = factory.getBuilderClass();
        if (builderClass == null) {
            return false;
        }
        for (PsiMethod psiMethod : PsiClassUtil.getAbstractGetters(factory.getTargetClass())) {
            if (!PsiClassUtil.alreadyInBuilder(builderClass, psiMethod)) {
                return false;
            }
        }
        for (PsiMethod psiMethod : builderClass.getAllMethods()) {
            if (PsiMethodUtil.isBuilderSetter(psiMethod) && !PsiMethodUtil.alreadyInClass(factory.getTargetClass(), psiMethod)) {
                return false;
            }
        }

        if(!containsBuilderFactoryMethod(factory.getTargetClass())) {
            return false;
        }

        return true;
    }

    public static boolean isCreateMethodUpToDate(PsiClass targetClass) {
        //If the class does not contain a create method, then it is assumed up to date.
        if(!containsCreateMethod(targetClass)) {
            return true;
        }

        //At this point there should be a create method in the class
        PsiMethod createMethod = targetClass.findMethodsByName("create", true)[0];
        PsiParameter[] parameters = createMethod.getParameterList().getParameters();

        // We compare base on names. A more precise comparison could be using the parameter types.
        List<String> parameterNames = new ArrayList<>();

        for(PsiParameter parameter: parameters) {
            parameterNames.add(parameter.getName());
        }

        for (PsiMethod psiMethod : PsiClassUtil.getAbstractGetters(targetClass)) {
            PsiMethodUtil.GetterProperties prop = PsiMethodUtil.GetterProperties.fromGetter(psiMethod);

            if (!parameterNames.contains(prop.setterParameterName)) {
                return false;
            }
        }
        // TODO: Comparing from parameters to class methods is missing. Need to compare return types with param types.


        //Now we check if it uses builder style and the Builder is not there.
        if(createMethod.getBody().getText().contains(".build()") && !containsBuilderClass(targetClass)) {
            return false;
        }

        return true;
    }

    public static boolean containsBuilderFactoryMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("builder", true).length != 0;
    }

    public static boolean containsBuilderClass(PsiClass targetClass) {
        return PsiClassUtil.findExistingBuilderClass(targetClass) != null;
    }

    public static boolean containsCreateMethod(PsiClass targetClass) {
        return targetClass.findMethodsByName("create", true).length != 0;
    }

    public static boolean containsBuildMethod(PsiClass builderClass) {
        return builderClass.findMethodsByName("build", true).length != 0;
    }
}
