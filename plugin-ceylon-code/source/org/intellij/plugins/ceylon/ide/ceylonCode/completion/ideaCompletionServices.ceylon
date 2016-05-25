import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import com.intellij.openapi.util {
    TextRange
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
    isModuleDescriptor,
    CompletionContext,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    CompletionServices,
    TextChange
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Package,
    Type,
    Unit,
    Scope,
    Reference
}

import java.util {
    JList=List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaTextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}

shared object ideaCompletionServices satisfies CompletionServices {
    
    shared actual void newParametersCompletionProposal(CompletionContext ctx, Integer offset,
        String prefix, String desc, String text, JList<Type> argTypes, Node node, Unit unit) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(prefix + desc, prefix + text, ideaIcons.correction));
        }
    }
    
    shared actual void newInvocationCompletion(CompletionContext ctx, Integer offset, String prefix,
        String desc, String text, Declaration dec, Reference? pr, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation, 
        Boolean inherited, Boolean qualified, Declaration? qualifyingDec) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaInvocationCompletionProposal(offset, prefix, desc, text, dec, pr, scope,
                includeDefaulted, positionalInvocation, namedInvocation, 
                inherited, qualified, qualifyingDec, ctx).lookupElement);
        }
    }
    
    shared actual void newRefinementCompletionProposal(Integer offset, String prefix,
        Reference? pr, String desc, String text, CompletionContext ctx,
        Declaration dec, Scope scope, Boolean fullType, Boolean explicitReturnType) {
        
        assert(exists pr);
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaRefinementCompletionProposal(offset, prefix, pr, desc, text,
                ctx, dec, scope, fullType, explicitReturnType).lookupElement);
        }
    }
    
    shared actual void newMemberNameCompletionProposal(CompletionContext ctx, 
        Integer offset, String prefix, String name, String unquotedName) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(LookupElementBuilder.create(unquotedName, name)
                .withIcon(ideaIcons.local));
        }
    }
    
    shared actual void newKeywordCompletionProposal(CompletionContext ctx, 
        Integer offset, String prefix, String keyword, String text) {
        
        value selection = if (exists close = text.firstOccurrence(')'))
        then TextRange.from(offset + close - prefix.size, 0)
        else null;
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(keyword, text, ideaIcons.correction, null, selection));
        }
    }
    
    shared actual void newAnonFunctionProposal(CompletionContext ctx, Integer offset, Type? requiredType,
        Unit unit, String text, String header, Boolean isVoid,
        Integer selectionStart, Integer selectionLength) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(text, text, ideaIcons.correction, 
                null, TextRange.from(selectionStart, selectionLength)));
        }
    }
    
    shared actual void newBasicCompletionProposal(CompletionContext ctx, Integer offset,
        String prefix, String text, String escapedText, Declaration decl) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(escapedText, text, ideaIcons.forDeclaration(decl)));
        }
    }
    
    shared actual void newPackageDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, String desc, String text) {
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(desc, text, ideaIcons.packages));
        }
    }
    
    shared actual void newCurrentPackageProposal(Integer offset, String prefix, String packageName, CompletionContext ctx) {
        value icon = isModuleDescriptor(ctx.lastCompilationUnit) 
        then ideaIcons.modules
        else ideaIcons.packages;
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(packageName, packageName, icon));
        }
    }
    
    shared actual void newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        Package candidate) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaImportedModulePackageProposal(offset, prefix, memberPackageSubname,
                withBody, fullPackageName, ctx, candidate).lookupElement);
        }
    }
    
    shared actual void newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaQueriedModulePackageProposal(offset, prefix, memberPackageSubname, withBody,
                fullPackageName, ctx, version, unit, md).lookupElement);
        }
    }       
    
    shared actual void newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
        ModuleVersionDetails version, String name, Node node, CompletionContext ctx) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaModuleCompletionProposal(offset, prefix, len, versioned,
                mod, withBody, version, name, node, ctx).lookupElement);
        }
    }
    
    shared actual void newModuleDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, String desc,
        String text, Integer selectionStart, Integer selectionLength) {
        
        value selection = TextRange.from(selectionStart, selectionLength); 
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(desc, text, ideaIcons.modules, null, selection));
        }
    }
    
    shared actual void newJDKModuleProposal(CompletionContext ctx, Integer offset, String prefix, Integer len, 
        String versioned, String name) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(versioned, versioned.spanFrom(len), ideaIcons.modules));
        }
    }
    
    // Not supported in IntelliJ (see CeylonParameterInfoHandler instead)
    shared actual void newParameterInfo(CompletionContext ctx, Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, Boolean namedInvocation) {}
    
    shared actual void newFunctionCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, Unit unit, CompletionContext ctx) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaFunctionCompletionProposal(offset, prefix, desc, text, dec, ctx).lookupElement);
        }
    }
    
    shared actual void newControlStructureCompletionProposal(Integer offset,
        String prefix, String desc, String text, Declaration dec,
        CompletionContext ctx, Node? node) {
        
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(IdeaControlStructureProposal(offset, prefix, desc, text, 
                dec, ctx, node).lookupElement);
        }
    }
    
    shared actual void newTypeProposal(ProposalsHolder ctx, Integer offset, Type? type, String text, String desc, Tree.CompilationUnit rootNode) {
        if (is IdeaProposalsHolder ctx) {
            ctx.add(
                newLookup(desc, text)
                    .withIcon(if (exists type) then ideaIcons.forDeclaration(type.declaration) else null)
            );
        }
    }
    
    createProposalsHolder() => IdeaProposalsHolder();
    
    shared actual void addProposal(ProposalsHolder proposals, Icons|Declaration icon,
        String description, DefaultRegion region, String text, TextChange? change) {
        
        if (is IdeaProposalsHolder proposals, is IdeaTextChange? change) {
            value myIcon = switch(icon)
            case (is Icons) null
            else ideaIcons.forDeclaration(icon);
            
            value handler = if (exists change)
            then object satisfies InsertHandler<LookupElement> {
                shared actual void handleInsert(InsertionContext? context, LookupElement? item) {
                    change.apply();
                }
            }
            else null;
            
            proposals.add(newLookup(description, text, myIcon, handler));
        }
    }
    
}