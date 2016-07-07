import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.lang {
    ASTNode
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.common {
    Backends {
        header=\iHEADER
    }
}

import java.lang {
    UnsupportedOperationException
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    CeylonNamedCompositeElementImpl
}

shared class CeylonResolvable(ASTNode node)
        extends CeylonNamedCompositeElementImpl(node) {

    reference
            => if (is CeylonPsi.ImportPathPsi parent = this.parent)
            then CeylonReference {
                element = parent;
                range = TextRange.from(0, parent.textLength);
                soft = true;
            }
            else CeylonReference {
                element = this;
                range = TextRange.from(0, textLength);
                soft = true;
            };

    function backendRef(Backends backend)
            => CeylonReference {
                element = this;
                range = TextRange.from(0, textLength);
                soft = true;
                backend = backend;
            };

    references
            => if (is CeylonPsi.MemberOrTypeExpressionPsi parent = this.parent,
                    exists model = parent.ceylonNode?.declaration,
                    model.native)
            //for native decs we need a ref per backend
            then createJavaObjectArray {
                backendRef(Backends.header),
                backendRef(Backends.java),
                backendRef(Backends.js)
            }
            //otherwise there is just one
            else createJavaObjectArray { reference };

    name => this is CeylonPsi.IdentifierPsi then text;

    shared actual PsiElement setName(String name) {
        throw UnsupportedOperationException("Not yet");
    }

}
