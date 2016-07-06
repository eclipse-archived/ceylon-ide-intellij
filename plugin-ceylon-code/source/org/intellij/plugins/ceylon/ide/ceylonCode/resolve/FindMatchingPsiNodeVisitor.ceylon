import ceylon.interop.java {
    javaClassFromInstance
}

import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}

import java.lang {
    Class
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonPsiVisitor
}

shared class FindMatchingPsiNodeVisitor(Node node, klass)
        extends CeylonPsiVisitor(true) {

    Class<out CeylonCompositeElement> klass;

    variable CeylonCompositeElement? result = null;

    shared CeylonCompositeElement? psi => result;

    shared actual void visitElement(PsiElement element) {
        super.visitElement(element);
        if (element.textRange.equalsToRange(node.startIndex.intValue(), node.endIndex.intValue()),
            klass.isAssignableFrom(javaClassFromInstance(element))) {
            assert (is CeylonCompositeElement element);
            result = element;
        }
    }

}
