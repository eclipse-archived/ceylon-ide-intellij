package org.intellij.plugins.ceylon.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonDocProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        CeylonPsi.DeclarationPsi parentDecl = PsiTreeUtil.getParentOfType(element, CeylonPsi.DeclarationPsi.class);

        if (parentDecl != null) {
            Declaration decl = parentDecl.getCeylonNode().getDeclarationModel();

            if (decl == null) {
                // TODO once again we should have the declaration model, would be useful
                return parentDecl.getName() + " in " + parentDecl.getContainingFile().getName();
            } else {
                StringBuilder builder = new StringBuilder();

                String pkg = DocBuilder.getPackageFor(decl);

                if (StringUtil.isNotEmpty(pkg)) {
                    builder.append("<p><b>").append(pkg).append("</b></p>");
                }

                String declKind = "";
                if (decl instanceof com.redhat.ceylon.compiler.typechecker.model.Class) {
                    declKind = "class ";
                } else if (decl instanceof Interface) {
                    declKind = "interface ";
                }
                builder.append("<p><code>").append(DocBuilder.getModifiers(decl)).append(declKind).append("<b>").append(decl.getQualifiedNameString()).append("</b>").append("</code></p>");

                // TODO extends & implements

                DocBuilder.appendDocAnnotationContent(parentDecl.getCeylonNode().getAnnotationList(), builder, decl.getScope());

                return builder.toString();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        return contextElement;
    }
}
