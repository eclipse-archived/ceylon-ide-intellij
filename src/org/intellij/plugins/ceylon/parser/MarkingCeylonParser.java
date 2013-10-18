package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonTypes;


// todo! Do something about overriding final methods
// todo: Some CeylonParser methods have abstract return types; we construct concrete corresponding AST/Psi elements.
public class MarkingCeylonParser extends CeylonParser {
    private final int textHash;
    private PsiBuilder psiBuilder;
    private int level = 0;

    public MarkingCeylonParser(PsiBuilder psiBuilder) {
//        super(new DebuggingPsiBuilderTokenStream(psiBuilder));
        super(new PsiBuilderTokenStream(psiBuilder));
        this.psiBuilder = psiBuilder;
        textHash = psiBuilder.getOriginalText().hashCode();
        print("================================================================================================%n");
        print("== %d: new Parser ==================================================================%n", textHash);
    }


    private PsiBuilder.Marker mark(String method) {
        print(String.format("%%20d: %%%ds%%s%%n", ++level), textHash, "?", method);
        return psiBuilder.mark();
//        return null;
    }

    private void end(PsiBuilder.Marker marker, IElementType elementType, Node node) {
        if (node == null) {
            print(String.format("%%20d: %%%ds%%s - NULL%%n", level--), textHash, "-", elementType);
//            marker.rollbackTo();
            marker.drop();
        } else {
            print(String.format("%%20d: %%%ds%%s%%n", level--), textHash, "+", elementType);
            marker.done(elementType);
        }
    }

