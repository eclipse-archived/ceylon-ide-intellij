import ceylon.collection {
    ArrayList
}

import com.intellij.lang {
    ASTNode
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement,
    PsiReference,
    PsiNamedElement
}
import com.redhat.ceylon.common {
    Backends {
        header=\iHEADER
    }
}

import java.lang {
    Types {
        nativeString
    },
    UnsupportedOperationException,
    ObjectArray
}
import java.util.regex {
    Pattern
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonTokens
}
import org.intellij.plugins.ceylon.ide.psi.impl {
    CeylonNamedCompositeElementImpl,
    CeylonNamedTypePsiImpl
}

"Makes identifiers resolvable to navigate to their original declaration."
shared abstract class ResolvableIdentifier(ASTNode node)
        extends CeylonNamedCompositeElementImpl(node)
        satisfies CeylonResolvable {

    reference => (super of CeylonResolvable).reference;
    references => (super of CeylonResolvable).references;
}

"Makes local modifiers (`value` and `function`) resolvable to navigate
 to their inferred type."
shared abstract class ResolvableLocalModifier(ASTNode node)
        extends CeylonNamedTypePsiImpl(node)
        satisfies CeylonResolvable {

    reference => (super of CeylonResolvable).reference;
    references => (super of CeylonResolvable).references;
}

"A mixin that makes PSI elements resolvable."
shared interface CeylonResolvable satisfies PsiNamedElement {

    shared actual default CeylonReference reference =>
            if (is CeylonPsi.ImportPathPsi parent = this.parent)
            then CeylonReference(this, parent)
            else CeylonReference(this);

    function backendRef(Backends backend) =>
            CeylonReference {
                element = this;
                backend = backend;
            };

    shared actual default ObjectArray<CeylonReference> references =>
            if (is CeylonPsi.MemberOrTypeExpressionPsi parent = this.parent,
                    exists model = parent.ceylonNode?.declaration,
                    model.native)
            //for native decs we need a ref per backend
            then ObjectArray.with {
                backendRef(Backends.header),
                backendRef(Backends.java),
                backendRef(Backends.js)
            }
            //otherwise there is just one
            else ObjectArray.with { reference };

    shared actual default String? name
            => this is CeylonPsi.IdentifierPsi then text;

    shared actual PsiElement setName(String name) {
        throw UnsupportedOperationException("Not yet");
    }

}

Pattern docLinkPattern = Pattern.compile("""\[\[([^\]\[]*)\]\]""");

"Makes links in documentation annotations resolvable to navigate to their
 original declaration."
shared class CeylonDocResolvable(ASTNode node)
        extends CeylonNamedCompositeElementImpl(node) {

    shared actual ObjectArray<PsiReference> references {
        if (exists type = node.firstChildNode?.elementType,
            type == CeylonTokens.astringLiteral
         || type == CeylonTokens.averbatimString) {
            value list = ArrayList<PsiReference>();
            value matcher = docLinkPattern.matcher(nativeString(node.text));
            while (matcher.find()) {
                value textRange
                        = TextRange(matcher.start(1),
                                    matcher.end(1));
                list.add(CeylonReference {
                    element = this;
                    range = textRange;
                });
                list.add(CeylonReference {
                    element = this;
                    range = textRange;
                    backend = Backends.header;
                });
            }
            return ObjectArray.with(list);
        }
        else {
            return PsiReference.emptyArray;
        }
    }

    shared actual PsiElement setName(String s) {
        throw UnsupportedOperationException("Not yet");
    }

}