import com.intellij.lang.refactoring {
    NamesValidator
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.patterns {
    PlatformPatterns
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.rename {
    RenameInputValidatorEx
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.ide.common.util {
    escaping
}

import java.lang {
    JString=String
}
import java.util.regex {
    Pattern
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

"Validates uppercase names (types)."
shared class CeylonRenameTypeInputValidator() satisfies RenameInputValidatorEx {

    shared actual String? getErrorMessage(String newName, Project project) {
        if (newName.empty) {
            return "missing identifier";
        }
        if (!Pattern.matches("""^(\\I\w|[a-zA-Z_])\w*$""", JString(newName))) {
            return "'``newName``' is not a legal identifier";
        }
        if (newName.startsWith("""\I""")) {
            return null;
        }
//        if (escaping.isKeyword(newName)) {
//            return "'``newName``' is a keyword";
//        }
        assert (exists first = newName[0]);
        if (!first.uppercase) {
            return "'``newName``' does not begin with uppercase";
        }
        return null;
    }

    isInputValid(String newName, PsiElement element, ProcessingContext processingContext) => true;

    pattern => PlatformPatterns.psiElement(`CeylonPsi.TypeDeclarationPsi`);

}

"Validates lowercase names (typed values)."
shared class CeylonRenameTypedInputValidator() satisfies RenameInputValidatorEx {

    shared actual String? getErrorMessage(String newName, Project project) {
        if (newName.empty) {
            return "missing identifier";
        }
        if (!Pattern.matches("""^(\\i\w|[a-zA-Z_])\w*$""", JString(newName))) {
            return "'``newName``' is not a legal identifier";
        }
        if (newName.startsWith("""\i""")) {
            return null;
        }
        if (escaping.isKeyword(newName)) {
            return "'``newName``' is a keyword";
        }
        assert (exists first = newName[0]);
        if (!first.lowercase && !first=='_') {
            return "'``newName``' does not begin with lowercase";
        }
        return null;
    }

    isInputValid(String newName, PsiElement element, ProcessingContext processingContext) => true;

    pattern => PlatformPatterns.psiElement(`CeylonPsi.TypedDeclarationPsi`);

}

"Validates module names."
shared class CeylonModuleNameInputValidator() satisfies RenameInputValidatorEx {

    shared actual String? getErrorMessage(String newName, Project project) {
        if (newName.empty) {
            return "module name cannot be empty";
        }
        if (!Pattern.matches("""^(\\i\w|[a-zA-Z_])\w*(\.(\\i\w|[a-zA-Z_])\w*)*$""", JString(newName))) {
            return "'``newName``' is not a legal module name";
        }
        for (part in newName.split('.'.equals)) {
            if (part.startsWith("""\i""")) {
                continue;
            }
            if (escaping.isKeyword(part)) {
                return "'``part``' is a keyword";
            }
            assert (exists first = part[0]);
            if (!first.lowercase && first!='_') {
                return "'``newName``' does not begin with lowercase";
            }
        }
        return null;
    }

    isInputValid(String newName, PsiElement element, ProcessingContext processingContext) => true;

    pattern => PlatformPatterns.psiElement(`CeylonPsi.ModuleDescriptorPsi`);

}

shared class CeylonNamesValidator() satisfies NamesValidator {

    isIdentifier(String string, Project project)
            => !string.empty
            && Pattern.matches("""^(\\i\w|\\I\w|[a-zA-Z_])\w*$""", JString(string))
            && !escaping.isKeyword(string);

    isKeyword(String string, Project project)
            => escaping.isKeyword(string);

}
