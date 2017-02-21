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

import org.intellij.plugins.ceylon.ide.psi {
    Tokens=CeylonTokens,
    Types=CeylonTypes,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.highlighting {
    ceylonHighlighter
}

// child attributes
ChildAttributes childAttrNoIndent = ChildAttributes(Indent.normalIndent, null);
ChildAttributes childAttrNormalIndent = ChildAttributes(Indent.noneIndent, null);

// indents
[IElementType*] indentChildrenNormal = [
    Types.importModuleList, Types.block, Types.classBody, Types.interfaceBody,
    Types.namedArgumentList, Types.sequencedArgument, Types.importMemberOrTypeList,
    Types.conditionList
];
[IElementType*] indentChildrenNone = [
    Types.compilationUnit,
    Types.switchStatement, Types.switchCaseList, Types.switchClause, Types.caseClause,
    Types.sequenceEnumeration, Types.annotationList, Types.typeConstraintList,
    Types.ifStatement, Types.elseClause
];
[IElementType*] indentChildrenContinue = [
    Types.extendedType, Types.satisfiedTypes, Types.caseTypes,
    Types.lazySpecifierExpression, Types.specifierExpression,
    Types.delegatedConstructor, Types.typeConstraintList
];

// spacings
Spacing spacingNone = Spacing.createSpacing(0, 0, 0, false, 0);
Spacing spacingNoneAllowNewLine = Spacing.createSpacing(0, 0, 0, true, 0);
Spacing spacingEmptyLine = Spacing.createSpacing(0, 0, 2, true, 0);
Spacing spacingNewLine = Spacing.createSpacing(0, 0, 1, true, 1);
Spacing spacingStrictNewLine = Spacing.createSpacing(0, 0, 1, false, 0);
Spacing spacingSingleSpace = Spacing.createSpacing(1, 1, 0, true, 0);
Spacing spacingInlineSingleSpace = Spacing.createSpacing(1, 1, 0, false, 0);
Spacing spacingKeepSomeSpace = Spacing.createSpacing(1, 1, 0, true, 1);
Spacing spacingComment = Spacing.createSpacing(0, 100, 0, true, 1);

// Groups of element types
[IElementType*] typesRequiringEmptyLine = [
    Types.importList, Types.classDefinition, Types.methodDefinition, Types.objectDefinition
];
[IElementType*] typesRequiringNoSpacing = [
    Types.typeArgumentList, Types.typeParameterList,
    Types.sequencedType, Types.indexExpression
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

shared class CeylonBlock(ASTNode node, Indent myIndent) satisfies Block {

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
                    if (type in [Tokens.rbrace, Tokens.lbrace]) then Indent.noneIndent
                    else if (exists p = prevChildType, p == Types.annotationList) then Indent.noneIndent
                    else if (type in memberOps) then Indent.normalIndent
                    else if (type == Types.listedArgument) then Indent.normalIndent
                    else if (nodeType == Types.letClause, type in [Types.variable, Types.destructure]) then Indent.normalIndent
                    else normalChildIndent;
                blocks.add(CeylonBlock(c, indent));
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
            return spacingNone;
        }

        value type1 = block1.node.elementType;
        value type2 = block2.node.elementType;
        value bothTypes = [type1, type2];
        value nodeType = node.elementType;

        if (type1 == Tokens.\ireturn && type2 == Tokens.semicolon) {
            // return;
            return spacingNone;
        } else if (bothTypes.containsAny([Tokens.multiComment, Tokens.lineComment])) {
            return spacingComment; //or just return null?
        } else if (type1 in typesRequiringEmptyLine && type2 != Tokens.rbrace
            || type2 in typesRequiringEmptyLine && !type1 in [Tokens.lbrace, Types.annotationList]) {
            return spacingEmptyLine;
        } else if (type1 == Types.importModule) {
            return spacingNewLine;
        } else if (type2 == Types.importMember) {
            return spacingNewLine;
        } else if (type1 == Types.anonymousAnnotation) {
            return spacingNewLine;
        } else if (Tokens.unionOp in bothTypes && nodeType == Types.caseTypes) {
            return spacingSingleSpace;
        } else if ((type1 == Types.variablePattern && type2 == Tokens.entryOp && block1.node.firstChildNode.firstChildNode.elementType == Types.baseType)
            || (type1 == Tokens.entryOp && type2 == Types.variablePattern && block2.node.firstChildNode.firstChildNode.elementType == Types.baseType)) {
            // value Boolean a -> Boolean b
            return spacingSingleSpace;
        } else if (Types.\ialias in [nodeType, type1]) {
            // import { A=B }
            return spacingNone;
        } else if (Tokens.intersectionOp in bothTypes && nodeType == Types.satisfiedTypes) {
            // satisfies A & B
            return spacingSingleSpace;
        } else if (type2 in memberOps) {
            return nodeType == Types.qualifiedMemberExpression
            then spacingNoneAllowNewLine
            else spacingNone;
        } else if (type1 in typesRequiringNoRightSpacing
            || (type2 in typesRequiringNoLeftSpacing && !ceylonHighlighter.keywords.contains(type1))
            || (type1 == Types.smallerOp && type2 == Types.typeParameterDeclaration)
            || (type1 == Types.typeParameterLiteral && type2 == Types.largerOp)
            || (type2 == Types.positionalArgumentList && nodeType != Types.annotation)) {
            return spacingNone;
        } else if ((type1 in smallerOp && isType(block2))
            || (type2 in largerOp && isType(block1))) {
            // <TypeArgOrParam>
            return spacingNone;
        } else if (type1 == Types.baseType && type2 == Tokens.productOp && node.treeNext.elementType == Types.identifier) {
            // String *a;
            return spacingSingleSpace;
        } else if (nodeType in typesRequiringNoSpacing) {
            return spacingNone;
        } else if (type1 == Tokens.lbrace && type2 == Tokens.rbrace) {
            // {}
            return spacingNoneAllowNewLine;
        } else if (nodeType == Types.spreadArgument && type1 == Tokens.productOp) {
            // *spreadArg
            return spacingNone;
        } else if (nodeType == Types.functionType) {
            return type1 == Tokens.comma
            then spacingSingleSpace
            else spacingNone;
        } else if (type1 == Types.annotationList) {
            return spacingKeepSomeSpace;
        } else if (nodeType in inlineKeywords) {
            return spacingInlineSingleSpace;
        } else if (nodeType == Types.annotationList && type1 == Types.stringLiteral) {
            return spacingNewLine;
        } else if (nodeType == Types.methodDefinition && type2 == Types.block) {
            return spacingInlineSingleSpace;
        } else if (Types.identifier in bothTypes && nodeType in classOrInterface) {
            return spacingInlineSingleSpace;
        } else if (nodeType in indentChildrenNormal
            && type2 == Tokens.rbrace
            && type1 != Tokens.lbrace) {
            return spacingStrictNewLine;
        } else if (nodeType in indentChildrenNormal
            && type1 == Tokens.lbrace
            && type2 != Tokens.rbrace) {
            return spacingNewLine;
        } else if (isStatement(block1) && isStatement(block2)) {
            return spacingNewLine;
        } else if (isMetaLiteral(this) && Tokens.backtick in bothTypes) {
            return spacingNone;
        } else if (nodeType == Types.annotation && type1 == Types.identifier) {
            return spacingSingleSpace;
        } else if (bothTypes.containsAny([Tokens.elseClause, Types.elseClause])) {
            return nodeType == Types.switchCaseList
            then spacingStrictNewLine
            else spacingInlineSingleSpace;
        } else if (nodeType == Types.stringTemplate) {
            return spacingNoneAllowNewLine; // TODO: spacing for longer expressions
        } else if (nodeType == Types.iterableType) {
            return spacingNone;
        }

        return spacingSingleSpace;
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