    @Override
    public Tree.ImportMemberOrTypeList importElementList() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importElementList");
        final Tree.ImportMemberOrTypeList node = super.importElementList();
        end(marker, CeylonTypes.IMPORT_LIST, node);
        return node;
    }

    @Override
    public Tree.Import importDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importDeclaration");
        final Tree.Import node = super.importDeclaration();
        end(marker, CeylonTypes.IMPORT, node);
        return node;
    }

    @Override
    public Tree.ImportMemberOrType importElement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importElement");
        final Tree.ImportMemberOrType node = super.importElement();
        end(marker, CeylonTypes.IMPORT_MEMBER_OR_TYPE, node);
        return node;
    }

    @Override
    public Tree.ImportModuleList importModuleList() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importModuleList");
        final Tree.ImportModuleList node = super.importModuleList();
        end(marker, CeylonTypes.IMPORT_MODULE_LIST, node);
        return node;
    }

    @Override
    public Tree.ImportPath packagePath() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("packagePath");
        final Tree.ImportPath node = super.packagePath();
        end(marker, CeylonTypes.IMPORT_PATH, node);
        return node;
    }

    @Override
    public Tree.AnyClass classDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("classDeclaration");
        final Tree.AnyClass node = super.classDeclaration();
        end(marker, CeylonTypes.ANY_CLASS, node);
        return node;
    }

    @Override
    public Tree.AnyMethod voidOrInferredMethodDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("voidOrInferredMethodDeclaration");
        final Tree.AnyMethod node = super.voidOrInferredMethodDeclaration();
        end(marker, CeylonTypes.ANY_METHOD, node);
        return node;
    }

    @Override
    public Tree.CompilationUnit compilationUnit() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("compilationUnit");
        final Tree.CompilationUnit node = super.compilationUnit();
        end(marker, CeylonTypes.COMPILATION_UNIT, node);
        return node;
    }

    @Override
    public Tree.InitializerParameter parameterRef() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("parameterRef");
        final Tree.InitializerParameter node = super.parameterRef();
        end(marker, CeylonTypes.INITIALIZER_PARAMETER, node);
        return node;
    }

    @Override
    public Tree.ModuleDescriptor moduleDescriptor() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("moduleDescriptor");
        final Tree.ModuleDescriptor node = super.moduleDescriptor();
        end(marker, CeylonTypes.MODULE_DESCRIPTOR, node);
        return node;
    }

    @Override
    public Tree.PackageDescriptor packageDescriptor() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("packageDescriptor");
        final Tree.PackageDescriptor node = super.packageDescriptor();
        end(marker, CeylonTypes.PACKAGE_DESCRIPTOR, node);
        return node;
    }

    @Override
    public Tree.ImportModule importModule() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importModule");
        final Tree.ImportModule node = super.importModule();
        end(marker, CeylonTypes.IMPORT_MODULE, node);
        return node;
    }

    @Override
    public Tree.ImportWildcard importWildcard() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importWildcard");
        final Tree.ImportWildcard node = super.importWildcard();
        end(marker, CeylonTypes.IMPORT_WILDCARD, node);
        return node;
    }

    @Override
    public Tree.Identifier importName() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("importName");
        final Tree.Identifier node = super.importName();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier packageName() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("packageName");
        final Tree.Identifier node = super.packageName();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier typeName() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeName");
        final Tree.Identifier node = super.typeName();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier annotationName() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("annotationName");
        final Tree.Identifier node = super.annotationName();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier memberName() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("memberName");
        final Tree.Identifier node = super.memberName();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier memberNameDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("memberNameDeclaration");
        final Tree.Identifier node = super.memberNameDeclaration();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.Identifier typeNameDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeNameDeclaration");
        final Tree.Identifier node = super.typeNameDeclaration();
        end(marker, CeylonTypes.IDENTIFIER, node);
        return node;
    }

    @Override
    public Tree.ObjectDefinition objectDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("objectDeclaration");
        final Tree.ObjectDefinition node = super.objectDeclaration();
        end(marker, CeylonTypes.OBJECT_DEFINITION, node);
        return node;
    }

    @Override
    public Tree.AttributeSetterDefinition setterDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("setterDeclaration");
        final Tree.AttributeSetterDefinition node = super.setterDeclaration();
        end(marker, CeylonTypes.ATTRIBUTE_SETTER_DEFINITION, node);
        return node;
    }

    @Override
    public Tree.AnyAttribute inferredAttributeDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("inferredAttributeDeclaration");
        final Tree.AnyAttribute node = super.inferredAttributeDeclaration();
        end(marker, CeylonTypes.ANY_ATTRIBUTE, node);
        return node;
    }

    @Override
    public Tree.TypedDeclaration typedMethodOrAttributeDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typedMethodOrAttributeDeclaration");
        final Tree.TypedDeclaration node = super.typedMethodOrAttributeDeclaration();
        end(marker, CeylonTypes.TYPED_DECLARATION, node);
        return node;
    }

    @Override
    public Tree.AnyInterface interfaceDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("interfaceDeclaration");
        final Tree.AnyInterface node = super.interfaceDeclaration();
        end(marker, CeylonTypes.ANY_INTERFACE, node);
        return node;
    }

    @Override
    public Tree.TypeAliasDeclaration aliasDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("aliasDeclaration");
        final Tree.TypeAliasDeclaration node = super.aliasDeclaration();
        end(marker, CeylonTypes.TYPE_ALIAS_DECLARATION, node);
        return node;
    }

    @Override
    public Tree.Assertion assertion() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("assertion");
        final Tree.Assertion node = super.assertion();
        end(marker, CeylonTypes.ASSERTION, node);
        return node;
    }

    @Override
    public Tree.Block block() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("block");
        final Tree.Block node = super.block();
        end(marker, CeylonTypes.BLOCK, node);
        return node;
    }

    @Override
    public Tree.InterfaceBody interfaceBody() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("interfaceBody");
        final Tree.InterfaceBody node = super.interfaceBody();
        end(marker, CeylonTypes.INTERFACE_BODY, node);
        return node;
    }

    @Override
    public Tree.ClassBody classBody() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("classBody");
        final Tree.ClassBody node = super.classBody();
        end(marker, CeylonTypes.CLASS_BODY, node);
        return node;
    }

    @Override
    public Tree.ExtendedType extendedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("extendedType");
        final Tree.ExtendedType node = super.extendedType();
        end(marker, CeylonTypes.EXTENDED_TYPE, node);
        return node;
    }

    @Override
    public Tree.ClassSpecifier classSpecifier() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("classSpecifier");
        final Tree.ClassSpecifier node = super.classSpecifier();
        end(marker, CeylonTypes.CLASS_SPECIFIER, node);
        return node;
    }


    @Override
    public Tree.SatisfiedTypes satisfiedTypes() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("satisfiedTypes");
        final Tree.SatisfiedTypes node = super.satisfiedTypes();
        end(marker, CeylonTypes.SATISFIED_TYPES, node);
        return node;
    }

    @Override
    public Tree.AbstractedType abstractedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("abstractedType");
        final Tree.AbstractedType node = super.abstractedType();
        end(marker, CeylonTypes.ABSTRACTED_TYPE, node);
        return node;
    }

    @Override
    public Tree.CaseTypes caseTypes() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("caseTypes");
        final Tree.CaseTypes node = super.caseTypes();
        end(marker, CeylonTypes.CASE_TYPES, node);
        return node;
    }


    @Override
    public Tree.ParameterList parameters() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("parameters");
        final Tree.ParameterList node = super.parameters();
        end(marker, CeylonTypes.PARAMETER_LIST, node);
        return node;
    }

    @Override
    public Tree.ParameterDeclaration parameter() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("parameter");
        final Tree.ParameterDeclaration node = super.parameter();
        end(marker, CeylonTypes.PARAMETER_DECLARATION, node);
        return node;
    }

    @Override
    public Tree.Parameter parameterDeclarationOrRef() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("parameterDeclarationOrRef");
        final Tree.Parameter node = super.parameterDeclarationOrRef();
        end(marker, CeylonTypes.PARAMETER, node);
        return node;
    }

    @Override
    public Tree.TypeParameterList typeParameters() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeParameters");
        final Tree.TypeParameterList node = super.typeParameters();
        end(marker, CeylonTypes.TYPE_PARAMETER_LIST, node);
        return node;
    }

    @Override
    public Tree.TypeParameterDeclaration typeParameter() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeParameter");
        final Tree.TypeParameterDeclaration node = super.typeParameter();
        end(marker, CeylonTypes.TYPE_PARAMETER_DECLARATION, node);
        return node;
    }

    @Override
    public Tree.TypeVariance variance() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("variance");
        final Tree.TypeVariance node = super.variance();
        end(marker, CeylonTypes.TYPE_VARIANCE, node);
        return node;
    }

    @Override
    public Tree.TypeConstraint typeConstraint() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeConstraint");
        final Tree.TypeConstraint node = super.typeConstraint();
        end(marker, CeylonTypes.TYPE_CONSTRAINT, node);
        return node;
    }

    @Override
    public Tree.TypeConstraintList typeConstraints() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeConstraints");
        final Tree.TypeConstraintList node = super.typeConstraints();
        end(marker, CeylonTypes.TYPE_CONSTRAINT_LIST, node);
        return node;
    }


    @Override
    public Tree.Statement declarationOrStatement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("declarationOrStatement");
        final Tree.Statement node = super.declarationOrStatement();
        end(marker, CeylonTypes.STATEMENT, node);
        return node;
    }

    @Override
    public Tree.Declaration declaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("declaration");
        final Tree.Declaration node = super.declaration();
        end(marker, CeylonTypes.DECLARATION, node);
        return node;
    }


    @Override
    public Tree.Statement statement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("statement");
        final Tree.Statement node = super.statement();
        end(marker, CeylonTypes.STATEMENT, node);
        return node;
    }

    @Override
    public Tree.Statement expressionOrSpecificationStatement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("expressionOrSpecificationStatement");
        final Tree.Statement node = super.expressionOrSpecificationStatement();
        end(marker, CeylonTypes.STATEMENT, node);
        return node;
    }

    @Override
    public Tree.Directive directiveStatement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("directiveStatement");
        final Tree.Directive node = super.directiveStatement();
        end(marker, CeylonTypes.DIRECTIVE, node);
        return node;
    }

    @Override
    public Tree.Directive directive() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("directive");
        final Tree.Directive node = super.directive();
        end(marker, CeylonTypes.DIRECTIVE, node);
        return node;
    }

    @Override
    public Tree.Return returnDirective() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("returnDirective");
        final Tree.Return node = super.returnDirective();
        end(marker, CeylonTypes.RETURN, node);
        return node;
    }

    @Override
    public Tree.Throw throwDirective() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("throwDirective");
        final Tree.Throw node = super.throwDirective();
        end(marker, CeylonTypes.THROW, node);
        return node;
    }

    @Override
    public Tree.Break breakDirective() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("breakDirective");
        final Tree.Break node = super.breakDirective();
        end(marker, CeylonTypes.BREAK, node);
        return node;
    }

    @Override
    public Tree.Continue continueDirective() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("continueDirective");
        final Tree.Continue node = super.continueDirective();
        end(marker, CeylonTypes.CONTINUE, node);
        return node;
    }

    @Override
    public Tree.TypeSpecifier typeSpecifier() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeSpecifier");
        final Tree.TypeSpecifier node = super.typeSpecifier();
        end(marker, CeylonTypes.TYPE_SPECIFIER, node);
        return node;
    }

    @Override
    public Tree.TypeSpecifier typeDefault() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeDefault");
        final Tree.TypeSpecifier node = super.typeDefault();
        end(marker, CeylonTypes.TYPE_SPECIFIER, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression specifier() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("specifier");
        final Tree.SpecifierExpression node = super.specifier();
        end(marker, CeylonTypes.SPECIFIER_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression lazySpecifier() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("lazySpecifier");
        final Tree.SpecifierExpression node = super.lazySpecifier();
        end(marker, CeylonTypes.SPECIFIER_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression functionSpecifier() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("functionSpecifier");
        final Tree.SpecifierExpression node = super.functionSpecifier();
        end(marker, CeylonTypes.SPECIFIER_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Expression expression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("expression");
        final Tree.Expression node = super.expression();
        end(marker, CeylonTypes.EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Primary base() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("base");
        final Tree.Primary node = super.base();
        end(marker, CeylonTypes.PRIMARY, node);
        return node;
    }


    @Override
    public Tree.Primary primary() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("primary");
        final Tree.Primary node = super.primary();
        end(marker, CeylonTypes.PRIMARY, node);
        return node;
    }


    @Override
    public Tree.MemberOperator memberSelectionOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("memberSelectionOperator");
        final Tree.MemberOperator node = super.memberSelectionOperator();
        end(marker, CeylonTypes.MEMBER_OPERATOR, node);
        return node;
    }

    @Override
    public Tree.SequenceEnumeration enumeration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("enumeration");
        final Tree.SequenceEnumeration node = super.enumeration();
        end(marker, CeylonTypes.SEQUENCE_ENUMERATION, node);
        return node;
    }

    @Override
    public Tree.Tuple tuple() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("tuple");
        final Tree.Tuple node = super.tuple();
        end(marker, CeylonTypes.TUPLE, node);
        return node;
    }

    @Override
    public Tree.Dynamic dynamicObject() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("dynamicObject");
        final Tree.Dynamic node = super.dynamicObject();
        end(marker, CeylonTypes.DYNAMIC, node);
        return node;
    }

    @Override
    public Tree.ExpressionList expressions() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("expressions");
        final Tree.ExpressionList node = super.expressions();
        end(marker, CeylonTypes.EXPRESSION_LIST, node);
        return node;
    }


    @Override
    public Tree.IndexExpression indexOrIndexRange() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("indexOrIndexRange");
        final Tree.IndexExpression node = super.indexOrIndexRange();
        end(marker, CeylonTypes.INDEX_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Expression index() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("index");
        final Tree.Expression node = super.index();
        end(marker, CeylonTypes.EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.NamedArgumentList namedArguments() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("namedArguments");
        final Tree.NamedArgumentList node = super.namedArguments();
        end(marker, CeylonTypes.NAMED_ARGUMENT_LIST, node);
        return node;
    }

    @Override
    public Tree.SequencedArgument sequencedArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("sequencedArgument");
        final Tree.SequencedArgument node = super.sequencedArgument();
        end(marker, CeylonTypes.SEQUENCED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.NamedArgument namedArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("namedArgument");
        final Tree.NamedArgument node = super.namedArgument();
        end(marker, CeylonTypes.NAMED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.SpecifiedArgument namedSpecifiedArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("namedSpecifiedArgument");
        final Tree.SpecifiedArgument node = super.namedSpecifiedArgument();
        end(marker, CeylonTypes.SPECIFIED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.SpecifiedArgument anonymousArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("anonymousArgument");
        final Tree.SpecifiedArgument node = super.anonymousArgument();
        end(marker, CeylonTypes.SPECIFIED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.ObjectArgument objectArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("objectArgument");
        final Tree.ObjectArgument node = super.objectArgument();
        end(marker, CeylonTypes.OBJECT_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.MethodArgument voidOrInferredMethodArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("voidOrInferredMethodArgument");
        final Tree.MethodArgument node = super.voidOrInferredMethodArgument();
        end(marker, CeylonTypes.METHOD_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.AttributeArgument inferredGetterArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("inferredGetterArgument");
        final Tree.AttributeArgument node = super.inferredGetterArgument();
        end(marker, CeylonTypes.ATTRIBUTE_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.TypedArgument typedMethodOrGetterArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typedMethodOrGetterArgument");
        final Tree.TypedArgument node = super.typedMethodOrGetterArgument();
        end(marker, CeylonTypes.TYPED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.TypedArgument untypedMethodOrGetterArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("untypedMethodOrGetterArgument");
        final Tree.TypedArgument node = super.untypedMethodOrGetterArgument();
        end(marker, CeylonTypes.TYPED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.NamedArgument namedArgumentDeclaration() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("namedArgumentDeclaration");
        final Tree.NamedArgument node = super.namedArgumentDeclaration();
        end(marker, CeylonTypes.NAMED_ARGUMENT, node);
        return node;
    }


    @Override
    public Tree.Expression parExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("parExpression");
        final Tree.Expression node = super.parExpression();
        end(marker, CeylonTypes.EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.PositionalArgumentList positionalArguments() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("positionalArguments");
        final Tree.PositionalArgumentList node = super.positionalArguments();
        end(marker, CeylonTypes.POSITIONAL_ARGUMENT_LIST, node);
        return node;
    }

    @Override
    public Tree.ListedArgument positionalArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("positionalArgument");
        final Tree.ListedArgument node = super.positionalArgument();
        end(marker, CeylonTypes.LISTED_ARGUMENT, node);
        return node;
    }

    @Override
    public Tree.SpreadArgument spreadArgument() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("spreadArgument");
        final Tree.SpreadArgument node = super.spreadArgument();
        end(marker, CeylonTypes.SPREAD_ARGUMENT, node);
        return node;
    }


    @Override
    public Tree.Expression functionOrExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("functionOrExpression");
        final Tree.Expression node = super.functionOrExpression();
        end(marker, CeylonTypes.EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Expression anonymousFunction() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("anonymousFunction");
        final Tree.Expression node = super.anonymousFunction();
        end(marker, CeylonTypes.EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Comprehension comprehension() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("comprehension");
        final Tree.Comprehension node = super.comprehension();
        end(marker, CeylonTypes.COMPREHENSION, node);
        return node;
    }

    @Override
    public Tree.ComprehensionClause comprehensionClause() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("comprehensionClause");
        final Tree.ComprehensionClause node = super.comprehensionClause();
        end(marker, CeylonTypes.COMPREHENSION_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ExpressionComprehensionClause expressionComprehensionClause() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("expressionComprehensionClause");
        final Tree.ExpressionComprehensionClause node = super.expressionComprehensionClause();
        end(marker, CeylonTypes.EXPRESSION_COMPREHENSION_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ForComprehensionClause forComprehensionClause() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("forComprehensionClause");
        final Tree.ForComprehensionClause node = super.forComprehensionClause();
        end(marker, CeylonTypes.FOR_COMPREHENSION_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.IfComprehensionClause ifComprehensionClause() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("ifComprehensionClause");
        final Tree.IfComprehensionClause node = super.ifComprehensionClause();
        end(marker, CeylonTypes.IF_COMPREHENSION_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.Term assignmentExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("assignmentExpression");
        final Tree.Term node = super.assignmentExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.AssignmentOp assignmentOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("assignmentOperator");
        final Tree.AssignmentOp node = super.assignmentOperator();
        end(marker, CeylonTypes.ASSIGNMENT_OP, node);
        return node;
    }

    @Override
    public Tree.Term thenElseExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("thenElseExpression");
        final Tree.Term node = super.thenElseExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression thenElseOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("thenElseOperator");
        final Tree.BinaryOperatorExpression node = super.thenElseOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term disjunctionExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("disjunctionExpression");
        final Tree.Term node = super.disjunctionExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.OrOp disjunctionOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("disjunctionOperator");
        final Tree.OrOp node = super.disjunctionOperator();
        end(marker, CeylonTypes.OR_OP, node);
        return node;
    }

    @Override
    public Tree.Term conjunctionExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("conjunctionExpression");
        final Tree.Term node = super.conjunctionExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.AndOp conjunctionOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("conjunctionOperator");
        final Tree.AndOp node = super.conjunctionOperator();
        end(marker, CeylonTypes.AND_OP, node);
        return node;
    }

    @Override
    public Tree.Term logicalNegationExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("logicalNegationExpression");
        final Tree.Term node = super.logicalNegationExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.NotOp notOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("notOperator");
        final Tree.NotOp node = super.notOperator();
        end(marker, CeylonTypes.NOT_OP, node);
        return node;
    }

    @Override
    public Tree.Term equalityExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("equalityExpression");
        final Tree.Term node = super.equalityExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression equalityOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("equalityOperator");
        final Tree.BinaryOperatorExpression node = super.equalityOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term comparisonExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("comparisonExpression");
        final Tree.Term node = super.comparisonExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.ComparisonOp smallerOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("smallerOperator");
        final Tree.ComparisonOp node = super.smallerOperator();
        end(marker, CeylonTypes.COMPARISON_OP, node);
        return node;
    }

    @Override
    public Tree.ComparisonOp largerOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("largerOperator");
        final Tree.ComparisonOp node = super.largerOperator();
        end(marker, CeylonTypes.COMPARISON_OP, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression comparisonOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("comparisonOperator");
        final Tree.BinaryOperatorExpression node = super.comparisonOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.TypeOperatorExpression typeOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeOperator");
        final Tree.TypeOperatorExpression node = super.typeOperator();
        end(marker, CeylonTypes.TYPE_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term existenceEmptinessExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("existenceEmptinessExpression");
        final Tree.Term node = super.existenceEmptinessExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.UnaryOperatorExpression existsNonemptyOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("existsNonemptyOperator");
        final Tree.UnaryOperatorExpression node = super.existsNonemptyOperator();
        end(marker, CeylonTypes.UNARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term entryRangeExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("entryRangeExpression");
        final Tree.Term node = super.entryRangeExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression rangeIntervalEntryOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("rangeIntervalEntryOperator");
        final Tree.BinaryOperatorExpression node = super.rangeIntervalEntryOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term additiveExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("additiveExpression");
        final Tree.Term node = super.additiveExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression additiveOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("additiveOperator");
        final Tree.BinaryOperatorExpression node = super.additiveOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term scaleExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("scaleExpression");
        final Tree.Term node = super.scaleExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.Term multiplicativeExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("multiplicativeExpression");
        final Tree.Term node = super.multiplicativeExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression multiplicativeOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("multiplicativeOperator");
        final Tree.BinaryOperatorExpression node = super.multiplicativeOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term unionExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("unionExpression");
        final Tree.Term node = super.unionExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression unionOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("unionOperator");
        final Tree.BinaryOperatorExpression node = super.unionOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term intersectionExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("intersectionExpression");
        final Tree.Term node = super.intersectionExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression intersectionOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("intersectionOperator");
        final Tree.BinaryOperatorExpression node = super.intersectionOperator();
        end(marker, CeylonTypes.BINARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term negationComplementExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("negationComplementExpression");
        final Tree.Term node = super.negationComplementExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.UnaryOperatorExpression unaryMinusOrComplementOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("unaryMinusOrComplementOperator");
        final Tree.UnaryOperatorExpression node = super.unaryMinusOrComplementOperator();
        end(marker, CeylonTypes.UNARY_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term exponentiationExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("exponentiationExpression");
        final Tree.Term node = super.exponentiationExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.PowerOp exponentiationOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("exponentiationOperator");
        final Tree.PowerOp node = super.exponentiationOperator();
        end(marker, CeylonTypes.POWER_OP, node);
        return node;
    }

    @Override
    public Tree.ScaleOp scaleOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("scaleOperator");
        final Tree.ScaleOp node = super.scaleOperator();
        end(marker, CeylonTypes.SCALE_OP, node);
        return node;
    }

    @Override
    public Tree.Term incrementDecrementExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("incrementDecrementExpression");
        final Tree.Term node = super.incrementDecrementExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.PrefixOperatorExpression prefixOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("prefixOperator");
        final Tree.PrefixOperatorExpression node = super.prefixOperator();
        end(marker, CeylonTypes.PREFIX_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Term postfixIncrementDecrementExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("postfixIncrementDecrementExpression");
        final Tree.Term node = super.postfixIncrementDecrementExpression();
        end(marker, CeylonTypes.TERM, node);
        return node;
    }

    @Override
    public Tree.PostfixOperatorExpression postfixOperator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("postfixOperator");
        final Tree.PostfixOperatorExpression node = super.postfixOperator();
        end(marker, CeylonTypes.POSTFIX_OPERATOR_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.Atom selfReference() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("selfReference");
        final Tree.Atom node = super.selfReference();
        end(marker, CeylonTypes.ATOM, node);
        return node;
    }

    @Override
    public Tree.Literal nonstringLiteral() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("nonstringLiteral");
        final Tree.Literal node = super.nonstringLiteral();
        end(marker, CeylonTypes.LITERAL, node);
        return node;
    }

    @Override
    public Tree.StringLiteral stringLiteral() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("stringLiteral");
        final Tree.StringLiteral node = super.stringLiteral();
        end(marker, CeylonTypes.STRING_LITERAL, node);
        return node;
    }

    @Override
    public Tree.Atom stringExpression() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("stringExpression");
        final Tree.Atom node = super.stringExpression();
        end(marker, CeylonTypes.ATOM, node);
        return node;
    }

    @Override
    public Tree.TypeArgumentList typeArguments() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("typeArguments");
        final Tree.TypeArgumentList node = super.typeArguments();
        end(marker, CeylonTypes.TYPE_ARGUMENT_LIST, node);
        return node;
    }

    @Override
    public Tree.Type variadicType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("variadicType");
        final Tree.Type node = super.variadicType();
        end(marker, CeylonTypes.TYPE, node);
        return node;
    }

    @Override
    public Tree.Type defaultedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("defaultedType");
        final Tree.Type node = super.defaultedType();
        end(marker, CeylonTypes.TYPE, node);
        return node;
    }

    @Override
    public Tree.TupleType tupleType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("tupleType");
        final Tree.TupleType node = super.tupleType();
        end(marker, CeylonTypes.TUPLE_TYPE, node);
        return node;
    }

    @Override
    public Tree.StaticType groupedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("groupedType");
        final Tree.StaticType node = super.groupedType();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.IterableType iterableType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("iterableType");
        final Tree.IterableType node = super.iterableType();
        end(marker, CeylonTypes.ITERABLE_TYPE, node);
        return node;
    }

    @Override
    public Tree.MetaLiteral metaLiteral() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("metaLiteral");
        final Tree.MetaLiteral node = super.metaLiteral();
        end(marker, CeylonTypes.META_LITERAL, node);
        return node;
    }

    @Override
    public Tree.StaticType type() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("type");
        final Tree.StaticType node = super.type();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.StaticType unionType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("unionType");
        final Tree.StaticType node = super.unionType();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.StaticType intersectionType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("intersectionType");
        final Tree.StaticType node = super.intersectionType();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.StaticType qualifiedOrTupleType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("qualifiedOrTupleType");
        final Tree.StaticType node = super.qualifiedOrTupleType();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.StaticType abbreviatedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("abbreviatedType");
        final Tree.StaticType node = super.abbreviatedType();
        end(marker, CeylonTypes.STATIC_TYPE, node);
        return node;
    }

    @Override
    public Tree.SimpleType qualifiedType() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("qualifiedType");
        final Tree.SimpleType node = super.qualifiedType();
        end(marker, CeylonTypes.SIMPLE_TYPE, node);
        return node;
    }


    @Override
    public Tree.AnnotationList annotations() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("annotations");
        final Tree.AnnotationList node = super.annotations();
        end(marker, CeylonTypes.ANNOTATION_LIST, node);
        return node;
    }

    @Override
    public Tree.Annotation annotation() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("annotation");
        final Tree.Annotation node = super.annotation();
        end(marker, CeylonTypes.ANNOTATION, node);
        return node;
    }



