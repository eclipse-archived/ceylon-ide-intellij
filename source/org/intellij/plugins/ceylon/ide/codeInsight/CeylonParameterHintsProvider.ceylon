import com.intellij.codeInsight.hints {
    InlayParameterHintsProvider,
    InlayInfo,
    MethodInfo,
    JavaInlayParameterHintsProvider
}
import com.intellij.openapi.util.text {
    StringUtil
}
import com.intellij.psi {
    PsiElement,
    PsiNamedElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.loader.model {
    JavaMethod,
    LazyClass,
    LazyFunction
}
import com.redhat.ceylon.model.typechecker.model {
    ParameterList,
    Declaration,
    Functional
}

import java.lang {
    JString=String
}
import java.util {
    List,
    Collections,
    ArrayList,
    Arrays
}

import org.intellij.plugins.ceylon.ide.model {
    PSIMethod
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

"Provide parameter names for calls to Java methods (we can't use named arguments on them)."
shared class CeylonInlayParameterHintsProvider() satisfies InlayParameterHintsProvider {

    blackListDependencyLanguage => null;

    // Just reuse the same list as the Java provider
    defaultBlackList => JavaInlayParameterHintsProvider().defaultBlackList;

    function findParameterList(Declaration? decl)
            => if (is Functional decl)
            then decl.firstParameterList
            else null;

    function findMethodMirror(Declaration? decl)
            => if (is PSIMethod mirror
                = switch (decl)
                case (is JavaMethod) decl.mirror
                case (is LazyClass) decl.constructor
                case (is LazyFunction) decl.methodMirror
                else null)
            then mirror else null;

    shared actual MethodInfo? getMethodInfo(PsiElement psiElement) {
        if (is CeylonPsi.InvocationExpressionPsi invocation = psiElement,
            is Tree.MemberOrTypeExpression mot = invocation.ceylonNode.primary,
            exists mirror = findMethodMirror(mot.declaration)) {

            value psiMethod = mirror.psi;
            if (exists containingClass = psiMethod.containingClass) {
                value fullMethodName = StringUtil.getQualifiedName(containingClass.qualifiedName, mirror.name);

                value paramNames = Arrays.asList(
                    for (p in psiMethod.parameterList.parameters)
                    JString((p of PsiNamedElement).name else "")
                );

                return MethodInfo(fullMethodName, paramNames);
            }
        }

        return null;
    }

    shared actual List<InlayInfo> getParameterHints(PsiElement psiElement) {
        if (is CeylonPsi.InvocationExpressionPsi invocation = psiElement,
            is Tree.MemberOrTypeExpression mot = invocation.ceylonNode.primary,
            exists argList = invocation.ceylonNode?.positionalArgumentList,
            exists pl = findParameterList(mot.declaration)) {

            value hints = ArrayList<InlayInfo>();
            value args = argList.positionalArguments;
            variable value idx = 0;
            for (param in pl.parameters) {
                if (idx < args.size(),
                    exists name = param.model?.nameAsString,
                    exists index = args[idx]?.startIndex) {

                    hints.add(InlayInfo(name, index.intValue()));
                }
                idx++;
            }
            return hints;
        }

        return Collections.emptyList<InlayInfo>();
    }

}
