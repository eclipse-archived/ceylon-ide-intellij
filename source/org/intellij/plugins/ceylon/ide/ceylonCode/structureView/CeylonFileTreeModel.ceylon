import ceylon.interop.java {
    javaClass
}

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
import com.intellij.ide.structureView.impl.java {
    AccessLevelProvider,
    VisibilitySorter
}
import com.intellij.ide.util.treeView {
    AbstractTreeNode
}
import com.intellij.ide.util.treeView.smartTree {
    ...
}
import com.intellij.navigation {
    ItemPresentation
}
import com.intellij.psi.util {
    PsiUtil
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

import java.lang {
    Class,
    UnsupportedOperationException,
    ObjectArray
}
import java.util {
    ...
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi
}

class CeylonFileTreeModel(CeylonFile psiFile)
        extends TextEditorBasedStructureViewModel(psiFile)
        satisfies StructureViewModel.ElementInfoProvider
                & StructureViewModel.ExpandInfoProvider {

    class SupertypesGrouper() satisfies Grouper {

        Boolean isParentGrouped(variable AbstractTreeNode<out Anything>? parent) {
            while (parent exists) {
                if (parent?.\ivalue is SupertypeGroup) {
                    return true;
                }
                parent = parent?.parent;
            }
            return false;
        }

        shared actual Collection<Group> group(AbstractTreeNode<out Object> parent, Collection<TreeElement> collection) {
            if (isParentGrouped(parent)) {
                return Collections.emptyList<Group>();
            }
            value map = HashMap<ClassOrInterface,SupertypeGroup>();
            for (element in collection) {
                ClassOrInterface? type;
                //TODO: introduce a SupertypeGroupable interface!
                if (is CeylonDeclarationTreeElement<out Anything> element) {
                    type = element.type;
                } else if (is CeylonSpecifierTreeElement element) {
                    type = element.type;
                } else {
                    type = null;
                }
                if (exists type) {
                    variable SupertypeGroup? group = map.get(type);
                    if (!exists g = group) {
                        group = SupertypeGroup(type);
                        map.put(type, group);
                    }
                    group?.list?.add(element);
                }
            }
            return Collections.unmodifiableCollection<Group>(map.values());
        }

        presentation
                => ActionPresentationData(
                    IdeBundle.message("action.structureview.group.methods.by.defining.type"),
                    null, AllIcons.General.\iImplementingMethod);

        name => "SHOW_INTERFACES";

        shared class SupertypeGroup(ClassOrInterface type)
                satisfies Group&ItemPresentation&AccessLevelProvider&SortableTreeElement {

            shared List<TreeElement> list = ArrayList<TreeElement>();

            presentation => this;

            presentableText => type.name;

            locationString => "";

            getIcon(Boolean b) => AllIcons.General.\iImplementingMethod;

            children => list;

            accessLevel => if (type.shared)
            then PsiUtil.accessLevelPublic
            else PsiUtil.accessLevelPrivate;

            subLevel => 1;

            alphaSortKey => " " + type.name;
        }
    }

    class KindSorter() satisfies Sorter {

        shared actual Comparator<out Object> comparator {
            return object satisfies Comparator<Object> {

                shared actual Integer compare(Object o1, Object o2) {
                    value i1 = o1 is CeylonImportTreeElement;
                    value i2 = o2 is CeylonImportTreeElement;
                    if (i1, !i2) {
                        return - 1;
                    }
                    if (i2, !i1) {
                        return 1;
                    }
                    value g1 = o1 is SupertypesGrouper.SupertypeGroup;
                    value g2 = o2 is SupertypesGrouper.SupertypeGroup;
                    if (g1, !g2) {
                        return - 1;
                    }
                    if (g2, !g1) {
                        return 1;
                    }
                    return 0;
                }

                equals(Object that) => false;
            };
        }

        visible => false;

        shared actual ActionPresentation presentation {
            throw UnsupportedOperationException();
        }

        name => "KIND";
    }

    value _nodeProviders
            = Collections.singletonList<NodeProvider<out TreeElement>>
                (CeylonInheritedMembersNodeProvider());
    value _sorters
            = ObjectArray<Sorter>.with {
                KindSorter(),
                VisibilitySorter.instance,
                Sorter.alphaSorter };
    value _classes
            = ObjectArray<Class<out Object>>.with {
                javaClass<CeylonPsi.DeclarationPsi>(),
                javaClass<CeylonPsi.SpecifierStatementPsi>() };
    value _filters
            = ObjectArray<Filter>.with { UnsharedDeclarationsFilter() };
    value _groupers
            = ObjectArray<Grouper>.with { SupertypesGrouper() };

    nodeProviders => _nodeProviders;

    root => CeylonFileTreeElement(psiFile);

    isAlwaysShowsPlus(StructureViewTreeElement element) => element is CeylonContainerTreeElement;

    isAlwaysLeaf(StructureViewTreeElement element) => !(element is CeylonContainerTreeElement);

    isAutoExpand(StructureViewTreeElement element) => !(element is CeylonImportListTreeElement);

    smartExpand => false;

    filters => _filters;

    suitableClasses => _classes;

    sorters => _sorters;

    groupers => _groupers;

    // TODO filters for inherited, fields, etc.
}
