import ceylon.interop.java {
    javaString
}

import com.intellij.ide.highlighter {
    HighlighterFactory
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
import com.redhat.ceylon.ide.common.doc {
    convertToHTML
}

import java.awt {
    ...
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

shared String highlightProposal(String description, Project project, 
    Boolean qualifiedNameIsPath = false, Boolean eliminateQuotes = true) {
    value result = StringBuffer();
    value tokens = StringTokenizer(description, "'\"", true);
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
                    result.append(style(token, stringAttrs));
                    while (tokens.hasMoreTokens()) {
                        value quoted = tokens.nextToken();
                        result.append(style(quoted, stringAttrs));
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
    value highlighter = 
            HighlighterFactory.createHighlighter(project, 
                FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
    highlighter.setText(javaString(rawText));

    value builder = StringBuilder();
    value iterator = highlighter.createIterator(0);
    while (!iterator.atEnd()) {
        value start = iterator.start;
        value end = iterator.end;
        value token = rawText[start..end-1];
        value attrs = 
                if (qualifiedNameIsPath 
                    //&& token.every((ch)=>ch.letter) //TODO: this is wrong!! 
                    && (rawText[start-1:1]=="." || rawText[end:1]=="."))
                        then textAttributes(ceylonHighlightingColors.packages)
                        else iterator.textAttributes;
        builder.append(style(token, attrs));
        iterator.advance();
    }

    return builder.string;
}

String style(String token, TextAttributes attr) {
    String? color =
            if (exists fg = attr.foregroundColor, !fg.equals(JBColor.\iBLACK))
                then "<font color='#" + ColorUtil.toHex(attr.foregroundColor) + "'>"
            else null;

    Boolean isBold = attr.fontType.and(Font.\iBOLD) != 0;
    Boolean isItalic = attr.fontType.and(Font.\iITALIC) != 0;

    value builder = StringBuilder();

    if (exists color) {
        builder.append(color);
    }
    if (isBold) {
        builder.append("<b>");
    }
    if (isItalic) {
        builder.append("<i>");
    }

    builder.append(convertToHTML(token));
    
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

