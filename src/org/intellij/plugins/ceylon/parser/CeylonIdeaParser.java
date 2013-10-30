package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonTokenType;
import org.intellij.plugins.ceylon.psi.CeylonTokens;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonIdeaParser implements PsiParser {

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("Ceylon Node");
    public static final TokenSet COMPOSITE_ELEMENTS = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTokens.WS) && !(type instanceof CeylonTokenType);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        final PsiFile file = builder.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
        final MyTree myTree = ((CeylonFile)file).getMyTree();
        final MarkingCeylonParser parser = new MarkingCeylonParser(builder, myTree);
        Node result;

        try {
            if (root == CeylonTypes.COMPILATION_UNIT) {
                result = parser.compilationUnit();
/*
            // This seems to be unnecessary unless we start using chameleon tokens (see parse method's javadocs).
            else if (root == CeylonTypes.MODULE_DESCRIPTOR) {
                result = parser.moduleDescriptor();
            } else if (root == CeylonTypes.PACKAGE_DESCRIPTOR) {
                result = parser.packageDescriptor();
            } else if (root == CeylonTypes.IMPORT_MODULE_LIST) {
                result = parser.importModuleList();
            } else if (root == CeylonTypes.IMPORT_MODULE) {
                result = parser.importModule();
            } else if (root == CeylonTypes.IMPORT_LIST) {
                result = parser.importElementList();
             ...
*/
            } else {
                throw new UnsupportedOperationException(String.format("Unsupported type: %s", root));
            }
        } catch (RecognitionException e) {
            throw new RuntimeException("Unrecognized", e);
        }
        if (result == null) {
            throw new NullPointerException("CeylonParser returned null.");
        }

        ASTNode astRoot = builder.getTreeBuilt();
        myTree.bindToRoot(astRoot);
        return astRoot;
    }

}
