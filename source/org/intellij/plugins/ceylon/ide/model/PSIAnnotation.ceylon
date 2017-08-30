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
    PsiAnnotationMemberValue,
    PsiPrefixExpression
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotationMirror
}

import java.lang {
    Types {
        nativeString
    },
    Short,
    Str=String
}
import java.util {
    Collections,
    Arrays,
    HashMap
}

class PSIAnnotation(PsiAnnotation psi) satisfies AnnotationMirror {

    // somehow, IntelliJ returns a single value when it reads things like
    // `@MyAnnotation({...})`, so we have to make sure we return the correct type
    Object? toListIfNeeded(Object? o, PsiType? type)
            => if (is PsiArrayType type)
            then Collections.singletonList(o)
            else o;

    Object? convert(PsiAnnotationMemberValue|PsiNameValuePair pair, String paramName) {
        value type
                = if (exists ref = pair.reference,
                      is PsiMethod method = ref.resolve())
                then method.returnType
                else null;

        switch (v = if (is PsiNameValuePair pair) then pair.\ivalue else pair)
        case (is PsiArrayInitializerMemberValue) {
            return Arrays.asList(for (p in v.initializers) convert(p, paramName));
        }
        else case (is PsiAnnotation) {
            return toListIfNeeded(PSIAnnotation(v), type);
        }
        else case (is PsiReferenceExpression) {
            value jstring
                    = if (exists vrn = v.referenceName)
                    then nativeString(vrn)
                    else null;
            return toListIfNeeded(jstring, type);
        }
        else case (is PsiClassObjectAccessExpression) {
            return PSIType(v.operand.type);
        }
        else case (is PsiLiteralExpression
                    | PsiPrefixExpression) {
            // TODO this is super ultra ugly, but we can't get the type associated
            // to a PsiArrayInitializerMemberValue, and IJ parses shorts as ints :(
            if ((psi.qualifiedName else "")
                    == "com.redhat.ceylon.compiler.java.metadata.AnnotationInstantiation",
                paramName == "arguments") {
                return Short(v.text);
            }

            if (is PsiLiteralExpression v) {
                return toListIfNeeded(v.\ivalue, type);
            }

            return v;
        }
        else {
            platformUtils.log(Status._WARNING,
                "unsupported PsiAnnotationMemberValue ``className(v)``");
            return v;
        }
    }

    //eager!

    value values = HashMap<Str,Object>();
    for (attr in psi.parameterList.attributes) {
        value name = attr.name else "value";
        values[nativeString(name)] = convert(attr, name);
    }

    getValue(String name) => values[nativeString(name)];
    
    \ivalue => getValue("value");
}
