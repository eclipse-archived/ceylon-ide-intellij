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
    CodeInsightColors,
    TextAttributesKey
}
import com.intellij.psi.util {
    PsiUtil
}
import com.intellij.util.ui {
    UIUtil
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Declaration
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

    Declaration? model
            => element?.ceylonNode?.declarationModel;

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
            else if (exists m = model, m.deprecated)
                then CodeInsightColors.deprecatedAttributes
            else null;

    shared ClassOrInterface? type {
        if (exists m = model, m.classOrInterfaceMember) {
            if (isInherited) {
                assert (is ClassOrInterface? c = m.container);
                return c;
            }
            if (exists refined = m.refinedDeclaration, refined != m) {
                assert (is ClassOrInterface? c = refined.container);
                return c;
            }
        }
        return null;
    }

    shared actual String? locationString {
        if (valid, exists m = model) {
            if (isInherited, m.classOrInterfaceMember) {
                assert (is ClassOrInterface container = m.container);
                return " " + UIUtil.rightArrow() + container.name;
            }
            if (exists refined = m.refinedDeclaration, refined != m) {
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
        if (exists m = model, m.shared) {
            return PsiUtil.accessLevelPublic;
        }

        if (exists el = element) {
            for (a in el.ceylonNode.annotationList.annotations) {
                if (a.primary.text=="shared") {
                    return PsiUtil.accessLevelPublic;
                }
            }
        }

        return PsiUtil.accessLevelPrivate;
    }

    subLevel => 0;
}

shared interface CeylonContainerTreeElement {}

