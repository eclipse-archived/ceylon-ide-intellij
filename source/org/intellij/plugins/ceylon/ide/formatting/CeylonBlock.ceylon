import com.intellij.formatting {
    Block,
    ChildAttributes,
    Spacing,
    Indent
}
import com.intellij.lang {
    ASTNode
}
import com.intellij.psi {
    TokenType
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
}
import com.intellij.psi.formatter {
    FormatterUtil
}
import com.intellij.psi.tree {
    IElementType
}

import java.util {
    List,
    ArrayList
}

import org.intellij.plugins.ceylon.ide.highlighting {
    ceylonHighlighter
}
import org.intellij.plugins.ceylon.ide.psi {
    Tokens=CeylonTokens,
    Types=CeylonTypes,
    CeylonPsi
}

// child attributes
ChildAttributes childAttrNoIndent = ChildAttributes(Indent.noneIndent, null);
ChildAttributes childAttrNormalIndent = ChildAttributes(Indent.normalIndent, null);

// indents
[IElementType*] indentChildrenNormal = [
    Types.importModuleList, Types.block, Types.classBody, Types.interfaceBody,
    Types.namedArgumentList, Types.sequencedArgument, Types.importMemberOrTypeList,
    Types.conditionList, Types.resourceList
];
[IElementType*] indentChildrenNone = [
    Types.compilationUnit, Types.importList, Types.moduleDescriptor,
    Types.switchStatement, Types.switchCaseList, Types.switchClause, Types.caseClause,
    Types.sequenceEnumeration, Types.annotationList, Types.typeConstraintList,
    Types.ifStatement, Types.elseClause
];
[IElementType*] indentChildrenContinue = [
    Types.extendedType, Types.satisfiedTypes, Types.abstractedType, Types.caseTypes,
    Types.lazySpecifierExpression, Types.specifierExpression,
    Types.delegatedConstructor, Types.typeConstraintList, Types.parameterList
];

// Groups of element types
[IElementType*] typesLikeClass = [
    Types.classDefinition, Types.interfaceDefinition, Types.objectDefinition
];
[IElementType*] typesRequiringNoSpacing = [
    Types.typeArgumentList, Types.typeParameterList,
    Types.sequencedType, Types.indexExpression,
    Types.negativeOp, Types.positiveOp,
    Types.decrementOp, Types.postfixDecrementOp,
    Types.incrementOp, Types.postfixIncrementOp
];
//be very careful here not to mix up Tokens vs Types!
[IElementType*] typesRequiringNoLeftSpacing = [
    Types.parameterList, Types.typeParameterList,
    Types.invocationExpression, Types.typeArgumentList,
    Types.postfixIncrementOp, Types.postfixDecrementOp,
    Types.incrementOp, Types.decrementOp,
    Types.sequencedType,
    Tokens.rparen, Tokens.rbracket, Tokens.lbracket,
    Tokens.comma, Tokens.semicolon,
    Tokens.unionOp, Types.unionOp,
    Tokens.intersectionOp, Types.intersectionOp,
    Tokens.optional,
    Types.safeMemberOp, Tokens.safeMemberOp,
    Tokens.spreadOp, Types.spreadOp,
    Tokens.entryOp, Types.entryOp,
    Tokens.smallAsOp, Tokens.smallerOp,
    Tokens.largeAsOp, Tokens.largerOp,
    Tokens.rangeOp, Tokens.segmentOp
];
//be very careful here not to mix up Tokens vs Types!
[IElementType*] typesRequiringNoRightSpacing = [
    Types.typeArgumentList, Types.sequencedType,
    Types.incrementOp, Types.decrementOp,
    Types.positiveOp, Types.negativeOp,
    Tokens.lparen,
    Tokens.memberOp, Types.memberOp,
    Tokens.unionOp, Types.unionOp,
    Tokens.intersectionOp, Types.intersectionOp,
    Tokens.notOp, Types.notOp,
    Tokens.lbracket,
    Types.safeMemberOp, Tokens.safeMemberOp,
    Tokens.spreadOp, Types.spreadOp,
    Tokens.entryOp, Types.entryOp,
    Tokens.smallAsOp, Tokens.smallerOp,
    Tokens.largeAsOp, Tokens.largerOp,
    Tokens.rangeOp, Tokens.segmentOp,
    Tokens.\icontinue, Tokens.\ibreak
];
[IElementType*] smallerOp = [
    Types.smallerOp, Tokens.smallerOp
];
[IElementType*] largerOp = [
    Types.largerOp, Tokens.largerOp
];
[IElementType*] memberOps = [
    Types.memberOp, Tokens.memberOp,
    Types.spreadOp, Tokens.spreadOp,
    Types.safeMemberOp, Tokens.safeMemberOp
];
[IElementType*] inlineKeywords = [
    Types.importMember, Types.\iimport,
    Types.\ithrow, Types.\ireturn, Types.assertion,
    Types.satisfiedTypes, Types.extendedType, Types.caseTypes
];
[IElementType*] classOrInterface = [
    Types.classDefinition, Types.interfaceDefinition
];

