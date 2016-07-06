import com.intellij.lang {
    ASTNode
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement,
    PsiReference
}
import com.redhat.ceylon.common {
    Backends {
        header=\iHEADER
    }
}

import java.lang {
    ObjectArray,
    UnsupportedOperationException
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    CeylonNamedCompositeElementImpl
}

shared class CeylonResolvable(ASTNode node)
        extends CeylonNamedCompositeElementImpl(node) {

    shared actual PsiReference reference {
        if (is CeylonPsi.ImportPathPsi parent = this.parent) {
            TextRange parentRange = TextRange.from(0, parent.textLength);
            return CeylonReference(parent, parentRange, true);
        } else {
            TextRange range = TextRange.from(0, textLength);
            return CeylonReference(this, range, true);
        }
    }

    shared actual ObjectArray<PsiReference> references {
        if (is CeylonPsi.MemberOrTypeExpressionPsi parent = this.parent,
            exists node = parent.ceylonNode,
            exists model = node.declaration, model.native) {
            value list = ArrayList<PsiReference>();
            addBackend(list, Backends.header);
            addBackend(list, Backends.java);
            addBackend(list, Backends.js);
            return list.toArray(ObjectArray<PsiReference>(0));
        }
        return super.references;
    }

    void addBackend(ArrayList<PsiReference> list, Backends backend) {
        TextRange range = TextRange.from(0, textLength);
        list.add(CeylonReference(this, range, true, backend));
    }

    shared actual String? name {
        if (this is CeylonPsi.IdentifierPsi) {
            return text;
        } else {
            return null;
        }
    }

    shared actual PsiElement setName(String name) {
        throw UnsupportedOperationException("Not yet");
    }
}
