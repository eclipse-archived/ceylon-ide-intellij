package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.treegen.Util;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Matija Mazi <br/>
 * @created 10/7/13 6:52 PM
 */
public class CeylonIdeaParser implements PsiParser {

    public static final Key<Node> CEYLON_NODE_KEY = new Key<>("Ceylon Node");
    public static final TokenSet NON_WS_TOKEN_SET = TokenSet.create(IElementType.enumerate(new IElementType.Predicate() {
        @Override
        public boolean matches(IElementType type) {
            return !type.equals(CeylonTypes.WS);
        }
    }));

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
//        final CeylonParser parser = new MyCeylonParser(builder, new CommonTokenStream(new CeylonLexer(new MyCharStream(builder.getOriginalText()))));
        final CeylonParser parser = new MarkingCeylonParser(builder);
        Node result = null;
/*
        if (root instanceof CeylonTokenType) {
            final PsiBuilder.Marker mark = builder.mark();
            if (builder.getTokenType() == root) {
                builder.advanceLexer();
            }
            // todo
        }
*/

        try {
            if (root == CeylonTypes.COMPILATION_UNIT) {
                result = parser.compilationUnit();
            } else if (root == CeylonTypes.MODULE_DESCRIPTOR) {
                result = parser.moduleDescriptor();
            } else if (root == CeylonTypes.PACKAGE_DESCRIPTOR) {
                result = parser.packageDescriptor();
            } else if (root == CeylonTypes.IMPORT_MODULE_LIST) {
                result = parser.importModuleList();
            } else if (root == CeylonTypes.IMPORT_MODULE) {
                result = parser.importModule();
            } else if (root == CeylonTypes.IMPORT_LIST) {
                result = parser.importElementList();
//            } else if (root == CeylonTypes.IMPORT_PATH) {
//                result = parser.importPath();
//            } else if (root == CeylonTypes.IMPORT_MEMBER_OR_TYPE_LIST) {
//                result = parser.xxx();
//            } else if (root == CeylonTypes.IMPORT_MEMBER_OR_TYPE) {
//                result = parser.xxx();
//            } else if (root == CeylonTypes.IMPORT_MEMBER) {
//                result = parser.xxx();
//            } else if (root == CeylonTypes.IMPORT_TYPE) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.IMPORT_WILDCARD) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DECLARATION) {
                result = parser.declaration();
//            } else if (root == CeylonTypes.MISSING_DECLARATION) {
//                result = parser.missingxxx();
            } else if (root == CeylonTypes.TYPE_DECLARATION) {
//                result = parser.typedeclaxxx();
            } else if (root == CeylonTypes.CLASS_OR_INTERFACE) {
//                result = parser.classOrxxx();
            } else if (root == CeylonTypes.TYPE_ALIAS_DECLARATION) {
//                result = parser.typeAliaxxx();
            } else if (root == CeylonTypes.SATISFIED_TYPES) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.ADAPTED_TYPES) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.EXTENDED_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_CONSTRAINT_LIST) {
                result = parser.typeConstraints();

            } else if (root == CeylonTypes.TYPE_SPECIFIER) {
                result = parser.typeSpecifier();
            } else if (root == CeylonTypes.DEFAULT_TYPE_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CLASS_SPECIFIER) {
                result = parser.classSpecifier();
            } else if (root == CeylonTypes.ANY_CLASS) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.CLASS_DECLARATION) {
                result = parser.classDeclaration();
            } else if (root == CeylonTypes.ANY_INTERFACE) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.INTERFACE_DECLARATION) {
                result = parser.interfaceDeclaration();
            } else if (root == CeylonTypes.TYPED_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ANY_ATTRIBUTE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ATTRIBUTE_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ATTRIBUTE_GETTER_DEFINITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ATTRIBUTE_SETTER_DEFINITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ANY_METHOD) {
                result = parser.voidOrInferredMethodDeclaration();
            } else if (root == CeylonTypes.METHOD_DEFINITION) {
//                result = parser.methodxxx();
            } else if (root == CeylonTypes.METHOD_DECLARATION) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.PARAMETER_LIST) {
                result = parser.parameters();
            } else if (root == CeylonTypes.PARAMETER) {
                result = parser.parameter();
            } else if (root == CeylonTypes.PARAMETER_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.VALUE_PARAMETER_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FUNCTIONAL_PARAMETER_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INITIALIZER_PARAMETER) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_PARAMETER_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_PARAMETER_DECLARATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_VARIANCE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BODY) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BLOCK) {
                result = parser.block();
            } else if (root == CeylonTypes.CLASS_BODY) {
                result = parser.classBody();
            } else if (root == CeylonTypes.INTERFACE_BODY) {
                result = parser.interfaceBody();
            } else if (root == CeylonTypes.TYPE) {
                result = parser.type();
            } else if (root == CeylonTypes.STATIC_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SIMPLE_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BASE_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.UNION_TYPE) {
                result = parser.unionType();
            } else if (root == CeylonTypes.INTERSECTION_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SEQUENCE_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ITERABLE_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.OPTIONAL_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TUPLE_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FUNCTION_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ENTRY_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.QUALIFIED_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SUPER_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.META_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MEMBER_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CLASS_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INTERFACE_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ALIAS_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_PARAMETER_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.VALUE_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FUNCTION_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MODULE_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.PACKAGE_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DYNAMIC_MODIFIER) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LOCAL_MODIFIER) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.SYNTHETIC_VARIABLE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_ARGUMENTS) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_ARGUMENT_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INFERRED_TYPE_ARGUMENTS) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SEQUENCED_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DEFAULTED_TYPE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DIRECTIVE) {
//                result = parser.xxx();




            } else if (root == CeylonTypes.STATEMENT_OR_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.STATEMENT) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.EXECUTABLE_STATEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ASSERTION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SPECIFIER_STATEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXPRESSION_STATEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CONTROL_STATEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CONTROL_CLAUSE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DYNAMIC_STATEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DYNAMIC_CLAUSE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.IF_STATEMENT) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.SWITCH_STATEMENT) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.SWITCH_CASE_LIST) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.CASE_ITEM) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MATCH_CASE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.IS_CASE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SATISFIES_CASE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TRY_CATCH_STATEMENT) {
//                result = parser.xxx();



            } else if (root == CeylonTypes.RESOURCE_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.RESOURCE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CATCH_VARIABLE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FOR_STATEMENT) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.FOR_ITERATOR) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.VALUE_ITERATOR) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.KEY_VALUE_ITERATOR) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.WHILE_STATEMENT) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.CONDITION_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BOOLEAN_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXISTS_OR_NONEMPTY_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXISTS_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.NONEMPTY_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.IS_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SATISFIES_CONDITION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.VARIABLE) {
                result = parser.variable();
            } else if (root == CeylonTypes.TERM) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.OPERATOR_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BINARY_OPERATOR_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ARITHMETIC_OP) {
//                result = parser.xxx();






            } else if (root == CeylonTypes.ASSIGNMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ARITHMETIC_ASSIGNMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ADD_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SUBTRACT_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MULTIPLY_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DIVIDE_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.REMAINDER_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BITWISE_ASSIGNMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INTERSECT_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.UNION_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.XOR_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.COMPLEMENT_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LOGICAL_ASSIGNMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.AND_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.OR_ASSIGN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LOGICAL_OP) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.BITWISE_OP) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.XOR_OP) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.EQUALITY_OP) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.COMPARISON_OP) {
//                result = parser.xxx();





            } else if (root == CeylonTypes.BOUND) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.OPEN_BOUND) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.CLOSED_BOUND) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.WITHIN_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DEFAULT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.THEN_OP) {
//                result = parser.xxx();






            } else if (root == CeylonTypes.UNARY_OPERATOR_EXPRESSION) {
//                result = parser.xxx();



            } else if (root == CeylonTypes.NEGATIVE_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSITIVE_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPE_OPERATOR_EXPRESSION) {
//                result = parser.xxx();



            } else if (root == CeylonTypes.OF_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.PREFIX_OPERATOR_EXPRESSION) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.POSTFIX_OPERATOR_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSTFIX_INCREMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSTFIX_DECREMENT_OP) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXPRESSION_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.PRIMARY) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSTFIX_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INVOCATION_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.PARAMETERIZED_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MEMBER_OR_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXTENDED_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.STATIC_MEMBER_OR_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BASE_MEMBER_OR_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BASE_MEMBER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.BASE_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.QUALIFIED_MEMBER_OR_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.QUALIFIED_MEMBER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.QUALIFIED_TYPE_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.MEMBER_OPERATOR) {
//                result = parser.xxx();



            } else if (root == CeylonTypes.INDEX_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ELEMENT_OR_RANGE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ELEMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ELEMENT_RANGE) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.ARGUMENT_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.NAMED_ARGUMENT_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SEQUENCED_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSITIONAL_ARGUMENT_LIST) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.POSITIONAL_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LISTED_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SPREAD_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FUNCTION_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.NAMED_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SPECIFIED_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TYPED_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.METHOD_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ATTRIBUTE_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.OBJECT_ARGUMENT) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SPECIFIER_OR_INITIALIZER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SPECIFIER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LAZY_SPECIFIER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.INITIALIZER_EXPRESSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ATOM) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.LITERAL) {
//                result = parser.xxx();




            } else if (root == CeylonTypes.QUOTED_LITERAL) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.DOC_LINK) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.SELF_EXPRESSION) {
//                result = parser.xxx();


            } else if (root == CeylonTypes.SEQUENCE_ENUMERATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.TUPLE) {
//                result = parser.xxx();

            } else if (root == CeylonTypes.STRING_TEMPLATE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ANNOTATION) {
                result = parser.annotation();
            } else if (root == CeylonTypes.ANONYMOUS_ANNOTATION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.ANNOTATION_LIST) {
                result = parser.annotations();
            } else if (root == CeylonTypes.IDENTIFIER) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.COMPREHENSION) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.COMPREHENSION_CLAUSE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.EXPRESSION_COMPREHENSION_CLAUSE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.FOR_COMPREHENSION_CLAUSE) {
//                result = parser.xxx();
            } else if (root == CeylonTypes.IF_COMPREHENSION_CLAUSE) {
//                result = parser.xxx();
            }
            // todo: token types
        } catch (RecognitionException e) {
            throw new RuntimeException("Unrecognized", e);
        }
        if (result == null) {
            throw new UnsupportedOperationException(String.format("Unsupported type: %s", root));
        }
        final RangeMapVisitor rangeMapVisitor = new RangeMapVisitor();
        rangeMapVisitor.visitAny(result);

        final ASTNode astRoot = builder.getTreeBuilt();
        bindASTs(astRoot, result, rangeMapVisitor.getMap());
        return astRoot;
    }

    private void bindASTs(ASTNode astNode, Node specNode, Map<TextRange, Node> map) {
/*
        if (!corresponds(astNode.getElementType(), (specNode.getNodeType()))) {
            System.out.printf("Subnode type mismatch: %s <> %s%n", astNode.getElementType(), (specNode.getNodeType()));
            return;
        }
*/
//        System.out.printf("Binding %s to %s%n", astNode.getElementType(), specNode == null ? null : specNode.getClass().getSimpleName());
        if (specNode != null) {
            astNode.putUserData(CEYLON_NODE_KEY, specNode);
        }
//        final List<Node> specChildren = specNode.getChildren();
        final ASTNode[] ijChildren = astNode.getChildren(NON_WS_TOKEN_SET);
//        System.out.printf("SpecChildren: %d, IJChildren: %d%n", specChildren.size(), ijChildren.length);
        for (final ASTNode ijChild : ijChildren) {
//            final Node specChild = specChildren.get(i);
            final Node specChild = map.get(ijChild.getTextRange());
            bindASTs(ijChild, specChild, map);
        }
    }
}
