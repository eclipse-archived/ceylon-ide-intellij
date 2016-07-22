package org.intellij.plugins.ceylon.ide.ceylonCode.model;

/**
 * Exposes Ceylon compiler's internal annotations to Ceylon code.
 */
public enum Annotations {
    attribute(com.redhat.ceylon.compiler.java.metadata.Attribute.class),
    annotationInstantiation(com.redhat.ceylon.compiler.java.metadata.AnnotationInstantiation.class),
    annotationType(ceylon.language.AnnotationAnnotation$annotation$.class) {
        @Override
        public String getClassName() {
            //work around bug in intelliJ's decompiler
            return "ceylon.language.AnnotationAnnotation.annotation$";
        }
    },
    clazz(com.redhat.ceylon.compiler.java.metadata.Class.class),
    object(com.redhat.ceylon.compiler.java.metadata.Object.class),
    method(com.redhat.ceylon.compiler.java.metadata.Method.class),
    container(com.redhat.ceylon.compiler.java.metadata.Container.class),
    localContainer(com.redhat.ceylon.compiler.java.metadata.LocalContainer.class),
    ceylon(com.redhat.ceylon.compiler.java.metadata.Ceylon.class),
    ignore(com.redhat.ceylon.compiler.java.metadata.Ignore.class),
    deprecated(java.lang.Deprecated.class),
    packageDescriptor(com.redhat.ceylon.compiler.java.metadata.Package.class),
    moduleDescriptor(com.redhat.ceylon.compiler.java.metadata.Module.class);

    final Class<?> klazz;

    public String getClassName() {
        return klazz.getName();
    }

    private Annotations(Class<?> klazz) {
        this.klazz = klazz;
    }
}
