package com.afcastano.intellij.autovalue.util;

import com.afcastano.intellij.autovalue.generator.AutoValueFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PsiMethodUtil {


    public static final ImmutableList<String> SUPPORTED_AUTOVALUE_LIBRARIES =
            ContainerUtil.immutableList(
                    "com.google.auto.value.AutoValue",
                    "auto.parcel.AutoParcel",
                    "auto.parcelgson.AutoParcelGson"
            );
    public static Logger LOG = Logger.getInstance(AutoValueFactory.class);

    public static boolean containsMethodByName(String name, List<PsiMethod> methods) {
        for (PsiMethod psiMethod : methods) {
            if(name.equals(psiMethod.getName())) {
                return true;
            }

        }

        return false;
    }

    public static boolean isGetter(PsiMethod psiMethod) {
        if (psiMethod.isConstructor()) {
            return false;
        }

        boolean noParameters = psiMethod.getParameterList().getParametersCount() == 0;
        boolean returnsSomething = !psiMethod.getReturnType().equals(PsiType.VOID);
        boolean isStatic = psiMethod.getModifierList().hasExplicitModifier("static");
        return noParameters && returnsSomething && !isStatic;
    }

    public static boolean isBuilderSetter(PsiMethod psiMethod) {
        if (psiMethod.isConstructor()) {
            return false;
        }

        String methodReturnName = psiMethod.getReturnType().getPresentableText();
        return methodReturnName.equals("Builder");
    }

    public static boolean alreadyInClass(PsiClass targetClass, PsiMethod psiMethod) {
        for (PsiMethod method : targetClass.getAllMethods()) {
            if (isAbstractGetter(method) && method.getName().equals(psiMethod.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAbstractGetter(PsiMethod psiMethod) {
        if(psiMethod.isConstructor()) {
            return false;
        }

        boolean isAbstract = psiMethod.getModifierList().hasModifierProperty("abstract");
        boolean noParameters = psiMethod.getParameterList().getParametersCount() == 0;
        boolean returnsSomething = !psiMethod.getReturnType().equals(PsiType.VOID);
        boolean noBody = psiMethod.getBody() == null;
        return isAbstract && noParameters && returnsSomething && noBody;
    }

    public static boolean isReservedMethod(PsiMethod psiMethod) {
        return isAbstractGetter(psiMethod) && "Builder".equals(psiMethod.getReturnType().getPresentableText());
    }

    public static List<PsiMethod> removeMethodByName(String name, List<PsiMethod> methods) {
        final List<PsiMethod> abstractGetters = new ArrayList<>();
        for (PsiMethod psiMethod : methods) {
            if(name.equals(psiMethod.getName())) {
                continue;
            }

            abstractGetters.add(psiMethod);
        }

        return abstractGetters;
    }

    public static PsiMethod newStaticMethod(String methodName, PsiClass returnClass, PsiElementFactory factory) {
        String returnTypeName = PsiClassUtil.getClassName(returnClass);
        String returnTypeParameters = PsiClassUtil.getTypeParameterString(returnClass.getTypeParameters());
        String methodText = returnTypeParameters + " " + returnTypeName + " " + methodName + "(){}";

        PsiMethod method = factory.createMethodFromText(methodText.trim(), null);
        method.getModifierList().setModifierProperty("public", true);
        method.getModifierList().setModifierProperty("static", true);
        return method;
    }

}
