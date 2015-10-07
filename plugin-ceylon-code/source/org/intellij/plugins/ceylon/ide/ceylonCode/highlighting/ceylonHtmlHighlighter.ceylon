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
import java.lang {
    StringBuffer
}
import java.util {
    StringTokenizer
}
import java.util.regex {
    Pattern
}

Pattern path = Pattern.compile("\\b\\p{Ll}+(\\.\\p{Ll}+)+\\b");

shared String highlightProposal(String description, Project project, Boolean qualifiedNameIsPath = false, Boolean eliminateQuotes = true) {
    value result = StringBuffer();
    StringTokenizer tokens = StringTokenizer(description, "'\"", true);
    result.append("<html>");
    result.append(tokens.nextToken());

    value stringAttrs = textAttributes(ceylonHighlightingColors.strings);
    
    while (tokens.hasMoreTokens()) {
        variable String tok = tokens.nextToken();
        if (tok.equals("\'")) {
            if (!eliminateQuotes) {
                result.append(tok);
            }
            while (tokens.hasMoreTokens()) {
                variable String token = tokens.nextToken();
                if (token.equals("\'")) {
                    if (!eliminateQuotes) {
                        result.append(token);
                    }
                    break;
                } else if (token.equals("\"")) {
                    result.append(style(token, 0, token.size, stringAttrs));
                    while (tokens.hasMoreTokens()) {
                        value quoted = tokens.nextToken();
                        result.append(style(quoted, 0, quoted.size, stringAttrs));
                        if (quoted.equals("\"")) {
                            break;
                        }
                    }
                } else {
                    result.append(highlight(token, project, qualifiedNameIsPath));
                }
            }
        } else {
            result.append(tok);
        }
    }
    result.append("</html>");
    
    return result.string;
}

shared String highlight(String rawText, Project project, Boolean qualifiedNameIsPath = false) {
    EditorHighlighter highlighter = HighlighterFactory.createHighlighter(project, FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
    highlighter.setText(javaString(rawText));

    HighlighterIterator iterator = highlighter.createIterator(0);
    StringBuilder builder = StringBuilder();

    while (!iterator.atEnd()) {
        // TODO this doesn't work because dots have their own attributes and split qualified names into multiple parts
        value attrs = if (qualifiedNameIsPath && path.matcher(javaString(rawText.span(iterator.start, iterator.end - 1))).find())
                        then textAttributes(ceylonHighlightingColors.packages)
                        else iterator.textAttributes;
        
        builder.append(style(rawText, iterator.start, iterator.end, attrs));
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

