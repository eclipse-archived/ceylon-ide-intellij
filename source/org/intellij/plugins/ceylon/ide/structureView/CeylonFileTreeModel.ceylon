import com.intellij.icons {
    AllIcons
}
import com.intellij.ide {
    IdeBundle
}
import com.intellij.ide.structureView {
    StructureViewModel,
    StructureViewTreeElement,
    TextEditorBasedStructureViewModel
}
import com.intellij.ide.structureView.impl.common {
    PsiTreeElementBase
}
import com.intellij.ide.structureView.impl.java {
    AccessLevelProvider,
    VisibilitySorter
}
import com.intellij.ide.util {
    FileStructureFilter
}
import com.intellij.ide.util.treeView {
    AbstractTreeNode
}
import com.intellij.ide.util.treeView.smartTree {
    Group,
    SortableTreeElement,
    TreeElement,
    Grouper,
    ActionPresentationData,
    Sorter,
    Filter,
    NodeProvider
}
import com.intellij.navigation {
    ItemPresentation
}
import com.intellij.openapi.actionSystem {
    KeyboardShortcut,
    Shortcut
}
import com.intellij.openapi.util {
    SystemInfo
}
import com.intellij.psi.util {
    PsiUtil
}
import com.intellij.util {
    PlatformIcons {
        publicIcon
    }
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

import java.lang {
    Class,
    UnsupportedOperationException,
    ObjectArray,
    Types {
        classForType
    }
}
import java.util {
    Collection,
    Collections,
    ArrayList,
    HashMap,
    Comparator
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi
}

class CeylonFileTreeModel(CeylonFile psiFile)
        extends TextEditorBasedStructureViewModel(psiFile)
        satisfies StructureViewModel.ElementInfoProvider
                & StructureViewModel.ExpandInfoProvider {

    class SupertypeGroup(ClassOrInterface type)
            satisfies Group
                    & ItemPresentation
                    & AccessLevelProvider
                    & SortableTreeElement {

        value list = ArrayList<TreeElement>();

        shared void add(TreeElement element)
                => list.add(element);

        presentation => this;

        presentableText => type.name;

        locationString => "";

        getIcon(Boolean b)
                => AllIcons.General.implementingMethod;

        children => list;

        accessLevel
                => type.shared
                then PsiUtil.accessLevelPublic
                else PsiUtil.accessLevelPrivate;

        subLevel => 1;

        alphaSortKey => " " + type.name;
    }

    object supertypesGrouper satisfies Grouper {

        Boolean isParentGrouped(parent) {
            AbstractTreeNode<out Anything>? parent;

            variable value current = parent;
            while (exists node = current) {
                if (node.\ivalue is SupertypeGroup) {
                    return true;
                }
                current = node.parent;
            }
            return false;
        }

        shared actual Collection<Group> group(parent, collection) {
            AbstractTreeNode<out Object> parent;
            Collection<TreeElement> collection;

            if (isParentGrouped(parent)) {
                return Collections.emptyList<Group>();
            }
            value map = HashMap<ClassOrInterface,SupertypeGroup>();
            for (element in collection) {
                //TODO: introduce a SupertypeGroupable interface!
                value type
                        = switch (element)
                        case (is CeylonDeclarationTreeElement<out Anything>)
                            element.type
                        case (is CeylonSpecifierTreeElement)
                            element.type
                        else null;
                if (exists type) {
                    if (exists group = map[type]) {
                        group.add(element);
                    } else {
                        value group = SupertypeGroup(type);
                        group.add(element);
                        map.put(type, group);
                    }

                }
            }
            return Collections.unmodifiableCollection<Group>(map.values());
        }

        presentation
                => ActionPresentationData(
            IdeBundle.message("action.structureview.group.methods.by.defining.type"),
            null, AllIcons.General.implementingMethod);

        name => "SHOW_INTERFACES";

    }

    object kindSorter satisfies Sorter {

        shared actual object comparator satisfies Comparator<Object> {

            shared actual Integer compare(Object o1, Object o2) {
                value i1 = o1 is CeylonImportTreeElement;
                value i2 = o2 is CeylonImportTreeElement;
                if (i1, !i2) {
                    return -1;
                }
                if (i2, !i1) {
                    return 1;
                }
                value g1 = o1 is SupertypeGroup;
                value g2 = o2 is SupertypeGroup;
                if (g1, !g2) {
                    return -1;
                }
                if (g2, !g1) {
                    return 1;
                }
                return 0;
            }

            equals(Object that) => false;
        }

        visible => false;

        shared actual Nothing presentation {
            throw UnsupportedOperationException();
        }

        name => "KIND";
    }

    object unsharedDeclarationsFilter
            satisfies FileStructureFilter {

        isVisible(TreeElement treeNode)
                => if (is PsiTreeElementBase<out Anything> treeNode,
                       is CeylonPsi.DeclarationPsi element = treeNode.element)
                then element.ceylonNode.declarationModel.shared
                else true;

        reverted => false;

        presentation => ActionPresentationData("Hide unshared declarations", null, publicIcon);

        name => "CEYLON_HIDE_UNSHARED";

        checkBoxText => "Hide unshared declarations";

        shortcut = ObjectArray<Shortcut>.with {
            KeyboardShortcut.fromString(SystemInfo.isMac then "meta U" else "control U")
        };
    }

    nodeProviders
            = Collections.singletonList<NodeProvider<out TreeElement>>
                (CeylonInheritedMembersNodeProvider());

    sorters = ObjectArray<Sorter>.with {
                kindSorter,
                VisibilitySorter.instance,
                Sorter.alphaSorter
            };

    suitableClasses
            = ObjectArray<Class<out Object>>.with {
                classForType<CeylonPsi.DeclarationPsi>(),
                classForType<CeylonPsi.SpecifierStatementPsi>()
            };

    filters = ObjectArray<Filter>.with { unsharedDeclarationsFilter };

    groupers = ObjectArray<Grouper>.with { supertypesGrouper };

    root => CeylonFileTreeElement(psiFile);

    isAlwaysShowsPlus(StructureViewTreeElement element)
            => element is CeylonContainerTreeElement;

    isAlwaysLeaf(StructureViewTreeElement element)
            => !(element is CeylonContainerTreeElement);

    isAutoExpand(StructureViewTreeElement element)
            => !(element is CeylonImportListTreeElement);

    smartExpand => false;

    // TODO filters for inherited, fields, etc.
}
