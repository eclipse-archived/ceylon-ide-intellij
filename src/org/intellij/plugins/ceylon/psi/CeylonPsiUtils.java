package org.intellij.plugins.ceylon.psi;

public class CeylonPsiUtils {
    public static boolean hasAnnotation(CeylonAnnotations annotations, String annotationName) {
        if (annotations == null) {
            return false;
        }

        for (CeylonAnnotation annotation : annotations.getAnnotationList()) {
            if (annotation.getAnnotationName().getText().equals(annotationName)) {
                return true;
            }
        }

        return false;
    }
}
