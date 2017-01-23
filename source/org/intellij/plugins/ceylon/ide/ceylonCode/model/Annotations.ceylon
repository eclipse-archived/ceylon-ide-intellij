

shared class Annotations {
    
    shared actual String string;
    shared String className;
    
    abstract new create(String string, String className) {
        this.string = string;
        this.className = className;
    }

    shared new attribute extends create("attribute",
        "com.redhat.ceylon.compiler.java.metadata.Attribute") {}

    shared
    new annotationInstantiation extends create("annotationInstantiation",
        "com.redhat.ceylon.compiler.java.metadata.AnnotationInstantiation") {}

    shared
    new annotationType extends create("annotationType",
        //work around bug in intelliJ's decompiler
        "ceylon.language.AnnotationAnnotation.annotation$") {}

    shared
    new \iclass extends create("clazz",
        "com.redhat.ceylon.compiler.java.metadata.Class") {}

    shared
    new \iobject extends create("object",
        "com.redhat.ceylon.compiler.java.metadata.Object") {}

    shared
    new method extends create("method",
        "com.redhat.ceylon.compiler.java.metadata.Method") {}

    shared
    new container extends create("container",
        "com.redhat.ceylon.compiler.java.metadata.Container") {}

    shared
    new localContainer extends create("localContainer",
        "com.redhat.ceylon.compiler.java.metadata.LocalContainer") {}

    shared
    new ceylon extends create("ceylon",
        "com.redhat.ceylon.compiler.java.metadata.Ceylon") {}

    shared
    new ignore extends create("ignore",
        "com.redhat.ceylon.compiler.java.metadata.Ignore") {}

    shared
    new deprecated extends create("deprecated",
        "java.lang.Deprecated") {}

    shared
    new packageDescriptor extends create("packageDescriptor",
        "com.redhat.ceylon.compiler.java.metadata.Package") {}

    shared
    new moduleDescriptor extends create("moduleDescriptor",
        "com.redhat.ceylon.compiler.java.metadata.Module") {}

    shared
    new constructorName extends create("constructorName",
        "com.redhat.ceylon.compiler.java.metadata.ConstructorName") {}
}
