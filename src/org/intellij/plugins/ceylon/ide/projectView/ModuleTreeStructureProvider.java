package org.intellij.plugins.ceylon.ide.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.*;

/**
 * Wraps ceylon modules in a special node, and shows the default module as such.
 */
public class ModuleTreeStructureProvider implements TreeStructureProvider, DumbAware {

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {
        // We're only interested in directories, which are source roots,
        // belonging to a module with a Ceylon facet
        if (parent instanceof PsiDirectoryNode) {
            VirtualFile sourceRoot = ((PsiDirectoryNode) parent).getVirtualFile();
            Project project = parent.getProject();

            if (isSourceFolder(sourceRoot, project)) {
                Module module = ProjectRootManager.getInstance(project)
                        .getFileIndex().getModuleForFile(sourceRoot);

                if (module != null) {
                    CeylonFacet ceylonFacet = CeylonFacet.forModule(module);

                    if (ceylonFacet != null) {
                        return buildModuleList(parent, children, sourceRoot);
                    }
                }
            }
        }
        return children;
    }

    private boolean isSourceFolder(VirtualFile sourceRoot, Project project) {
        if (project != null && sourceRoot != null) {
            SourceFolder root = ProjectRootsUtil.getModuleSourceRoot(sourceRoot, project);
            return (root != null && root.getRootType() instanceof JavaSourceRootType);
        }

        return false;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        if (selected.size() != 1) {
            return null;
        }
        AbstractTreeNode node = selected.iterator().next();
        if (node instanceof ModuleTreeNode) {
            ModuleTreeNode module = (ModuleTreeNode) node;

            if (module.directory == null) {
                return null; // default module is not a real folder
            }
            if (dataName.equals("context.Project")) {
                return module.directory.getProject();
            } else if (dataName.equals("psi.Element")) {
                return module.directory;
            }
        }
        return null;
    }

    @NotNull
    private Collection<AbstractTreeNode> buildModuleList(@NotNull AbstractTreeNode parent,
                                                         @NotNull Collection<AbstractTreeNode> children,
                                                         @NotNull VirtualFile sourceRoot) {
        List<AbstractTreeNode> modules = new ArrayList<>();
//        PsiDirectory root = null;
//        if (parent instanceof PsiDirectoryNode) {
//            root = ((PsiDirectoryNode) parent).getValue();
//        }
//        ModuleTreeNode defaultModule = new ModuleTreeNode(parent.getProject(), root, "(default module)");
//        modules.add(defaultModule);

        Map<String, ModuleTreeNode> modulesByName = new HashMap<>();

        for (AbstractTreeNode child : children) {
            List<PsiDirectoryNode> submodules = new ArrayList<>();
            detectInnerModule(child, submodules);

            if (submodules.isEmpty()) {
                // either something that belongs to the default module, or a subpackage of a previous module
                ModuleTreeNode container = getContainingModule(modulesByName, child, sourceRoot);

                if (container == null) {
//                    defaultModule.addChild(child);
                    modules.add(child);
                } else {
                    container.addChild(child);
                }
            } else {
                for (PsiDirectoryNode submodule : submodules) {
                    String moduleName = computeModuleName(sourceRoot, submodule.getVirtualFile());
                    ModuleTreeNode subModule = new ModuleTreeNode(parent.getProject(),
                            submodule.getValue(), moduleName);
                    subModule.addChild(submodule);
                    modules.add(subModule);
                    modulesByName.put(moduleName, subModule);
                }
            }
        }

        return modules;
    }

    @Nullable
    private ModuleTreeNode getContainingModule(Map<String, ModuleTreeNode> existingModules,
                                               AbstractTreeNode node, VirtualFile root) {
        if (node instanceof PsiDirectoryNode) {
            String packageName = computeModuleName(root, ((PsiDirectoryNode) node).getVirtualFile());

            for (Map.Entry<String, ModuleTreeNode> entry : existingModules.entrySet()) {
                if (packageName.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @NotNull
    private String computeModuleName(VirtualFile root, VirtualFile moduleDirectory) {
        return VfsUtil.getRelativePath(moduleDirectory, root).replace('/', '.');
    }

    private void detectInnerModule(AbstractTreeNode node, List<PsiDirectoryNode> result) {
        if (node instanceof PsiDirectoryNode) {
            PsiDirectoryNode directory = (PsiDirectoryNode) node;

            for (AbstractTreeNode child : directory.getChildren()) {
                if (child instanceof PsiFileNode) {
                    if (((PsiFileNode) child).getValue().getName().equals("module.ceylon")) {
                        result.add(directory);
                        return; // No need to look for other modules in children nodes
                    }
                } else {
                    detectInnerModule(child, result);
                }
            }
        }
    }

    private static class ModuleTreeNode extends ProjectViewNode<String> {

        List<AbstractTreeNode> children = new ArrayList<>();
        @Nullable
        protected final PsiDirectory directory;
        private final String moduleName;

        public ModuleTreeNode(Project parent, @Nullable PsiDirectory directory, String moduleName) {
            super(parent, moduleName, ViewSettings.DEFAULT);
            this.directory = directory;
            this.moduleName = moduleName;
        }

        public void addChild(AbstractTreeNode child) {
            children.add(child);
        }

        @NotNull
        @Override
        public Collection<AbstractTreeNode> getChildren() {
            return children;
        }

        @Override
        public void update(PresentationData presentation) {
            presentation.setPresentableText(moduleName);
            presentation.setIcon(icons_.get_().getModuleFolders());
        }

        @Override
        public boolean contains(@NotNull VirtualFile file) {
            for (AbstractTreeNode child : children) {
                if (child instanceof ProjectViewNode) {
                    if (((ProjectViewNode) child).contains(file)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
