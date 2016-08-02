import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.pom {
    PomNamedTarget
}
import com.intellij.psi {
    PsiClass,
    PsiMethod,
    PsiType,
    PsiPrimitiveType
}
import com.redhat.ceylon.cmr.api {
    ModuleSearchResult,
    ModuleVersionDetails
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    CompletionContext,
    ProposalsHolder,
    ProposalKind
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.platform {
    CompletionServices,
    TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.ide.common.util {
    OccurrenceLocation,
    escaping
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Package,
    Type,
    Unit,
    Scope,
    Reference,
    DeclarationWithProximity
}

import org.intellij.plugins.ceylon.ide.ceylonCode.compiled {
    classFileDecompilerUtil
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    FakeCompletionDeclaration
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}
import ceylon.interop.java {
    javaString
}

shared object ideaCompletionServices satisfies CompletionServices {
    
    shared actual void newParametersCompletionProposal(CompletionContext ctx, Integer offset,
        String prefix, String desc, String text, List<Type> argTypes, Node node, Unit unit) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup {
                desc = prefix + desc;
                text = prefix + text;
                icon = icons.correction;
            });
        }
    }
    
    shared actual void newInvocationCompletion(CompletionContext ctx, Integer offset,
        String prefix, String desc, String text, Declaration dec, Reference()? reference, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation, 
        Boolean inherited, Boolean qualified, Declaration? qualifyingDec) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaInvocationCompletionProposal {
                offset = offset;
                prefix = prefix;
                desc = desc;
                text = text;
                declaration = dec;
                producedReference = reference;
                scope = scope;
                includeDefaulted = includeDefaulted;
                positionalInvocation = positionalInvocation;
                namedInvocation = namedInvocation;
                inherited = inherited;
                qualified = qualified;
                qualifyingValue = qualifyingDec;
                ctx = ctx;
            }.lookupElement);
        }
    }
    
    shared actual void newRefinementCompletionProposal(Integer offset, String prefix,
        Reference? pr, String desc, String text, CompletionContext ctx,
        Declaration dec, Scope scope, Boolean fullType, Boolean explicitReturnType) {
        
        assert(exists pr);
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaRefinementCompletionProposal {
                offset = offset;
                prefix = prefix;
                pr = pr;
                desc = desc;
                text = text;
                ctx = ctx;
                dec = dec;
                scope = scope;
                fullType = fullType;
                explicitReturnType = explicitReturnType;
            }.lookupElement);
        }
    }
    
    shared actual void newPackageDescriptorProposal(CompletionContext ctx, Integer offset,
            String prefix, String desc, String text) {
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup {
                desc = desc;
                text = text;
                icon = icons.packageDescriptors;
            });
        }
    }
    
    shared actual void newImportedModulePackageProposal(Integer offset, String prefix,
            String memberPackageSubname, Boolean withBody, String fullPackageName,
            CompletionContext ctx, Package candidate) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaImportedModulePackageProposal {
                offset = offset;
                prefix = prefix;
                memberPackageSubname = memberPackageSubname;
                withBody = withBody;
                fullPackageName = fullPackageName;
                ctx = ctx;
                candidate = candidate;
            }.lookupElement);
        }
    }
    
    shared actual void newQueriedModulePackageProposal(Integer offset, String prefix,
            String memberPackageSubname, Boolean withBody, String fullPackageName,
            CompletionContext ctx, ModuleVersionDetails version, Unit unit,
            ModuleSearchResult.ModuleDetails md) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaQueriedModulePackageProposal {
                offset = offset;
                prefix = prefix;
                memberPackageSubname = memberPackageSubname;
                withBody = withBody;
                fullPackageName = fullPackageName;
                completionCtx = ctx;
                version = version;
                unit = unit;
                md = md;
            }.lookupElement);
        }
    }       
    
    shared actual void newModuleProposal(Integer offset, String prefix, Integer len, 
            String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
            ModuleVersionDetails version, String name, Node node, CompletionContext ctx) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaModuleCompletionProposal {
                offset = offset;
                prefix = prefix;
                len = len;
                versioned = versioned;
                mod = mod;
                withBody = withBody;
                version = version;
                name = name;
                node = node;
                ctx = ctx;
            }.lookupElement);
        }
    }
    
    shared actual void newModuleDescriptorProposal(CompletionContext ctx, Integer offset,
            String prefix, String desc, String text,
            Integer selectionStart, Integer selectionLength) {
        
        value selection = TextRange.from(selectionStart, selectionLength); 
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup {
                desc = desc;
                text = text;
                icon = icons.moduleDescriptors;
                handler = null;
                selection = selection;
            });
        }
    }
    
    shared actual void newJDKModuleProposal(CompletionContext ctx, Integer offset,
            String prefix, Integer len, String versioned, String name) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup {
                desc = versioned;
                text = versioned.spanFrom(len);
                icon = icons.moduleArchives;
            });
        }
    }
    
    // Not supported in IntelliJ (see CeylonParameterInfoHandler instead)
    shared actual void newParameterInfo(CompletionContext ctx, Integer offset, Declaration dec, 
            Reference producedReference, Scope scope, Boolean namedInvocation) {}
    
    shared actual void newFunctionCompletionProposal(Integer offset, String prefix,
            String desc, String text, Declaration dec, Unit unit, CompletionContext ctx) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaFunctionCompletionProposal {
                offset = offset;
                prefix = prefix;
                desc = desc;
                text = text;
                decl = dec;
                ctx = ctx;
            }.lookupElement);
        }
    }
    
    shared actual void newControlStructureCompletionProposal(Integer offset, String prefix,
            String desc, String text, Declaration dec, CompletionContext ctx, Node? node) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaControlStructureProposal {
                offset = offset;
                prefix = prefix;
                desc = desc;
                text = text;
                declaration = dec;
                ctx = ctx;
                node = node;
            }.lookupElement);
        }
    }
    
    shared actual void newTypeProposal(ProposalsHolder ctx, Integer offset, Type? type,
            String text, String desc, Tree.CompilationUnit rootNode) {
        if (is IdeaProposalsHolder ctx) {
            value icon
                    = if (exists type)
                    then icons.forDeclaration(type.declaration)
                    else null;
            ctx.add(newLookup(desc, text).withIcon(icon));
        }
    }
    
    createProposalsHolder() => IdeaProposalsHolder();
    
    shared actual void addNestedProposal(ProposalsHolder proposals, Icons|Declaration icon,
            String description, DefaultRegion region, String text) {
        
        if (is IdeaProposalsHolder proposals) {
            value myIcon = switch(icon)
            case (is Icons) null
            else icons.forDeclaration(icon);
            
            proposals.add(newLookup(description, text, myIcon));
        }
    }
    
    shared actual void addProposal(CompletionContext ctx, Integer offset, String prefix,
            Icons|Declaration icon, String description, String text, ProposalKind kind,
            TextChange? additionalChange, DefaultRegion? selection) {
        
        if (is IdeaCompletionContext ctx) {
            value myIcon = switch(icon)
            case (is Icons) null
            else icons.forDeclaration(icon);
            
            value myRange
                    = if (exists selection)
                    then TextRange.from(selection.start, selection.length)
                    else null;
            
            value myHandler = if (is IdeaTextChange additionalChange)
            then object satisfies InsertHandler<LookupElement> {
                handleInsert(InsertionContext context, LookupElement? element)
                        => additionalChange.apply();
            }
            else null;
            
            ctx.proposals.add(newLookup {
                desc = description;
                text = text;
                icon = myIcon;
                selection = myRange;
                handler = myHandler;
                obj = description;
            });
        }
    }

    shared actual Boolean customizeInvocationProposals(Integer offset, String prefix,
        CompletionContext ctx, DeclarationWithProximity? dwp, Declaration dec, Reference() reference,
        Scope scope, OccurrenceLocation? ol, String? typeArgs, Boolean isMember) {

        if (is FakeCompletionDeclaration dec,
            !classFileDecompilerUtil.isCeylonCompiledFile(dec.psiClass.containingFile.virtualFile)) {

            assert(is IdeaCompletionContext ctx);

            function toCeylonType(PsiType javaType) {
                if (javaType in [PsiPrimitiveType.long, PsiPrimitiveType.int, PsiPrimitiveType.short]) {
                    return "Integer";
                } else if (javaType in [PsiPrimitiveType.double, PsiPrimitiveType.float]) {
                    return "Float";
                } else if (javaType == PsiPrimitiveType.boolean) {
                    return "Boolean";
                }

                class MyString(String str) {
                    shared MyString replaceAll(String regex, String replacement)
                        => MyString(javaString(str).replaceAll(regex, replacement));

                    string => str;
                }

                // arrays -> ObjectArray
                variable value withoutArrays = MyString(javaType.presentableText)
                    .replaceAll("\\bboolean\\[\\]", "BooleanArray")
                    .replaceAll("\\bchar\\[\\]", "CharArray")
                    .replaceAll("\\blong\\[\\]", "LongArray")
                    .replaceAll("\\bint\\[\\]", "IntArray")
                    .replaceAll("\\bshort\\[\\]", "ShortArray")
                    .replaceAll("\\bbyte\\[\\]", "ByteArray")
                    .replaceAll("\\bdouble\\[\\]", "DoubleArray")
                    .replaceAll("\\bfloat\\[\\]", "FloatArray");

                variable Integer i = 0;
                while (withoutArrays.string.contains("[]") && i < 20) {
                    withoutArrays = withoutArrays.replaceAll("\\b(\\w+)\\[\\]", "ObjectArray<$1>");
                    i++;
                }

                if (i == 20) {
                    print("Too many iterations on ``withoutArrays``");
                }

                // primitive types -> Ceylon types
                value withoutPrimitives = MyString(withoutArrays.string)
                    .replaceAll("\\bboolean\\b", "Boolean")
                    .replaceAll("\\bchar\\b", "Character")
                    .replaceAll("\\b(long|int|short)\\b", "Integer")
                    .replaceAll("\\bbyte\\b", "Byte")
                    .replaceAll("\\b(double|float)\\b", "Float");

                return withoutPrimitives.string
                    .replace("? extends ", "out ")
                    .replace("? super ", "in ");
            }

            function printParameters(PsiMethod? ctor, Boolean includeTypes) {
                value sb = StringBuilder()
                    .append((dec.psiClass of PomNamedTarget).name)
                    .append("(");

                if (exists ctor) {
                    for (param in ctor.parameterList.parameters) {
                        if (includeTypes) {
                            sb.append(toCeylonType(param.type))
                                .append(" ");
                        }

                        value name = includeTypes
                            then (param of PomNamedTarget).name
                            else escaping.escapeInitialLowercase((param of PomNamedTarget).name);

                        sb.append(name)
                            .append(", ");
                    }

                    if (ctor.parameterList.parametersCount > 0) {
                        sb.deleteTerminal(2);
                    }
                }

                sb.append(")");

                return sb.string;
            }

            for (ctor in dec.psiClass.constructors) {
                ctx.proposals.add(IdeaInvocationCompletionProposal {
                    offset = offset;
                    prefix = prefix;
                    desc = printParameters(ctor, true);
                    text = printParameters(ctor, false);
                    declaration = dec;
                    producedReference = reference;
                    scope = scope;
                    includeDefaulted = false;
                    positionalInvocation = true;
                    namedInvocation = false;
                    inherited = false;
                    qualified = false;
                    qualifyingValue = null;
                    ctx = ctx;
                }.lookupElement);
            }

            return true;
        }

        return false;
    }


    shared actual Boolean customizeReferenceProposals(Tree.CompilationUnit cu, Integer offset,
        String prefix, CompletionContext ctx, DeclarationWithProximity dwp, Reference()? reference,
        Scope scope, OccurrenceLocation? ol, Boolean isMember) {

        if (is FakeCompletionDeclaration dec = dwp.declaration,
            !classFileDecompilerUtil.isCeylonCompiledFile(dec.psiClass.containingFile.virtualFile)) {

            value psiClass = dec.psiClass;
            assert(is IdeaCompletionContext ctx);

            void addLookupElement(String text) {
                ctx.proposals.add(IdeaInvocationCompletionProposal {
                    offset = offset;
                    prefix = prefix;
                    desc = text;
                    text = text;
                    declaration = dec;
                    producedReference = reference;
                    scope = scope;
                    includeDefaulted = false;
                    positionalInvocation = true;
                    namedInvocation = false;
                    inherited = false;
                    qualified = false;
                    qualifyingValue = null;
                    ctx = ctx;
                }.lookupElement);
            }

            if (psiClass.hasTypeParameters()) {
                String printTypeParams(PsiClass cls) {
                    value text = StringBuilder()
                        .append((cls of PomNamedTarget).name)
                        .append("<");

                    for (tp in psiClass.typeParameters) {
                        text.append((tp of PomNamedTarget).name);

                        if (tp.hasTypeParameters()) {
                            printTypeParams(tp);
                        }

                        text.append(", ");
                    }

                    text.deleteTerminal(2);

                    text.append(">");

                    return text.string;
                }

                addLookupElement(printTypeParams(psiClass));
            }

            addLookupElement(dec.name);

            return true;
        }

        return false;
    }

}
