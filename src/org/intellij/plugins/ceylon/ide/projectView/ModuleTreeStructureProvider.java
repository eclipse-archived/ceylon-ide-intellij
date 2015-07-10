package org.intellij.plugins.ceylon.ide.projectView;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Wraps ceylon modules in a special node, and shows the default module as such.
 */
public class ModuleTreeStructureProvider implements TreeStructureProvider {

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings settings) {
        // We're only interested in directories, which are source roots, belonging to a module with a Ceylon facet
        if (parent instanceof PsiDirectoryNode) {
            VirtualFile sourceRoot = ((PsiDirectoryNode) parent).getVirtualFile();
            Project project = parent.getProject();

            if (project != null && sourceRoot != null && ProjectRootsUtil.isModuleSourceRoot(sourceRoot, project)) {
                Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(sourceRoot);

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

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }

    @NotNull
    private Collection<AbstractTreeNode> buildModuleList(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, @NotNull VirtualFile sourceRoot) {
        List<AbstractTreeNode> modules = new ArrayList<>();
        ModuleTreeNode defaultModule = new ModuleTreeNode(parent, "(default module)");
        modules.add(defaultModule);

        Map<String, ModuleTreeNode> modulesByName = new HashMap<>();

        for (AbstractTreeNode child : children) {
            List<PsiDirectoryNode> submodules = new ArrayList<>();
            detectInnerModule(child, submodules);

            if (submodules.isEmpty()) {
                // either something that belongs to the default module, or a subpackage of a previous module
                ModuleTreeNode container = getContainingModule(modulesByName, child, sourceRoot);

                if (container == null) {
                    defaultModule.addChild(child);
                } else {
                    container.addChild(child);
                }
            } else {
                for (PsiDirectoryNode submodule : submodules) {
                    String moduleName = computeModuleName(sourceRoot, submodule.getVirtualFile());
                    ModuleTreeNode subModule = new ModuleTreeNode(parent, moduleName);
                    subModule.addChild(submodule);
                    modules.add(subModule);
                    modulesByName.put(moduleName, subModule);
                }
            }
        }

        return modules;
    }

    @Nullable
    private ModuleTreeNode getContainingModule(Map<String, ModuleTreeNode> existingModules, AbstractTreeNode node, VirtualFile root) {
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

    private static class ModuleTreeNode extends AbstractTreeNode<String> {

        List<AbstractTreeNode> children = new ArrayList<>();

        public ModuleTreeNode(AbstractTreeNode parent, String moduleName) {
            super(parent.getProject(), moduleName);
        }

        public void addChild(AbstractTreeNode child) {
            children.add(child);
        }

        @NotNull
        @Override
        public Collection<? extends AbstractTreeNode> getChildren() {
            return children;
        }

        @Override
        protected void update(PresentationData presentation) {
            presentation.setPresentableText(getValue());
            presentation.setIcon(AllIcons.Nodes.Artifact);
        }
    }
}