class Spacings(CodeStyleSettings globalSettings) {
    value settings = globalSettings.getCustomSettings(`CeylonCodeStyleSettings`);

    shared Spacing none = Spacing.createSpacing(0, 0, 0, false, 0);
    shared Spacing noneAllowNewLine = Spacing.createSpacing(0, 0, 0, true, 0);
    shared Spacing aroundClass = Spacing.createSpacing(0, 0, globalSettings.blankLinesAroundClass + 1, true, globalSettings.keepBlankLinesInCode);
    shared Spacing aroundMethod = Spacing.createSpacing(0, 0, globalSettings.blankLinesAroundMethod + 1, true, globalSettings.keepBlankLinesInCode);
    shared Spacing newLine = Spacing.createSpacing(0, 0, 1, true, globalSettings.keepBlankLinesInCode);
    shared Spacing strictNewLine = Spacing.createSpacing(0, 0, 1, false, 0);
    shared Spacing beforeRbrace = Spacing.createSpacing(0, 0, 1, true, globalSettings.keepBlankLinesBeforeRbrace);
    shared Spacing afterImportList = Spacing.createSpacing(0, 0, globalSettings.blankLinesAfterImports + 1, true, globalSettings.keepBlankLinesInCode);
    shared Spacing singleSpace = Spacing.createSpacing(1, 1, 0, true, 0);
    shared Spacing inlineSingleSpace = Spacing.createSpacing(1, 1, 0, false, 0);
    shared Spacing keepSomeSpace = Spacing.createSpacing(1, 1, 0, true, globalSettings.keepBlankLinesInCode);
    shared Spacing comment = Spacing.createSpacing(0, 100, 0, true, globalSettings.keepBlankLinesInCode);

    shared Spacing beforePositionalArgs
            => settings.spaceBeforePositionalArgs then singleSpace else none;
    shared Spacing beforeAnnotationPositionalArgs
            => settings.spaceBeforeAnnotationPositionalArgs then singleSpace else none;
    shared Spacing inSatisfiesAndOf
            => settings.spaceInSatisfiesAndOf then singleSpace else none;
    shared Spacing aroundEqualsInImportAlias
            => settings.spaceAroundEqualsInImportAlias then singleSpace else none;
    shared Spacing afterTypeParam
            => settings.spaceAfterTypeParam then singleSpace else none;
    shared Spacing afterTypeArg
            => settings.spaceAfterTypeArg then singleSpace else none;
    shared Spacing aroundEqualsInTypeArgs
            => settings.spaceAroundEqualsInTypeArgs then singleSpace else none;
    shared Spacing afterKeyword
            => settings.spaceAfterKeyword then singleSpace else none;

