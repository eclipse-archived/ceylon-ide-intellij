import com.intellij.application.options {
    CodeStyleAbstractConfigurable,
    TabbedLanguageCodeStylePanel,
    SmartIndentOptionsEditor
}
import com.intellij.application.options.codeStyle {
    CodeStyleSpacesPanel
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings,
    CodeStyleSettingsProvider,
    LanguageCodeStyleSettingsProvider {
        SettingsType
    },
    CodeStyleSettingsCustomizable
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}

"Adds a 'Ceylon' entry to the 'Code Style' UI."
shared class CeylonCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    shared static String spacingOptionsGroup = "Spacing Options";
    shared static String parameterListGroup = "Parameter lists";
    shared static String iterableEnumsGroup = "Iterable Enumerations";
    shared static String iteratorInLoopGroup = "Iterator in 'for' Loop";

    shared new () extends CodeStyleSettingsProvider() {}

    createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) =>
            object extends CodeStyleAbstractConfigurable(settings, originalSettings, "Ceylon") {
                createPanel(CodeStyleSettings? settings)
                        => CodeStylePanel(currentSettings, originalSettings);

                helpTopic => null;
            };

    createCustomSettings(CodeStyleSettings settings)
            => CeylonCodeStyleSettings(settings);

    language => CeylonLanguage.instance;

    "A panel that shows additional tabs we're interested in."
    class CodeStylePanel(CodeStyleSettings settings, CodeStyleSettings originalSettings)
            extends TabbedLanguageCodeStylePanel(CeylonLanguage.instance, settings, originalSettings) {

        shared actual void initTabs(CodeStyleSettings mySettings) {
            addIndentOptionsTab(mySettings);
            addTab(object extends CodeStyleSpacesPanel(mySettings) {
                somethingChanged() => super.somethingChanged();
                defaultLanguage => outer.defaultLanguage;

                shared actual void initTables() {
                    initCustomOptions(spacingOptionsGroup);
                    initCustomOptions(parameterListGroup);
                    initCustomOptions(iterableEnumsGroup);
                    initCustomOptions(iteratorInLoopGroup);
                }
            });
//            addWrappingAndBracesTab(mySettings);
            addBlankLinesTab(mySettings);
            // TODO tabs for imports etc.
        }
    }
}

