import ceylon.interop.java {
    createJavaObjectArray,
    JavaStringMap,
    JavaMap
}

import com.intellij.openapi.editor {
    DefaultColors=DefaultLanguageHighlighterColors {
        strings=\iSTRING,
        ...
    }
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey {
        createKey=createTextAttributesKey
    },
    EditorColors,
    CodeInsightColors,
    EditorColorsManager
}
import com.intellij.openapi.editor.markup {
    TextAttributes
}
import com.intellij.openapi.options.colors {
    ColorSettingsPage,
    AttributesDescriptor,
    ColorDescriptor
}
import com.intellij.psi.codeStyle {
    DisplayPrioritySortable,
    DisplayPriority
}
import com.intellij.ui {
    JBColor
}

import java.awt {
    Color
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}


shared abstract class AbstractCeylonColorSettingsPage()
        satisfies ColorSettingsPage & DisplayPrioritySortable {

    displayName => "Ceylon";

    icon => icons.ceylon;

    value ourDescriptors = [
        ["Identifiers//Package identifiers", ceylonHighlightingColors.packages],
        ["Identifiers//Type identifiers", ceylonHighlightingColors.type],
        ["Identifiers//Other unqualified identifiers", ceylonHighlightingColors.identifier],
        ["Identifiers//Other qualified identifiers", ceylonHighlightingColors.member],
        ["Identifiers//Annotations", ceylonHighlightingColors.annotation],

        ["Keywords", ceylonHighlightingColors.keyword],

        ["Literals//Numeric literals", ceylonHighlightingColors.number],
        ["Literals//String literals", ceylonHighlightingColors.strings],
        ["Literals//Character literals", ceylonHighlightingColors.char],
        ["Literals//Strings in annotations", ceylonHighlightingColors.annotationString],

        ["Comments//Block comments", ceylonHighlightingColors.blockComment],
        ["Comments//Line comments", ceylonHighlightingColors.lineComment],
        ["Comments//Todos", ceylonHighlightingColors.todo],

        ["Metamodel expressions", ceylonHighlightingColors.meta],
        ["Interpolated expressions", ceylonHighlightingColors.interp],

        ["Punctuation//Braces", ceylonHighlightingColors.brace],
        ["Punctuation//Parentheses", ceylonHighlightingColors.paren],
        ["Punctuation//Brackets", ceylonHighlightingColors.bracket],
        ["Punctuation//Semicolons", ceylonHighlightingColors.semi],
        ["Punctuation//Commas", ceylonHighlightingColors.comma],
        ["Punctuation//Member operators", ceylonHighlightingColors.dot],
        ["Punctuation//Assignment operators", ceylonHighlightingColors.assignment]
    ]
    .collect(([name, key]) => AttributesDescriptor(name, key));

    attributeDescriptors => createJavaObjectArray(ourDescriptors);

    colorDescriptors => ColorDescriptor.emptyArray;

    priority = DisplayPriority.keyLanguageSettings;

    value ourTags = map {
        "anno" -> ceylonHighlightingColors.annotation,
        "interp" -> ceylonHighlightingColors.interp,
        "meta" -> ceylonHighlightingColors.meta,
        "stringInAnno" -> ceylonHighlightingColors.annotationString,
        "member" -> ceylonHighlightingColors.member,
        "pkg" -> ceylonHighlightingColors.packages,
        "todo" -> ceylonHighlightingColors.todo
    };

    additionalHighlightingTagToDescriptorMap => JavaMap(JavaStringMap(ourTags));
    
    demoText => """import <pkg>ceylon</pkg>.<pkg>numeric</pkg>.<pkg>integer</pkg> { smallest }

                   /*
                     The entry point.
                    */
                   <anno>see</anno>(<meta>`class Duck`</meta>)
                   <anno>shared</anno> void run() {
                       String greeting = "hello, world";
                       print("This program prints <interp>``mygreeting``</interp>");
                       value number = 13.37;
                       value char = 'X';
                       Duck().<member>quack</member>();
                    }

                    //Cool class
                    <todo>//TODO: make it way cooler</todo>
                    "Represents a duck"
                    <anno>by</anno>(<stringInAnno>"Trompon"</stringInAnno>)
                    class Duck() {
                        <anno>shared</anno> void quack() => print("Quack!");
                    }""";
    
}

shared object ceylonHighlightingColors {
    shared TextAttributesKey packages = createKey("CEYLON_PACKAGE", DefaultColors.identifier);
    shared TextAttributesKey identifier = createKey("CEYLON_IDENTIFIER", DefaultColors.identifier);
    shared TextAttributesKey type = createKey("CEYLON_TYPE", DefaultColors.className);
    shared TextAttributesKey member = createKey("CEYLON_MEMBER", DefaultColors.instanceField);
    shared TextAttributesKey annotation = createKey("CEYLON_ANNOTATION", DefaultColors.metadata);

    shared TextAttributesKey keyword = createKey("CEYLON_KEYWORD", DefaultColors.keyword);

    shared TextAttributesKey blockComment = createKey("CEYLON_BLOCK_COMMENT", DefaultColors.blockComment);
    shared TextAttributesKey lineComment = createKey("CEYLON_LINE_COMMENT", DefaultColors.lineComment);
    shared TextAttributesKey todo = createKey("CEYLON_TODO", CodeInsightColors.todoDefaultAttributes);

    shared TextAttributesKey number = createKey("CEYLON_NUMBER", DefaultColors.number);
    shared TextAttributesKey strings = createKey("CEYLON_STRING", DefaultColors.strings);
    shared TextAttributesKey char = createKey("CEYLON_CHARACTER", DefaultColors.strings);
    shared TextAttributesKey annotationString = createKey("CEYLON_ANNOTATION_STRING", DefaultColors.docComment);

    shared TextAttributesKey interp = createKey("CEYLON_INTERP", EditorColors.injectedLanguageFragment);
    shared TextAttributesKey meta = createKey("CEYLON_META", EditorColors.injectedLanguageFragment);

    shared TextAttributesKey brace = createKey("CEYLON_BRACE", DefaultColors.braces);
    shared TextAttributesKey paren = createKey("CEYLON_PAREN", DefaultColors.parentheses);
    shared TextAttributesKey bracket = createKey("CEYLON_BRACKET", DefaultColors.brackets);

    shared TextAttributesKey comma = createKey("CEYLON_COMMA", DefaultColors.comma);
    shared TextAttributesKey dot = createKey("CEYLON_DOT", DefaultColors.dot);
    shared TextAttributesKey semi = createKey("CEYLON_SEMI", DefaultColors.semicolon);
    shared TextAttributesKey assignment = createKey("CEYLON_ASSIGN", DefaultColors.operationSign);
}

shared TextAttributes textAttributes(TextAttributesKey key)
        => EditorColorsManager.instance.globalScheme.getAttributes(key);

shared Color foregroundColor(TextAttributesKey key)
        => textAttributes(key).foregroundColor
        else JBColor.foreground();