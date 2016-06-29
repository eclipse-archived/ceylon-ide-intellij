import ceylon.collection {
    HashMap
}

import com.intellij.psi {
    PsiAnnotation,
    PsiLiteralExpression,
    PsiArrayInitializerMemberValue,
    PsiReferenceExpression,
    PsiClassObjectAccessExpression,
    PsiNameValuePair,
    PsiMethod,
    PsiType,
    PsiArrayType,
    PsiAnnotationMemberValue
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.util {
    toJavaString
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotationMirror
}

import java.lang {
    JShort=Short
}
import java.util {
    ArrayList,
    Collections
}

class PSIAnnotation(shared PsiAnnotation psi) satisfies AnnotationMirror {

    value values = HashMap<String, Object?>();

    // somehow, IntelliJ returns a single value when it reads things like
    // `@MyAnnotation({...})`, so we have to make sure we return the correct type
    Object? toListIfNeeded(Object? o, PsiType? type) {
        return if (is PsiArrayType type)
        then Collections.singletonList(o)
        else o;
    }

    Object? convert(PsiAnnotationMemberValue|PsiNameValuePair pair, String paramName) {
        PsiType? type;
        if (exists ref = pair.reference,
            is PsiMethod method = ref.resolve()) {

            type = method.returnType;
        } else {
            type = null;
        }

        value v = if (is PsiNameValuePair pair) then pair.\ivalue else pair;

        if (is PsiArrayInitializerMemberValue v) {
            value inits = v.initializers.array.coalesced;
            value values = ArrayList<Object>(inits.size);
            inits.each((_) => values.add(convert(_, paramName)));
            
            return values;
        } else if (is PsiAnnotation v) {
            return toListIfNeeded(PSIAnnotation(v), type);
        } else if (is PsiReferenceExpression v) {
            return toListIfNeeded(toJavaString(v.referenceName), type);
        } else if (is PsiClassObjectAccessExpression v) {
            return PSIType(v.operand.type);
        } else if (is PsiLiteralExpression v) {
            // TODO this is super ultra ugly, but we can't get the type associated
            // to a PsiArrayInitializerMemberValue, and IJ parses shorts as ints :(
            if (doWithLock(() => psi.qualifiedName else "") == "com.redhat.ceylon.compiler.java.metadata.AnnotationInstantiation",
                paramName == "arguments") {

                return JShort(v.text);
            }
            return toListIfNeeded(v.\ivalue, type);
        } else {
            platformUtils.log(Status._WARNING,
                "unsupported PsiAnnotationMemberValue ``className(v)``");
            return v;
        }
    }

    doWithLock(() {
        psi.parameterList.attributes.array.coalesced.each((attr) {
            value name = attr.name else "value";
            value val = convert(attr, name);
            
            values.put(name, val);
        });
    });
    
    shared actual Object? getValue(String name) => values.get(name);
    
    shared actual Object? \ivalue => getValue("value");
}
