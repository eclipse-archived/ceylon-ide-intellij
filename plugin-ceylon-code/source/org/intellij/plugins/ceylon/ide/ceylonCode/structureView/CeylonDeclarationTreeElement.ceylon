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

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    descriptions
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

abstract class CeylonDeclarationTreeElement<Decl>(Decl psiElement, Boolean isInherited)
        extends PsiTreeElementBase<Decl>(psiElement)
        satisfies ColoredItemPresentation
                & SortableTreeElement
                & AccessLevelProvider
        given Decl satisfies CeylonPsi.DeclarationPsi {

    shared actual default List<StructureViewTreeElement> childrenBase
            => Collections.emptyList<StructureViewTreeElement>();

    shared actual Boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        assert (is CeylonDeclarationTreeElement<out Anything> that = o);
        return isInherited == that.isInherited;
    }

    shared actual String? string
            => presentableText;

    Declaration? model
            => element?.ceylonNode?.declarationModel;

    shared actual TextAttributesKey? textAttributesKey {
        if (isInherited) {
            return CodeInsightColors.notUsedElementAttributes;
        } else if (exists m = model, m.deprecated) {
            return CodeInsightColors.deprecatedAttributes;
        } else {
            return null;
        }
    }

    shared ClassOrInterface? type {
        if (exists m = model, m.classOrInterfaceMember) {
            if (isInherited) {
                assert(is ClassOrInterface? c = m.container);
                return c;
            }
            if (exists refined = m.refinedDeclaration, refined != m) {
                assert(is ClassOrInterface? c = refined.container);
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

    shared actual String? presentableText {
        if (!valid) {
            return "INVALID";
        }
        if (exists el = element) {
            return descriptions.descriptionForPsi(el, false, false);
        }
        return null;
    }

    shared actual Icon? getIcon(Boolean open) {
        if (valid, exists node = element?.ceylonNode) {
            return icons.forDeclaration(node);
        }
        return null;
    }

    alphaSortKey
            => if (exists id = element?.ceylonNode?.identifier) then id.text else "";

    shared actual Integer accessLevel {
        if (isInherited) {
            return PsiUtil.accessLevelPublic;
        }
        if (exists m = model, m.shared) {
            return PsiUtil.accessLevelPublic;
        }

        if (exists el = element) {
            value annotationList = el.ceylonNode.annotationList;
            for (a in annotationList.annotations) {
                if (a.primary.text.equals("shared")) {
                    return PsiUtil.accessLevelPublic;
                }
            }
        }
        return PsiUtil.accessLevelPrivate;
    }

    subLevel => 0;
}

shared interface CeylonContainerTreeElement {}

