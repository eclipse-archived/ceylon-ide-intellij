package org.intellij.plugins.ceylon.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        final Node mayBeDeclaration = ((CeylonCompositeElementImpl) element.getParent().getParent().getParent().getParent()).getCeylonNode();
        if (mayBeDeclaration instanceof Tree.Declaration) {
            final Tree.Declaration declaration = (Tree.Declaration) mayBeDeclaration;
            final Tree.AnnotationList annotationList = declaration.getAnnotationList();

            // This code is copied and slightly adapted from com.redhat.ceylon.eclipse.code.hover.CeylonHover.appendDocAnnotationContent().

            if (annotationList!=null) {
                Tree.AnonymousAnnotation aa = annotationList.getAnonymousAnnotation();
                if (aa!=null) {
                    return aa.getStringLiteral().getText();
                }
                for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                    Tree.Primary annotPrim = annotation.getPrimary();
                    if (annotPrim instanceof Tree.BaseMemberExpression) {
                        String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                        if ("doc".equals(name)) {
                            Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                            if (argList!=null) {
                                List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                                if (!args.isEmpty()) {
                                    Tree.PositionalArgument a = args.get(0);
                                    if (a instanceof Tree.ListedArgument) {
                                        String text = ((Tree.ListedArgument) a).getExpression()
                                                .getTerm().getText();
                                        if (text!=null) {
                                            return text;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