/*
    @Override
    public List<Tree.CompilerAnnotation> compilerAnnotations() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final List<Tree.CompilerAnnotation> node = super.compilerAnnotations();
        marker.done(List < CeylonTypes.COMPILER_ANNOTATION >); return node;
    }
*/

    @Override
    public Tree.CompilerAnnotation compilerAnnotation() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("compilerAnnotation");
        final Tree.CompilerAnnotation node = super.compilerAnnotation();
        end(marker, CeylonTypes.COMPILER_ANNOTATION, node);
        return node;
    }

    @Override
    public Tree.ConditionList conditions() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("conditions");
        final Tree.ConditionList node = super.conditions();
        end(marker, CeylonTypes.CONDITION_LIST, node);
        return node;
    }

    @Override
    public Tree.Condition condition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("condition");
        final Tree.Condition node = super.condition();
        end(marker, CeylonTypes.CONDITION, node);
        return node;
    }

    @Override
    public Tree.BooleanCondition booleanCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("booleanCondition");
        final Tree.BooleanCondition node = super.booleanCondition();
        end(marker, CeylonTypes.BOOLEAN_CONDITION, node);
        return node;
    }

    @Override
    public Tree.ExistsCondition existsCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("existsCondition");
        final Tree.ExistsCondition node = super.existsCondition();
        end(marker, CeylonTypes.EXISTS_CONDITION, node);
        return node;
    }

    @Override
    public Tree.NonemptyCondition nonemptyCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("nonemptyCondition");
        final Tree.NonemptyCondition node = super.nonemptyCondition();
        end(marker, CeylonTypes.NONEMPTY_CONDITION, node);
        return node;
    }

    @Override
    public Tree.IsCondition isCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("isCondition");
        final Tree.IsCondition node = super.isCondition();
        end(marker, CeylonTypes.IS_CONDITION, node);
        return node;
    }

    @Override
    public Tree.SatisfiesCondition satisfiesCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("satisfiesCondition");
        final Tree.SatisfiesCondition node = super.satisfiesCondition();
        end(marker, CeylonTypes.SATISFIES_CONDITION, node);
        return node;
    }

    @Override
    public Tree.ControlStatement controlStatement() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("controlStatement");
        final Tree.ControlStatement node = super.controlStatement();
        end(marker, CeylonTypes.CONTROL_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.Block controlBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("controlBlock");
        final Tree.Block node = super.controlBlock();
        end(marker, CeylonTypes.BLOCK, node);
        return node;
    }

    @Override
    public Tree.DynamicStatement dynamic() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("dynamic");
        final Tree.DynamicStatement node = super.dynamic();
        end(marker, CeylonTypes.DYNAMIC_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.IfStatement ifElse() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("ifElse");
        final Tree.IfStatement node = super.ifElse();
        end(marker, CeylonTypes.IF_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.IfClause ifBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("ifBlock");
        final Tree.IfClause node = super.ifBlock();
        end(marker, CeylonTypes.IF_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ElseClause elseBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("elseBlock");
        final Tree.ElseClause node = super.elseBlock();
        end(marker, CeylonTypes.ELSE_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.Block elseIf() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("elseIf");
        final Tree.Block node = super.elseIf();
        end(marker, CeylonTypes.BLOCK, node);
        return node;
    }

    @Override
    public Tree.SwitchStatement switchCaseElse() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("switchCaseElse");
        final Tree.SwitchStatement node = super.switchCaseElse();
        end(marker, CeylonTypes.SWITCH_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.SwitchClause switchHeader() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("switchHeader");
        final Tree.SwitchClause node = super.switchHeader();
        end(marker, CeylonTypes.SWITCH_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.SwitchCaseList cases() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("cases");
        final Tree.SwitchCaseList node = super.cases();
        end(marker, CeylonTypes.SWITCH_CASE_LIST, node);
        return node;
    }

    @Override
    public Tree.CaseClause caseBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("caseBlock");
        final Tree.CaseClause node = super.caseBlock();
        end(marker, CeylonTypes.CASE_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.CaseItem caseItemList() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("caseItemList");
        final Tree.CaseItem node = super.caseItemList();
        end(marker, CeylonTypes.CASE_ITEM, node);
        return node;
    }

    @Override
    public Tree.ElseClause defaultCaseBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("defaultCaseBlock");
        final Tree.ElseClause node = super.defaultCaseBlock();
        end(marker, CeylonTypes.ELSE_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.CaseItem caseItem() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("caseItem");
        final Tree.CaseItem node = super.caseItem();
        end(marker, CeylonTypes.CASE_ITEM, node);
        return node;
    }

    @Override
    public Tree.MatchCase matchCaseCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("matchCaseCondition");
        final Tree.MatchCase node = super.matchCaseCondition();
        end(marker, CeylonTypes.MATCH_CASE, node);
        return node;
    }

    @Override
    public CustomTree.IsCase isCaseCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("isCaseCondition");
        final CustomTree.IsCase node = super.isCaseCondition();
        end(marker, CeylonTypes.IS_CASE, node);
        return node;
    }

    @Override
    public Tree.SatisfiesCase satisfiesCaseCondition() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("satisfiesCaseCondition");
        final Tree.SatisfiesCase node = super.satisfiesCaseCondition();
        end(marker, CeylonTypes.SATISFIES_CASE, node);
        return node;
    }

    @Override
    public Tree.ForStatement forElse() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("forElse");
        final Tree.ForStatement node = super.forElse();
        end(marker, CeylonTypes.FOR_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.ForClause forBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("forBlock");
        final Tree.ForClause node = super.forBlock();
        end(marker, CeylonTypes.FOR_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ElseClause failBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("failBlock");
        final Tree.ElseClause node = super.failBlock();
        end(marker, CeylonTypes.ELSE_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ForIterator forIterator() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("forIterator");
        final Tree.ForIterator node = super.forIterator();
        end(marker, CeylonTypes.FOR_ITERATOR, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression containment() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("containment");
        final Tree.SpecifierExpression node = super.containment();
        end(marker, CeylonTypes.SPECIFIER_EXPRESSION, node);
        return node;
    }

    @Override
    public Tree.WhileStatement whileLoop() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("whileLoop");
        final Tree.WhileStatement node = super.whileLoop();
        end(marker, CeylonTypes.WHILE_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.WhileClause whileBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("whileBlock");
        final Tree.WhileClause node = super.whileBlock();
        end(marker, CeylonTypes.WHILE_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.TryCatchStatement tryCatchFinally() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("tryCatchFinally");
        final Tree.TryCatchStatement node = super.tryCatchFinally();
        end(marker, CeylonTypes.TRY_CATCH_STATEMENT, node);
        return node;
    }

    @Override
    public Tree.TryClause tryBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("tryBlock");
        final Tree.TryClause node = super.tryBlock();
        end(marker, CeylonTypes.TRY_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.CatchClause catchBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("catchBlock");
        final Tree.CatchClause node = super.catchBlock();
        end(marker, CeylonTypes.CATCH_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.CatchVariable catchVariable() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("catchVariable");
        final Tree.CatchVariable node = super.catchVariable();
        end(marker, CeylonTypes.CATCH_VARIABLE, node);
        return node;
    }

    @Override
    public Tree.FinallyClause finallyBlock() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("finallyBlock");
        final Tree.FinallyClause node = super.finallyBlock();
        end(marker, CeylonTypes.FINALLY_CLAUSE, node);
        return node;
    }

    @Override
    public Tree.ResourceList resources() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("resources");
        final Tree.ResourceList node = super.resources();
        end(marker, CeylonTypes.RESOURCE_LIST, node);
        return node;
    }

    @Override
    public Tree.Resource resource() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("resource");
        final Tree.Resource node = super.resource();
        end(marker, CeylonTypes.RESOURCE, node);
        return node;
    }

    @Override
    public CustomTree.Variable specifiedVariable() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("specifiedVariable");
        final CustomTree.Variable node = super.specifiedVariable();
        end(marker, CeylonTypes.VARIABLE, node);
        return node;
    }

    @Override
    public CustomTree.Variable variable() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("variable");
        final CustomTree.Variable node = super.variable();
        end(marker, CeylonTypes.VARIABLE, node);
        return node;
    }

    @Override
    public CustomTree.Variable var() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("var");
        final CustomTree.Variable node = super.var();
        end(marker, CeylonTypes.VARIABLE, node);
        return node;
    }

    @Override
    public CustomTree.Variable impliedVariable() throws RecognitionException {
        final PsiBuilder.Marker marker = mark("impliedVariable");
        final CustomTree.Variable node = super.impliedVariable();
        end(marker, CeylonTypes.VARIABLE, node);
        return node;
    }


    ////////////////////////////////////////////////////////////////////////////////
    // synpreds


    @Override
    public void synpred1_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred1_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred2_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred2_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred3_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred3_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred4_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred4_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred5_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred5_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred6_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred6_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred7_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred7_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred8_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred8_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred9_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred9_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred10_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred10_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred11_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred11_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred12_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred12_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred13_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred13_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred14_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred14_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred15_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred15_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred16_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred16_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred17_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred17_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred18_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred18_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred19_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred19_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred20_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred20_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred21_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred21_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred22_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred22_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred23_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred23_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred24_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred24_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred25_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred25_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred26_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred26_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred27_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred27_Ceylon_fragment();
        marker.drop();
    }

/*
    // boolean synpreds rollback their own markers.

    @Override
    public boolean synpred24_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred24_Ceylon();
        marker.drop();
        return b;
    }

    @Override
    public boolean synpred25_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred25_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred13_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred13_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred9_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred9_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred10_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred10_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred15_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred15_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred7_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred7_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred1_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred1_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred16_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred16_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred12_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred12_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred2_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred2_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred20_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred20_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred6_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred6_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred23_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred23_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred11_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred11_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred14_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred14_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred8_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred8_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred18_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred18_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred27_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred27_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred4_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred4_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred21_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred21_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred22_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred22_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred26_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred26_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred3_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred3_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred17_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred17_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred5_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred5_Ceylon();
        marker.drop();
        return b;

    }

    @Override
    public boolean synpred19_Ceylon() {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        final boolean b = super.synpred19_Ceylon();
        marker.drop();
        return b;

    }
*/

    private void print(String format, Object... params) {
//        System.out.printf(format, params);
    }
}
