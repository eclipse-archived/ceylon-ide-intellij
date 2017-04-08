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
import com.intellij.openapi.roots.ui.util {
    CompositeAppearance
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
    Font {
        bold=\iBOLD,
        italic=\iITALIC
    }
}
import java.lang {
    Types {
        nativeString
    },
    StringBuffer
}
import java.util {
    StringTokenizer
}

shared object highlighter {

    "Highlights a message that contains code snippets in single quotes, and returns
     an HTML representation of that message surrounded by `<html>` tags."
    shared String highlightQuotedMessage(String description, Project project,
        /*Boolean qualifiedNameIsPath = false,*/ Boolean eliminateQuotes = true) {

        value result = StringBuffer();
        result.append("<html>");

        iterateTokens(description, eliminateQuotes, (token, highlightMode) {
            switch(highlightMode)
            case (is Boolean) {
                result.append(highlight(token, project/*, qualifiedNameIsPath*/));
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

    "Parses a message that contains code snippets in single quotes, and delegates the actual
     highlighting to a callback."
    shared void parseQuotedMessage(String message, Project project,
        Boolean eliminateQuotes, Anything(String, TextAttributes?) callback) {

        iterateTokens(message, eliminateQuotes, (token, highlightMode) {
            switch(highlightMode)
            case (is Boolean) {
                highlightInternal(token, project, callback);
            }
            case (is TextAttributes) {
                callback(token, highlightMode);
            }
            else {
                callback(token, null);
            }
        });
    }

    "Highlights a message that contains code snippets in single quotes, and add colored
     fragments to the given [[data]]."
    shared void highlightPresentationData(PresentationData data, String description,
        Project project, /*Boolean qualifiedNameIsPath = false,*/ Boolean eliminateQuotes = true) {

        iterateTokens(description, eliminateQuotes, (token, highlight) {
            switch(highlight)
            case (is Boolean) {
                highlightInternal(token, project, /*qualifiedNameIsPath,*/ (token, attrs) {
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
    shared void highlightCompositeAppearance(CompositeAppearance appearance, String description,
            Project project, /*Boolean qualifiedNameIsPath = false,*/ Boolean eliminateQuotes = true) {

        value data = appearance.ending;
        iterateTokens(description, eliminateQuotes, (token, highlight) {
            switch(highlight)
            case (is Boolean) {
                highlightInternal(token, project, /*qualifiedNameIsPath,*/ (token, attrs) {
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
    shared String highlight(String rawText, Project project/*, Boolean qualifiedNameIsPath = false*/) {
        value builder = StringBuilder();

        highlightInternal(rawText, project, /*qualifiedNameIsPath,*/ (token, attrs) {
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
            String tok = tokens.nextToken();
            if (tok=="'") {
                if (!eliminateQuotes) {
                    consume(tok, null);
                }
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken();
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

    void highlightInternal(String rawText, Project project, //Boolean qualifiedNameIsPath,
        Anything(String, TextAttributes) consume) {

        value highlighter
                = HighlighterFactory.createHighlighter(project,
                    FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
        highlighter.setText(nativeString(rawText));

        value iterator = highlighter.createIterator(0);
        while (!iterator.atEnd()) {
            value start = iterator.start;
            value end = iterator.end;
            value token = rawText[start..end-1];
            value attrs =
                /*if (qualifiedNameIsPath
                    //&& token.every((ch)=>ch.letter) //TODO: this is wrong!!
                    && (rawText[start-1:1]=="." || rawText[end:1]==".") || token==".")
                then textAttributes(ceylonHighlightingColors.packages)
                else*/ iterator.textAttributes;

            consume(token, attrs);
            iterator.advance();
        }
    }

    shared String toColoredHtml(String token, TextAttributes attr) {
        String? color =
            if (exists fg = attr.foregroundColor, fg!=JBColor.black)
            then "<font color='#``ColorUtil.toHex(fg)``'>"
            else null;

        Boolean isBold = attr.fontType.and(bold) != 0;
        Boolean isItalic = attr.fontType.and(italic) != 0;

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

        value endsWithNewLine = (builder.last else ' ') == '\n';
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

