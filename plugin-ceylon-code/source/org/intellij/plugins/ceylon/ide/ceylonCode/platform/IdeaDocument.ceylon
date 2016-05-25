import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}

shared class IdeaDocument(shared Document nativeDocument) satisfies CommonDocument {
    getLineOfOffset(Integer offset) => nativeDocument.getLineNumber(offset);

    getLineContent(Integer line)
            => let (range = TextRange(getLineStartOffset(line), getLineEndOffset(line)))
               nativeDocument.getText(range);

    getLineEndOffset(Integer line) => nativeDocument.getLineEndOffset(line);

    getLineStartOffset(Integer line) => nativeDocument.getLineStartOffset(line);

    getText(Integer offset, Integer length)
            => nativeDocument.getText(TextRange.from(offset, length));

    defaultLineDelimiter => "\n";

    size => nativeDocument.textLength;

    shared actual Boolean equals(Object that) {
        if (is IdeaDocument that) {
            return nativeDocument==that.nativeDocument;
        }
        else {
            return false;
        }
    }
}
