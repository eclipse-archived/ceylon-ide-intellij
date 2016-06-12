import ceylon.interop.java {
    javaString
}

import com.intellij.ide.highlighter {
    HighlighterFactory
}
import com.intellij.ide.projectView {
    PresentationData
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
    JBColor,
    SimpleTextAttributes
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
import com.intellij.openapi.roots.ui.util {
    CompositeAppearance
}

shared object highlighter {

    "Highlights a message that contains code snippets in single quotes, and returns
     an HTML representation of that message surrounded by `<html>` tags."
    shared String highlightQuotedMessage(String description, Project project,
        Boolean qualifiedNameIsPath = false, Boolean eliminateQuotes = true) {

        value result = StringBuffer();
        result.append("<html>");

        iterateTokens(description, eliminateQuotes, (token, highlightMode) {
            switch(highlightMode)
            case (is Boolean) {
                result.append(highlight(token, project, qualifiedNameIsPath));
            }
            case (is TextAttributes) {
                result.append(toColoredHtml(token, highlightMode));
            }
            else {
                result.append(token);
            }
        });

        result.append("</html>");

        return result.string;
    }

    "Highlights a message that contains code snippets in single quotes, and add colored
     fragments to the given [[data]]."
    shared void highlightPresentationData(PresentationData data, String description, Project project,
        Boolean qualifiedNameIsPath = false, Boolean eliminateQuotes = true) {

        iterateTokens(description, eliminateQuotes, (token, highlight) {
            switch(highlight)
            case (is Boolean) {
                highlightInternal(token, project, qualifiedNameIsPath, (token, attrs) {
                    data.addText(token, SimpleTextAttributes.fromTextAttributes(attrs));
                });
            }
            case (is TextAttributes) {
                data.addText(token, SimpleTextAttributes.fromTextAttributes(highlight));
            }
            else {
                data.addText(token, SimpleTextAttributes.regularAttributes);
            }
        });
    }

    "Highlights a message that contains code snippets in single quotes, and add colored
     fragments to the given [[appearance]]."
    shared void highlightCompositeAppearance(CompositeAppearance appearance, String description, Project project,
            Boolean qualifiedNameIsPath = false, Boolean eliminateQuotes = true) {

        value data = appearance.ending;
        iterateTokens(description, eliminateQuotes, (token, highlight) {
            switch(highlight)
            case (is Boolean) {
                highlightInternal(token, project, qualifiedNameIsPath, (token, attrs) {
                    data.addText(token, SimpleTextAttributes.fromTextAttributes(attrs));
                });
            }
            case (is TextAttributes) {
                data.addText(token, SimpleTextAttributes.fromTextAttributes(highlight));
            }
            else {
                data.addText(token, SimpleTextAttributes.regularAttributes);
            }
        });
    }

    "Highlights a snippet of Ceylon code and returns its HTML representation *without*
     surrounding `<html>` tags."
    shared String highlight(String rawText, Project project, Boolean qualifiedNameIsPath = false) {
        value builder = StringBuilder();

        highlightInternal(rawText, project, qualifiedNameIsPath, (token, attrs) {
            builder.append(toColoredHtml(token, attrs));
        });

        return builder.string;
    }

    void iterateTokens(String description, Boolean eliminateQuotes,
        Anything(String, <TextAttributes|Boolean>?) consume) {

        value tokens = StringTokenizer(description, "'\"", true);
//        consume(tokens.nextToken(), null);

        value stringAttrs = textAttributes(ceylonHighlightingColors.strings);

        while (tokens.hasMoreTokens()) {
            variable String tok = tokens.nextToken();
            if (tok=="'") {
                if (!eliminateQuotes) {
                    consume(tok, null);
                }
                while (tokens.hasMoreTokens()) {
                    variable String token = tokens.nextToken();
                    if (token=="'") {
                        if (!eliminateQuotes) {
                            consume(token, null);
                        }
                        break;
                    } else if (token=="\"") {
                        consume(token, stringAttrs);
                        while (tokens.hasMoreTokens()) {
                            value quoted = tokens.nextToken();
                            consume(quoted, stringAttrs);
                            if (quoted=="\"") {
                                break;
                            }
                        }
                    } else {
                        consume(token, true);
                    }
                }
            } else {
                consume(tok, null);
            }
        }
    }

    void highlightInternal(String rawText, Project project, Boolean qualifiedNameIsPath,
        Anything(String, TextAttributes) consume) {

        value highlighter = HighlighterFactory.createHighlighter(
            project,
            FileTypeManager.instance.getFileTypeByFileName("coin.ceylon")
        );
        highlighter.setText(javaString(rawText));

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

            consume(token, attrs);
            iterator.advance();
        }
    }

    String toColoredHtml(String token, TextAttributes attr) {
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
}

