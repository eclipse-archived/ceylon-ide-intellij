import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.codeStyle {
    SuggestedNameInfo
}
import com.intellij.refactoring.rename {
    NameSuggestionProvider
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Parameter
}

import java.lang {
    Types {
        nativeString
    },
    JString=String,
    ObjectArray,
    overloaded
}
import java.util {
    Set
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonFile
}

class CeylonNameSuggestionProvider() satisfies NameSuggestionProvider {

    value noStrings = ObjectArray<JString>(0);

    shared actual SuggestedNameInfo? getSuggestedNames(PsiElement element, PsiElement nameSuggestionContext, Set<JString> result) {
        if (is CeylonFile file = element.containingFile) {
            file.localAnalyzer?.ensureTypechecked();
        }
        if (element.language.isKindOf(ceylonLanguage)) {
            nodes.renameProposals {
                node = if (is CeylonPsi.DeclarationPsi element)
                    then element.ceylonNode else null;
                rootNode = if (is CeylonFile file = element.containingFile)
                    then file.compilationUnit else null;
            }
            .map(nativeString)
            .each(result.add);

            //This code runs with the after-refactoring AST,
            //so ceylon-ide-common doesn't know how to suggest
            //a name for an argument. This crap implementation
            //attempts to reproduce what nodes.addArgumentNameProposals()
            //already does better!
            //TODO: reproduce the special handling for sequenced args!
            if (is CeylonFile file = element.containingFile,
                is CeylonPsi.TypedDeclarationPsi element,
                exists dec = element.ceylonNode?.declarationModel) {

                object extends Visitor() {
                    variable Tree.ListedArgument|Tree.SpreadArgument|Tree.SpecifiedArgument?
                    currentArg = null;

                    overloaded
                    shared actual void visit(Tree.ListedArgument that) {
                        value oca = currentArg;
                        currentArg = that;
                        super.visit(that);
                        currentArg = oca;
                    }
                    overloaded
                    shared actual void visit(Tree.SpreadArgument that) {
                        value oca = currentArg;
                        currentArg = that;
                        super.visit(that);
                        currentArg = oca;
                    }
                    overloaded
                    shared actual void visit(Tree.SpecifiedArgument that) {
                        value oca = currentArg;
                        currentArg = that;
                        super.visit(that);
                        currentArg = oca;
                    }

                    void addName(Parameter param)
                            => nodes.addNameProposals(result, false, param.name);

                    function isReference(Tree.Expression? ex, Tree.BaseMemberExpression bme)
                            => switch (term = ex?.term)
                            case (is Tree.InvocationExpression) term.primary == bme
                            case (is Tree.BaseMemberExpression) term == bme
                            else false;

                    overloaded
                    shared actual void visit(Tree.BaseMemberExpression that) {
                        if (that.declaration?.equals(dec) else false,
                            exists arg = currentArg) {
                            switch (arg)
                            case (is Tree.ListedArgument) {
                                if (isReference(arg.expression, that),
                                    exists param = arg.parameter) {
                                    addName(param);
                                }
                            }
                            case (is Tree.SpreadArgument) {
                                if (isReference(arg.expression, that),
                                    exists param = arg.parameter) {
                                    addName(param);
                                }
                            }
                            case (is Tree.SpecifiedArgument) {
                                if (isReference(arg.specifierExpression?.expression, that),
                                    exists param = arg.parameter) {
                                    addName(param);
                                }
                            }
                        }
                    }

                }.visit(file.compilationUnit);
            }

            return object extends SuggestedNameInfo(result.toArray(noStrings)) {};
        }
        else {
            return null;
        }
    }

}