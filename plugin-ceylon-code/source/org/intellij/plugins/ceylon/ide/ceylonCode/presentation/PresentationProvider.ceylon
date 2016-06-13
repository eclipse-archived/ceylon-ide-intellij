import com.intellij.navigation {
    ItemPresentation,
    ItemPresentationProvider
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope,
    TypeDeclaration
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonClass
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner,
    SpecifierStatementPsiIdOwner
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class ClassPresentationProvider() 
        satisfies ItemPresentationProvider<CeylonClass> {

    shared actual ItemPresentation getPresentation(CeylonClass item) {
        return object satisfies ItemPresentation {

            shared actual String? presentableText {
                if (exists model = item.ceylonNode?.declarationModel) {
                    value text = StringBuilder().append(model.name);
                    variable Scope container = model.container;
                    while (is TypeDeclaration type = container) {
                        text.append(" in ").append(type.name);
                        container = container.container;
                    }
                    return text.string;
                }
                else {
                    return item.ceylonNode?.identifier?.text;
                }
            }

            shared actual String? locationString 
                    => if (exists node = item.ceylonNode) 
                    then "(``node.unit.\ipackage.\imodule.nameAsString``)" 
                    else null;

            shared actual Icon? getIcon(Boolean unused) 
                    => if (exists node = item.ceylonNode) 
                    then ideaIcons.forDeclaration(node)
                    else null;
        };
    }
}

shared class DeclarationPresentationProvider() 
        satisfies ItemPresentationProvider<DeclarationPsiNameIdOwner> {

    shared actual ItemPresentation getPresentation(DeclarationPsiNameIdOwner item) {
        return object satisfies ItemPresentation {

            shared actual String? presentableText {
                if (exists model = item.ceylonNode?.declarationModel) {
                    value name = model.name;
                    if (is ClassOrInterface classOrInterface = model.container) {
                        return classOrInterface.name + "." + name;
                    } else {
                        return name;
                    }
                } else {
                    return item.ceylonNode?.identifier?.text;
                }
            }

            shared actual String? locationString {
                if (exists model = item.ceylonNode?.declarationModel) {
                    value dec 
                        = if (is ClassOrInterface container = model.container)
                        then container else model;
                    value qualifiedNameString 
                        = dec.container.qualifiedNameString;
                    return qualifiedNameString.empty 
                    then "(default module)" 
                    else "(``qualifiedNameString``)";
                }
                else {
                    return null;
                }
            }

            shared actual Icon? getIcon(Boolean unused) 
                    => if (exists node = item.ceylonNode) 
                    then ideaIcons.forDeclaration(node) 
                    else null;
        };
    }
}

shared class SpecifierPresentationProvider() 
        satisfies ItemPresentationProvider<SpecifierStatementPsiIdOwner> {

    shared actual ItemPresentation? getPresentation(SpecifierStatementPsiIdOwner item) {
        if (! item.ceylonNode.refinement) {
            return null;
        }
        return object satisfies ItemPresentation {

            shared actual String? presentableText {
                if (exists model = item.ceylonNode ?. declaration) {
                    value name = model.name;
                    if (is ClassOrInterface classOrInterface = model.container) {
                        return classOrInterface.name + "." + name;
                    } else {
                        return name;
                    }
                } else {
                    return null;
                }
            }

            shared actual String? locationString {
                if (exists model = item.ceylonNode?. declaration) {
                    value dec
                        = if (is ClassOrInterface container = model.container)
                        then container else model;
                    value qualifiedNameString 
                        = dec.container.qualifiedNameString;
                    return if (qualifiedNameString.empty) 
                    then "(default module)" 
                    else "(``qualifiedNameString``)";
                }
                return null;
            }

            shared actual Icon? getIcon(Boolean unused)
                    => if (exists node = item.ceylonNode)
                    then ideaIcons.forDeclaration(node) 
                    else null;
            
        };
    }
    
}
