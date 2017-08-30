import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFileManager,
    VirtualFile
}
import com.intellij.psi {
    PsiManager,
    PsiNameIdentifierOwner,
    PsiFile,
    PsiElement,
    PsiLiteralExpression,
    PsiMethod
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.ide.common.open {
    AbstractNavigation
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import com.redhat.ceylon.model.loader.model {
    JavaBeanValue,
    LazyClass,
    LazyInterface,
    JavaMethod,
    FieldValue,
    AnnotationProxyClass
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ClassOrInterface,
    Referenceable,
    Function,
    Value,
    ModelUtil
}

import java.lang {
    Integer
}

import org.intellij.plugins.ceylon.ide.model {
    PSIClass,
    PSIMethod,
    concurrencyManager {
        withAlternateResolution
    },
    PsiElementGoneException
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonTreeUtil
}


shared PsiNameIdentifierOwner? declarationToPsi(Declaration rawDeclaration) {
    function getJavaClass(ClassOrInterface cls) {
        switch (cls)
        case (is LazyClass) {
            if (is PSIClass mirror = cls.classMirror) {
                return mirror.psi;
            }
        }
        case (is LazyInterface) {
            if (is PSIClass mirror = cls.classMirror) {
                return mirror.psi;
            }
        }
        else {}
        return null;
//        value qn = cls.qualifiedNameString.replace("::", ".");
//
//        return concurrencyManager.withAlternateResolution {
//            func() => concurrencyManager.needIndexes(project, () {
//                value facade = JavaPsiFacade.getInstance(project);
//                value scope = GlobalSearchScope.allScope(project);
//
//                if (exists psi = facade.findClass(qn, scope)) {
//                    return psi;
//                }
//                return null;
//            });
//        };
    }

    function hasAnnotationName(PsiMethod ctor, Declaration declaration)
            => if (exists ann = ctor.modifierList.findAnnotation("com.redhat.ceylon.compiler.java.metadata.Name"),
                   is PsiLiteralExpression name = ann.findAttributeValue("value"),
                   exists text = name.\ivalue?.string)
            then text == declaration.name
            else false;


    value declaration
            = if (is Function rawDeclaration, rawDeclaration.annotation)
            then rawDeclaration.typeDeclaration
            else rawDeclaration;

    try {
        switch (declaration)
        case (is LazyClass) {
            if (is PSIMethod meth = declaration.constructor,
                meth.psi.canNavigate()) {
                return meth.psi;
            }
            if (is PSIClass cls = declaration.classMirror) {
                return cls.psi;
            }
        } case (is LazyInterface) {
            if (is PSIClass cls = declaration.classMirror) {
                return cls.psi;
            }
        } case (is JavaMethod) {
            if (is PSIMethod meth = declaration.mirror) {
                return meth.psi;
            }
        } case (is JavaBeanValue) {
            if (is PSIMethod meth = declaration.mirror) {
                return meth.psi;
            }
        } case (is FieldValue) {
            if (is ClassOrInterface container = declaration.container,
                exists cls = getJavaClass(container)) {
                return cls.findFieldByName(declaration.realName, true);
            }
        }
        else
        case (is Value) {
            if (is AnnotationProxyClass container = declaration.container,
                is PSIClass cls = container.iface.classMirror) {
                return cls.psi.findMethodsByName(declaration.name, false).array.first;
            }
        }
        else if (ModelUtil.isConstructor(declaration),
            is LazyClass container = declaration.container,
            is PSIClass cls = container.classMirror) {

            for (ctor in cls.psi.constructors) {
                if (hasAnnotationName(ctor, declaration)) {
                    return ctor;
                }
            }
            return cls.psi.findMethodsByName(declaration.name, false).array.first;
        }
    }
    catch (PsiElementGoneException e) {}
    return null;
}

shared class IdeaNavigation(Project project)
        extends AbstractNavigation<PsiElement,VirtualFile>() {
    
    filePath(VirtualFile file) => Path(file.path);
    
    gotoDeclaration(Referenceable? model)
            => withAlternateResolution(() => super.gotoDeclaration(model));

    function findElementAtOffset(PsiFile psiFile, Integer offset)
            => PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset.intValue(), `PsiElement`, false);

    shared actual PsiElement? gotoFile(VirtualFile file, Integer offset, Integer length) {
//        try {
            if (file.\iexists(),
                exists psiFile = PsiManager.getInstance(project).findFile(file)) {
                return findElementAtOffset(psiFile, offset);
            }
//        }
//        catch (Throwable t) {
//            //IDEA really throws Throwable from findFile()
//        }
        return null;
    }
    
    gotoJavaNode(Declaration rawDeclaration) => declarationToPsi(rawDeclaration);

    shared actual PsiElement? gotoLocation(Path? path, Integer offset, Integer length) {
        if (exists path) {
            value pathWithProtocol = CeylonTreeUtil.withProtocol(path.string);
//        try {
            if (exists file = VirtualFileManager.instance.findFileByUrl(pathWithProtocol),
                file.\iexists(),
                exists psiFile = PsiManager.getInstance(project).findFile(file)) {
                return findElementAtOffset(psiFile, offset);
            }
//        }
//        catch (Throwable t) {
//            //IDEA really throws Throwable from findFile()
//        }
        }
        return null;
    }

}
