package org.intellij.plugins.ceylon.ide.doc;

import com.intellij.lang.ExpressionTypeProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Type;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CeylonExpressionTypeProvider extends ExpressionTypeProvider<CeylonPsi.TermPsi> {
    @NotNull
    @Override
    public String getInformationHint(@NotNull CeylonPsi.TermPsi termPsi) {
        Tree.Term node = termPsi.getCeylonNode();
        Type type = node.getTypeModel();
        String text = type == null ? "<unknown>" : type.asString(node.getUnit());
        return StringUtil.escapeXml(text);
    }

    @NotNull
    @Override
    public String getErrorHint() {
        return "No expression found";
    }

    @NotNull
    @Override
    public List<CeylonPsi.TermPsi> getExpressionsAt(@NotNull PsiElement psiElement) {
        List<CeylonPsi.TermPsi> list = new ArrayList<>();
        for (CeylonPsi.TermPsi expression
                = PsiTreeUtil.getParentOfType(psiElement, CeylonPsi.TermPsi.class);
             expression != null;
             expression = PsiTreeUtil.getParentOfType(expression, CeylonPsi.TermPsi.class)) {
            if (!(expression instanceof CeylonPsi.ExpressionPsi) &&
                    !list.contains(expression)) {
                list.add(expression);
            }
        }
        return list;
    }
}
