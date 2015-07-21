package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.ide.ceylonCode.parser.PsiCompatibleCeylonParser;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.NodeToIElementTypeMap;


public class MarkingCeylonParser extends PsiCompatibleCeylonParser {
    private PsiBuilder psiBuilder;
    private MyTree myTree = new MyTree();

    public MarkingCeylonParser(PsiBuilder psiBuilder) {
        super(new PsiBuilderTokenStream(psiBuilder));
        this.psiBuilder = psiBuilder;
    }

    public MyTree getMyTree() {
        return myTree;
    }

    MyTree.MyMarker mark() {
        return myTree.mark(psiBuilder.mark());
    }

    void end(MyTree.MyMarker marker, Node node) {
        if (node == null) {
            marker.drop();
        } else {
            IElementType elementType = NodeToIElementTypeMap.get(node);
            marker.done(elementType, node);
        }
    }

    @Override
    public ParseError newParseError(String[] tn, RecognitionException re) {
        ParseError parseError = super.newParseError(tn, re);
        error(parseError);
        return parseError;
    }

    @Override
    public ParseError newParseError(String[] tn, RecognitionException re, int code) {
        ParseError parseError = super.newParseError(tn, re, code);
        error(parseError);
        return parseError;
    }

    private void error(ParseError parseError) {
        psiBuilder.error(parseError.getMessage());
        myTree.error();
    }

