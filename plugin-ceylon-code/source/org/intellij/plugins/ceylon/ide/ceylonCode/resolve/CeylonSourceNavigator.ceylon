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
    PsiElement
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsMethodImpl,
    ClsMemberImpl
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.util {
    FindDeclarationNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.util {
    List,
    Collections
}
import java.util.concurrent {
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    Annotations
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTreeUtil,
    CeylonFile
}

shared class CeylonSourceNavigator() extends GeneratedSourcesFilter() {

    isGeneratedSource(VirtualFile file, Project project) => false;

    function modeltoPsi(Declaration member, Tree.CompilationUnit cu, CeylonFile file) {
        value vis = FindDeclarationNodeVisitor(member);
        vis.visit(cu);
        if (exists result = vis.declarationNode,
            exists psi = CeylonTreeUtil.findPsiElement(result, file)) {

            return Collections.singletonList(psi);
        }

        return null;
    }

    shared actual List<out PsiElement> getOriginalElements(PsiElement element) {
        if (is ClsClassImpl element,
            is CeylonFile file = element.containingFile.navigationElement,
            exists [decl, cu] = findClassModel(element, file),
            exists psi = modeltoPsi(decl, cu, file)) {

            return psi;
        }

        if (is ClsMethodImpl element,
            is ClsClassImpl parent = element.parent,
            is CeylonFile file = element.containingFile.navigationElement,
            exists [decl, cu] = findClassModel(parent, file)) {

            value matchingName = ArrayList<Declaration>();

            for (member in decl.members) {
                if (exists memberName = member.name,
                    exists clsName = getCeylonSimpleName(element),
                    memberName == clsName) {

                    matchingName.add(member);
                }
            }

            if (matchingName.size == 1,
                exists first = matchingName.first,
                exists psi = modeltoPsi(first, cu, file)) {

                return psi;
            }

            if (exists psi = modeltoPsi(decl, cu, file)) {
                return psi;
            }
        }

        return super.getOriginalElements(element);
    }

    String? getCeylonSimpleName(ClsMemberImpl<out Anything> member) {
        variable value name = member.name;

        if (is ClsMethodImpl member) {
            if (name.startsWith("$default$")) {
                name = name.substring("$default$".size);
            } else if (name == "get_") {
                if (member.hasModifierProperty("static"),
                    member.parameterList.parametersCount == 0) {
                    return null;
                }
            } else if (name.startsWith("$")) {
                name = name.substring(1);
            } else if (name.startsWith("get"), name.size > 3, member.parameterList.parametersCount == 0) {
                name = (name[3]?.lowercased else ' ').string + name.spanFrom(4);
            } else if (name.startsWith("set"), name.size > 3, member.parameterList.parametersCount == 1) {
                name = (name[3]?.lowercased else ' ').string + name.spanFrom(4);
            } else if (name == "toString") {
                name = "string";
            } else if (name == "hashCode") {
                name = "hash";
            } else if (name.contains("$")) {
                // TODO
            }
            if (name.endsWith("$canonical$")) {
                name = name.substring(0, name.size - "$canonical$".size);
            }
            if (name.endsWith("$priv$")) {
                name = name.substring(0, name.size - "$priv$".size);
            }
        } else if (is ClsClassImpl member,
            name.endsWith("_"),
            member.modifierList.findAnnotation(Annotations.\iobject.className) exists
            || member.modifierList.findAnnotation(Annotations.method.className) exists) {

            name = name.trimTrailing('_'.equals);
        }

        return name;
    }

    [Declaration, Tree.CompilationUnit]? findClassModel(ClsClassImpl cls, CeylonFile file) {
        if (exists pu = file.waitForUpToDatePhasedUnit(4, TimeUnit.seconds),
            exists cu = pu.compilationUnit) {

            for (decl in pu.declarations) {
                if (exists ceylonName = getCeylonSimpleName(cls),
                    decl.name == ceylonName) {

                    return [decl, cu];
                }
            }
        }

        return null;
    }
}
