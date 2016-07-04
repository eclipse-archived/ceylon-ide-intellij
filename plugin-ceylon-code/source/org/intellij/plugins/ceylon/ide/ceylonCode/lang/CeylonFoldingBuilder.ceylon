import com.intellij.lang {
    ASTNode
}
import com.intellij.lang.folding {
    FoldingBuilderEx,
    FoldingDescriptor
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonTokens,
    CeylonTypes
}

shared class CeylonFoldingBuilder() extends FoldingBuilderEx() {

    isCollapsedByDefault(ASTNode node) => node.elementType == CeylonTypes.importList;

    value blockElementTypes = [
        CeylonTypes.importModuleList,
        CeylonTypes.block,
        CeylonTypes.classBody,
        CeylonTypes.interfaceBody
    ];

    shared actual String getPlaceholderText(ASTNode node) {
        if (node.elementType in blockElementTypes) {
            value text = node.text.normalized.trimmed;
            return text.size<40 then text else "{...}";
        } else {
            return "...";
        }
    }

    shared actual ObjectArray<FoldingDescriptor> buildFoldRegions(PsiElement root,
            Document document, Boolean quick) {
        value descriptors = ArrayList<FoldingDescriptor>();
        appendDescriptors(root, descriptors);
        return descriptors.toArray(ObjectArray<FoldingDescriptor>(descriptors.size()));
    }

    void foldElement(PsiElement element, List<FoldingDescriptor> descriptors)
            => descriptors.add(FoldingDescriptor(element.node, element.textRange));

    void foldRange(Integer[2] range, PsiElement element, List<FoldingDescriptor> descriptors)
            => descriptors.add(FoldingDescriptor(element.node, TextRange(*range)));

    void foldAfterFirstLine(PsiElement element, List<FoldingDescriptor> descriptors) {
        if (exists newLine = element.text.firstOccurrence('\n')) {
            value start = element.textRange.startOffset + newLine;
            value end = element.textRange.endOffset;
            foldRange([start, end], element, descriptors);
        }
    }

    void appendDescriptors(PsiElement element, List<FoldingDescriptor> descriptors) {
        if (element is CeylonPsi.BodyPsi) {
            foldElement(element, descriptors);
        }
        else if (element is CeylonPsi.ImportModuleListPsi ) {
            foldElement(element, descriptors);
        }
        else if (element.node.elementType == CeylonTokens.multiComment) {
            foldAfterFirstLine(element, descriptors);
        }
        else if (element.node.elementType == CeylonTokens.lineComment) {
            value prev = PsiTreeUtil.prevVisibleLeaf(element);
            value lineComment
                    = if (exists prev)
                    then prev.node.elementType == CeylonTokens.lineComment
                    else false;
            if (lineComment) {
                variable PsiElement lastComment = element;
                while (exists next = PsiTreeUtil.nextVisibleLeaf(lastComment),
                       next.node.elementType == CeylonTokens.lineComment) {
                    lastComment = next;
                }
                if (lastComment != element) {
                    value start = element.textRange.endOffset-1;
                    value end = lastComment.textRange.endOffset-1;
                    foldRange([start, end], element, descriptors);
                }
            }
        }
        else if (element is CeylonPsi.StringLiteralPsi) {
            foldAfterFirstLine(element, descriptors);
        }
        else if (element is CeylonPsi.ImportListPsi,
                 element.textLength>0) {
            value start
                    = element.textRange.startOffset
                    + (element.text.startsWith("import ") then 7 else 0);
            value end = element.textRange.endOffset;
            foldRange([start, end], element, descriptors);
        }
        variable PsiElement? child = element.firstChild;
        while (exists ch = child) {
            appendDescriptors(ch, descriptors);
            child = ch.nextSibling;
        }
    }

}
