import com.intellij.ide.projectView {
    TreeStructureProvider
}
import com.intellij.ide.util.treeView {
    AbstractTreeStructureBase
}
import com.intellij.openapi.project {
    Project
}

import java.util {
    Collections
}

class ProblemsTreeStructure(Project project, ProblemsModel model) extends AbstractTreeStructureBase(project) {

    value rootNode = ProblemsRootNode(project, model);

    commit() => noop();

    hasSomethingToCommit() => false;

    providers => Collections.emptyList<TreeStructureProvider>();

    rootElement => rootNode;
}