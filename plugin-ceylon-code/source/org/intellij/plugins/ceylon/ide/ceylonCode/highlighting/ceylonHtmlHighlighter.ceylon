import com.intellij.ide.highlighter {
    HighlighterFactory
}
import com.intellij.openapi.editor.highlighter {
    EditorHighlighter,
    HighlighterIterator
}
import com.intellij.openapi.editor.markup {
    TextAttributes
}
import com.intellij.openapi.fileTypes {
    FileTypeManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.ui {
    ColorUtil,
    JBColor
}
import java.awt {
    ...
}
import ceylon.interop.java {
    javaString
}
import com.redhat.ceylon.ide.common.doc {
    convertToHTML
}

shared String highlight(String rawText, Project project) {
    EditorHighlighter highlighter = HighlighterFactory.createHighlighter(project, FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
    highlighter.setText(javaString(rawText));

    HighlighterIterator iterator = highlighter.createIterator(0);
    StringBuilder builder = StringBuilder();

    while (!iterator.atEnd()) {
        builder.append(style(rawText, iterator.start, iterator.end, iterator.textAttributes));
        iterator.advance();
    }

    return builder.string;
}

String style(String text, Integer start, Integer end, TextAttributes attr) {
    String? color =
            if (exists fg = attr.foregroundColor, !fg.equals(JBColor.\iBLACK))
                then "<font color='#" + ColorUtil.toHex(attr.foregroundColor) + "'>"
            else null;

    Boolean isBold = (attr.fontType.and(Font.\iBOLD)) != 0;
    Boolean isItalic = (attr.fontType.and(Font.\iITALIC)) != 0;

    StringBuilder builder = StringBuilder();

    if (exists color) {
        builder.append(color);
    }
    if (isBold) {
        builder.append("<b>");
    }
    if (isItalic) {
        builder.append("<i>");
    }

    builder.append(convertToHTML(text.span(start, end - 1)));
    
    value endsWithNewLine = if ((builder.last else ' ') == '\n') then true else false;
    if (endsWithNewLine) {
        builder.deleteTerminal(1);
    }
    
    if (isItalic) {
        builder.append("</i>");
    }
    if (isBold) {
        builder.append("</b>");
    }
    if (exists color) {
        builder.append("</font>");
    }

    if (endsWithNewLine) {
        builder.append("\n");
    }
    
    return builder.string;
}

