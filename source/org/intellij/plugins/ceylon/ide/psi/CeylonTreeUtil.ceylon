import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFileManager,
    JarFileSystem,
    LocalFileSystem
}
import com.intellij.psi {
    PsiFile,
    PsiManager,
    PsiElement
}
import com.intellij.psi.util {
    PsiUtilCore
}
import com.redhat.ceylon.compiler.typechecker.tree {
    CustomTree,
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import java.util {
    Objects
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager
}

shared class CeylonTreeUtil {

    shared static String withProtocol(String path)
            => (JarFileSystem.jarSeparator in path
            then JarFileSystem.protocolPrefix
            else LocalFileSystem.protocolPrefix)
                    + path;

    shared static PsiFile? getDeclaringFile(Unit unit, Project project)
            => if (is CeylonUnit unit,
                    exists path = unit.sourceFullPath,
                    exists vfile = VirtualFileManager.instance.findFileByUrl(withProtocol(path)))
            then concurrencyManager.needReadAccess(()
                    => PsiManager.getInstance(project).findFile(vfile))
            else null;

    shared static PsiElement? findPsiElement(Node? node, PsiFile file) {
        Node ceylonNode;
        switch (node)
        case (null) {
            return null;
        }
        case (is CustomTree.GuardedVariable) {
            if (exists identifier = node.identifier) {
                ceylonNode = identifier;
            }
            else {
                ceylonNode = node; //TODO return null?
            }
        }
        else {
            ceylonNode = node;
        }

        value index = ceylonNode.startIndex;
        if (!exists index) {
            return null;
        }

//        value ceylonNodeCandidates = ArrayList<Node>();
//        value candidates = ArrayList<PsiElement>();
        variable value candidate
                = PsiUtilCore.getElementAtOffset(file, index.intValue());
        while (!(candidate is PsiFile)) {
//            candidates.add(candidate);
            if (is CeylonCompositeElement composite = candidate,
                exists candidateNode = composite.ceylonNode) {
//                ceylonNodeCandidates.add(candidateNode);
                value candidateCeylonNode =
                        if (is Tree.ParameterDeclaration candidateNode)
                        then candidateNode.typedDeclaration else composite.ceylonNode;
                if (candidateCeylonNode == ceylonNode) {
                    return candidate;
                } else if (candidateCeylonNode exists,
                    className(candidateCeylonNode) ==className(ceylonNode),
                    Objects.equals(candidateCeylonNode.startIndex, ceylonNode.startIndex),
                    Objects.equals(candidateCeylonNode.endIndex, ceylonNode.endIndex)) {
                    return candidate;
                }
            }
//            else {
//                ceylonNodeCandidates.add(null);
//            }
            candidate = candidate.parent;
        }
//        variable value message =
//                "No PSI node found for ceylon node of type ``ceylonNode.nodeType`` at (``ceylonNode.startIndex``-``ceylonNode.endIndex``) in ``if (!ceylonNode.unit exists) then "<null>" else ceylonNode.unit.filename``.
//                 ====================================
//                 Searched ceylon node: ``ceylonNode.nodeType``(``ceylonNode.location``)
//                 In the following Psi Nodes:\n";
//        for (i in 0: candidates.size()) {
//            message += "    ``candidates.get(i)`` -> candidate Ceylon node: ";
//            value ceylonNodeCandidate = ceylonNodeCandidates.get(i);
//            if (!exists ceylonNodeCandidate) {
//                message += "<null>\n";
//            } else {
//                message += "``ceylonNodeCandidates.get(i).nodeType``(``ceylonNodeCandidates.get(i).location``)\n";
//            }
//        }
//        message += "====================================\n";
//        logger.warn(message);
        return null;
    }

    new () {}

}