    shared Spacing beforeParamListOpen
            => settings.spaceBeforeParamListOpen then singleSpace else none;
    shared Spacing afterParamListOpen
            => settings.spaceAfterParamListOpen then singleSpace else noneAllowNewLine;
    shared Spacing beforeParamListClose
            => settings.spaceBeforeParamListClose then singleSpace else none;
    shared Spacing afterParamListClose
            => settings.spaceAfterParamListClose then singleSpace else none;

    shared Spacing afterIterableEnumOpen
            => settings.spaceAfterIterableEnumOpen then singleSpace else none;
    shared Spacing beforeIterableEnumClose
            => settings.spaceBeforeIterableEnumClose then singleSpace else none;

    shared Spacing afterIteratorInLoopOpen
            => settings.spaceAfterIteratorInLoopOpen then singleSpace else none;
    shared Spacing beforeIteratorInLoopClose
            => settings.spaceBeforeIteratorInLoopClose then singleSpace else none;
}

class CeylonBlock(ASTNode node, Indent myIndent, Spacings spacings) satisfies Block {

    alignment => null;

    getChildAttributes(Integer newChildIndex)
            => node.elementType in indentChildrenNone
                then childAttrNoIndent
                else childAttrNormalIndent;

    shared actual Spacing? getSpacing(Block? child1, Block child2)
            => if (is CeylonBlock? child1, is CeylonBlock child2)
                then calcSpacing(child1, child2)
                else null;

    incomplete => false;

    indent => myIndent;

    leaf => !node.firstChildNode exists;

    Boolean atStartOfLine(ASTNode node) =>
            if (exists prev = node.treePrev,
                prev.elementType == TokenType.whiteSpace,
                prev.chars.length()>0,
                prev.chars.charAt(prev.chars.length() - 1) == '\n')
            then true
            else false;

    shared actual List<Block> subBlocks {
        value blocks = ArrayList<Block>();
        value nodeType = node.elementType;
        value normalChildIndent =
            if (nodeType in indentChildrenNormal) then Indent.normalIndent
            else if (nodeType in indentChildrenContinue) then Indent.continuationIndent
            else Indent.noneIndent;

        variable IElementType? prevChildType = null;
        variable ASTNode? child = node.firstChildNode;

        while (exists c = child) {
            value type = c.elementType;

            if (!FormatterUtil.containsWhiteSpacesOnly(c),
                c.textLength > 0) {

                value indent =
                    if (type in [Tokens.rbrace, Tokens.lbrace])
                    then Indent.noneIndent
                    else if (type == Tokens.lineComment, atStartOfLine(c))
                    then Indent.absoluteNoneIndent
                    else if (exists p = prevChildType, p == Types.annotationList)
                    then Indent.noneIndent
                    else if (type in memberOps)
                    then Indent.normalIndent
                    else if (nodeType == Types.sequencedArgument, node.treeParent.elementType == Types.namedArgumentList)
                    then Indent.noneIndent // no extra indent for sequenced args in named args
                    else if (type == Types.listedArgument || (type == Tokens.lineComment && nodeType == Types.positionalArgumentList))
                    then Indent.normalIndent
                    else if (nodeType == Types.letClause, type in [Types.variable, Types.destructure])
                    then Indent.normalIndent // indent stuff in let() clause
                    else if (nodeType == Types.specifierExpression, node.treeParent.elementType == Types.specifiedArgument)
                    then Indent.noneIndent // no extra indent for named args
                    else normalChildIndent;
                blocks.add(CeylonBlock(c, indent, spacings));
//                print(node.elementType.string + "/" + type.string + " => " + indent.type.string);
            }

            if (!type in [TokenType.whiteSpace, Tokens.lineComment, Tokens.multiComment]) {
                prevChildType = type;
            }

            child = c.treeNext;
        }

        return blocks;
    }

    textRange => node.textRange;

    wrap => null;

