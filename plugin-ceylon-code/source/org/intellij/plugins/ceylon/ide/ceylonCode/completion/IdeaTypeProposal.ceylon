import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    TypeProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaTypeProposal(Integer offset, Type? type, String text, String desc, Tree.CompilationUnit rootNode)
        extends TypeProposal
        (offset, type, text, desc, rootNode)
        satisfies IdeaCompletionProposal {
    
    shared actual Boolean toggleOverwrite => false;
    
    shared LookupElement lookupElement => newLookup(desc, text)
        .withIcon(if (exists type) then ideaIcons.forDeclaration(type.declaration) else null);
    
}