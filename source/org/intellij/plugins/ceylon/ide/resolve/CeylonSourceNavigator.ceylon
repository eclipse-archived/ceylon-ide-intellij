import ceylon.collection {
    ArrayList
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.roots {
    GeneratedSourcesFilter
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiElement,
    PsiElementVisitor,
    PsiNameIdentifierOwner,
    PsiMethod,
    PsiField
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsMethodImpl,
    ClsMemberImpl,
    ClsFieldImpl
}
import com.intellij.psi.impl.light {
    LightElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.ide.common.util {
    FindDeclarationNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    TypeDeclaration,
    Value,
    TypedDeclaration
}

import java.util {
    List,
    Collections {
        singletonList
    }
}
import java.util.concurrent {
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.model {
    Annotations
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonTreeUtil,
    CeylonFile
}

shared CeylonSourceNavigator ceylonSourceNavigator = CeylonSourceNavigator();

shared class CeylonSourceNavigator() extends GeneratedSourcesFilter() {

    isGeneratedSource(VirtualFile file, Project project) => false;

    function modelToPsi(Declaration member, Tree.CompilationUnit cu, CeylonFile file) {
        Tree.CompilationUnit? actualCu;
        CeylonFile actualFile;

        if (member.unit != cu.unit,
            is CeylonUnit unit = member.unit,
            is CeylonFile f = CeylonTreeUtil.getDeclaringFile(unit, file.project)) {
            actualCu = unit.compilationUnit;
            actualFile = f;
        } else {
            actualCu = cu;
            actualFile = file;
        }

        if (exists actualCu) {
            value vis = FindDeclarationNodeVisitor(member);
            vis.visit(actualCu);
            if (exists result = vis.declarationNode,
                exists psi = CeylonTreeUtil.findPsiElement(result, actualFile)) {

                return psi;
            }
        }

        return null;
    }

    shared class DelegatingClass(ClsMemberImpl<out Anything> binaryClass, CeylonFile file, PsiElement? sourcePsi())
            extends LightElement(binaryClass.manager, CeylonLanguage.instance) {

        parent => binaryClass.parent;

        containingFile => file;

        shared PsiElement? delegate => sourcePsi();

        shared actual void accept(PsiElementVisitor visitor) {
            if (exists theSourcePsi = sourcePsi()) {
                theSourcePsi.accept(visitor);
            } else {
                super.accept(visitor);
            }
        }

        findElementAt(Integer offset)
                => sourcePsi()?.findElementAt(offset)
                else super.findElementAt(offset);

        children => sourcePsi()?.children else super.children;
        node => sourcePsi()?.node else super.node;
        nextSibling => sourcePsi()?.nextSibling else super.nextSibling;
        prevSibling => sourcePsi()?.prevSibling else super.prevSibling;
        startOffsetInParent => sourcePsi()?.startOffsetInParent else super.startOffsetInParent;
        string => sourcePsi()?.string else binaryClass.string;
        text => sourcePsi()?.string else "";
        textOffset => sourcePsi()?.textOffset else super.textOffset;
        textRange => sourcePsi()?.textRange else super.textRange;
        name => getCeylonSimpleName(binaryClass) else binaryClass.name;
    }


    function isGetter(PsiMethod member)
            => let (name = member.name)
                   name.startsWith("get")
                && name.longerThan(3)
                && member.parameterList.parametersCount == 0;

    function isSetter(PsiMethod member)
            => let (name = member.name)
                   name.startsWith("set")
                && name.longerThan(3)
                && member.parameterList.parametersCount == 1;

    shared actual List<out PsiElement> getOriginalElements(PsiElement element) {
        if (is ClsClassImpl element,
            is CeylonFile file = element.containingFile.navigationElement) {

            function sourcePsi() {
                if (exists [decl, cu] = findClassModel(element, file),
                    exists psi = modelToPsi(decl, cu, file)) {
                    return psi;
                }
                return null;
            }
            return singletonList(DelegatingClass(element, file, sourcePsi));
        }

        if (is ClsMethodImpl|ClsFieldImpl element,
            is ClsClassImpl parent = element.parent,
            is CeylonFile file = element.containingFile.navigationElement) {

            function sourcePsi() {
                if (exists [decl, cu] = findClassModel(parent, file)) {
                    if (exists first = findMatchingDeclaration(decl, element),
                        exists psi = modelToPsi(first, cu, file)) {

                        return psi;
                    }

                    if (exists psi = modelToPsi(decl, cu, file)) {
                        return psi;
                    }
                }
                return null;
            }

            return singletonList(DelegatingClass(element, file, sourcePsi));
        }

        return super.getOriginalElements(element);
    }

    "Finds a member of [[parent]] that matches the given PSI [[element]]."
    shared Declaration? findMatchingDeclaration(Declaration parent, PsiMethod|PsiField element) {
        value matchingName = ArrayList<Declaration>();
        value ceylonName = getCeylonSimpleName(element);

        for (member in parent.members) {
            if (exists memberName = member.name,
                exists clsName = ceylonName,
                memberName == clsName) {

                if (is PsiMethod element) {
                    if (isGetter(element)) {
                        if (is Value member) {
                            matchingName.add(member);
                        }
                    } else if (isSetter(element)) {
                        if (is TypedDeclaration member,
                            member.variable) {
                            matchingName.add(member);
                        }
                    } else {
                        matchingName.add(member);
                    }
                }
                else if (is Value member) {
                    matchingName.add(member);
                }
            }
        }

        if (matchingName.empty, is TypeDeclaration parent) {
            for (inherited in parent.getInheritedMembers(ceylonName)) {
                if (inherited.default) {
                    matchingName.add(inherited);
                }
            }
        }

        return matchingName.first;
    }

    shared String? getCeylonSimpleName(PsiNameIdentifierOwner member) {
        variable value name = member.name else "<unknown>";

        switch (member)
        case (is ClsMethodImpl) {
            if (name.startsWith("$default$")) {
                name = name.removeInitial("$default$");
            } else if (name == "get_") {
                if (member.hasModifierProperty("static"),
                    member.parameterList.parametersCount == 0) {
                    return null;
                }
            } else if (name.startsWith("$")) {
                name = name[1...];
            } else if (isGetter(member) || isSetter(member)) {
                name = name[3:1].lowercased + name[4...];
            } else if (name == "toString") {
                name = "string";
            } else if (name == "hashCode") {
                name = "hash";
            } else if (name.contains("$")) {
                // TODO
            }
            if (name.endsWith("$canonical$")) {
                name = name.removeTerminal("$canonical$");
            }
            if (name.endsWith("$priv$")) {
                name = name.removeTerminal("$priv$");
            }
        }
        case (is ClsClassImpl) {
            if (name.endsWith("_"),
                member.modifierList.findAnnotation(Annotations.\iobject.className) exists
                || member.modifierList.findAnnotation(Annotations.attribute.className) exists
                || member.modifierList.findAnnotation(Annotations.constructorName.className) exists
                || member.modifierList.findAnnotation(Annotations.method.className) exists) {

                name = name.trimTrailing('_'.equals);
            } else if (name.endsWith("$impl"),
                member.modifierList.findAnnotation(Annotations.ignore.className) exists) {

                name = name.removeTerminal("$impl");
            }
        }
        else {}

        return name;
    }

    [Declaration, Tree.CompilationUnit]? findClassModel(ClsClassImpl cls, CeylonFile file) {
        if (exists pu = file.waitForUpToDatePhasedUnit(4, TimeUnit.seconds),
            exists cu = pu.compilationUnit) {

            for (decl in pu.declarations) {
                if (decl.toplevel || cls.containingClass exists,
                    exists ceylonName = getCeylonSimpleName(cls),
                    exists declName = decl.name,
                    declName == ceylonName) {

                    return [decl, cu];
                }
            }
        }

        return null;
    }
}
