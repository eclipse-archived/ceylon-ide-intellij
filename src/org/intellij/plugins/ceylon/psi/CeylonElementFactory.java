package org.intellij.plugins.ceylon.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

public enum CeylonElementFactory {
    ;

    @NotNull
    public static PsiElement createAnnotation(@NotNull String annotationName, @NotNull Project project) {
        CeylonFile ceylonFile = (CeylonFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.ceylon", CeylonLanguage.INSTANCE, annotationName + " class Foo(){}");

        //noinspection ConstantConditions
        return ceylonFile.findChildByClass(CeylonDeclaration.class).getAnnotations().getAnnotationList().get(0);
    }
}
