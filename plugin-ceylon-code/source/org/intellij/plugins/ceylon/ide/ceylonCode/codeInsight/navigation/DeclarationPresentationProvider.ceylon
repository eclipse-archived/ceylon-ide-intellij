import com.intellij.navigation {
    ItemPresentationProvider,
    ItemPresentation
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    getPresentation(DeclarationNavigationItem item) => object satisfies ItemPresentation {

        shared actual Icon? getIcon(Boolean unused) => ideaIcons.forDeclaration(item.decl);

        locationString
            => let (qName = item.decl.container.qualifiedNameString else "default module")
               "(" + qName + ")";

        presentableText => item.decl.name;
    };
}
