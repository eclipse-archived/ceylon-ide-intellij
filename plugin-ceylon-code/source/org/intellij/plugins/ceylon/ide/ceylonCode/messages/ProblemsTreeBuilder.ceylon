import com.intellij.ide.util.treeView {
    AbstractTreeBuilder
}
import javax.swing {
    JTree
}
import javax.swing.tree {
    DefaultTreeModel
}
import com.intellij.openapi.project {
    Project
}

class ProblemsTreeBuilder(JTree tree, DefaultTreeModel model, Project project)
        extends AbstractTreeBuilder(tree, model, null, null, false) {


}