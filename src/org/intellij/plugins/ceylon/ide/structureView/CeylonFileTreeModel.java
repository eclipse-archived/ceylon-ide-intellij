package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.structureView.impl.java.AccessLevelProvider;
import com.intellij.ide.structureView.impl.java.VisibilitySorter;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtil;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

class CeylonFileTreeModel extends TextEditorBasedStructureViewModel
        implements StructureViewModel.ElementInfoProvider, StructureViewModel.ExpandInfoProvider {

    private static final List<NodeProvider> NODE_PROVIDERS = Collections.<NodeProvider>singletonList(
            new CeylonInheritedMembersNodeProvider()
    );
    private static final Sorter[] SORTERS = new Sorter[] {
            new KindSorter(),
            VisibilitySorter.INSTANCE,
            Sorter.ALPHA_SORTER,
    };
    private static final Class[] CLASSES = new Class[] {
            CeylonPsi.DeclarationPsi.class,
            CeylonPsi.SpecifierStatementPsi.class
    };
    private static final Filter[] FILTERS = new Filter[] { new UnsharedDeclarationsFilter() };
    private static final Grouper[] GROUPERS = new Grouper[] { new SupertypesGrouper() };

    CeylonFileTreeModel(PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public Collection<NodeProvider> getNodeProviders() {
        return NODE_PROVIDERS;
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new CeylonFileTreeElement((CeylonFile) getPsiFile());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return true;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof CeylonFunctionTreeElement ||
                element instanceof CeylonAttributeTreeElement;
    }

    @Override
    public boolean isAutoExpand(@NotNull StructureViewTreeElement element) {
        return !(element instanceof CeylonImportListTreeElement);
    }

    @Override
    public boolean isSmartExpand() {
        return false;
    }

    @NotNull
    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @NotNull
    @Override
    protected Class[] getSuitableClasses() {
        return CLASSES;
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return SORTERS;
    }

    @NotNull
    @Override
    public Grouper[] getGroupers() {
        return GROUPERS;
    }

    private static class SupertypesGrouper implements Grouper {

        private static boolean isParentGrouped(AbstractTreeNode parent) {
            while (parent != null) {
                if (parent.getValue() instanceof SupertypeGroup) return true;
                parent = parent.getParent();
            }
            return false;
        }

        @NotNull
        @Override
        public Collection<Group> group(@NotNull AbstractTreeNode parent,
                                       @NotNull Collection<TreeElement> collection) {
            if (isParentGrouped(parent)) return Collections.emptyList();
            HashMap<ClassOrInterface,SupertypeGroup> map = new HashMap<>();
            for (TreeElement element: collection) {
                ClassOrInterface type = null;
                //TODO: introduce a SupertypeGroupable interface!
                if (element instanceof CeylonDeclarationTreeElement) {
                    CeylonDeclarationTreeElement treeElement =
                            (CeylonDeclarationTreeElement) element;
                    type = treeElement.getType();
                }
                else if (element instanceof CeylonSpecifierTreeElement) {
                    CeylonSpecifierTreeElement treeElement =
                            (CeylonSpecifierTreeElement) element;
                    type = treeElement.getType();
                }
                if (type!=null) {
                    SupertypeGroup group = map.get(type);
                    if (group == null) {
                        group = new SupertypeGroup(type);
                        map.put(type, group);
                    }
                    group.getList().add(element);
                }
            }
            return Collections.<Group>unmodifiableCollection(map.values());
        }

        @NotNull
        @Override
        public ActionPresentation getPresentation() {
            return new ActionPresentationData(
                    IdeBundle.message("action.structureview.group.methods.by.defining.type"),
                    null, AllIcons.General.ImplementingMethod);
        }

        @NotNull
        @Override
        public String getName() {
            return "SHOW_INTERFACES";
        }

        private static class SupertypeGroup
                implements Group, ItemPresentation,
                           AccessLevelProvider, SortableTreeElement {
            private final ClassOrInterface type;
            private List<TreeElement> list = new ArrayList<>();

            private List<TreeElement> getList() {
                return list;
            }

            private SupertypeGroup(ClassOrInterface type) {
                this.type = type;
            }

            @NotNull
            @Override
            public ItemPresentation getPresentation() {
                return this;
            }

            @Nullable
            @Override
            public String getPresentableText() {
                return type.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.General.ImplementingMethod;
//                        return ideaIcons_.get_().forDeclaration(type);
            }

            @NotNull
            @Override
            public Collection<TreeElement> getChildren() {
                return list;
            }

            @Override
            public int getAccessLevel() {
                return type.isShared() ? PsiUtil.ACCESS_LEVEL_PUBLIC : PsiUtil.ACCESS_LEVEL_PRIVATE;
            }

            @Override
            public int getSubLevel() {
                return 1;
            }

            @NotNull
            @Override
            public String getAlphaSortKey() {
                return " " + type.getName();
            }
        }
    }

    private static class KindSorter implements Sorter {
        @Override
        public Comparator getComparator() {
            return new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    boolean i1 = o1 instanceof CeylonImportTreeElement;
                    boolean i2 = o2 instanceof CeylonImportTreeElement;
                    if (i1 && !i2) {
                        return -1;
                    }
                    if (i2 && !i1) {
                        return 1;
                    }
                    boolean g1 = o1 instanceof SupertypesGrouper.SupertypeGroup;
                    boolean g2 = o2 instanceof SupertypesGrouper.SupertypeGroup;
                    if (g1 && !g2) {
                        return -1;
                    }
                    if (g2 && !g1) {
                        return 1;
                    }
                    return 0;
                }
            };
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @NotNull
        @Override
        public ActionPresentation getPresentation() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public String getName() {
            return "KIND";
        }
    }

    // TODO filters for inherited, fields, etc.
}
