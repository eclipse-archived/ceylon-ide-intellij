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
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaTextChange
}
import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
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
    
    shared actual void newPackageDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, String desc, String text) {
        if (is IdeaCompletionContext ctx) {
            ctx.proposals.add(newLookup(desc, text, ideaIcons.packages));
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
    
    shared actual void addNestedProposal(ProposalsHolder proposals, Icons|Declaration icon,
        String description, DefaultRegion region, String text) {
        
        if (is IdeaProposalsHolder proposals) {
            value myIcon = switch(icon)
            case (is Icons) null
            else ideaIcons.forDeclaration(icon);
            
            proposals.add(newLookup(description, text, myIcon));
        }
    }
    
    shared actual void addProposal(CompletionContext ctx, Integer offset, 
        String prefix, Icons|Declaration icon, String description, String text,
        ProposalKind kind, TextChange? additionalChange, DefaultRegion? selection) {
        
        if (is IdeaCompletionContext ctx) {
            value myIcon = switch(icon)
            case (is Icons) null
            else ideaIcons.forDeclaration(icon);
            
            value myRange = if (exists selection)
            then TextRange.from(selection.start, selection.length)
            else null;
            
            value myHandler = if (is IdeaTextChange additionalChange)
            then object satisfies InsertHandler<LookupElement> {
                handleInsert(InsertionContext ic, LookupElement? t)
                        => additionalChange.apply();
            }
            else null;
            
            ctx.proposals.add(newLookup {
                desc = description;
                text = text;
                icon = myIcon;
                selection = myRange;
                handler = myHandler;
            });
        }
    }
}