    @Override
    public Tree.ImportMemberOrTypeList importElementList() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportMemberOrTypeList node = super.importElementList();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Import importDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Import node = super.importDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ImportMemberOrType importElement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportMemberOrType node = super.importElement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ImportModuleList importModuleList() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportModuleList node = super.importModuleList();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ImportPath packagePath() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportPath node = super.packagePath();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AnyClass classDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AnyClass node = super.classDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AnyMethod voidOrInferredMethodDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AnyMethod node = super.voidOrInferredMethodDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CompilationUnit compilationUnit() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CompilationUnit node = super.compilationUnit();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.InitializerParameter parameterRef() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.InitializerParameter node = super.parameterRef();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ModuleDescriptor moduleDescriptor() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ModuleDescriptor node = super.moduleDescriptor();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.PackageDescriptor packageDescriptor() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.PackageDescriptor node = super.packageDescriptor();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ImportModule importModule() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportModule node = super.importModule();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ImportWildcard importWildcard() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ImportWildcard node = super.importWildcard();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier importName() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.importName();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier packageName() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.packageName();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier typeName() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.typeName();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Declaration toplevelDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Declaration node = super.toplevelDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ObjectExpression objectExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ObjectExpression node = super.objectExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Pattern variableOrTuplePattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Pattern node = super.variableOrTuplePattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Pattern pattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Pattern node = super.pattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Pattern tupleOrEntryPattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Pattern node = super.tupleOrEntryPattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.VariablePattern variablePattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.VariablePattern node = super.variablePattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TuplePattern tuplePattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TuplePattern node = super.tuplePattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Pattern variadicPattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Pattern node = super.variadicPattern();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable variadicVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.variadicVariable();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.KeyValuePattern keyItemPattern() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.KeyValuePattern node = super.keyItemPattern();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Destructure destructure() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Destructure node = super.destructure();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Constructor constructor() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Constructor node = super.constructor();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.DelegatedConstructor delegatedConstructor() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.DelegatedConstructor node = super.delegatedConstructor();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypedDeclaration parameterDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypedDeclaration node = super.parameterDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ExpressionList valueCaseList() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ExpressionList node = super.valueCaseList();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.LetExpression let() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.LetExpression node = super.let();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Statement letVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Statement node = super.letVariable();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.LetClause letClause() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.LetClause node = super.letClause();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SwitchExpression switchExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SwitchExpression node = super.switchExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SwitchCaseList caseExpressions() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SwitchCaseList node = super.caseExpressions();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CaseClause caseExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CaseClause node = super.caseExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ElseClause defaultCaseExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ElseClause node = super.defaultCaseExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IfExpression ifExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IfExpression node = super.ifExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Expression conditionalBranch() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Expression node = super.conditionalBranch();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Type spreadType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Type node = super.spreadType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StaticType baseType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StaticType node = super.baseType();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable isConditionVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.isConditionVariable();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Switched switched() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Switched node = super.switched();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier annotationName() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.annotationName();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier memberName() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.memberName();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier memberNameDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.memberNameDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Identifier typeNameDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Identifier node = super.typeNameDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ObjectDefinition objectDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ObjectDefinition node = super.objectDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AttributeSetterDefinition setterDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AttributeSetterDefinition node = super.setterDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AnyAttribute inferredAttributeDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AnyAttribute node = super.inferredAttributeDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypedDeclaration typedMethodOrAttributeDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypedDeclaration node = super.typedMethodOrAttributeDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AnyInterface interfaceDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AnyInterface node = super.interfaceDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeAliasDeclaration aliasDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeAliasDeclaration node = super.aliasDeclaration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Assertion assertion() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Assertion node = super.assertion();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Block block() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Block node = super.block();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.InterfaceBody interfaceBody() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.InterfaceBody node = super.interfaceBody();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ClassBody classBody() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ClassBody node = super.classBody();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ExtendedType extendedType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ExtendedType node = super.extendedType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ClassSpecifier classSpecifier() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ClassSpecifier node = super.classSpecifier();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.SatisfiedTypes satisfiedTypes() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SatisfiedTypes node = super.satisfiedTypes();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AbstractedType abstractedType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AbstractedType node = super.abstractedType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CaseTypes caseTypes() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CaseTypes node = super.caseTypes();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.ParameterList parameters() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ParameterList node = super.parameters();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ParameterDeclaration parameter() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ParameterDeclaration node = super.parameter();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Parameter parameterDeclarationOrRef() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Parameter node = super.parameterDeclarationOrRef();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeParameterList typeParameters() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeParameterList node = super.typeParameters();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeParameterDeclaration typeParameter() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeParameterDeclaration node = super.typeParameter();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeVariance variance() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeVariance node = super.variance();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeConstraint typeConstraint() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeConstraint node = super.typeConstraint();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeConstraintList typeConstraints() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeConstraintList node = super.typeConstraints();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.Statement declarationOrStatement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Statement node = super.declarationOrStatement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Declaration declaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Declaration node = super.declaration();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.Statement statement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Statement node = super.statement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Statement expressionOrSpecificationStatement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Statement node = super.expressionOrSpecificationStatement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Directive directiveStatement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Directive node = super.directiveStatement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Directive directive() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Directive node = super.directive();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Return returnDirective() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Return node = super.returnDirective();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Throw throwDirective() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Throw node = super.throwDirective();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Break breakDirective() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Break node = super.breakDirective();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Continue continueDirective() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Continue node = super.continueDirective();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeSpecifier typeSpecifier() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeSpecifier node = super.typeSpecifier();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeSpecifier typeDefault() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeSpecifier node = super.typeDefault();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression specifier() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifierExpression node = super.specifier();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression lazySpecifier() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifierExpression node = super.lazySpecifier();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression functionSpecifier() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifierExpression node = super.functionSpecifier();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Expression expression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Expression node = super.expression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Primary base() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Primary node = super.base();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.Primary primary() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Primary node = super.primary();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.MemberOperator memberSelectionOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.MemberOperator node = super.memberSelectionOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SequenceEnumeration enumeration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SequenceEnumeration node = super.enumeration();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Tuple tuple() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Tuple node = super.tuple();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Dynamic dynamicObject() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Dynamic node = super.dynamicObject();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IndexExpression indexOrIndexRange() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IndexExpression node = super.indexOrIndexRange();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Expression index() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Expression node = super.index();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.NamedArgumentList namedArguments() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.NamedArgumentList node = super.namedArguments();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SequencedArgument sequencedArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SequencedArgument node = super.sequencedArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.NamedArgument namedArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.NamedArgument node = super.namedArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifiedArgument namedSpecifiedArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifiedArgument node = super.namedSpecifiedArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifiedArgument anonymousArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifiedArgument node = super.anonymousArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ObjectArgument objectArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ObjectArgument node = super.objectArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.MethodArgument voidOrInferredMethodArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.MethodArgument node = super.voidOrInferredMethodArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AttributeArgument inferredGetterArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AttributeArgument node = super.inferredGetterArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypedArgument typedMethodOrGetterArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypedArgument node = super.typedMethodOrGetterArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypedArgument untypedMethodOrGetterArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypedArgument node = super.untypedMethodOrGetterArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.NamedArgument namedArgumentDeclaration() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.NamedArgument node = super.namedArgumentDeclaration();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.Expression parExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Expression node = super.parExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.PositionalArgumentList positionalArguments() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.PositionalArgumentList node = super.positionalArguments();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ListedArgument positionalArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ListedArgument node = super.positionalArgument();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpreadArgument spreadArgument() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpreadArgument node = super.spreadArgument();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.Expression functionOrExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Expression node = super.functionOrExpression();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.FunctionArgument anonymousFunction() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.FunctionArgument node = super.anonymousFunction();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Comprehension comprehension() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Comprehension node = super.comprehension();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ComprehensionClause comprehensionClause() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ComprehensionClause node = super.comprehensionClause();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ExpressionComprehensionClause expressionComprehensionClause() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ExpressionComprehensionClause node = super.expressionComprehensionClause();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ForComprehensionClause forComprehensionClause() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ForComprehensionClause node = super.forComprehensionClause();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IfComprehensionClause ifComprehensionClause() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IfComprehensionClause node = super.ifComprehensionClause();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term assignmentExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.assignmentExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AssignmentOp assignmentOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AssignmentOp node = super.assignmentOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term thenElseExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.thenElseExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression thenElseOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.thenElseOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term disjunctionExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.disjunctionExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.OrOp disjunctionOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.OrOp node = super.disjunctionOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term conjunctionExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.conjunctionExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.AndOp conjunctionOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AndOp node = super.conjunctionOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term logicalNegationExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.logicalNegationExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.NotOp notOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.NotOp node = super.notOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term equalityExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.equalityExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression equalityOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.equalityOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term comparisonExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.comparisonExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ComparisonOp smallerOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ComparisonOp node = super.smallerOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ComparisonOp largerOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ComparisonOp node = super.largerOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression comparisonOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.comparisonOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeOperatorExpression typeOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeOperatorExpression node = super.typeOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term existenceEmptinessExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.existenceEmptinessExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.UnaryOperatorExpression existsNonemptyOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.UnaryOperatorExpression node = super.existsNonemptyOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term entryRangeExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.entryRangeExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression rangeIntervalEntryOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.rangeIntervalEntryOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term additiveExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.additiveExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression additiveOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.additiveOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term scaleExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.scaleExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term multiplicativeExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.multiplicativeExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression multiplicativeOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.multiplicativeOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term unionExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.unionExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression unionOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.unionOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term intersectionExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.intersectionExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BinaryOperatorExpression intersectionOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BinaryOperatorExpression node = super.intersectionOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term negationComplementExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.negationComplementExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.UnaryOperatorExpression unaryMinusOrComplementOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.UnaryOperatorExpression node = super.unaryMinusOrComplementOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term exponentiationExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.exponentiationExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.PowerOp exponentiationOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.PowerOp node = super.exponentiationOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ScaleOp scaleOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ScaleOp node = super.scaleOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term incrementDecrementExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.incrementDecrementExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.PrefixOperatorExpression prefixOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.PrefixOperatorExpression node = super.prefixOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Term postfixIncrementDecrementExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Term node = super.postfixIncrementDecrementExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.PostfixOperatorExpression postfixOperator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.PostfixOperatorExpression node = super.postfixOperator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Atom selfReference() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Atom node = super.selfReference();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Literal nonstringLiteral() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Literal node = super.nonstringLiteral();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StringLiteral stringLiteral() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StringLiteral node = super.stringLiteral();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Atom stringExpression() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Atom node = super.stringExpression();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TypeArgumentList typeArguments() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TypeArgumentList node = super.typeArguments();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Type variadicType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Type node = super.variadicType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Type defaultedType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Type node = super.defaultedType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TupleType tupleType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TupleType node = super.tupleType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.GroupedType groupedType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.GroupedType node = super.groupedType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IterableType iterableType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IterableType node = super.iterableType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.MetaLiteral metaLiteral() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.MetaLiteral node = super.metaLiteral();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StaticType type() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StaticType node = super.type();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StaticType unionType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StaticType node = super.unionType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StaticType intersectionType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StaticType node = super.intersectionType();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.StaticType qualifiedType() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.StaticType node = super.qualifiedType();
        end(marker, node);
        return node;
    }


    @Override
    public Tree.AnnotationList annotations() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.AnnotationList node = super.annotations();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Annotation annotation() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Annotation node = super.annotation();
        end(marker, node);
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
        final MyTree.MyMarker marker = mark();
        final Tree.CompilerAnnotation node = super.compilerAnnotation();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ConditionList conditions() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ConditionList node = super.conditions();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Condition condition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Condition node = super.condition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.BooleanCondition booleanCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.BooleanCondition node = super.booleanCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ExistsCondition existsCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ExistsCondition node = super.existsCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.NonemptyCondition nonemptyCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.NonemptyCondition node = super.nonemptyCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IsCondition isCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IsCondition node = super.isCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SatisfiesCondition satisfiesCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SatisfiesCondition node = super.satisfiesCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ControlStatement controlStatement() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ControlStatement node = super.controlStatement();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Block controlBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Block node = super.controlBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.DynamicStatement dynamic() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.DynamicStatement node = super.dynamic();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IfStatement ifElse() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IfStatement node = super.ifElse();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.IfClause ifBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.IfClause node = super.ifBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ElseClause elseBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ElseClause node = super.elseBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Block elseIf() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Block node = super.elseIf();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SwitchStatement switchCaseElse() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SwitchStatement node = super.switchCaseElse();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SwitchClause switchHeader() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SwitchClause node = super.switchHeader();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SwitchCaseList cases() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SwitchCaseList node = super.cases();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CaseClause caseBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CaseClause node = super.caseBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CaseItem caseItemList() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CaseItem node = super.caseItemList();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ElseClause defaultCaseBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ElseClause node = super.defaultCaseBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CaseItem caseItem() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CaseItem node = super.caseItem();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.MatchCase matchCaseCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.MatchCase node = super.matchCaseCondition();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.IsCase isCaseCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.IsCase node = super.isCaseCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SatisfiesCase satisfiesCaseCondition() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SatisfiesCase node = super.satisfiesCaseCondition();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ForStatement forElse() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ForStatement node = super.forElse();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ForClause forBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ForClause node = super.forBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ElseClause failBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ElseClause node = super.failBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ForIterator forIterator() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ForIterator node = super.forIterator();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.SpecifierExpression containment() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.SpecifierExpression node = super.containment();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.WhileStatement whileLoop() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.WhileStatement node = super.whileLoop();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.WhileClause whileBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.WhileClause node = super.whileBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TryCatchStatement tryCatchFinally() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TryCatchStatement node = super.tryCatchFinally();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.TryClause tryBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.TryClause node = super.tryBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CatchClause catchBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CatchClause node = super.catchBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.CatchVariable catchVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.CatchVariable node = super.catchVariable();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.FinallyClause finallyBlock() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.FinallyClause node = super.finallyBlock();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.ResourceList resources() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.ResourceList node = super.resources();
        end(marker, node);
        return node;
    }

    @Override
    public Tree.Resource resource() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final Tree.Resource node = super.resource();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable specifiedVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.specifiedVariable();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable variable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.variable();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable var() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.var();
        end(marker, node);
        return node;
    }

    @Override
    public CustomTree.Variable impliedVariable() throws RecognitionException {
        final MyTree.MyMarker marker = mark();
        final CustomTree.Variable node = super.impliedVariable();
        end(marker, node);
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

    @Override
    public void synpred28_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred28_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred29_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred29_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred30_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred30_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred31_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred31_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred32_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred32_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred33_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred33_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred34_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred34_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred35_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred35_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred36_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred36_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred37_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred37_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred38_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred38_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred39_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred39_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred40_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred40_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred41_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred41_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred42_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred42_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred43_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred43_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred44_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred44_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred45_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred45_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred46_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred46_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred47_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred47_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred48_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred48_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred49_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred49_Ceylon_fragment();
        marker.drop();
    }

    @Override
    public void synpred50_Ceylon_fragment() throws RecognitionException {
        final PsiBuilder.Marker marker = psiBuilder.mark();
        super.synpred50_Ceylon_fragment();
        marker.drop();
    }
}

