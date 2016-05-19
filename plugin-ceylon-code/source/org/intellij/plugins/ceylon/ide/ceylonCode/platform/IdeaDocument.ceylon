import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
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

    // TODO take the settings from the current project
    indentSpaces => CodeStyleSettings().indentOptions.indentSize;

    indentWithSpaces => true;

    size => nativeDocument.textLength;
}