"The actual configurable part of the code style (code sample, configurable fields etc)."
shared class CeylonLanguageCodeStyleSettingsProvider()
        extends LanguageCodeStyleSettingsProvider() {

    language => CeylonLanguage.instance;

    "Here we determine which standard+custom settings to show in the UI."
    shared actual void customizeSettings(CodeStyleSettingsCustomizable consumer,
            SettingsType settingsType) {

        switch (settingsType)
        case (SettingsType.spacingSettings) {
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforePositionalArgs", "Before positional argument list", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforeAnnotationPositionalArgs", "Before annotation positional argument list", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceInSatisfiesAndOf", "Around '&' and '|' in 'satisfies' and 'of'", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAroundEqualsInImportAlias", "Around '=' in 'import' alias", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterTypeParam", "After type parameter list comma", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterTypeArg", "After type argument list comma", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAroundEqualsInTypeArgs", "Around '=' in default type arguments", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterKeyword", "After control structure keyword ('if', 'for', etc.)", CeylonCodeStyleSettingsProvider.spacingOptionsGroup);

            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforeParamListOpen", "Space before opening '('", CeylonCodeStyleSettingsProvider.parameterListGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterParamListOpen", "Space after opening '('", CeylonCodeStyleSettingsProvider.parameterListGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforeParamListClose", "Space before closing ')'", CeylonCodeStyleSettingsProvider.parameterListGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterParamListClose", "Space after closing ')'", CeylonCodeStyleSettingsProvider.parameterListGroup);

            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterIterableEnumOpen", "Space after opening '{'", CeylonCodeStyleSettingsProvider.iterableEnumsGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforeIterableEnumClose", "Space before closing '}'", CeylonCodeStyleSettingsProvider.iterableEnumsGroup);

            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceAfterIteratorInLoopOpen", "Space after opening '('", CeylonCodeStyleSettingsProvider.iteratorInLoopGroup);
            consumer.showCustomOption(`CeylonCodeStyleSettings`, "spaceBeforeIteratorInLoopClose", "Space before closing ')'", CeylonCodeStyleSettingsProvider.iteratorInLoopGroup);
        }
        case (SettingsType.blankLinesSettings) {
            consumer.showStandardOptions(
//                "KEEP_BLANK_LINES_IN_DECLARATIONS",
                "KEEP_BLANK_LINES_IN_CODE",
                "KEEP_BLANK_LINES_BEFORE_RBRACE",

                "BLANK_LINES_AFTER_IMPORTS",
                "BLANK_LINES_AROUND_CLASS",
                "BLANK_LINES_AROUND_METHOD"
            );
        }
        else {}
    }

    indentOptionsEditor => SmartIndentOptionsEditor();

    value codeSampleSpaces =>
            """import ceylon.file {
                   pp=parsePath,
                   File,
                   Reader
               }
               import ceylon.collection {
                   LL=LinkedList
               }

               by ("Someone")
               throws (`class Exception`, "If anything goes wrong")
               shared class WithLineNumbersReader(Reader r)
                       satisfies Reader & Iterable<String,Null> {

                   LL<String> lines = LL<String>();
                   variable Integer lineNum = 0;

                   close() => r.close();
                   shared actual Byte[] readBytes(Integer max) {
                       throw AssertionError("Can't read bytes from line-oriented reader");
                   }
                   shared actual Iterator<String> iterator() => lines.iterator();
                   shared actual String? readLine() {
                       if (exists line = r.readLine()) {
                           value ret = lineNum.string + "t" + line;
                           lines.add(ret);
                           lineNum++;
                           return ret;
                       } else {
                           return null;
                       }
                   }
               }

               void run() {
                   assert (is File f = pp(process.arguments.first else nothing).resource);
                   try (r = WithLineNumbersReader(f.Reader())) {
                       variable String? line = "";
                       while (line exists) {
                           line = r.readLine();
                       }
                       for (l in r) {
                           print(l);
                       }
                   } catch (e) {
                       e.printStackTrace();
                   }
                   print({ "Here", "have", "an", "iterable", "enumeration" });
                   value hollowCubeVol = w*h*d - iW*iH*iD;
               }

               void printTypeArgs<Param1=Anything, Param2=Nothing>()
                       => print(`Param1`.string + " " + `Param2`.string);
               """;

    value codeSampleIndents =>
            """shared class AccessCountingIterable<Element, Absent>(Iterable<Element,Absent> wrapped)
                       extends Object()
                       satisfies Iterable<Element,Absent>
                       given Absent satisfies Null {

                   variable Integer accessCounter = 0;
                   shared Integer accessCount => accessCounter;

                   shared actual Iterator<Element> iterator() {
                       accessCounter++;
                       return wrapped.iterator();
                   }

                   shared actual Boolean equals(Object that) {
                       if (is Iterable<Element,Absent> that) {
                           return wrapped == that;
                       } else {
                           return false;
                       }
                   }

                   shared actual Integer hash =>
                       sum {
                           accessCounter.hash,
                           wrapped.hash
                       };
               }
               """;

    value codeSampleBlankLines
            => """import ceylon.language {
                      pub=shared,
                      var=variable,
                      Str=String
                  }

                  {String*} words = { "You", "may", "want", "to", "break", "this", "up", "into", "multiple", "lines", "because", "like", "this", "it", "can", "be", "hard", "to", "read" };
                  {String*} moreWords = {
                      "However", "keep", "in", "mind", "that",
                      "the", "formatter", "can", "never", "be",
                      "as", "clever", "as", "a", "human",
                      "when", "deciding", "where", "line", "breaks",
                      "are", "most", "appropriate"
                  };

                  class ManyTypeParams<P1, P2, P3>() {
                      String s1 = "";

                      // How many line breaks do you want to allow between this comment and other content?

                      String s2 = "";

                      /* How about your single line comments like this one? */

                      String s3 = "";

                      /*
                       And how about your
                       multiline comments
                       like this one?
                       */

                      String s4 = "";
                      "";
                  }
                  """;

    getCodeSample(SettingsType settingsType) =>
            switch (settingsType)
            case (SettingsType.spacingSettings) codeSampleSpaces
            case (SettingsType.indentSettings) codeSampleIndents
            case (SettingsType.blankLinesSettings) codeSampleBlankLines
            else """shared class MyClass<A, B, C>()
                        given A satisfies Object
                        given B satisfies Foo {
                    }
                    """;
}
