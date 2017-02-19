package com.afcastano.intellij.autovalue.util;

import com.afcastano.intellij.autovalue.util.typeproperties.SetterProperties;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PsiClassUtil {

    public static boolean isBuilderClass(PsiClass clz) {
        PsiAnnotation[] annotations = clz.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            if (annotation.getQualifiedName().contains("Builder")) {
                return true;
            }
        }

        return false;
    }

    public static List<PsiMethod> getGettersFromAllExtendedInterfaces(PsiClass targetClass) {
        List<PsiMethod> abstractGetters = new ArrayList<>();
        PsiClass[] interfaces = targetClass.getInterfaces();

        for (PsiClass currentInterface : interfaces) {
            if (isBlackListedInterface(currentInterface)) {
                continue;
            }
            List<PsiMethod> interfaceGetters = gettersFromInterface(currentInterface);

            for (PsiMethod method : interfaceGetters) {
                //If exists don't add it twice
                if (PsiMethodUtil.containsMethodByName(method.getName(), abstractGetters)) {
                    continue;
                }

                abstractGetters.add(method);
            }

        }

        return abstractGetters;
    }

    private static boolean isBlackListedInterface(PsiClass psiInterface) {
        String qualifiedName = psiInterface.getQualifiedName();
        if (qualifiedName == null) {
            return false;
        }

        return qualifiedName.equals("android.os.Parcelable")
                || qualifiedName.startsWith("java.util.");
    }

    public static List<PsiMethod> gettersFromInterface(PsiClass interfaceClass) {
        List<PsiMethod> abstractGetters = new ArrayList<>();

        abstractGetters.addAll(getGettersFromAllExtendedInterfaces(interfaceClass));

        for (PsiMethod psiMethod : interfaceClass.getMethods()) {
            //If exists don't add it twice
            if (PsiMethodUtil.containsMethodByName(psiMethod.getName(), abstractGetters)) {
                continue;
            }

            if (PsiMethodUtil.isAbstractGetter(psiMethod) && !PsiMethodUtil.isReservedMethod(psiMethod)) {
                abstractGetters.add(psiMethod);
            }
        }

        return abstractGetters;
    }

    @NotNull
    public static List<PsiMethod> getAbstractGetters(PsiClass targetClass) {
        List<PsiMethod> abstractGetters = new ArrayList<>();
        abstractGetters.addAll(getGettersFromAllExtendedInterfaces(targetClass));

        for (PsiMethod psiMethod : targetClass.getMethods()) {
            abstractGetters = PsiMethodUtil.removeMethodByName(psiMethod.getName(), abstractGetters);
            if (PsiMethodUtil.isAbstractGetter(psiMethod) && !PsiMethodUtil.isReservedMethod(psiMethod)) {
                abstractGetters.add(psiMethod);
            }
        }

        return abstractGetters;
    }

    public static boolean alreadyInBuilder(PsiClass builderClass, SetterProperties setter) {
        for (PsiMethod method : builderClass.getAllMethods()) {
            if (PsiMethodUtil.isBuilderSetter(method) && method.getName().equals(setter.getName())) {
                return true;
            }
        }
        return false;
    }

    public static List<PsiClass> findAllParents(PsiClass childClass) {
        List<PsiClass> parents = new ArrayList<>();
        PsiClass parent = PsiTreeUtil.getParentOfType(childClass, PsiClass.class);

        if (parent == null) {
            return parents;
        }

        parents.addAll(findAllParents(parent));
        parents.add(parent);
        return parents;
    }

    @Nullable
    public static PsiClass findExistingBuilderClass(PsiClass targetClass) {
        for (PsiClass clz : targetClass.getInnerClasses()) {
            if (isBuilderClass(clz)) {
                return clz;
            }
        }
        return null;
    }

    public static PsiClass loadTargetClass(Editor editor, PsiJavaFile javaFile) {
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = javaFile.findElementAt(caretOffset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

}