    Spacing calcSpacing(CeylonBlock? block1, CeylonBlock block2) {
        if (!exists block1) {
            return spacings.none;
        }

        value type1 = block1.node.elementType;
        value type2 = block2.node.elementType;
        value bothTypes = [type1, type2];
        value nodeType = node.elementType;

        if (type1 == Tokens.\ireturn && type2 == Tokens.semicolon) {
            // return;
            return spacings.none;
        } else if (type1 == Tokens.\inew && type2 == Types.parameterList) {
            // new () {}
            return spacings.singleSpace;
        } else if (type2 == Types.parameterList) {
            return spacings.beforeParamListOpen;
        } else if (type1 == Types.parameterList) {
            return spacings.afterParamListClose;
        } else if (type1 == Tokens.lparen || type2 == Tokens.rparen,
            nodeType == Types.positionalArgumentList) {
            return spacings.noneAllowNewLine;
        } else if (type1 == Tokens.lparen, nodeType == Types.parameterList) {
            return spacings.afterParamListOpen;
        } else if (type2 == Tokens.rparen, nodeType == Types.parameterList) {
            return spacings.beforeParamListClose;
        } else if (type1 == Tokens.lparen, nodeType == Types.valueIterator) {
            return spacings.afterIteratorInLoopOpen;
        } else if (type2 == Tokens.rparen, nodeType == Types.valueIterator) {
            return spacings.beforeIteratorInLoopClose;
        } else if (bothTypes.containsAny([Tokens.multiComment, Tokens.lineComment])) {
            return spacings.comment; //or just return null?
        } else if (type1 == Types.importList) {
            return spacings.afterImportList;
        } else if (type1 in typesLikeClass && type2 != Tokens.rbrace
            || type2 in typesLikeClass && !type1 in [Tokens.lbrace, Types.annotationList]) {
            return spacings.aroundClass;
        } else if (type1 == Types.methodDefinition && type2 != Tokens.rbrace
            || type2 == Types.methodDefinition && !type1 in [Tokens.lbrace, Types.annotationList]) {
            return spacings.aroundMethod;
        } else if (type1 in [Types.importModule, Types.\iimport]) {
            return spacings.newLine;
        } else if (type2 == Types.importMember) {
            return spacings.newLine;
        } else if (type1 == Types.anonymousAnnotation) {
            return spacings.newLine;
        } else if (Tokens.unionOp in bothTypes && nodeType == Types.caseTypes) {
            // of A | B
            return spacings.inSatisfiesAndOf;
        } else if ((type1 == Types.variablePattern && type2 == Tokens.entryOp && block1.node.firstChildNode.firstChildNode.elementType == Types.baseType)
            || (type1 == Tokens.entryOp && type2 == Types.variablePattern && block2.node.firstChildNode.firstChildNode.elementType == Types.baseType)) {
            // value Boolean a -> Boolean b
            return spacings.singleSpace;
        } else if (Types.\ialias in [nodeType, type1]) {
            // import { A=B }
            return spacings.aroundEqualsInImportAlias;
        } else if (Types.defaultTypeArgument in [nodeType, type2]) {
            // <A=B>
            return spacings.aroundEqualsInTypeArgs;
        } else if (type1 == Tokens.comma, nodeType == Types.typeParameterList) {
            // <A, B> in type params
            return spacings.afterTypeParam;
        } else if (type1 == Tokens.comma, nodeType == Types.typeArgumentList) {
            // <A, B> in type args
            return spacings.afterTypeArg;
        } else if (Tokens.intersectionOp in bothTypes && nodeType == Types.satisfiedTypes) {
            // satisfies A & B
            return spacings.inSatisfiesAndOf;
        } else if (type2 in memberOps) {
            return nodeType == Types.qualifiedMemberExpression
            then spacings.noneAllowNewLine
            else spacings.none;
        } else if (type1 in typesRequiringNoRightSpacing
            || (type2 in typesRequiringNoLeftSpacing && !ceylonHighlighter.keywords.contains(type1))
            || (type1 == Types.smallerOp && type2 == Types.typeParameterDeclaration)
            || (type1 == Types.typeParameterLiteral && type2 == Types.largerOp)) {
            return spacings.none;
        } else if (type2 == Types.positionalArgumentList && nodeType != Types.annotation) {
            // print("hello")
            return spacings.beforePositionalArgs;
        } else if (type2 == Types.positionalArgumentList && nodeType == Types.annotation) {
            // by ("bye")
            return spacings.beforeAnnotationPositionalArgs;
        } else if ((type1 in smallerOp && isType(block2))
            || (type2 in largerOp && isType(block1))) {
            // <TypeArgOrParam>
            return spacings.none;
        } else if (type1 == Types.baseType && type2 == Tokens.productOp && node.treeNext.elementType == Types.identifier) {
            // String *a;
            return spacings.singleSpace;
        } else if (nodeType in typesRequiringNoSpacing) {
            return spacings.none;
        } else if (type1 == Tokens.lbrace && type2 == Tokens.rbrace) {
            // {}
            return spacings.noneAllowNewLine;
        } else if (nodeType in [Types.spreadArgument, Types.spreadType] && type1 == Tokens.productOp) {
            // *spreadArg
            return spacings.none;
        } else if (nodeType == Types.functionType) {
            return type1 == Tokens.comma
            then spacings.singleSpace
            else spacings.none;
        } else if (type1 == Types.annotationList) {
            return spacings.keepSomeSpace;
        } else if (nodeType in inlineKeywords) {
            return spacings.inlineSingleSpace;
        } else if (nodeType == Types.annotationList && type1 == Types.stringLiteral) {
            return spacings.newLine;
        } else if (nodeType == Types.methodDefinition && type2 == Types.block) {
            return spacings.inlineSingleSpace;
        } else if (Types.identifier in bothTypes && nodeType in classOrInterface) {
            return spacings.inlineSingleSpace;
        } else if (type1 == Tokens.lbrace && type2 in [Types.sequencedArgument, Types.specifiedArgument]) {
            // { 1, 2, 3}
            return spacings.afterIterableEnumOpen;
        } else if (type1 in [Types.sequencedArgument, Types.specifiedArgument] && type2 == Tokens.rbrace) {
            // {1, 2, 3 }
            return spacings.beforeIterableEnumClose;
        } else if (nodeType in indentChildrenNormal
            && type2 == Tokens.rbrace
            && type1 != Tokens.lbrace) {
            return spacings.beforeRbrace;
        } else if (nodeType in indentChildrenNormal
            && type1 == Tokens.lbrace
            && type2 != Tokens.rbrace) {
            return spacings.newLine;
        } else if (isStatement(block1) && isStatement(block2)) {
            return spacings.newLine;
        } else if (isMetaLiteral(this) && Tokens.backtick in bothTypes) {
            return spacings.none;
        } else if (bothTypes.containsAny([Tokens.elseClause, Types.elseClause])) {
            return nodeType == Types.switchCaseList
            then spacings.strictNewLine
            else spacings.inlineSingleSpace;
        } else if (nodeType == Types.stringTemplate) {
            return spacings.noneAllowNewLine; // TODO: spacing for longer expressions
        } else if (nodeType == Types.iterableType) {
            return spacings.none;
        } else if (type1 in [Tokens.ifClause, Tokens.elseClause, Tokens.forClause,
            Tokens.whileClause, Tokens.switchClause, Tokens.caseClause, Tokens.\iassert,
            Tokens.tryClause, Tokens.catchClause, Tokens.finallyClause, Tokens.\idynamic,
            Tokens.\ilet]) {
            return spacings.afterKeyword;
        } else if (type1 == Tokens.comma, type2 == Types.listedArgument) {
            return spacings.keepSomeSpace;
        }

        return spacings.singleSpace;
    }

    Boolean isStatement(CeylonBlock block) {
        return block.node.psi is CeylonPsi.StatementPsi;
    }

    Boolean isType(CeylonBlock block) {
        return block.node.psi is CeylonPsi.TypePsi;
    }

    Boolean isMetaLiteral(CeylonBlock block) {
        return block.node.psi is CeylonPsi.MetaLiteralPsi;
    }
}
