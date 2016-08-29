import ceylon.interop.java {
    CeylonList
}
import ceylon.language {
    ceylonTrue=true,
    ceylonFalse=false
}

import com.intellij.codeInsight.completion {
    CompletionInitializationContext
}
import com.intellij.lang {
    ASTNode,
    Language
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiElement,
    PsiDocumentManager,
    PsiFile
}
import com.intellij.psi.stubs {
    PsiFileStub
}
import com.intellij.psi.tree {
    IStubFileElementType
}
import com.redhat.ceylon.ide.common.model.parsing {
    sourceCodeParser
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit
}

import java.io {
    StringReader
}
import java.lang {
    RuntimeException
}
import java.lang.ref {
    WeakReference
}
import java.util {
    JList=List
}

import org.antlr.runtime {
    RecognitionException,
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}

CeylonLogger<IdeaCeylonParser> ideaCeylonParserLogger = CeylonLogger<IdeaCeylonParser>();

"This class avoids calling a custom parser. Instead, we transform the Ceylon AST generated by
 the official parser into an ASTNode tree. Comments and whitespaces are not present in the Ceylon AST,
 but we can retrieve them by synchronizing our transformation with a lexer."
shared  class IdeaCeylonParser(Language language) extends IStubFileElementType<PsiFileStub<PsiFile>>(language) {


    function structurallyDifferent(JList<CommonToken> puTokens, JList<CommonToken> localTokens)
            => puTokens.size() != localTokens.size() ||
                        zipPairs(CeylonList(puTokens).reversed, CeylonList(localTokens).reversed).any((tokensToCompare) => 
                                    tokensToCompare[0].stopIndex != tokensToCompare[1].stopIndex || 
                                    tokensToCompare[0].startIndex != tokensToCompare[1].startIndex ||
                                    tokensToCompare[0].type != tokensToCompare[1].type);
    
    shared actual ASTNode? doParseContents(ASTNode chameleon, PsiElement psi) {
        assert(is CeylonFile file = psi);
        Boolean verbose = false;

        value psiManager = PsiDocumentManager.getInstance(file.project);
        VirtualFile? virtualFile;
        value document = psiManager.getDocument(file.originalFile);
        value isCommitted = if (exists document) then psiManager.isCommitted(document) else false;
        value lastCommittedDocument = psiManager.getLastCommittedDocument(file.originalFile);
        virtualFile = file.originalFile.virtualFile;
        value localAnalyzer = file.localAnalyzer else null;

        try {
            value translator = CompilationUnitTranslator(file, verbose);
            value parseResult = sourceCodeParser.parseSourceCode(StringReader(file.text));

            if (isCommitted, exists virtualFile) {
                if (isInSourceArchive(virtualFile)) {
                    if (isCommitted) {
                        ExternalPhasedUnit? externalPhasedUnit;
                        Boolean parseExplicitelyRequested;
                        if (exists externalPuToParse = virtualFile.getUserData(uneditedExternalPhasedUnitToParse)) {
                            externalPhasedUnit = externalPuToParse;
                            parseExplicitelyRequested = ceylonTrue;
                        } else if(exists externalPuRef = virtualFile.getUserData(uneditedExternalPhasedUnitRef),
                                  exists externalPuRefGet = externalPuRef.get()) {
                            externalPhasedUnit = externalPuRefGet;
                            parseExplicitelyRequested = false;
                        } else {
                            externalPhasedUnit = null;
                            parseExplicitelyRequested = false;
                        }

                        if (exists externalPu = externalPhasedUnit) {
                            value puTokens = externalPu.tokens;
                            value localTokens = parseResult.tokens;
                            if (structurallyDifferent(puTokens, localTokens)) {
                                ideaCeylonParserLogger.warn(() => "Tokens of the externalPhasedUnit are not the same as the parsed tokens for file `` file.originalFile.name ``(``file.originalFile.hash``)", 20);
                                ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(parseResult.compilationUnit (`` parseResult.compilationUnit.hash ``), parseResult.tokens (`` parseResult.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                                return translator.translateToAstNode(parseResult.compilationUnit, parseResult.tokens);
                            } else {
                                ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(externalPu.compilationUnit (`` externalPu.compilationUnit.hash ``), externalPu.tokens (`` externalPu.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                                ASTNode root = translator.translateToAstNode(externalPu.compilationUnit, externalPu.tokens);
                                void cleanUserDataIfNecessary() {
                                    if(parseExplicitelyRequested) {
                                        virtualFile.putUserData(uneditedExternalPhasedUnitToParse, null);
                                        virtualFile.putUserData(uneditedExternalPhasedUnitRef, WeakReference(externalPhasedUnit));
                                    }
                                }
                                void postParseAction();

                                if (exists localAnalyzer) {
                                    assert(exists lastCommittedDocument);
                                    postParseAction = () {
                                        cleanUserDataIfNecessary();
                                        localAnalyzer.translatedExternalSource(lastCommittedDocument, externalPu);
                                    };
                                } else {
                                    postParseAction = () {
                                        cleanUserDataIfNecessary();
                                        if (exists future = virtualFile.getUserData(uneditedExternalPhasedUnitFuture)) {
                                            if (! future.done) {
                                                try {
                                                    future.set(externalPu);
                                                } catch(Throwable t) {
                                                    noop();
                                                }
                                            }
                                            virtualFile.putUserData(uneditedExternalPhasedUnitFuture, null);
                                        }
                                    };
                                }
                                root.putUserData(parserConstants._POST_PARSE_ACTION, postParseAction);
                                return root;
                            }
                        }
                    } else {
                        ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(parseResult.compilationUnit (`` parseResult.compilationUnit.hash ``), parseResult.tokens (`` parseResult.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                        return translator.translateToAstNode(parseResult.compilationUnit, parseResult.tokens);
                    }
                } else {
                    if (! localAnalyzer exists) {
                        if (isCommitted, exists projectPhasedUnit = retrieveProjectPhasedUnit(file)) {
                            value puTokens = projectPhasedUnit.tokens;
                            value localTokens = parseResult.tokens;
                            if (structurallyDifferent(puTokens, localTokens)) {
                                ideaCeylonParserLogger.warn(() => "Tokens of the projectphasedUnit are not the same as the parsed tokens for file `` file.originalFile.name ``(``file.originalFile.hash``)", 20);
                                ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(parseResult.compilationUnit (`` parseResult.compilationUnit.hash ``), parseResult.tokens (`` parseResult.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                                return translator.translateToAstNode(parseResult.compilationUnit, parseResult.tokens);
                            } else {
                                ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(projectPhasedUnit.compilationUnit (`` projectPhasedUnit.compilationUnit.hash ``), projectPhasedUnit.tokens (`` projectPhasedUnit.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                                return translator.translateToAstNode(projectPhasedUnit.compilationUnit, projectPhasedUnit.tokens);
                            }
                        } else {
                            ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(parseResult.compilationUnit (`` parseResult.compilationUnit.hash ``), parseResult.tokens (`` parseResult.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
                            return translator.translateToAstNode(parseResult.compilationUnit, parseResult.tokens);
                        }
                    }
                }
            }

            ideaCeylonParserLogger.trace(() => "translator.translateToAstNode(parseResult.compilationUnit (`` parseResult.compilationUnit.hash ``), parseResult.tokens (`` parseResult.tokens.hash ``)) for file `` file.originalFile.name ``(``file.originalFile.hash``)");
            ASTNode root = translator.translateToAstNode(parseResult.compilationUnit, parseResult.tokens);
            if (exists localAnalyzer) {
                if (! CompletionInitializationContext.dummyIdentifierTrimmed in file.text) {
                    assert(exists lastCommittedDocument);
                    root.putUserData(parserConstants._POST_PARSE_ACTION, void () {
                        localAnalyzer.parsedProjectSource(
                            lastCommittedDocument,
                            parseResult.compilationUnit,
                            parseResult.tokens);
                    });

                } else {
                    ideaCeylonParserLogger.debug(() => "Local analyzer not notified of parsing for file `` file.originalFile.name ``(``file.originalFile.hash``) because the text contains '`` CompletionInitializationContext.dummyIdentifierTrimmed ``'");
                }
            }

            return root;
        } catch (RuntimeException re) {
            if (is RecognitionException cause = re.cause) {
                cause.printStackTrace();
            } else {
            throw re;
            }
        }
        return null;
    }

//    // For debugging purposes only
//    void dump(ASTNode root, String indent) {
//        ideaCeylonParserLogger.debug(() =>
//            indent
//            + root.elementType.string
//            + (if (is LeafPsiElement root) then " (`` root.text ``)" else "" ));
//
//        for (child in root.getChildren(null)) {
//            dump(child, indent + "  ");
//        }
//    }
//
//    class CompilationUnitTranslator(CeylonFile file, Boolean verbose) extends Visitor() {
//
//        variable CompositeElement? parent = null;
//        late variable Queue<CommonToken> customizedTokens;
//        variable Integer index = 0;
//
//        shared ASTNode translateToAstNode(Tree.CompilationUnit cu, JList<CommonToken> originalTokens) {
//            customizedTokens = LinkedList<CommonToken>();
//            value lastToken = Ref<CommonToken>();
//
//            for (CommonToken? token in originalTokens) {
//                if (exists token) {
//                    variable value lastStopIndex = -1;
//                    if (!lastToken.null) {
//                        lastStopIndex = lastToken.get().stopIndex;
//                    }
//                    if (token.type != CeylonLexer.eof
//                        && lastStopIndex != token.startIndex - 1) {
//                        value badToken = CommonToken(token.inputStream, -2, 0,
//                        lastStopIndex + 1,
//                        token.startIndex - 1);
//                        customizedTokens.add(badToken);
//                    }
//                    customizedTokens.add(token);
//                    lastToken.set(token);
//                }
//            }
//
//            if (lastToken.get().stopIndex < file.textLength) {
//                value badToken = CommonToken(lastToken.get().inputStream, -2, 0,
//                lastToken.get().stopIndex + 1,
//                file.textLength - 1);
//                customizedTokens.add(badToken);
//            }
//
//            visit(cu);
//
//            assert(exists root = parent);
//            if (verbose) {
//                dump(root, "");
//            }
//            return root;
//        }
//
//        shared actual void visit(Tree.CompilationUnit that) {
//            super.visit(that);
//
//            assert(exists parent = this.parent);
//
//            while (!customizedTokens.empty) {
//                Token token = customizedTokens.remove();
//
//                if (token.type != CeylonLexer.eof) {
//                    parent.rawAddChildrenWithoutNotifications(buildLeaf(null, getElementType(token.type), token));
//                }
//
//                if (verbose && ! token.type in parserConstants._NODES_ALLOWED_AT_EOF) {
//                    ideaCeylonParserLogger.error(()=>"Unexpected token `` token `` in `` file.name ``", 20);
//                }
//            }
//
//            value parentTextLengthRef = Ref<Integer>();
//            value fileTextLengthRef = Ref<Integer>();
//            ApplicationManager.application.runReadAction(JavaRunnable(() {
//                parentTextLengthRef.set(parent.textLength);
//                fileTextLengthRef.set(file.textLength);
//            }));
//            assert(exists parentTextLength = parentTextLengthRef.get());
//            assert(exists fileTextLength = fileTextLengthRef.get());
//            if (parentTextLength < fileTextLength) {
//                String notParsed = file.text.substring(parentTextLength);
//                parent.rawAddChildrenWithoutNotifications(LeafPsiElement(TokenType.badCharacter, javaString(notParsed)));
//            }
//        }
//
//        shared actual void visitAny(Node that) {
//            IElementType? type = NodeToIElementTypeMap.get(that);
//
//            if (! exists type) {
//                ideaCeylonParserLogger.error(()=>"Unknown IElementType for ``  that `` in `` that.unit?.fullPath else "unknown" ``", 20);
//                return;
//            }
//
//            variable Boolean parentForced = false;
//
//            if (! parent exists) {
//                parentForced = languageTrue;
//                parent = CompositeElement(type);
//            }
//
//            if (is Tree.DocLink that) {
//                return;
//            }
//
//            index = consumeTokens(that, index, languageTrue);
//
//            Token? token = that.mainToken;
//
//            value visitor = OrderedChildrenVisitor();
//            try {
//                that.visitChildren(visitor);
//            } catch (Exception e) {
//                that.handleException(e, visitor);
//            }
//
//            if (exists theToken = that.token,
//                visitor.children.empty) {
//
//                assert(exists parent = this.parent);
//
//                Token peek = customizedTokens.peek();
//                if (getTokenLength(peek) == that.endIndex.intValue() - that.startIndex.intValue()) {
//                    Token toRemove = customizedTokens.remove();
//                    parent.rawAddChildrenWithoutNotifications(buildLeaf(that, type, toRemove));
//                    if (verbose) {
//                        ideaCeylonParserLogger.debug(()=>"t \"" + toRemove.text + "\"");
//                    }
//                    index += getTokenLength(toRemove);
//                } else {
//                    CompositeElement comp = CompositeElement(type);
//
//                    assert(exists token);
//                    while (index < that.endIndex.intValue()) {
//                        Token toRemove = customizedTokens.remove();
//                        comp.rawAddChildrenWithoutNotifications(buildLeaf(null, getElementType(token.type), toRemove));
//                        if (verbose) {
//                            ideaCeylonParserLogger.debug(()=>"t \"" + toRemove.text + "\"");
//                        }
//                        index += getTokenLength(toRemove);
//                    }
//
//                    parent.rawAddChildrenWithoutNotifications(comp);
//                }
//
//                // TODO should be == but sometimes the tree includes a node that was already included before
//                // (see `exists` constructs for example)
//                assert (index >= that.endIndex.intValue());
//            } else {
//                assert(exists oldParent = parent);
//                if (!parentForced) {
//                    parent = CompositeElement(type);
//                    oldParent.rawAddChildrenWithoutNotifications(parent);
//                }
//                assert(exists parent=this.parent);
//                parent.putUserData(parserConstants._CEYLON_NODE_KEY, that);
//
//                for (child in visitor.children) {
//                    visitAny(child);
//                }
//
//                index = consumeTokens(that, index, false);
//
//                this.parent = oldParent;
//            }
//        }
//
//        IElementType getElementType(Integer idx) {
//            if (idx == -2) {
//                return TokenType.badCharacter;
//            }
//            return TokenTypes.fromInt(idx);
//        }
//
//        TreeElement buildLeaf(Node? ceylonNode, IElementType type, Token token) {
//            variable value tokenText = token.text;
//            if (tokenText.size != getTokenLength(token)) {
//                value tokenType = token.type;
//                if (tokenType == CeylonLexer.\iPIDENTIFIER ||
//                    tokenType == CeylonLexer.\iAIDENTIFIER ||
//                    tokenType == CeylonLexer.\iLIDENTIFIER) {
//                    tokenText = "\\i" + tokenText;
//                } else if (tokenType == CeylonLexer.\iUIDENTIFIER) {
//                    tokenText = "\\I" + tokenText;
//                } else {
//                    throw UnsupportedOperationException("Unsupported token type `` token ``");
//                }
//            }
//            if (type in parserConstants._LEAVES_TO_WRAP) {
//                value comp = CompositeElement(type);
//                value leaf = LeafPsiElement(TokenTypes.fromInt(token.type), javaString(tokenText));
//                comp.rawAddChildrenWithoutNotifications(leaf);
//                comp.putUserData(parserConstants._CEYLON_NODE_KEY, ceylonNode);
//                return comp;
//            } else if (type == TokenType.\iWHITE_SPACE || token.type == CeylonLexer.\iWS) {
//                return PsiWhiteSpaceImpl(javaString(tokenText));
//            } else {
//                return LeafPsiElement(getElementType(token.type), javaString(tokenText));
//            }
//        }
//
//        Integer consumeTokens(Node that, variable Integer index, Boolean before) {
//            Integer? targetIndex = if (before) then that.startIndex?.intValue() else that.endIndex?.intValue();
//
//            if (! exists targetIndex) {
//                return index;
//            }
//
//            if (index > targetIndex) {
//                if (verbose) {
//                    ideaCeylonParserLogger.warn(()=>"WARN : index (``index``) > targetIndex (``targetIndex``)", 20);
//                }
//                return index;
//            }
//
//            while (index < targetIndex) {
//                Token token = customizedTokens.remove();
//                String text = token.text;
//                assert(exists parent = this.parent);
//                if (token.type == CeylonLexer.\iLINE_COMMENT && text.endsWith("\n")) {
//                    parent.rawAddChildrenWithoutNotifications(LeafPsiElement(getElementType(token.type),
//                            javaString(text.substring(0, text.size-1))));
//                    parent.rawAddChildrenWithoutNotifications(PsiWhiteSpaceImpl(javaString("\n")));
//                }
//                else {
//                    parent.rawAddChildrenWithoutNotifications(buildLeaf(null, getElementType(token.type), token));
//                }
//                index += getTokenLength(token);
//                if (verbose) {
//                    ideaCeylonParserLogger.debug(()=>"c \"`` text ``\"");
//                }
//            }
//
//            assert (index == targetIndex);
//
//            return index;
//        }
//
//        Integer getTokenLength(Token token) {
//            if (is CommonToken commonToken = token) {
//                return commonToken.stopIndex - commonToken.startIndex + 1;
//            } else {
//                return token.text.size;
//            }
//        }
//    }
//
//    class OrderedChildrenVisitor() extends Visitor() {
//        value children_ = ArrayList<Node>();
//
//        shared actual void visitAny(Node that) {
//            children_.add(that);
//        }
//
//        shared JList<Node> children {
//            Collections.sort(children_, object satisfies Comparator<Node> {
//                shared actual Integer compare(Node o1, Node o2) {
//                    JInteger? idx1 = o1.startIndex;
//                    JInteger? idx2 = o2.startIndex;
//
//                    if (! exists idx1) {
//                        return if (! idx2 exists) then 0 else 1;
//                    }
//                    if (! exists idx2) {
//                        return -1;
//                    }
//
//                    return idx1.compareTo(idx2);
//                }
//
//                shared actual Boolean equals(Object that) => (super of Identifiable).equals(that);
//            });
//
//            return children_;
//        }
//    }
}
