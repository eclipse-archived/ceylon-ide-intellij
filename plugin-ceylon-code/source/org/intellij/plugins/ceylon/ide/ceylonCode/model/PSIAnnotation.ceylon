import ceylon.collection {
    HashMap
}
import ceylon.interop.java {
    javaString
}

import com.intellij.psi {
    PsiAnnotation,
    PsiLiteralExpression,
    PsiAnnotationMemberValue,
    PsiArrayInitializerMemberValue,
    PsiReferenceExpression,
    PsiClassObjectAccessExpression
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotationMirror
}

import java.lang {
    JShort=Short
}
import java.util {
    ArrayList
}

class PSIAnnotation(shared PsiAnnotation psi) satisfies AnnotationMirror {

    value values = HashMap<String, Object>();

    Object convert(PsiAnnotationMemberValue v, String paramName) {
        if (is PsiArrayInitializerMemberValue v) {
            value inits = v.initializers.array.coalesced;
            value values = ArrayList<Object>(inits.size);
            inits.each((_) => values.add(convert(_, paramName)));
            
            return values;
        } else if (is PsiAnnotation v) {
            return PSIAnnotation(v);
        } else if (is PsiReferenceExpression v) {
            return javaString(v.referenceName);
        } else if (is PsiClassObjectAccessExpression v) {
            return PSIType(v.operand.type);
        } else if (is PsiLiteralExpression v) {
            // TODO this is super ultra ugly, but we can't get the type associated
            // to a PsiArrayInitializerMemberValue, and IJ parses shorts as ints :(
            if (doWithLock(() => psi.qualifiedName) == "com.redhat.ceylon.compiler.java.metadata.AnnotationInstantiation",
                paramName == "arguments") {

                return JShort(v.text);
            }
            return v.\ivalue;
        } else {
            platformUtils.log(Status._WARNING,
                "unsupported PsiAnnotationMemberValue ``className(v)``");
            return v;
        }
    }
    
    doWithLock(() {
        psi.parameterList.attributes.array.coalesced.each((pair) {
            value name = pair.name else "value";
            value val = convert(pair.\ivalue, name);
            
            values.put(name, val);
        });
    });
    
    shared actual Object? getValue(String name) => values.get(name);
    
    shared actual Object? \ivalue => getValue("value");
}
