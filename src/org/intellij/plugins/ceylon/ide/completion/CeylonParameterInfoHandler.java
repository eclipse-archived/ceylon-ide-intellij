package org.intellij.plugins.ceylon.ide.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.FindNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.*;
import com.redhat.ceylon.model.typechecker.util.TypePrinter;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlight_.highlight;

public class CeylonParameterInfoHandler implements ParameterInfoHandler<CeylonPsi.ArgumentListPsi, Functional> {

    public static final com.intellij.util.Function<String, String> HTMLIZE = new com.intellij.util.Function<String, String>() {
        @Override
        public String fun(String s) {
            return s.replace("≤", "<").replace("≥", ">");
        }
    };

    private TypePrinter printer = new TypePrinter(true, true, false, true, false);

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return new Object[0];
    }

    @Nullable
    @Override
    public Object[] getParametersForDocumentation(Functional p, ParameterInfoContext context) {
        return new Object[0];
    }

    @Nullable
    @Override
    public CeylonPsi.ArgumentListPsi findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        PsiElement elementAtOffset = PsiUtilCore.getElementAtOffset(context.getFile(), context.getOffset());

        CeylonPsi.InvocationExpressionPsi invocation = PsiTreeUtil.getParentOfType(elementAtOffset, CeylonPsi.InvocationExpressionPsi.class);
        CeylonPsi.ArgumentListPsi args = PsiTreeUtil.getParentOfType(elementAtOffset, CeylonPsi.ArgumentListPsi.class);

        if (invocation != null && args != null) {
            FindNodeVisitor visitor = new FindNodeVisitor(invocation.getTextOffset(), invocation.getTextOffset() + invocation.getTextLength());
            ((CeylonFile) context.getFile()).getCompilationUnit().visit(visitor);
            Node node = visitor.getNode();

            if (node instanceof Tree.InvocationExpression) {
                Tree.Primary primary = ((Tree.InvocationExpression) node).getPrimary();
                Declaration declaration = ((Tree.InvocationExpression) node).getTypeModel().getDeclaration();

                if (primary instanceof Tree.MemberOrTypeExpression) {
                    declaration = ((Tree.MemberOrTypeExpression) primary).getDeclaration();
                }

                if (declaration instanceof Functional) {
                    context.setItemsToShow(new Object[]{declaration});
                }
            }
            return args;
        }
        return null;
    }

    @Override
    public void showParameterInfo(@NotNull CeylonPsi.ArgumentListPsi element, @NotNull CreateParameterInfoContext context) {
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Nullable
    @Override
    public CeylonPsi.ArgumentListPsi findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiElement elementAtOffset = PsiUtilCore.getElementAtOffset(context.getFile(), context.getOffset());

        return PsiTreeUtil.getParentOfType(elementAtOffset, CeylonPsi.ArgumentListPsi.class);
    }

    @Override
    public void updateParameterInfo(@NotNull CeylonPsi.ArgumentListPsi parameterListPsi, @NotNull UpdateParameterInfoContext context) {
        PsiElement[] children = parameterListPsi.getChildren();
        if (children.length == 1) {
            int index = ParameterInfoUtils.getCurrentParameterIndex(children[0].getNode(), context.getOffset(), TokenTypes.COMMA.getTokenType());
            context.setCurrentParameter(index);
        }
    }

    @Nullable
    @Override
    public String getParameterCloseChars() {
        return ParameterInfoUtils.DEFAULT_PARAMETER_CLOSE_CHARS;
    }

    @Override
    public boolean tracksParameterIndex() {
        return true;
    }

    @Override
    public void updateUI(Functional fun, @NotNull ParameterInfoUIContext context) {
        String params = "<no parameters>";

        StringBuilder builder = new StringBuilder();

        int highlightOffsetStart = -1;
        int highlightOffsetEnd = -1;
        int i = 0;

        if (fun.getFirstParameterList().getParameters().size() > 0) {
            for (Parameter param : fun.getFirstParameterList().getParameters()) {
                String paramLabel = getParameterLabel(param, ((Declaration) fun).getUnit());
                paramLabel = highlight(paramLabel, context.getParameterOwner().getProject()).replace('<', '≤').replace('>', '≥');
                if (i == context.getCurrentParameterIndex()) {
                    highlightOffsetStart = builder.length();
                    highlightOffsetEnd = builder.length() + paramLabel.length();
                }

                builder.append(paramLabel).append(", ");
                i++;
            }

            builder.delete(builder.length() - 2, builder.length());

            params = builder.toString();
        }

        if (context instanceof ParameterInfoUIContextEx) {
            ((ParameterInfoUIContextEx) context).setEscapeFunction(HTMLIZE);
        }
        context.setupUIComponentPresentation(params, highlightOffsetStart, highlightOffsetEnd, false, false, false, context.getDefaultParameterColor());
    }

    private String getParameterLabel(Parameter param, Unit unit) {
        StringBuilder builder = new StringBuilder(printer.print(param.getType(), unit)).append(" ").append(param.getName());

        if (param.getModel() instanceof Function) {
            Function model = (Function) param.getModel();

            builder.append('(');

            for (Parameter parameter : model.getFirstParameterList().getParameters()) {
                builder.append(getParameterLabel(parameter, unit));
                builder.append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
            builder.append(')');
        }

        return builder.toString();
    }
}
