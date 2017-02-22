package com.afcastano.intellij.autovalue.util;

import com.afcastano.intellij.autovalue.util.typeproperties.SetterProperties;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
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

    public static PsiClass createClass(Project project, String className, PsiTypeParameter[] typeParameters) {
        PsiClass newClass = JavaPsiFacade.getElementFactory(project).createClass(className);

        if (typeParameters.length > 0) {
            newClass = createParameterisedClass(project, className, typeParameters);
        }

        return newClass;
    }

    private static PsiClass createParameterisedClass(Project project,
                                                    String className, PsiTypeParameter[] typeParameters) {

        String fullClassName = getParameterisedClassName(className, typeParameters);
        String classContent = "class "+ fullClassName + "{}";
        return createJavaClass(project, className, classContent);
    }

    public static String getClassName(PsiClass psiClass) {
        if(!psiClass.hasTypeParameters()) {
            return psiClass.getName();
        }

        return getParameterisedClassName(psiClass.getName(), psiClass.getTypeParameters());
    }

    public static String getTypeParameterString(PsiTypeParameter[] typeParameters) {
        if (typeParameters.length > 0) {

            List<String> typeParamStr = new ArrayList<>();
            for(PsiTypeParameter tp: typeParameters) {
                typeParamStr.add(tp.getName());
            }

            String paramStr = StringUtil.join(typeParamStr, ", ");
            return "<" + paramStr + ">";
        }

        return "";
    }

    @NotNull
    private static String getParameterisedClassName(String name, PsiTypeParameter[] typeParameters) {
        return name + getTypeParameterString(typeParameters);
    }

    private static PsiClass createJavaClass(Project project, String className, String classContent) {
        PsiClass newBuilderClass;
        PsiJavaFile psiFile = (PsiJavaFile) PsiFileFactory.getInstance(project)
                .createFileFromText(className + ".java",
                JavaFileType.INSTANCE,
                classContent);

        newBuilderClass = psiFile.getClasses()[0];
        return newBuilderClass;
    }

}
