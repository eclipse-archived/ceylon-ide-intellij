import ceylon.language {
    nil=null
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.pom.java {
    LanguageLevel
}
import com.intellij.psi {
    PsiClassType,
    PsiClass,
    PsiAnnotation,
    PsiTypeVisitor,
    PsiType
}
import com.intellij.psi.impl {
    EmptySubstitutorImpl
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.util.containers {
    ContainerUtil
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.ide.common.model.asjava {
    AbstractClassMirror
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyClass
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.model {
    PSIClass,
    PSIType
}

PsiClassType createType(TypeMirror mirror, Project project)
        => if (is PSIType mirror,
               is PsiClassType psiType = mirror.psi)
        then psiType
        else CeyLightType(mirror, project);

class CeyLightType(TypeMirror mirror, Project project)
        extends PsiClassType(LanguageLevel.highest, PsiAnnotation.emptyArray) {
    
    presentableText => mirror.string;

    canonicalText => mirror.qualifiedName.replace("::", ".");

    internalCanonicalText => mirror.qualifiedName;

    valid => true;

    equalsToText(String text) => false;

    resolveScope => GlobalSearchScope.emptyScope;

    resolve() => nil;

    className
            => if (exists cls = mirror.declaredClass)
            then cls.name
            else "unknown";

    shared actual ObjectArray<PsiType> parameters {
        value types = ArrayList<PsiType>();
        for (type in mirror.typeArguments) {
            types.add(createType(type, project));
        }
        if (types.empty) {
            return PsiType.emptyArray;
        }
        return ContainerUtil.toArray(types, PsiType.arrayFactory);
    }

    superTypes => PsiType.emptyArray;

    variable LanguageLevel level = LanguageLevel.highest;
    languageLevel => level;

    object classResolveResult satisfies ClassResolveResult {

        shared actual PsiClass? element {
            if (is AbstractClassMirror mirror = mirror.declaredClass) {
                if (is CeylonUnit unit = mirror.decl.unit) {
                    return CeyLightClass.fromMirror(mirror, project);
                } else if (is LazyClass cls = mirror.decl,
                           is PSIClass psiCls = cls.classMirror) {
                    return psiCls.psi;
                } 
            }
            
            return nil;
        }

        substitutor => EmptySubstitutorImpl();
        packagePrefixPackageReference => false;
        accessible => true;
        staticsScopeCorrect => true;
        currentFileResolveScope => nil;
        validResult => true;
    }

    resolveGenerics() => classResolveResult;

    shared actual A? accept<A>(PsiTypeVisitor<A> visitor)
            given A satisfies Object
            => visitor.visitClassType(this);

    rawType() => this;

    shared actual PsiClassType setLanguageLevel(LanguageLevel languageLevel) {
        this.level = languageLevel;
        return this;
    }
}
