import com.intellij.navigation {
    ItemPresentation,
    ItemPresentationProvider,
    ColoredItemPresentation
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.psi.impl {
    DeclarationPsiNameIdOwner,
    SpecifierStatementPsiIdOwner
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class DeclarationPresentationProvider() 
        satisfies ItemPresentationProvider<DeclarationPsiNameIdOwner> {

    shared actual ItemPresentation getPresentation(DeclarationPsiNameIdOwner item)
            => object satisfies ColoredItemPresentation {

                shared actual String? presentableText {
                    if (exists model = item.ceylonNode?.declarationModel) {
                        value name = model.name;
                        if (is ClassOrInterface classOrInterface = model.container) {
                            return classOrInterface.name + "." + name;
                        } else {
                            return name;
                        }
                    } else {
                        if (exists name = item.ceylonNode?.identifier?.text) {
                            if (is CeylonPsi.ClassOrInterfacePsi classOrInterface = item.parent) {
                                if (exists typeName = classOrInterface.ceylonNode?.identifier?.text) {
                                    return typeName + "." + name;
                                }
                            } else {
                                return name;
                            }
                        }
                        return null;
                    }
                }

                shared actual String? locationString {
                    if (exists model = item.ceylonNode?.declarationModel) {
                        value nat = model.native then "``model.nativeBackends`` " else "";
                        value dec
                            = if (is ClassOrInterface container = model.container)
                            then container else model;
                        value qualifiedNameString = dec.container.qualifiedNameString;
                        return qualifiedNameString.empty
                            then nat + "(default package)"
                            else nat + "(``qualifiedNameString``)";
                    } else if (exists unit = item.ceylonNode?.unit) {
                        value nameAsString = unit.\ipackage.nameAsString;
                        return nameAsString.empty
                            then "(default package)"
                            else "(``nameAsString``)";
                    } else {
                        return null;
                    }
                }

                getIcon(Boolean unused)
                        => if (exists node = item.ceylonNode)
                        then icons.forDeclaration(node)
                        else null;

                textAttributesKey
                        => if (exists model = item.ceylonNode?.declarationModel, model.deprecated)
                        then CodeInsightColors.deprecatedAttributes
                        else null;
            };
}

shared class SpecifierPresentationProvider() 
        satisfies ItemPresentationProvider<SpecifierStatementPsiIdOwner> {

    shared actual ItemPresentation? getPresentation(SpecifierStatementPsiIdOwner item)
            => item.ceylonNode.refinement then
            object satisfies ItemPresentation {

                shared actual String? presentableText {
                    if (exists model = item.ceylonNode?.declaration) {
                        value name = model.name;
                        if (is ClassOrInterface classOrInterface = model.container) {
                            return classOrInterface.name + "." + name;
                        } else {
                            return name;
                        }
                    } else {
                        if (exists text = item.ceylonNode?.baseMemberExpression?.text) {
                            if (is CeylonPsi.ClassOrInterfacePsi classOrInterface = item.parent) {
                                if (exists typeName = classOrInterface.ceylonNode?.identifier?.text) {
                                    return typeName + "." + text;
                                }
                            } else {
                                return text;
                            }
                        }
                        return null;
                    }
                }

                shared actual String? locationString {
                    if (exists model = item.ceylonNode?.declaration) {
                        value dec
                            = if (is ClassOrInterface container = model.container)
                            then container else model;
                        value qualifiedNameString = dec.container.qualifiedNameString;
                        return if (qualifiedNameString.empty)
                            then "(default package)"
                            else "(``qualifiedNameString``)";
                    } else if (exists unit = item.ceylonNode?.unit) {
                        value nameAsString = unit.\ipackage.nameAsString;
                        return nameAsString.empty
                            then "(default package)"
                            else "(``nameAsString``)";
                    }
                    else {
                        return null;
                    }
                }

                getIcon(Boolean unused)
                        => if (exists node = item.ceylonNode)
                        then icons.forDeclaration(node)
                        else null;

            };
    
}
