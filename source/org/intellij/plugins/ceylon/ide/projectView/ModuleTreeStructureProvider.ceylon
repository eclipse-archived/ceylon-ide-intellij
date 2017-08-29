import com.intellij.ide.projectView {
    PresentationData,
    ProjectViewNode,
    TreeStructureProvider,
    ViewSettings
}
import com.intellij.ide.projectView.impl {
    ProjectRootsUtil
}
import com.intellij.ide.projectView.impl.nodes {
    PsiDirectoryNode,
    PsiFileNode
}
import com.intellij.ide.util.treeView {
    AbstractTreeNode
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import com.intellij.openapi.project {
    DumbAware,
    Project
}
import com.intellij.openapi.roots {
    ProjectRootManager
}
import com.intellij.openapi.vfs {
    VfsUtil,
    VirtualFile
}
import com.intellij.problems {
    WolfTheProblemSolver
}
import com.intellij.psi {
    PsiDirectory
}

import java.util {
    List,
    Map,
    Collections,
    Collection,
    ArrayList,
    HashMap
}

import org.intellij.plugins.ceylon.ide.facet {
    CeylonFacet
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}
import org.jetbrains.jps.model.java {
    JavaSourceRootType
}


shared alias Node => AbstractTreeNode<out Object>;

shared class ModuleTreeStructureProvider()
        satisfies TreeStructureProvider & DumbAware {

    shared actual Collection<Node> modify(Node parent,
            Collection<Node> children, ViewSettings settings) {
        if (is PsiDirectoryNode parent,
            exists sourceRoot = parent.virtualFile,
            exists project = parent.project,
            isSourceFolder(sourceRoot, project)) {
            if (exists mod
                    = ProjectRootManager.getInstance(project)
                        .fileIndex.getModuleForFile(sourceRoot)) {
                value ceylonFacet = CeylonFacet.forModule(mod);
                if (exists ceylonFacet) {
                    return buildModuleList(parent, children, sourceRoot);
                }
            }
        }
        return children;
    }

    Boolean isSourceFolder(VirtualFile sourceRoot, Project project)
            => ProjectRootsUtil.getModuleSourceRoot(sourceRoot, project)
                ?.rootType is JavaSourceRootType;

    shared actual Object? getData(Collection<Node> selected, String dataName) {
        if (selected.size() == 1,
            is ModuleTreeNode node = selected.iterator().next(),
            exists dir = node.directory) {
            return switch (dataName)
                case ("context.Project") dir.project
                case ("psi.Element") dir
                else null;
        }
        else {
            return null;
        }
    }

    Collection<Node> buildModuleList(Node parent,
            Collection<Node> children, VirtualFile sourceRoot) {
        value modules = ArrayList<Node>();
        value modulesByName = HashMap<String,ModuleTreeNode>();
        for (child in children) {
            value submodules = ArrayList<PsiDirectoryNode>();
            detectInnerModule(child, submodules);
            if (submodules.empty) {
                value container = getContainingModule {
                    existingModules = modulesByName;
                    node = child;
                    root = sourceRoot;
                };
                if (!exists container) {
                    modules.add(child);
                } else {
                    container.addChild(child);
                }
            } else {
                for (submodule in submodules) {
                    value moduleName = computeModuleName {
                        root = sourceRoot;
                        moduleDirectory = submodule.virtualFile;
                    };
                    value subModule = ModuleTreeNode {
                        parent = parent.project;
                        directory = submodule.\ivalue;
                        moduleName = moduleName;
                    };
                    subModule.addChild(submodule);
                    modules.add(subModule);
                    modulesByName.put(moduleName, subModule);
                }
            }
        }
        return modules;
    }

    ModuleTreeNode? getContainingModule(Map<String,ModuleTreeNode> existingModules,
            Node node, VirtualFile root) {
        if (is PsiDirectoryNode node,
            exists packageName
                    = computeModuleName {
                        root = root;
                        moduleDirectory = node.virtualFile;
                    }) {
            for (entry in existingModules.entrySet()) {
                if (packageName.startsWith(entry.key)) {
                    return entry.\ivalue;
                }
            }
        }
        return null;
    }

    String? computeModuleName(VirtualFile root, VirtualFile moduleDirectory)
            => VfsUtil.getRelativePath(moduleDirectory, root)?.replace("/", ".");

    void detectInnerModule(Node node, List<PsiDirectoryNode> result) {
        if (is PsiDirectoryNode node) {
            for (child in node.children) {
                if (is PsiFileNode child) {
                    if (child.\ivalue.name.equals("module.ceylon")) {
                        result.add(node);
                        return;
                    }
                } else {
                    detectInnerModule(child, result);
                }
            }
        }
    }

}


class ModuleTreeNode(Project? parent, shared PsiDirectory? directory, String? moduleName)
        extends ProjectViewNode<String>(parent, moduleName, ViewSettings.default) {

    shared actual Collection<Node> children = ArrayList<Node>();

    shared void addChild(Node child) => children.add(child);

    shared actual void update(PresentationData presentation) {
        presentation.presentableText = moduleName;
        presentation.setIcon(icons.moduleFolders);
        if (exists project = super.project,
            WolfTheProblemSolver.getInstance(project)
                .hasProblemFilesBeneath((file) => someChildContainsFile(file))) {
            presentation.setAttributesKey(CodeInsightColors.errorsAttributes);
        }
    }

    shared actual Collection<VirtualFile> roots
            => if (!exists directory)
            then Collections.emptyList<VirtualFile>()
            else Collections.singletonList(directory.virtualFile);

    shared actual Boolean contains(VirtualFile file) {
        for (child in children) {
            if (is ProjectViewNode<out Anything> child,
                child.contains(file)) {
                return true;
            }
        }
        return false;
    }

}