import com.intellij.ide.structureView {
    StructureViewTreeElement
}
import com.intellij.ide.structureView.impl.common {
    PsiTreeElementBase
}
import com.intellij.ide.structureView.impl.java {
    AccessLevelProvider
}
import com.intellij.ide.util.treeView.smartTree {
    SortableTreeElement
}
import com.intellij.navigation {
    ColoredItemPresentation
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import com.intellij.psi.util {
    PsiUtil
}
import com.intellij.util.ui {
    UIUtil
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

import java.util {
    Collections,
    List
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    descriptions
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

abstract class CeylonDeclarationTreeElement<Decl>
            (Decl psiElement, Boolean isInherited)
        extends PsiTreeElementBase<Decl>(psiElement)
        satisfies ColoredItemPresentation
                & SortableTreeElement
                & AccessLevelProvider
        given Decl satisfies CeylonPsi.DeclarationPsi {

    value model => element?.ceylonNode?.declarationModel;

    shared actual default List<StructureViewTreeElement> childrenBase
            => Collections.emptyList<StructureViewTreeElement>();

    equals(Object that)
        => if (!super.equals(that))
            then false
        else if (is CeylonDeclarationTreeElement<out Anything> that)
            then isInherited == that.isInherited
        else false;

    textAttributesKey
            => if (isInherited)
                then CodeInsightColors.notUsedElementAttributes
            else if (exists dec = model, dec.deprecated)
                then CodeInsightColors.deprecatedAttributes
            else null;

    shared ClassOrInterface? type {
        if (exists dec = model, dec.classOrInterfaceMember) {
            if (isInherited) {
                assert (is ClassOrInterface? c = dec.container);
                return c;
            }
            if (exists refined = dec.refinedDeclaration,
                refined != dec) {
                assert (is ClassOrInterface? c = refined.container);
                return c;
            }
        }
        return null;
    }

    shared actual String? locationString {
        if (valid, exists dec = model) {
            if (isInherited && dec.classOrInterfaceMember) {
                assert (is ClassOrInterface container = dec.container);
                return " " + UIUtil.rightArrow() + container.name;
            }
            if (exists refined = dec.refinedDeclaration,
                refined != dec) {
                assert (is ClassOrInterface container = refined.container);
                return " " + UIUtil.upArrow("^") + container.name;
            }
        }
        return super.locationString;
    }

    presentableText
            => if (!valid)
                then "INVALID"
            else if (exists el = element)
                then descriptions.descriptionForPsi(el, false, false)
            else null;

    getIcon(Boolean open)
            => if (valid, exists node = element?.ceylonNode)
            then icons.forDeclaration(node)
            else null;

    alphaSortKey
            => element?.ceylonNode?.identifier?.text else "";

    string => presentableText else "";

    shared actual Integer accessLevel {
        if (isInherited) {
            return PsiUtil.accessLevelPublic;
        }
        else if (exists node = element?.ceylonNode) {
            if (exists dec = node.declarationModel) {
                return dec.shared
                    then PsiUtil.accessLevelPublic
                    else PsiUtil.accessLevelPrivate;
            }
            for (a in node.annotationList.annotations) {
                if (a.primary.text=="shared") {
                    return PsiUtil.accessLevelPublic;
                }
            }
            else {
                return PsiUtil.accessLevelPrivate;
            }
        }
        else {
            return PsiUtil.accessLevelPrivate;
        }
    }

    subLevel => 0;
}

shared interface CeylonContainerTreeElement {}

