// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.parser;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;
import static org.intellij.plugins.ceylon.parser.CeylonParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CeylonParser implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("org.intellij.plugins.ceylon.parser.CeylonParser");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == ABBREVIATED_TYPE) {
      result_ = abbreviatedType(builder_, level_ + 1);
    }
    else if (root_ == ABSTRACTED_TYPE) {
      result_ = abstractedType(builder_, level_ + 1);
    }
    else if (root_ == ADAPTED_TYPES) {
      result_ = adaptedTypes(builder_, level_ + 1);
    }
    else if (root_ == ADDITIVE_EXPRESSION) {
      result_ = additiveExpression(builder_, level_ + 1);
    }
    else if (root_ == ADDITIVE_OPERATOR) {
      result_ = additiveOperator(builder_, level_ + 1);
    }
    else if (root_ == ALIAS_DECLARATION) {
      result_ = aliasDeclaration(builder_, level_ + 1);
    }
    else if (root_ == ANNOTATION) {
      result_ = annotation(builder_, level_ + 1);
    }
    else if (root_ == ANNOTATION_ARGUMENTS) {
      result_ = annotationArguments(builder_, level_ + 1);
    }
    else if (root_ == ANNOTATION_NAME) {
      result_ = annotationName(builder_, level_ + 1);
    }
    else if (root_ == ANNOTATIONS) {
      result_ = annotations(builder_, level_ + 1);
    }
    else if (root_ == ARGUMENTS) {
      result_ = arguments(builder_, level_ + 1);
    }
    else if (root_ == ASSERTION) {
      result_ = assertion(builder_, level_ + 1);
    }
    else if (root_ == ASSIGNMENT_EXPRESSION) {
      result_ = assignmentExpression(builder_, level_ + 1);
    }
    else if (root_ == ASSIGNMENT_OPERATOR) {
      result_ = assignmentOperator(builder_, level_ + 1);
    }
    else if (root_ == BASE) {
      result_ = base(builder_, level_ + 1);
    }
    else if (root_ == BASE_REFERENCE) {
      result_ = baseReference(builder_, level_ + 1);
    }
    else if (root_ == BLOCK) {
      result_ = block(builder_, level_ + 1);
    }
    else if (root_ == BOOLEAN_CONDITION) {
      result_ = booleanCondition(builder_, level_ + 1);
    }
    else if (root_ == BREAK_DIRECTIVE) {
      result_ = breakDirective(builder_, level_ + 1);
    }
    else if (root_ == CASE_BLOCK) {
      result_ = caseBlock(builder_, level_ + 1);
    }
    else if (root_ == CASE_ITEM) {
      result_ = caseItem(builder_, level_ + 1);
    }
    else if (root_ == CASE_TYPE) {
      result_ = caseType(builder_, level_ + 1);
    }
    else if (root_ == CASE_TYPES) {
      result_ = caseTypes(builder_, level_ + 1);
    }
    else if (root_ == CASES) {
      result_ = cases(builder_, level_ + 1);
    }
    else if (root_ == CATCH_BLOCK) {
      result_ = catchBlock(builder_, level_ + 1);
    }
    else if (root_ == CATCH_VARIABLE) {
      result_ = catchVariable(builder_, level_ + 1);
    }
    else if (root_ == CLASS_BODY) {
      result_ = classBody(builder_, level_ + 1);
    }
    else if (root_ == CLASS_DECLARATION) {
      result_ = classDeclaration(builder_, level_ + 1);
    }
    else if (root_ == COMPARABLE_TYPE) {
      result_ = comparableType(builder_, level_ + 1);
    }
    else if (root_ == COMPARISON_EXPRESSION) {
      result_ = comparisonExpression(builder_, level_ + 1);
    }
    else if (root_ == COMPARISON_OPERATOR) {
      result_ = comparisonOperator(builder_, level_ + 1);
    }
    else if (root_ == COMPILATION_UNIT) {
      result_ = compilationUnit(builder_, level_ + 1);
    }
    else if (root_ == COMPILER_ANNOTATION) {
      result_ = compilerAnnotation(builder_, level_ + 1);
    }
    else if (root_ == COMPILER_ANNOTATIONS) {
      result_ = compilerAnnotations(builder_, level_ + 1);
    }
    else if (root_ == COMPREHENSION) {
      result_ = comprehension(builder_, level_ + 1);
    }
    else if (root_ == COMPREHENSION_CLAUSE) {
      result_ = comprehensionClause(builder_, level_ + 1);
    }
    else if (root_ == CONDITION) {
      result_ = condition(builder_, level_ + 1);
    }
    else if (root_ == CONDITIONS) {
      result_ = conditions(builder_, level_ + 1);
    }
    else if (root_ == CONJUNCTION_EXPRESSION) {
      result_ = conjunctionExpression(builder_, level_ + 1);
    }
    else if (root_ == CONJUNCTION_OPERATOR) {
      result_ = conjunctionOperator(builder_, level_ + 1);
    }
    else if (root_ == CONTAINMENT) {
      result_ = containment(builder_, level_ + 1);
    }
    else if (root_ == CONTINUE_DIRECTIVE) {
      result_ = continueDirective(builder_, level_ + 1);
    }
    else if (root_ == CONTROL_BLOCK) {
      result_ = controlBlock(builder_, level_ + 1);
    }
    else if (root_ == CONTROL_STATEMENT) {
      result_ = controlStatement(builder_, level_ + 1);
    }
    else if (root_ == DECLARATION) {
      result_ = declaration(builder_, level_ + 1);
    }
    else if (root_ == DECLARATION_OR_STATEMENT) {
      result_ = declarationOrStatement(builder_, level_ + 1);
    }
    else if (root_ == DEFAULT_CASE_BLOCK) {
      result_ = defaultCaseBlock(builder_, level_ + 1);
    }
    else if (root_ == DEFAULT_EXPRESSION) {
      result_ = defaultExpression(builder_, level_ + 1);
    }
    else if (root_ == DEFAULT_OPERATOR) {
      result_ = defaultOperator(builder_, level_ + 1);
    }
    else if (root_ == DIRECTIVE) {
      result_ = directive(builder_, level_ + 1);
    }
    else if (root_ == DIRECTIVE_STATEMENT) {
      result_ = directiveStatement(builder_, level_ + 1);
    }
    else if (root_ == DISJUNCTION_EXPRESSION) {
      result_ = disjunctionExpression(builder_, level_ + 1);
    }
    else if (root_ == DISJUNCTION_OPERATOR) {
      result_ = disjunctionOperator(builder_, level_ + 1);
    }
    else if (root_ == ELEMENT_SELECTION_OPERATOR) {
      result_ = elementSelectionOperator(builder_, level_ + 1);
    }
    else if (root_ == ELSE_BLOCK) {
      result_ = elseBlock(builder_, level_ + 1);
    }
    else if (root_ == ELSE_IF) {
      result_ = elseIf(builder_, level_ + 1);
    }
    else if (root_ == ENTRY_TYPE) {
      result_ = entryType(builder_, level_ + 1);
    }
    else if (root_ == ENUMERATION) {
      result_ = enumeration(builder_, level_ + 1);
    }
    else if (root_ == EQUALITY_EXPRESSION) {
      result_ = equalityExpression(builder_, level_ + 1);
    }
    else if (root_ == EQUALITY_OPERATOR) {
      result_ = equalityOperator(builder_, level_ + 1);
    }
    else if (root_ == EXISTENCE_EMPTINESS_EXPRESSION) {
      result_ = existenceEmptinessExpression(builder_, level_ + 1);
    }
    else if (root_ == EXISTS_CONDITION) {
      result_ = existsCondition(builder_, level_ + 1);
    }
    else if (root_ == EXISTS_NONEMPTY_OPERATOR) {
      result_ = existsNonemptyOperator(builder_, level_ + 1);
    }
    else if (root_ == EXPONENTIATION_EXPRESSION) {
      result_ = exponentiationExpression(builder_, level_ + 1);
    }
    else if (root_ == EXPONENTIATION_OPERATOR) {
      result_ = exponentiationOperator(builder_, level_ + 1);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, level_ + 1);
    }
    else if (root_ == EXPRESSION_COMPREHENSION_CLAUSE) {
      result_ = expressionComprehensionClause(builder_, level_ + 1);
    }
    else if (root_ == EXPRESSION_OR_SPECIFICATION_STATEMENT) {
      result_ = expressionOrSpecificationStatement(builder_, level_ + 1);
    }
    else if (root_ == EXPRESSIONS) {
      result_ = expressions(builder_, level_ + 1);
    }
    else if (root_ == EXTENDED_TYPE) {
      result_ = extendedType(builder_, level_ + 1);
    }
    else if (root_ == FAIL_BLOCK) {
      result_ = failBlock(builder_, level_ + 1);
    }
    else if (root_ == FINALLY_BLOCK) {
      result_ = finallyBlock(builder_, level_ + 1);
    }
    else if (root_ == FOR_BLOCK) {
      result_ = forBlock(builder_, level_ + 1);
    }
    else if (root_ == FOR_COMPREHENSION_CLAUSE) {
      result_ = forComprehensionClause(builder_, level_ + 1);
    }
    else if (root_ == FOR_ELSE) {
      result_ = forElse(builder_, level_ + 1);
    }
    else if (root_ == FOR_ITERATOR) {
      result_ = forIterator(builder_, level_ + 1);
    }
    else if (root_ == FUNCTION_OR_EXPRESSION) {
      result_ = functionOrExpression(builder_, level_ + 1);
    }
    else if (root_ == IF_BLOCK) {
      result_ = ifBlock(builder_, level_ + 1);
    }
    else if (root_ == IF_COMPREHENSION_CLAUSE) {
      result_ = ifComprehensionClause(builder_, level_ + 1);
    }
    else if (root_ == IF_ELSE) {
      result_ = ifElse(builder_, level_ + 1);
    }
    else if (root_ == IMPLIED_VARIABLE) {
      result_ = impliedVariable(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_DECLARATION) {
      result_ = importDeclaration(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_ELEMENT) {
      result_ = importElement(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_ELEMENT_LIST) {
      result_ = importElementList(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_LIST) {
      result_ = importList(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_MODULE) {
      result_ = importModule(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_MODULE_LIST) {
      result_ = importModuleList(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_NAME) {
      result_ = importName(builder_, level_ + 1);
    }
    else if (root_ == IMPORT_WILDCARD) {
      result_ = importWildcard(builder_, level_ + 1);
    }
    else if (root_ == INCREMENT_DECREMENT_EXPRESSION) {
      result_ = incrementDecrementExpression(builder_, level_ + 1);
    }
    else if (root_ == INDEX) {
      result_ = index(builder_, level_ + 1);
    }
    else if (root_ == INDEX_OR_INDEX_RANGE) {
      result_ = indexOrIndexRange(builder_, level_ + 1);
    }
    else if (root_ == INFERRED_ATTRIBUTE_DECLARATION) {
      result_ = inferredAttributeDeclaration(builder_, level_ + 1);
    }
    else if (root_ == INFERRED_GETTER_ARGUMENT) {
      result_ = inferredGetterArgument(builder_, level_ + 1);
    }
    else if (root_ == INITIALIZER) {
      result_ = initializer(builder_, level_ + 1);
    }
    else if (root_ == INTERFACE_BODY) {
      result_ = interfaceBody(builder_, level_ + 1);
    }
    else if (root_ == INTERFACE_DECLARATION) {
      result_ = interfaceDeclaration(builder_, level_ + 1);
    }
    else if (root_ == INTERSECTION_TYPE) {
      result_ = intersectionType(builder_, level_ + 1);
    }
    else if (root_ == INTERSECTION_TYPE_EXPRESSION) {
      result_ = intersectionTypeExpression(builder_, level_ + 1);
    }
    else if (root_ == IS_CASE_CONDITION) {
      result_ = isCaseCondition(builder_, level_ + 1);
    }
    else if (root_ == IS_CONDITION) {
      result_ = isCondition(builder_, level_ + 1);
    }
    else if (root_ == LITERAL_ARGUMENT) {
      result_ = literalArgument(builder_, level_ + 1);
    }
    else if (root_ == LITERAL_ARGUMENTS) {
      result_ = literalArguments(builder_, level_ + 1);
    }
    else if (root_ == LOGICAL_NEGATION_EXPRESSION) {
      result_ = logicalNegationExpression(builder_, level_ + 1);
    }
    else if (root_ == MATCH_CASE_CONDITION) {
      result_ = matchCaseCondition(builder_, level_ + 1);
    }
    else if (root_ == MEMBER_NAME) {
      result_ = memberName(builder_, level_ + 1);
    }
    else if (root_ == MEMBER_NAME_DECLARATION) {
      result_ = memberNameDeclaration(builder_, level_ + 1);
    }
    else if (root_ == MEMBER_REFERENCE) {
      result_ = memberReference(builder_, level_ + 1);
    }
    else if (root_ == MEMBER_SELECTION_OPERATOR) {
      result_ = memberSelectionOperator(builder_, level_ + 1);
    }
    else if (root_ == MODULE_DESCRIPTOR) {
      result_ = moduleDescriptor(builder_, level_ + 1);
    }
    else if (root_ == MULTIPLICATIVE_EXPRESSION) {
      result_ = multiplicativeExpression(builder_, level_ + 1);
    }
    else if (root_ == MULTIPLICATIVE_OPERATOR) {
      result_ = multiplicativeOperator(builder_, level_ + 1);
    }
    else if (root_ == MY_STRING_LITERAL) {
      result_ = myStringLiteral(builder_, level_ + 1);
    }
    else if (root_ == NAMED_ARGUMENT) {
      result_ = namedArgument(builder_, level_ + 1);
    }
    else if (root_ == NAMED_ARGUMENT_DECLARATION) {
      result_ = namedArgumentDeclaration(builder_, level_ + 1);
    }
    else if (root_ == NAMED_ARGUMENTS) {
      result_ = namedArguments(builder_, level_ + 1);
    }
    else if (root_ == NAMED_SPECIFIED_ARGUMENT) {
      result_ = namedSpecifiedArgument(builder_, level_ + 1);
    }
    else if (root_ == NEGATION_COMPLEMENT_EXPRESSION) {
      result_ = negationComplementExpression(builder_, level_ + 1);
    }
    else if (root_ == NONEMPTY_CONDITION) {
      result_ = nonemptyCondition(builder_, level_ + 1);
    }
    else if (root_ == NONSTRING_LITERAL) {
      result_ = nonstringLiteral(builder_, level_ + 1);
    }
    else if (root_ == NOT_OPERATOR) {
      result_ = notOperator(builder_, level_ + 1);
    }
    else if (root_ == OBJECT_ARGUMENT) {
      result_ = objectArgument(builder_, level_ + 1);
    }
    else if (root_ == OBJECT_DECLARATION) {
      result_ = objectDeclaration(builder_, level_ + 1);
    }
    else if (root_ == PACKAGE_DESCRIPTOR) {
      result_ = packageDescriptor(builder_, level_ + 1);
    }
    else if (root_ == PACKAGE_NAME) {
      result_ = packageName(builder_, level_ + 1);
    }
    else if (root_ == PACKAGE_PATH) {
      result_ = packagePath(builder_, level_ + 1);
    }
    else if (root_ == PAR_EXPRESSION) {
      result_ = parExpression(builder_, level_ + 1);
    }
    else if (root_ == PARAMETER) {
      result_ = parameter(builder_, level_ + 1);
    }
    else if (root_ == PARAMETER_DECLARATION) {
      result_ = parameterDeclaration(builder_, level_ + 1);
    }
    else if (root_ == PARAMETER_REF) {
      result_ = parameterRef(builder_, level_ + 1);
    }
    else if (root_ == PARAMETER_TYPE) {
      result_ = parameterType(builder_, level_ + 1);
    }
    else if (root_ == PARAMETERS) {
      result_ = parameters(builder_, level_ + 1);
    }
    else if (root_ == POSITIONAL_ARGUMENT) {
      result_ = positionalArgument(builder_, level_ + 1);
    }
    else if (root_ == POSITIONAL_ARGUMENTS) {
      result_ = positionalArguments(builder_, level_ + 1);
    }
    else if (root_ == POSTFIX_INCREMENT_DECREMENT_EXPRESSION) {
      result_ = postfixIncrementDecrementExpression(builder_, level_ + 1);
    }
    else if (root_ == POSTFIX_OPERATOR) {
      result_ = postfixOperator(builder_, level_ + 1);
    }
    else if (root_ == PREFIX_OPERATOR) {
      result_ = prefixOperator(builder_, level_ + 1);
    }
    else if (root_ == PRIMARY) {
      result_ = primary(builder_, level_ + 1);
    }
    else if (root_ == QUALIFIED_REFERENCE) {
      result_ = qualifiedReference(builder_, level_ + 1);
    }
    else if (root_ == QUALIFIED_TYPE) {
      result_ = qualifiedType(builder_, level_ + 1);
    }
    else if (root_ == RANGE_INTERVAL_ENTRY_EXPRESSION) {
      result_ = rangeIntervalEntryExpression(builder_, level_ + 1);
    }
    else if (root_ == RANGE_INTERVAL_ENTRY_OPERATOR) {
      result_ = rangeIntervalEntryOperator(builder_, level_ + 1);
    }
    else if (root_ == RESOURCE) {
      result_ = resource(builder_, level_ + 1);
    }
    else if (root_ == RETURN_DIRECTIVE) {
      result_ = returnDirective(builder_, level_ + 1);
    }
    else if (root_ == SATISFIED_TYPES) {
      result_ = satisfiedTypes(builder_, level_ + 1);
    }
    else if (root_ == SATISFIES_CASE_CONDITION) {
      result_ = satisfiesCaseCondition(builder_, level_ + 1);
    }
    else if (root_ == SATISFIES_CONDITION) {
      result_ = satisfiesCondition(builder_, level_ + 1);
    }
    else if (root_ == SELF_REFERENCE) {
      result_ = selfReference(builder_, level_ + 1);
    }
    else if (root_ == SEQUENCED_ARGUMENT) {
      result_ = sequencedArgument(builder_, level_ + 1);
    }
    else if (root_ == SETTER_DECLARATION) {
      result_ = setterDeclaration(builder_, level_ + 1);
    }
    else if (root_ == SPECIFIED_VARIABLE) {
      result_ = specifiedVariable(builder_, level_ + 1);
    }
    else if (root_ == SPECIFIER) {
      result_ = specifier(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
    }
    else if (root_ == STRING_EXPRESSION) {
      result_ = stringExpression(builder_, level_ + 1);
    }
    else if (root_ == STRING_TEMPLATE) {
      result_ = stringTemplate(builder_, level_ + 1);
    }
    else if (root_ == SUPERTYPE_QUALIFIER) {
      result_ = supertypeQualifier(builder_, level_ + 1);
    }
    else if (root_ == SWITCH_CASE_ELSE) {
      result_ = switchCaseElse(builder_, level_ + 1);
    }
    else if (root_ == SWITCH_HEADER) {
      result_ = switchHeader(builder_, level_ + 1);
    }
    else if (root_ == THEN_ELSE_EXPRESSION) {
      result_ = thenElseExpression(builder_, level_ + 1);
    }
    else if (root_ == THEN_ELSE_OPERATOR) {
      result_ = thenElseOperator(builder_, level_ + 1);
    }
    else if (root_ == THROW_DIRECTIVE) {
      result_ = throwDirective(builder_, level_ + 1);
    }
    else if (root_ == TRY_BLOCK) {
      result_ = tryBlock(builder_, level_ + 1);
    }
    else if (root_ == TRY_CATCH_FINALLY) {
      result_ = tryCatchFinally(builder_, level_ + 1);
    }
    else if (root_ == TYPE) {
      result_ = type(builder_, level_ + 1);
    }
    else if (root_ == TYPE_ARGUMENT) {
      result_ = typeArgument(builder_, level_ + 1);
    }
    else if (root_ == TYPE_ARGUMENTS) {
      result_ = typeArguments(builder_, level_ + 1);
    }
    else if (root_ == TYPE_CONSTRAINT) {
      result_ = typeConstraint(builder_, level_ + 1);
    }
    else if (root_ == TYPE_CONSTRAINTS) {
      result_ = typeConstraints(builder_, level_ + 1);
    }
    else if (root_ == TYPE_NAME) {
      result_ = typeName(builder_, level_ + 1);
    }
    else if (root_ == TYPE_NAME_DECLARATION) {
      result_ = typeNameDeclaration(builder_, level_ + 1);
    }
    else if (root_ == TYPE_NAME_WITH_ARGUMENTS) {
      result_ = typeNameWithArguments(builder_, level_ + 1);
    }
    else if (root_ == TYPE_OPERATOR) {
      result_ = typeOperator(builder_, level_ + 1);
    }
    else if (root_ == TYPE_PARAMETER) {
      result_ = typeParameter(builder_, level_ + 1);
    }
    else if (root_ == TYPE_PARAMETERS) {
      result_ = typeParameters(builder_, level_ + 1);
    }
    else if (root_ == TYPE_REFERENCE) {
      result_ = typeReference(builder_, level_ + 1);
    }
    else if (root_ == TYPE_SPECIFIER) {
      result_ = typeSpecifier(builder_, level_ + 1);
    }
    else if (root_ == TYPED_METHOD_OR_ATTRIBUTE_DECLARATION) {
      result_ = typedMethodOrAttributeDeclaration(builder_, level_ + 1);
    }
    else if (root_ == TYPED_METHOD_OR_GETTER_ARGUMENT) {
      result_ = typedMethodOrGetterArgument(builder_, level_ + 1);
    }
    else if (root_ == UNARY_MINUS_OR_COMPLEMENT_OPERATOR) {
      result_ = unaryMinusOrComplementOperator(builder_, level_ + 1);
    }
    else if (root_ == UNION_TYPE) {
      result_ = unionType(builder_, level_ + 1);
    }
    else if (root_ == UNION_TYPE_EXPRESSION) {
      result_ = unionTypeExpression(builder_, level_ + 1);
    }
    else if (root_ == VALUE_PARAMETER) {
      result_ = valueParameter(builder_, level_ + 1);
    }
    else if (root_ == VAR) {
      result_ = var(builder_, level_ + 1);
    }
    else if (root_ == VARIABLE) {
      result_ = variable(builder_, level_ + 1);
    }
    else if (root_ == VARIANCE) {
      result_ = variance(builder_, level_ + 1);
    }
    else if (root_ == VOID_OR_INFERRED_METHOD_ARGUMENT) {
      result_ = voidOrInferredMethodArgument(builder_, level_ + 1);
    }
    else if (root_ == VOID_OR_INFERRED_METHOD_DECLARATION) {
      result_ = voidOrInferredMethodDeclaration(builder_, level_ + 1);
    }
    else if (root_ == WHILE_BLOCK) {
      result_ = whileBlock(builder_, level_ + 1);
    }
    else if (root_ == WHILE_LOOP) {
      result_ = whileLoop(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = parse_root_(root_, builder_, level_);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return parseCeylon(builder_, level_ + 1, compilationUnit_parser_);
  }

  /* ********************************************************** */
  // LINE_COMMENT | MULTI_LINE_COMMENT
  static boolean Comment(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Comment")) return false;
    if (!nextTokenIs(builder_, LINE_COMMENT) && !nextTokenIs(builder_, MULTI_LINE_COMMENT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, LINE_COMMENT);
    if (!result_) result_ = consumeToken(builder_, MULTI_LINE_COMMENT);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // qualifiedType ("?" | "[]" | "(" (type ("," type)* )? ")")*
  public static boolean abbreviatedType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedType(builder_, level_ + 1);
    result_ = result_ && abbreviatedType_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(ABBREVIATED_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("?" | "[]" | "(" (type ("," type)* )? ")")*
  private static boolean abbreviatedType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!abbreviatedType_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "abbreviatedType_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "?" | "[]" | "(" (type ("," type)* )? ")"
  private static boolean abbreviatedType_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_QUESTION);
    if (!result_) result_ = consumeToken(builder_, OP_BRACKETS);
    if (!result_) result_ = abbreviatedType_1_0_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "(" (type ("," type)* )? ")"
  private static boolean abbreviatedType_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && abbreviatedType_1_0_2_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (type ("," type)* )?
  private static boolean abbreviatedType_1_0_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0_2_1")) return false;
    abbreviatedType_1_0_2_1_0(builder_, level_ + 1);
    return true;
  }

  // type ("," type)*
  private static boolean abbreviatedType_1_0_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0_2_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    result_ = result_ && abbreviatedType_1_0_2_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ("," type)*
  private static boolean abbreviatedType_1_0_2_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0_2_1_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!abbreviatedType_1_0_2_1_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "abbreviatedType_1_0_2_1_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," type
  private static boolean abbreviatedType_1_0_2_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abbreviatedType_1_0_2_1_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && type(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "abstracts" qualifiedType
  public static boolean abstractedType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abstractedType")) return false;
    if (!nextTokenIs(builder_, KW_ABSTRACTS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ABSTRACTS);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (result_) {
      marker_.done(ABSTRACTED_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "adapts" qualifiedType ("&" qualifiedType)*
  public static boolean adaptedTypes(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "adaptedTypes")) return false;
    if (!nextTokenIs(builder_, KW_ADAPTS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ADAPTS);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    result_ = result_ && adaptedTypes_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(ADAPTED_TYPES);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("&" qualifiedType)*
  private static boolean adaptedTypes_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "adaptedTypes_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!adaptedTypes_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "adaptedTypes_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "&" qualifiedType
  private static boolean adaptedTypes_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "adaptedTypes_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_INTERSECTION);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // multiplicativeExpression
  //       (
  //         additiveOperator 
  //         multiplicativeExpression
  //       )*
  public static boolean additiveExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additiveExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<additive expression>");
    result_ = multiplicativeExpression(builder_, level_ + 1);
    result_ = result_ && additiveExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(ADDITIVE_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         additiveOperator 
  //         multiplicativeExpression
  //       )*
  private static boolean additiveExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additiveExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!additiveExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "additiveExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // additiveOperator 
  //         multiplicativeExpression
  private static boolean additiveExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additiveExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = additiveOperator(builder_, level_ + 1);
    result_ = result_ && multiplicativeExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "+" 
  //     | "-"
  //     | "|"
  //     | "^"
  //     | "~"
  public static boolean additiveOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additiveOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<additive operator>");
    result_ = consumeToken(builder_, OP_PLUS);
    if (!result_) result_ = consumeToken(builder_, OP_MINUS);
    if (!result_) result_ = consumeToken(builder_, OP_UNION);
    if (!result_) result_ = consumeToken(builder_, OP_XOR);
    if (!result_) result_ = consumeToken(builder_, OP_TIDLE);
    if (result_) {
      marker_.done(ADDITIVE_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "alias" typeNameDeclaration typeParameters? typeConstraints? typeSpecifier? ";"
  public static boolean aliasDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "aliasDeclaration")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<alias declaration>");
    result_ = consumeToken(builder_, "alias");
    result_ = result_ && typeNameDeclaration(builder_, level_ + 1);
    result_ = result_ && aliasDeclaration_2(builder_, level_ + 1);
    result_ = result_ && aliasDeclaration_3(builder_, level_ + 1);
    result_ = result_ && aliasDeclaration_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(ALIAS_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // typeParameters?
  private static boolean aliasDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "aliasDeclaration_2")) return false;
    typeParameters(builder_, level_ + 1);
    return true;
  }

  // typeConstraints?
  private static boolean aliasDeclaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "aliasDeclaration_3")) return false;
    typeConstraints(builder_, level_ + 1);
    return true;
  }

  // typeSpecifier?
  private static boolean aliasDeclaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "aliasDeclaration_4")) return false;
    typeSpecifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // annotationName annotationArguments
  public static boolean annotation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotation")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = annotationName(builder_, level_ + 1);
    result_ = result_ && annotationArguments(builder_, level_ + 1);
    if (result_) {
      marker_.done(ANNOTATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // arguments | literalArguments
  public static boolean annotationArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotationArguments")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<annotation arguments>");
    result_ = arguments(builder_, level_ + 1);
    if (!result_) result_ = literalArguments(builder_, level_ + 1);
    if (result_) {
      marker_.done(ANNOTATION_ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // LIDENTIFIER
  public static boolean annotationName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotationName")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, LIDENTIFIER);
    if (result_) {
      marker_.done(ANNOTATION_NAME);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // annotation*
  public static boolean annotations(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotations")) return false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<annotations>");
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!annotation(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "annotations");
        break;
      }
      offset_ = next_offset_;
    }
    marker_.done(ANNOTATIONS);
    exitErrorRecordingSection(builder_, level_, true, false, _SECTION_GENERAL_, null);
    return true;
  }

  /* ********************************************************** */
  // positionalArguments | namedArguments
  public static boolean arguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arguments")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN) && !nextTokenIs(builder_, OP_LBRACE)
        && replaceVariants(builder_, 2, "<arguments>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<arguments>");
    result_ = positionalArguments(builder_, level_ + 1);
    if (!result_) result_ = namedArguments(builder_, level_ + 1);
    if (result_) {
      marker_.done(ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // ASSERT conditions ";"
  public static boolean assertion(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assertion")) return false;
    if (!nextTokenIs(builder_, ASSERT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ASSERT);
    result_ = result_ && conditions(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(ASSERTION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // thenElseExpression
  //       (
  //         assignmentOperator 
  //         assignmentExpression
  //       )?
  public static boolean assignmentExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assignmentExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<assignment expression>");
    result_ = thenElseExpression(builder_, level_ + 1);
    result_ = result_ && assignmentExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(ASSIGNMENT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         assignmentOperator 
  //         assignmentExpression
  //       )?
  private static boolean assignmentExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assignmentExpression_1")) return false;
    assignmentExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // assignmentOperator 
  //         assignmentExpression
  private static boolean assignmentExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assignmentExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = assignmentOperator(builder_, level_ + 1);
    result_ = result_ && assignmentExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // ":="
  //     //| APPLY_OP 
  //     | "+="
  //     | "-="
  //     | "*="
  //     | "/="
  //     | "%="
  //     | "&="
  //     | "|="
  //     | "^="
  //     | "~="
  //     | "&&="
  //     | "||="
  public static boolean assignmentOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assignmentOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<assignment operator>");
    result_ = consumeToken(builder_, OP_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, OP_PLUS_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_MINUS_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_MULT_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_DIV_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_MOD_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_AND_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_OR_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_XOR_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_NOT_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_LOG_AND_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_LOG_OR_EQ);
    if (result_) {
      marker_.done(ASSIGNMENT_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // nonstringLiteral | stringExpression | enumeration | selfReference | parExpression | baseReference
  public static boolean base(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "base")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<base>");
    result_ = nonstringLiteral(builder_, level_ + 1);
    if (!result_) result_ = stringExpression(builder_, level_ + 1);
    if (!result_) result_ = enumeration(builder_, level_ + 1);
    if (!result_) result_ = selfReference(builder_, level_ + 1);
    if (!result_) result_ = parExpression(builder_, level_ + 1);
    if (!result_) result_ = baseReference(builder_, level_ + 1);
    if (result_) {
      marker_.done(BASE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // supertypeQualifier? (memberReference | typeReference)
  public static boolean baseReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "baseReference")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<base reference>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<base reference>");
    result_ = baseReference_0(builder_, level_ + 1);
    result_ = result_ && baseReference_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(BASE_REFERENCE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // supertypeQualifier?
  private static boolean baseReference_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "baseReference_0")) return false;
    supertypeQualifier(builder_, level_ + 1);
    return true;
  }

  // memberReference | typeReference
  private static boolean baseReference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "baseReference_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberReference(builder_, level_ + 1);
    if (!result_) result_ = typeReference(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "{" declarationOrStatement* "}"
  public static boolean block(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "block")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && block_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // declarationOrStatement*
  private static boolean block_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "block_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!declarationOrStatement(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "block_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // expression
  public static boolean booleanCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "booleanCondition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<boolean condition>");
    result_ = expression(builder_, level_ + 1);
    if (result_) {
      marker_.done(BOOLEAN_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "break"
  public static boolean breakDirective(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "breakDirective")) return false;
    if (!nextTokenIs(builder_, KW_BREAK)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_BREAK);
    if (result_) {
      marker_.done(BREAK_DIRECTIVE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "case" "(" caseItem? ")" block
  public static boolean caseBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseBlock")) return false;
    if (!nextTokenIs(builder_, KW_CASE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_CASE);
    result_ = result_ && consumeToken(builder_, OP_LPAREN);
    result_ = result_ && caseBlock_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(CASE_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // caseItem?
  private static boolean caseBlock_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseBlock_2")) return false;
    caseItem(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // isCaseCondition | satisfiesCaseCondition | matchCaseCondition
  public static boolean caseItem(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseItem")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<case item>");
    result_ = isCaseCondition(builder_, level_ + 1);
    if (!result_) result_ = satisfiesCaseCondition(builder_, level_ + 1);
    if (!result_) result_ = matchCaseCondition(builder_, level_ + 1);
    if (result_) {
      marker_.done(CASE_ITEM);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // qualifiedType | memberName
  public static boolean caseType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseType")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<case type>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<case type>");
    result_ = qualifiedType(builder_, level_ + 1);
    if (!result_) result_ = memberName(builder_, level_ + 1);
    if (result_) {
      marker_.done(CASE_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "of" caseType ("|" caseType)*
  public static boolean caseTypes(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseTypes")) return false;
    if (!nextTokenIs(builder_, KW_OF)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_OF);
    result_ = result_ && caseType(builder_, level_ + 1);
    result_ = result_ && caseTypes_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(CASE_TYPES);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("|" caseType)*
  private static boolean caseTypes_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseTypes_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!caseTypes_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "caseTypes_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "|" caseType
  private static boolean caseTypes_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "caseTypes_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_UNION);
    result_ = result_ && caseType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // caseBlock+ defaultCaseBlock?
  public static boolean cases(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cases")) return false;
    if (!nextTokenIs(builder_, KW_CASE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = cases_0(builder_, level_ + 1);
    result_ = result_ && cases_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(CASES);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // caseBlock+
  private static boolean cases_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cases_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = caseBlock(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!caseBlock(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "cases_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // defaultCaseBlock?
  private static boolean cases_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cases_1")) return false;
    defaultCaseBlock(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "catch" catchVariable block
  public static boolean catchBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catchBlock")) return false;
    if (!nextTokenIs(builder_, KW_CATCH)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_CATCH);
    result_ = result_ && catchVariable(builder_, level_ + 1);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(CATCH_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "(" variable? ")"
  public static boolean catchVariable(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catchVariable")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && catchVariable_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(CATCH_VARIABLE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // variable?
  private static boolean catchVariable_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catchVariable_1")) return false;
    variable(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // !compilationUnit
  static boolean ceylon_statement_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ceylon_statement_recover")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !compilationUnit(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // "{" declarationOrStatement* "}"
  public static boolean classBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classBody")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && classBody_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(CLASS_BODY);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // declarationOrStatement*
  private static boolean classBody_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classBody_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!declarationOrStatement(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "classBody_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // "class" typeNameDeclaration
  //         typeParameters? parameters? caseTypes? /*metatypes?*/ extendedType? satisfiedTypes?
  //         typeConstraints? (classBody | typeSpecifier? ";")
  public static boolean classDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_CLASS)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, KW_CLASS);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, typeNameDeclaration(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, classDeclaration_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, classDeclaration_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, classDeclaration_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, classDeclaration_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, classDeclaration_6(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, classDeclaration_7(builder_, level_ + 1)) && result_;
    result_ = pinned_ && classDeclaration_8(builder_, level_ + 1) && result_;
    if (result_ || pinned_) {
      marker_.done(CLASS_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // typeParameters?
  private static boolean classDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_2")) return false;
    typeParameters(builder_, level_ + 1);
    return true;
  }

  // parameters?
  private static boolean classDeclaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_3")) return false;
    parameters(builder_, level_ + 1);
    return true;
  }

  // caseTypes?
  private static boolean classDeclaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_4")) return false;
    caseTypes(builder_, level_ + 1);
    return true;
  }

  // extendedType?
  private static boolean classDeclaration_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_5")) return false;
    extendedType(builder_, level_ + 1);
    return true;
  }

  // satisfiedTypes?
  private static boolean classDeclaration_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_6")) return false;
    satisfiedTypes(builder_, level_ + 1);
    return true;
  }

  // typeConstraints?
  private static boolean classDeclaration_7(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_7")) return false;
    typeConstraints(builder_, level_ + 1);
    return true;
  }

  // classBody | typeSpecifier? ";"
  private static boolean classDeclaration_8(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_8")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = classBody(builder_, level_ + 1);
    if (!result_) result_ = classDeclaration_8_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeSpecifier? ";"
  private static boolean classDeclaration_8_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_8_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = classDeclaration_8_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeSpecifier?
  private static boolean classDeclaration_8_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classDeclaration_8_1_0")) return false;
    typeSpecifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // unionTypeExpression
  public static boolean comparableType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparableType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = unionTypeExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPARABLE_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // existenceEmptinessExpression
  //       (
  //         comparisonOperator 
  //         existenceEmptinessExpression
  //       | typeOperator
  //         type
  //       )?
  //     | typeOperator
  //       comparableType //TODO: support "type" here, using a predicate
  //       existenceEmptinessExpression
  public static boolean comparisonExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<comparison expression>");
    result_ = comparisonExpression_0(builder_, level_ + 1);
    if (!result_) result_ = comparisonExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPARISON_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // existenceEmptinessExpression
  //       (
  //         comparisonOperator 
  //         existenceEmptinessExpression
  //       | typeOperator
  //         type
  //       )?
  private static boolean comparisonExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = existenceEmptinessExpression(builder_, level_ + 1);
    result_ = result_ && comparisonExpression_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //         comparisonOperator 
  //         existenceEmptinessExpression
  //       | typeOperator
  //         type
  //       )?
  private static boolean comparisonExpression_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_0_1")) return false;
    comparisonExpression_0_1_0(builder_, level_ + 1);
    return true;
  }

  // comparisonOperator 
  //         existenceEmptinessExpression
  //       | typeOperator
  //         type
  private static boolean comparisonExpression_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = comparisonExpression_0_1_0_0(builder_, level_ + 1);
    if (!result_) result_ = comparisonExpression_0_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // comparisonOperator 
  //         existenceEmptinessExpression
  private static boolean comparisonExpression_0_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_0_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = comparisonOperator(builder_, level_ + 1);
    result_ = result_ && existenceEmptinessExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeOperator
  //         type
  private static boolean comparisonExpression_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_0_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeOperator(builder_, level_ + 1);
    result_ = result_ && type(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeOperator
  //       comparableType //TODO: support "type" here, using a predicate
  //       existenceEmptinessExpression
  private static boolean comparisonExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonExpression_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeOperator(builder_, level_ + 1);
    result_ = result_ && comparableType(builder_, level_ + 1);
    result_ = result_ && existenceEmptinessExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "<=>" 
  //     | "<="
  //     | ">="
  //     | ">"
  //     | "<"
  //     | "in"
  public static boolean comparisonOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comparisonOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<comparison operator>");
    result_ = consumeToken(builder_, OP_DIFFERENT);
    if (!result_) result_ = consumeToken(builder_, OP_LTE);
    if (!result_) result_ = consumeToken(builder_, OP_GTE);
    if (!result_) result_ = consumeToken(builder_, OP_GT);
    if (!result_) result_ = consumeToken(builder_, OP_LT);
    if (!result_) result_ = consumeToken(builder_, KW_IN);
    if (result_) {
      marker_.done(COMPARISON_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // (compilerAnnotations ";")?
  //       (
  //         Comment
  //         | compilerAnnotations annotations (moduleDescriptor | packageDescriptor)
  //         | importList? (compilerAnnotations declaration)+ // TODO was * instead of +
  //       )
  public static boolean compilationUnit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, "<compilation unit>");
    result_ = compilationUnit_0(builder_, level_ + 1);
    result_ = result_ && compilationUnit_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPILATION_UNIT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_RECOVER_, ceylon_statement_recover_parser_);
    return result_;
  }

  // (compilerAnnotations ";")?
  private static boolean compilationUnit_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_0")) return false;
    compilationUnit_0_0(builder_, level_ + 1);
    return true;
  }

  // compilerAnnotations ";"
  private static boolean compilationUnit_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // Comment
  //         | compilerAnnotations annotations (moduleDescriptor | packageDescriptor)
  //         | importList? (compilerAnnotations declaration)+
  private static boolean compilationUnit_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = Comment(builder_, level_ + 1);
    if (!result_) result_ = compilationUnit_1_1(builder_, level_ + 1);
    if (!result_) result_ = compilationUnit_1_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // compilerAnnotations annotations (moduleDescriptor | packageDescriptor)
  private static boolean compilationUnit_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && annotations(builder_, level_ + 1);
    result_ = result_ && compilationUnit_1_1_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // moduleDescriptor | packageDescriptor
  private static boolean compilationUnit_1_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_1_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = moduleDescriptor(builder_, level_ + 1);
    if (!result_) result_ = packageDescriptor(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // importList? (compilerAnnotations declaration)+
  private static boolean compilationUnit_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilationUnit_1_2_0(builder_, level_ + 1);
    result_ = result_ && compilationUnit_1_2_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // importList?
  private static boolean compilationUnit_1_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_2_0")) return false;
    importList(builder_, level_ + 1);
    return true;
  }

  // (compilerAnnotations declaration)+
  private static boolean compilationUnit_1_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_2_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilationUnit_1_2_1_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!compilationUnit_1_2_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "compilationUnit_1_2_1");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // compilerAnnotations declaration
  private static boolean compilationUnit_1_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilationUnit_1_2_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && declaration(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "@" annotationName ("[" myStringLiteral "]")?
  public static boolean compilerAnnotation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilerAnnotation")) return false;
    if (!nextTokenIs(builder_, OP_ANNOTATION)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ANNOTATION);
    result_ = result_ && annotationName(builder_, level_ + 1);
    result_ = result_ && compilerAnnotation_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPILER_ANNOTATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("[" myStringLiteral "]")?
  private static boolean compilerAnnotation_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilerAnnotation_2")) return false;
    compilerAnnotation_2_0(builder_, level_ + 1);
    return true;
  }

  // "[" myStringLiteral "]"
  private static boolean compilerAnnotation_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilerAnnotation_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACKET);
    result_ = result_ && myStringLiteral(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACKET);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotation*
  public static boolean compilerAnnotations(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compilerAnnotations")) return false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<compiler annotations>");
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!compilerAnnotation(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "compilerAnnotations");
        break;
      }
      offset_ = next_offset_;
    }
    marker_.done(COMPILER_ANNOTATIONS);
    exitErrorRecordingSection(builder_, level_, true, false, _SECTION_GENERAL_, null);
    return true;
  }

  /* ********************************************************** */
  // forComprehensionClause
  public static boolean comprehension(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comprehension")) return false;
    if (!nextTokenIs(builder_, KW_FOR)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = forComprehensionClause(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPREHENSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // forComprehensionClause 
  //     | ifComprehensionClause 
  //     | expressionComprehensionClause
  public static boolean comprehensionClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "comprehensionClause")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<comprehension clause>");
    result_ = forComprehensionClause(builder_, level_ + 1);
    if (!result_) result_ = ifComprehensionClause(builder_, level_ + 1);
    if (!result_) result_ = expressionComprehensionClause(builder_, level_ + 1);
    if (result_) {
      marker_.done(COMPREHENSION_CLAUSE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // existsCondition | nonemptyCondition | isCondition | satisfiesCondition | booleanCondition
  public static boolean condition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "condition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<condition>");
    result_ = existsCondition(builder_, level_ + 1);
    if (!result_) result_ = nonemptyCondition(builder_, level_ + 1);
    if (!result_) result_ = isCondition(builder_, level_ + 1);
    if (!result_) result_ = satisfiesCondition(builder_, level_ + 1);
    if (!result_) result_ = booleanCondition(builder_, level_ + 1);
    if (result_) {
      marker_.done(CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "(" (condition ("," condition)*)? ")"
  public static boolean conditions(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditions")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && conditions_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(CONDITIONS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (condition ("," condition)*)?
  private static boolean conditions_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditions_1")) return false;
    conditions_1_0(builder_, level_ + 1);
    return true;
  }

  // condition ("," condition)*
  private static boolean conditions_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditions_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = condition(builder_, level_ + 1);
    result_ = result_ && conditions_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ("," condition)*
  private static boolean conditions_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditions_1_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!conditions_1_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "conditions_1_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," condition
  private static boolean conditions_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditions_1_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && condition(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // logicalNegationExpression
  //       (
  //         conjunctionOperator 
  //         logicalNegationExpression
  //       )*
  public static boolean conjunctionExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjunctionExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<conjunction expression>");
    result_ = logicalNegationExpression(builder_, level_ + 1);
    result_ = result_ && conjunctionExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(CONJUNCTION_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         conjunctionOperator 
  //         logicalNegationExpression
  //       )*
  private static boolean conjunctionExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjunctionExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!conjunctionExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "conjunctionExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // conjunctionOperator 
  //         logicalNegationExpression
  private static boolean conjunctionExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjunctionExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = conjunctionOperator(builder_, level_ + 1);
    result_ = result_ && logicalNegationExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "&&"
  public static boolean conjunctionOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjunctionOperator")) return false;
    if (!nextTokenIs(builder_, OP_LOGICAL_AND)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LOGICAL_AND);
    if (result_) {
      marker_.done(CONJUNCTION_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "in" expression?
  public static boolean containment(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containment")) return false;
    if (!nextTokenIs(builder_, KW_IN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IN);
    result_ = result_ && containment_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(CONTAINMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // expression?
  private static boolean containment_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "containment_1")) return false;
    expression(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "continue"
  public static boolean continueDirective(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "continueDirective")) return false;
    if (!nextTokenIs(builder_, KW_CONTINUE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_CONTINUE);
    if (result_) {
      marker_.done(CONTINUE_DIRECTIVE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // block
  public static boolean controlBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "controlBlock")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = block(builder_, level_ + 1);
    if (result_) {
      marker_.done(CONTROL_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // ifElse | switchCaseElse | whileLoop | forElse | tryCatchFinally
  public static boolean controlStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "controlStatement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<control statement>");
    result_ = ifElse(builder_, level_ + 1);
    if (!result_) result_ = switchCaseElse(builder_, level_ + 1);
    if (!result_) result_ = whileLoop(builder_, level_ + 1);
    if (!result_) result_ = forElse(builder_, level_ + 1);
    if (!result_) result_ = tryCatchFinally(builder_, level_ + 1);
    if (result_) {
      marker_.done(CONTROL_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // annotations (objectDeclaration | setterDeclaration | voidOrInferredMethodDeclaration | inferredAttributeDeclaration
  //     | typedMethodOrAttributeDeclaration | classDeclaration | interfaceDeclaration | aliasDeclaration)
  public static boolean declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<declaration>");
    result_ = annotations(builder_, level_ + 1);
    result_ = result_ && declaration_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // objectDeclaration | setterDeclaration | voidOrInferredMethodDeclaration | inferredAttributeDeclaration
  //     | typedMethodOrAttributeDeclaration | classDeclaration | interfaceDeclaration | aliasDeclaration
  private static boolean declaration_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = objectDeclaration(builder_, level_ + 1);
    if (!result_) result_ = setterDeclaration(builder_, level_ + 1);
    if (!result_) result_ = voidOrInferredMethodDeclaration(builder_, level_ + 1);
    if (!result_) result_ = inferredAttributeDeclaration(builder_, level_ + 1);
    if (!result_) result_ = typedMethodOrAttributeDeclaration(builder_, level_ + 1);
    if (!result_) result_ = classDeclaration(builder_, level_ + 1);
    if (!result_) result_ = interfaceDeclaration(builder_, level_ + 1);
    if (!result_) result_ = aliasDeclaration(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotations
  //       ( 
  //         annotation* (assertion | declaration)
  //       | statement
  //       | Comment
  //       )
  public static boolean declarationOrStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarationOrStatement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<declaration or statement>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && declarationOrStatement_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(DECLARATION_OR_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // annotation* (assertion | declaration)
  //       | statement
  //       | Comment
  private static boolean declarationOrStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarationOrStatement_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = declarationOrStatement_1_0(builder_, level_ + 1);
    if (!result_) result_ = statement(builder_, level_ + 1);
    if (!result_) result_ = Comment(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // annotation* (assertion | declaration)
  private static boolean declarationOrStatement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarationOrStatement_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = declarationOrStatement_1_0_0(builder_, level_ + 1);
    result_ = result_ && declarationOrStatement_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // annotation*
  private static boolean declarationOrStatement_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarationOrStatement_1_0_0")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!annotation(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "declarationOrStatement_1_0_0");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // assertion | declaration
  private static boolean declarationOrStatement_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarationOrStatement_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = assertion(builder_, level_ + 1);
    if (!result_) result_ = declaration(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "else" block
  public static boolean defaultCaseBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defaultCaseBlock")) return false;
    if (!nextTokenIs(builder_, KW_ELSE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ELSE);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(DEFAULT_CASE_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // negationComplementExpression
  //       (
  //         defaultOperator 
  //         negationComplementExpression
  //       )*
  public static boolean defaultExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defaultExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<default expression>");
    result_ = negationComplementExpression(builder_, level_ + 1);
    result_ = result_ && defaultExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(DEFAULT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         defaultOperator 
  //         negationComplementExpression
  //       )*
  private static boolean defaultExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defaultExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!defaultExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "defaultExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // defaultOperator 
  //         negationComplementExpression
  private static boolean defaultExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defaultExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = defaultOperator(builder_, level_ + 1);
    result_ = result_ && negationComplementExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "default"
  public static boolean defaultOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defaultOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<default operator>");
    result_ = consumeToken(builder_, "default");
    if (result_) {
      marker_.done(DEFAULT_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // returnDirective | throwDirective | breakDirective | continueDirective
  public static boolean directive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "directive")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<directive>");
    result_ = returnDirective(builder_, level_ + 1);
    if (!result_) result_ = throwDirective(builder_, level_ + 1);
    if (!result_) result_ = breakDirective(builder_, level_ + 1);
    if (!result_) result_ = continueDirective(builder_, level_ + 1);
    if (result_) {
      marker_.done(DIRECTIVE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // directive ";"
  public static boolean directiveStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "directiveStatement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<directive statement>");
    result_ = directive(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(DIRECTIVE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // conjunctionExpression
  //       (
  //         disjunctionOperator 
  //         conjunctionExpression
  //       )*
  public static boolean disjunctionExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "disjunctionExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<disjunction expression>");
    result_ = conjunctionExpression(builder_, level_ + 1);
    result_ = result_ && disjunctionExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(DISJUNCTION_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         disjunctionOperator 
  //         conjunctionExpression
  //       )*
  private static boolean disjunctionExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "disjunctionExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!disjunctionExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "disjunctionExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // disjunctionOperator 
  //         conjunctionExpression
  private static boolean disjunctionExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "disjunctionExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = disjunctionOperator(builder_, level_ + 1);
    result_ = result_ && conjunctionExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "||"
  public static boolean disjunctionOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "disjunctionOperator")) return false;
    if (!nextTokenIs(builder_, OP_LOGICAL_OR)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LOGICAL_OR);
    if (result_) {
      marker_.done(DISJUNCTION_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "?[" | "["
  public static boolean elementSelectionOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elementSelectionOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<element selection operator>");
    result_ = consumeToken(builder_, "?[");
    if (!result_) result_ = consumeToken(builder_, OP_LBRACKET);
    if (result_) {
      marker_.done(ELEMENT_SELECTION_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "else" (elseIf | block)
  public static boolean elseBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elseBlock")) return false;
    if (!nextTokenIs(builder_, KW_ELSE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ELSE);
    result_ = result_ && elseBlock_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(ELSE_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // elseIf | block
  private static boolean elseBlock_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elseBlock_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = elseIf(builder_, level_ + 1);
    if (!result_) result_ = block(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // ifElse
  public static boolean elseIf(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elseIf")) return false;
    if (!nextTokenIs(builder_, KW_IF)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = ifElse(builder_, level_ + 1);
    if (result_) {
      marker_.done(ELSE_IF);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // abbreviatedType
  //       (
  //         "->"
  //         (
  //           abbreviatedType
  // //        | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //         )
  //       )?
  public static boolean entryType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "entryType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = abbreviatedType(builder_, level_ + 1);
    result_ = result_ && entryType_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(ENTRY_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (
  //         "->"
  //         (
  //           abbreviatedType
  // //        | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //         )
  //       )?
  private static boolean entryType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "entryType_1")) return false;
    entryType_1_0(builder_, level_ + 1);
    return true;
  }

  // "->"
  //         (
  //           abbreviatedType
  // //        | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //         )
  private static boolean entryType_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "entryType_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ARROW);
    result_ = result_ && entryType_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //           abbreviatedType
  // //        | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //         )
  private static boolean entryType_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "entryType_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = abbreviatedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "{"  (sequencedArgument | comprehension)? "}"
  public static boolean enumeration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enumeration")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && enumeration_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(ENUMERATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (sequencedArgument | comprehension)?
  private static boolean enumeration_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enumeration_1")) return false;
    enumeration_1_0(builder_, level_ + 1);
    return true;
  }

  // sequencedArgument | comprehension
  private static boolean enumeration_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enumeration_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = sequencedArgument(builder_, level_ + 1);
    if (!result_) result_ = comprehension(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // comparisonExpression
  //       (
  //         equalityOperator 
  //         comparisonExpression
  //       )?
  public static boolean equalityExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "equalityExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<equality expression>");
    result_ = comparisonExpression(builder_, level_ + 1);
    result_ = result_ && equalityExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(EQUALITY_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         equalityOperator 
  //         comparisonExpression
  //       )?
  private static boolean equalityExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "equalityExpression_1")) return false;
    equalityExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // equalityOperator 
  //         comparisonExpression
  private static boolean equalityExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "equalityExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = equalityOperator(builder_, level_ + 1);
    result_ = result_ && comparisonExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "==" 
  //     | "!="
  //     | "==="
  public static boolean equalityOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "equalityOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<equality operator>");
    result_ = consumeToken(builder_, OP_EQ_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_EXCL_EQ);
    if (!result_) result_ = consumeToken(builder_, OP_EQ_EQ_EQ);
    if (result_) {
      marker_.done(EQUALITY_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // rangeIntervalEntryExpression
  //       (
  //         existsNonemptyOperator
  //       )?
  //     | existsNonemptyOperator
  //       rangeIntervalEntryExpression
  public static boolean existenceEmptinessExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existenceEmptinessExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<existence emptiness expression>");
    result_ = existenceEmptinessExpression_0(builder_, level_ + 1);
    if (!result_) result_ = existenceEmptinessExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXISTENCE_EMPTINESS_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // rangeIntervalEntryExpression
  //       (
  //         existsNonemptyOperator
  //       )?
  private static boolean existenceEmptinessExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existenceEmptinessExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = rangeIntervalEntryExpression(builder_, level_ + 1);
    result_ = result_ && existenceEmptinessExpression_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //         existsNonemptyOperator
  //       )?
  private static boolean existenceEmptinessExpression_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existenceEmptinessExpression_0_1")) return false;
    existenceEmptinessExpression_0_1_0(builder_, level_ + 1);
    return true;
  }

  // (
  //         existsNonemptyOperator
  //       )
  private static boolean existenceEmptinessExpression_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existenceEmptinessExpression_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = existsNonemptyOperator(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // existsNonemptyOperator
  //       rangeIntervalEntryExpression
  private static boolean existenceEmptinessExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existenceEmptinessExpression_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = existsNonemptyOperator(builder_, level_ + 1);
    result_ = result_ && rangeIntervalEntryExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "exists" (impliedVariable | specifiedVariable) | booleanCondition
  public static boolean existsCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existsCondition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<exists condition>");
    result_ = existsCondition_0(builder_, level_ + 1);
    if (!result_) result_ = booleanCondition(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXISTS_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // "exists" (impliedVariable | specifiedVariable)
  private static boolean existsCondition_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existsCondition_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_EXISTS);
    result_ = result_ && existsCondition_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // impliedVariable | specifiedVariable
  private static boolean existsCondition_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existsCondition_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = impliedVariable(builder_, level_ + 1);
    if (!result_) result_ = specifiedVariable(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "exists" 
  //     | "nonempty"
  public static boolean existsNonemptyOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "existsNonemptyOperator")) return false;
    if (!nextTokenIs(builder_, KW_EXISTS) && !nextTokenIs(builder_, KW_NONEMPTY)
        && replaceVariants(builder_, 2, "<exists nonempty operator>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<exists nonempty operator>");
    result_ = consumeToken(builder_, KW_EXISTS);
    if (!result_) result_ = consumeToken(builder_, KW_NONEMPTY);
    if (result_) {
      marker_.done(EXISTS_NONEMPTY_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // incrementDecrementExpression
  //       (
  //         exponentiationOperator
  //         exponentiationExpression
  //       )?
  public static boolean exponentiationExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "exponentiationExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<exponentiation expression>");
    result_ = incrementDecrementExpression(builder_, level_ + 1);
    result_ = result_ && exponentiationExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPONENTIATION_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         exponentiationOperator
  //         exponentiationExpression
  //       )?
  private static boolean exponentiationExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "exponentiationExpression_1")) return false;
    exponentiationExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // exponentiationOperator
  //         exponentiationExpression
  private static boolean exponentiationExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "exponentiationExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = exponentiationOperator(builder_, level_ + 1);
    result_ = result_ && exponentiationExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "**"
  public static boolean exponentiationOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "exponentiationOperator")) return false;
    if (!nextTokenIs(builder_, OP_MULT_MULT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_MULT_MULT);
    if (result_) {
      marker_.done(EXPONENTIATION_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // assignmentExpression
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expression>");
    result_ = assignmentExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // expression
  //     | 
  public static boolean expressionComprehensionClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionComprehensionClause")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expression comprehension clause>");
    result_ = expression(builder_, level_ + 1);
    if (!result_) result_ = expressionComprehensionClause_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPRESSION_COMPREHENSION_CLAUSE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  private static boolean expressionComprehensionClause_1(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // expression specifier? (";" | ",")
  public static boolean expressionOrSpecificationStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionOrSpecificationStatement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expression or specification statement>");
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && expressionOrSpecificationStatement_1(builder_, level_ + 1);
    result_ = result_ && expressionOrSpecificationStatement_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPRESSION_OR_SPECIFICATION_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // specifier?
  private static boolean expressionOrSpecificationStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionOrSpecificationStatement_1")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  // ";" | ","
  private static boolean expressionOrSpecificationStatement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressionOrSpecificationStatement_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) result_ = consumeToken(builder_, OP_COMMA);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // expression ("," expression)*
  public static boolean expressions(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressions")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expressions>");
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && expressions_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPRESSIONS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // ("," expression)*
  private static boolean expressions_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressions_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!expressions_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "expressions_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," expression
  private static boolean expressions_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expressions_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "extends" (qualifiedType | "super" "." typeReference) positionalArguments
  public static boolean extendedType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "extendedType")) return false;
    if (!nextTokenIs(builder_, KW_EXTENDS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_EXTENDS);
    result_ = result_ && extendedType_1(builder_, level_ + 1);
    result_ = result_ && positionalArguments(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXTENDED_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // qualifiedType | "super" "." typeReference
  private static boolean extendedType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "extendedType_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedType(builder_, level_ + 1);
    if (!result_) result_ = extendedType_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "super" "." typeReference
  private static boolean extendedType_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "extendedType_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_SUPER);
    result_ = result_ && consumeToken(builder_, OP_DOT);
    result_ = result_ && typeReference(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "else" block
  public static boolean failBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "failBlock")) return false;
    if (!nextTokenIs(builder_, KW_ELSE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ELSE);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(FAIL_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "finally" block
  public static boolean finallyBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "finallyBlock")) return false;
    if (!nextTokenIs(builder_, KW_FINALLY)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_FINALLY);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(FINALLY_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "for" forIterator controlBlock
  public static boolean forBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forBlock")) return false;
    if (!nextTokenIs(builder_, KW_FOR)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_FOR);
    result_ = result_ && forIterator(builder_, level_ + 1);
    result_ = result_ && controlBlock(builder_, level_ + 1);
    if (result_) {
      marker_.done(FOR_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "for"
  //       forIterator
  //       comprehensionClause
  public static boolean forComprehensionClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forComprehensionClause")) return false;
    if (!nextTokenIs(builder_, KW_FOR)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_FOR);
    result_ = result_ && forIterator(builder_, level_ + 1);
    result_ = result_ && comprehensionClause(builder_, level_ + 1);
    if (result_) {
      marker_.done(FOR_COMPREHENSION_CLAUSE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // forBlock failBlock?
  public static boolean forElse(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forElse")) return false;
    if (!nextTokenIs(builder_, KW_FOR)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = forBlock(builder_, level_ + 1);
    result_ = result_ && forElse_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(FOR_ELSE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // failBlock?
  private static boolean forElse_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forElse_1")) return false;
    failBlock(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "(" compilerAnnotations (var (containment | "->" var containment)?)? ")"
  public static boolean forIterator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && forIterator_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(FOR_ITERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (var (containment | "->" var containment)?)?
  private static boolean forIterator_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator_2")) return false;
    forIterator_2_0(builder_, level_ + 1);
    return true;
  }

  // var (containment | "->" var containment)?
  private static boolean forIterator_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = var(builder_, level_ + 1);
    result_ = result_ && forIterator_2_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (containment | "->" var containment)?
  private static boolean forIterator_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator_2_0_1")) return false;
    forIterator_2_0_1_0(builder_, level_ + 1);
    return true;
  }

  // containment | "->" var containment
  private static boolean forIterator_2_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator_2_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = containment(builder_, level_ + 1);
    if (!result_) result_ = forIterator_2_0_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "->" var containment
  private static boolean forIterator_2_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "forIterator_2_0_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ARROW);
    result_ = result_ && var(builder_, level_ + 1);
    result_ = result_ && containment(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // (
  //         "function" 
  //       | "void"
  //       )?
  //       parameters
  //       ( 
  // //        (parametersStart)=>
  //         parameters
  //       )*
  //       expression
  //     /*| "value"
  //       { fa.setType(new FunctionModifier($"value")); } 
  //       e1=expression
  //       { fa.addParameterList(new ParameterList(null));
  //         fa.setExpression($e1.expression); 
  //         $positionalArgument = fa; }*/
  //     | expression
  public static boolean functionOrExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<function or expression>");
    result_ = functionOrExpression_0(builder_, level_ + 1);
    if (!result_) result_ = expression(builder_, level_ + 1);
    if (result_) {
      marker_.done(FUNCTION_OR_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         "function" 
  //       | "void"
  //       )?
  //       parameters
  //       ( 
  // //        (parametersStart)=>
  //         parameters
  //       )*
  //       expression
  private static boolean functionOrExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = functionOrExpression_0_0(builder_, level_ + 1);
    result_ = result_ && parameters(builder_, level_ + 1);
    result_ = result_ && functionOrExpression_0_2(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //         "function" 
  //       | "void"
  //       )?
  private static boolean functionOrExpression_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression_0_0")) return false;
    functionOrExpression_0_0_0(builder_, level_ + 1);
    return true;
  }

  // "function" 
  //       | "void"
  private static boolean functionOrExpression_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_FUNCTION);
    if (!result_) result_ = consumeToken(builder_, KW_VOID);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ( 
  // //        (parametersStart)=>
  //         parameters
  //       )*
  private static boolean functionOrExpression_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression_0_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!functionOrExpression_0_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "functionOrExpression_0_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ( 
  // //        (parametersStart)=>
  //         parameters
  //       )
  private static boolean functionOrExpression_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionOrExpression_0_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "if" conditions controlBlock
  public static boolean ifBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ifBlock")) return false;
    if (!nextTokenIs(builder_, KW_IF)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IF);
    result_ = result_ && conditions(builder_, level_ + 1);
    result_ = result_ && controlBlock(builder_, level_ + 1);
    if (result_) {
      marker_.done(IF_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "if"
  //       conditions
  //       comprehensionClause
  public static boolean ifComprehensionClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ifComprehensionClause")) return false;
    if (!nextTokenIs(builder_, KW_IF)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IF);
    result_ = result_ && conditions(builder_, level_ + 1);
    result_ = result_ && comprehensionClause(builder_, level_ + 1);
    if (result_) {
      marker_.done(IF_COMPREHENSION_CLAUSE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // ifBlock elseBlock?
  public static boolean ifElse(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ifElse")) return false;
    if (!nextTokenIs(builder_, KW_IF)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = ifBlock(builder_, level_ + 1);
    result_ = result_ && ifElse_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(IF_ELSE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // elseBlock?
  private static boolean ifElse_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ifElse_1")) return false;
    elseBlock(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // memberName
  public static boolean impliedVariable(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "impliedVariable")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberName(builder_, level_ + 1);
    if (result_) {
      marker_.done(IMPLIED_VARIABLE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "import" (packagePath) importElementList
  public static boolean importDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_IMPORT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IMPORT);
    result_ = result_ && importDeclaration_1(builder_, level_ + 1);
    result_ = result_ && importElementList(builder_, level_ + 1);
    if (result_) {
      marker_.done(IMPORT_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (packagePath)
  private static boolean importDeclaration_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importDeclaration_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = packagePath(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotations importName ("=" importName)? importElementList?
  public static boolean importElement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<import element>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && importName(builder_, level_ + 1);
    result_ = result_ && importElement_2(builder_, level_ + 1);
    result_ = result_ && importElement_3(builder_, level_ + 1);
    if (result_) {
      marker_.done(IMPORT_ELEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // ("=" importName)?
  private static boolean importElement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElement_2")) return false;
    importElement_2_0(builder_, level_ + 1);
    return true;
  }

  // "=" importName
  private static boolean importElement_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElement_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_EQUALS);
    result_ = result_ && importName(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // importElementList?
  private static boolean importElement_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElement_3")) return false;
    importElementList(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "{" (importElement ( "," (importElement | importWildcard))* | importWildcard )? "}"
  public static boolean importElementList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && importElementList_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(IMPORT_ELEMENT_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (importElement ( "," (importElement | importWildcard))* | importWildcard )?
  private static boolean importElementList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1")) return false;
    importElementList_1_0(builder_, level_ + 1);
    return true;
  }

  // importElement ( "," (importElement | importWildcard))* | importWildcard
  private static boolean importElementList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = importElementList_1_0_0(builder_, level_ + 1);
    if (!result_) result_ = importWildcard(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // importElement ( "," (importElement | importWildcard))*
  private static boolean importElementList_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = importElement(builder_, level_ + 1);
    result_ = result_ && importElementList_1_0_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ( "," (importElement | importWildcard))*
  private static boolean importElementList_1_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1_0_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!importElementList_1_0_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "importElementList_1_0_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," (importElement | importWildcard)
  private static boolean importElementList_1_0_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1_0_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && importElementList_1_0_0_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // importElement | importWildcard
  private static boolean importElementList_1_0_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importElementList_1_0_0_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = importElement(builder_, level_ + 1);
    if (!result_) result_ = importWildcard(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // importDeclaration+
  public static boolean importList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importList")) return false;
    if (!nextTokenIs(builder_, KW_IMPORT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = importDeclaration(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!importDeclaration(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "importList");
        break;
      }
      offset_ = next_offset_;
    }
    if (result_) {
      marker_.done(IMPORT_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "import" (QUOTED_LITERAL | packagePath) QUOTED_LITERAL ";"
  public static boolean importModule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importModule")) return false;
    if (!nextTokenIs(builder_, KW_IMPORT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IMPORT);
    result_ = result_ && importModule_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, QUOTED_LITERAL);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(IMPORT_MODULE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // QUOTED_LITERAL | packagePath
  private static boolean importModule_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importModule_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, QUOTED_LITERAL);
    if (!result_) result_ = packagePath(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "{" (compilerAnnotations annotations importModule)* "}"
  public static boolean importModuleList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importModuleList")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && importModuleList_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(IMPORT_MODULE_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (compilerAnnotations annotations importModule)*
  private static boolean importModuleList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importModuleList_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!importModuleList_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "importModuleList_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // compilerAnnotations annotations importModule
  private static boolean importModuleList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importModuleList_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && annotations(builder_, level_ + 1);
    result_ = result_ && importModule(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // memberName | typeName
  public static boolean importName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importName")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<import name>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<import name>");
    result_ = memberName(builder_, level_ + 1);
    if (!result_) result_ = typeName(builder_, level_ + 1);
    if (result_) {
      marker_.done(IMPORT_NAME);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "..."
  public static boolean importWildcard(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "importWildcard")) return false;
    if (!nextTokenIs(builder_, OP_ELLIPSIS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ELLIPSIS);
    if (result_) {
      marker_.done(IMPORT_WILDCARD);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // prefixOperator
  //       incrementDecrementExpression
  //     | postfixIncrementDecrementExpression
  public static boolean incrementDecrementExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "incrementDecrementExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<increment decrement expression>");
    result_ = incrementDecrementExpression_0(builder_, level_ + 1);
    if (!result_) result_ = postfixIncrementDecrementExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(INCREMENT_DECREMENT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // prefixOperator
  //       incrementDecrementExpression
  private static boolean incrementDecrementExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "incrementDecrementExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = prefixOperator(builder_, level_ + 1);
    result_ = result_ && incrementDecrementExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // additiveExpression
  public static boolean index(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "index")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<index>");
    result_ = additiveExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(INDEX);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // elementSelectionOperator index ("..." | ".."  index | ":" index)? "]"
  public static boolean indexOrIndexRange(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "indexOrIndexRange")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<index or index range>");
    result_ = elementSelectionOperator(builder_, level_ + 1);
    result_ = result_ && index(builder_, level_ + 1);
    result_ = result_ && indexOrIndexRange_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACKET);
    if (result_) {
      marker_.done(INDEX_OR_INDEX_RANGE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // ("..." | ".."  index | ":" index)?
  private static boolean indexOrIndexRange_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "indexOrIndexRange_2")) return false;
    indexOrIndexRange_2_0(builder_, level_ + 1);
    return true;
  }

  // "..." | ".."  index | ":" index
  private static boolean indexOrIndexRange_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "indexOrIndexRange_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ELLIPSIS);
    if (!result_) result_ = indexOrIndexRange_2_0_1(builder_, level_ + 1);
    if (!result_) result_ = indexOrIndexRange_2_0_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ".."  index
  private static boolean indexOrIndexRange_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "indexOrIndexRange_2_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_DOT_DOT);
    result_ = result_ && index(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ":" index
  private static boolean indexOrIndexRange_2_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "indexOrIndexRange_2_0_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ":");
    result_ = result_ && index(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "value" memberNameDeclaration ((specifier | initializer)? ";" | block)
  public static boolean inferredAttributeDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredAttributeDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_VALUE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_VALUE);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && inferredAttributeDeclaration_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(INFERRED_ATTRIBUTE_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (specifier | initializer)? ";" | block
  private static boolean inferredAttributeDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredAttributeDeclaration_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = inferredAttributeDeclaration_2_0(builder_, level_ + 1);
    if (!result_) result_ = block(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (specifier | initializer)? ";"
  private static boolean inferredAttributeDeclaration_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredAttributeDeclaration_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = inferredAttributeDeclaration_2_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (specifier | initializer)?
  private static boolean inferredAttributeDeclaration_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredAttributeDeclaration_2_0_0")) return false;
    inferredAttributeDeclaration_2_0_0_0(builder_, level_ + 1);
    return true;
  }

  // specifier | initializer
  private static boolean inferredAttributeDeclaration_2_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredAttributeDeclaration_2_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = specifier(builder_, level_ + 1);
    if (!result_) result_ = initializer(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "value" 
  //       memberNameDeclaration 
  //       block
  public static boolean inferredGetterArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inferredGetterArgument")) return false;
    if (!nextTokenIs(builder_, KW_VALUE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_VALUE);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(INFERRED_GETTER_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // ":=" expression
  public static boolean initializer(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializer")) return false;
    if (!nextTokenIs(builder_, OP_ASSIGN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ASSIGN);
    result_ = result_ && expression(builder_, level_ + 1);
    if (result_) {
      marker_.done(INITIALIZER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "{" declarationOrStatement* "}"
  public static boolean interfaceBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceBody")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && interfaceBody_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(INTERFACE_BODY);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // declarationOrStatement*
  private static boolean interfaceBody_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceBody_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!declarationOrStatement(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "interfaceBody_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // "interface" typeNameDeclaration
  //         typeParameters? caseTypes? /*metatypes?*/ adaptedTypes? satisfiedTypes?
  //         typeConstraints? (interfaceBody | typeSpecifier? ";")
  public static boolean interfaceDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_INTERFACE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, KW_INTERFACE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, typeNameDeclaration(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, interfaceDeclaration_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, interfaceDeclaration_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, interfaceDeclaration_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, interfaceDeclaration_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, interfaceDeclaration_6(builder_, level_ + 1)) && result_;
    result_ = pinned_ && interfaceDeclaration_7(builder_, level_ + 1) && result_;
    if (result_ || pinned_) {
      marker_.done(INTERFACE_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // typeParameters?
  private static boolean interfaceDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_2")) return false;
    typeParameters(builder_, level_ + 1);
    return true;
  }

  // caseTypes?
  private static boolean interfaceDeclaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_3")) return false;
    caseTypes(builder_, level_ + 1);
    return true;
  }

  // adaptedTypes?
  private static boolean interfaceDeclaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_4")) return false;
    adaptedTypes(builder_, level_ + 1);
    return true;
  }

  // satisfiedTypes?
  private static boolean interfaceDeclaration_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_5")) return false;
    satisfiedTypes(builder_, level_ + 1);
    return true;
  }

  // typeConstraints?
  private static boolean interfaceDeclaration_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_6")) return false;
    typeConstraints(builder_, level_ + 1);
    return true;
  }

  // interfaceBody | typeSpecifier? ";"
  private static boolean interfaceDeclaration_7(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_7")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = interfaceBody(builder_, level_ + 1);
    if (!result_) result_ = interfaceDeclaration_7_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeSpecifier? ";"
  private static boolean interfaceDeclaration_7_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_7_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = interfaceDeclaration_7_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeSpecifier?
  private static boolean interfaceDeclaration_7_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interfaceDeclaration_7_1_0")) return false;
    typeSpecifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // entryType
  //       ( 
  //         (
  //           "&"
  //           (
  //             entryType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  //       )?
  public static boolean intersectionType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = entryType(builder_, level_ + 1);
    result_ = result_ && intersectionType_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(INTERSECTION_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( 
  //         (
  //           "&"
  //           (
  //             entryType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  //       )?
  private static boolean intersectionType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionType_1")) return false;
    intersectionType_1_0(builder_, level_ + 1);
    return true;
  }

  // (
  //           "&"
  //           (
  //             entryType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  private static boolean intersectionType_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionType_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = intersectionType_1_0_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!intersectionType_1_0_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "intersectionType_1_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "&"
  //           (
  //             entryType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  private static boolean intersectionType_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionType_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_INTERSECTION);
    result_ = result_ && intersectionType_1_0_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //             entryType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  private static boolean intersectionType_1_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionType_1_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = entryType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // qualifiedType (("&" qualifiedType)+)?
  public static boolean intersectionTypeExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionTypeExpression")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedType(builder_, level_ + 1);
    result_ = result_ && intersectionTypeExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(INTERSECTION_TYPE_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (("&" qualifiedType)+)?
  private static boolean intersectionTypeExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionTypeExpression_1")) return false;
    intersectionTypeExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // ("&" qualifiedType)+
  private static boolean intersectionTypeExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionTypeExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = intersectionTypeExpression_1_0_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!intersectionTypeExpression_1_0_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "intersectionTypeExpression_1_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "&" qualifiedType
  private static boolean intersectionTypeExpression_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "intersectionTypeExpression_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_INTERSECTION);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "is" type
  public static boolean isCaseCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCaseCondition")) return false;
    if (!nextTokenIs(builder_, KW_IS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_IS);
    result_ = result_ && type(builder_, level_ + 1);
    if (result_) {
      marker_.done(IS_CASE_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // ("!")? "is" type (impliedVariable | memberName specifier) | booleanCondition
  public static boolean isCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<is condition>");
    result_ = isCondition_0(builder_, level_ + 1);
    if (!result_) result_ = booleanCondition(builder_, level_ + 1);
    if (result_) {
      marker_.done(IS_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // ("!")? "is" type (impliedVariable | memberName specifier)
  private static boolean isCondition_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = isCondition_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, KW_IS);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && isCondition_0_3(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ("!")?
  private static boolean isCondition_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition_0_0")) return false;
    isCondition_0_0_0(builder_, level_ + 1);
    return true;
  }

  // ("!")
  private static boolean isCondition_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_NOT);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // impliedVariable | memberName specifier
  private static boolean isCondition_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition_0_3")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = impliedVariable(builder_, level_ + 1);
    if (!result_) result_ = isCondition_0_3_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // memberName specifier
  private static boolean isCondition_0_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "isCondition_0_3_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberName(builder_, level_ + 1);
    result_ = result_ && specifier(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // nonstringLiteral | myStringLiteral
  public static boolean literalArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalArgument")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<literal argument>");
    result_ = nonstringLiteral(builder_, level_ + 1);
    if (!result_) result_ = myStringLiteral(builder_, level_ + 1);
    if (result_) {
      marker_.done(LITERAL_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // literalArgument*
  public static boolean literalArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literalArguments")) return false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<literal arguments>");
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!literalArgument(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "literalArguments");
        break;
      }
      offset_ = next_offset_;
    }
    marker_.done(LITERAL_ARGUMENTS);
    exitErrorRecordingSection(builder_, level_, true, false, _SECTION_GENERAL_, null);
    return true;
  }

  /* ********************************************************** */
  // notOperator 
  //       logicalNegationExpression
  //     | equalityExpression
  public static boolean logicalNegationExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "logicalNegationExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<logical negation expression>");
    result_ = logicalNegationExpression_0(builder_, level_ + 1);
    if (!result_) result_ = equalityExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(LOGICAL_NEGATION_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // notOperator 
  //       logicalNegationExpression
  private static boolean logicalNegationExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "logicalNegationExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = notOperator(builder_, level_ + 1);
    result_ = result_ && logicalNegationExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // expressions
  public static boolean matchCaseCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchCaseCondition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<match case condition>");
    result_ = expressions(builder_, level_ + 1);
    if (result_) {
      marker_.done(MATCH_CASE_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // LIDENTIFIER
  public static boolean memberName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberName")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, LIDENTIFIER);
    if (result_) {
      marker_.done(MEMBER_NAME);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // memberName | typeName
  public static boolean memberNameDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberNameDeclaration")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<member name declaration>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<member name declaration>");
    result_ = memberName(builder_, level_ + 1);
    if (!result_) result_ = typeName(builder_, level_ + 1);
    if (result_) {
      marker_.done(MEMBER_NAME_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // memberName typeArguments?
  public static boolean memberReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberReference")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberName(builder_, level_ + 1);
    result_ = result_ && memberReference_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(MEMBER_REFERENCE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // typeArguments?
  private static boolean memberReference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberReference_1")) return false;
    typeArguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "." | "?." | "[]."
  public static boolean memberSelectionOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberSelectionOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<member selection operator>");
    result_ = consumeToken(builder_, OP_DOT);
    if (!result_) result_ = consumeToken(builder_, OP_DOT_QUESTION);
    if (!result_) result_ = consumeToken(builder_, OP_BRACKETS_DOT);
    if (result_) {
      marker_.done(MEMBER_SELECTION_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "module" packagePath QUOTED_LITERAL importModuleList
  public static boolean moduleDescriptor(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "moduleDescriptor")) return false;
    if (!nextTokenIs(builder_, KW_MODULE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_MODULE);
    result_ = result_ && packagePath(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, QUOTED_LITERAL);
    result_ = result_ && importModuleList(builder_, level_ + 1);
    if (result_) {
      marker_.done(MODULE_DESCRIPTOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // defaultExpression
  //       (
  //         multiplicativeOperator 
  //         defaultExpression
  //       )*
  public static boolean multiplicativeExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicativeExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<multiplicative expression>");
    result_ = defaultExpression(builder_, level_ + 1);
    result_ = result_ && multiplicativeExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(MULTIPLICATIVE_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         multiplicativeOperator 
  //         defaultExpression
  //       )*
  private static boolean multiplicativeExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicativeExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!multiplicativeExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "multiplicativeExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // multiplicativeOperator 
  //         defaultExpression
  private static boolean multiplicativeExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicativeExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = multiplicativeOperator(builder_, level_ + 1);
    result_ = result_ && defaultExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "*" 
  //     | "/"
  //     | "%"
  //     | "&"
  public static boolean multiplicativeOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicativeOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<multiplicative operator>");
    result_ = consumeToken(builder_, OP_MULTIPLY);
    if (!result_) result_ = consumeToken(builder_, OP_DIVIDE);
    if (!result_) result_ = consumeToken(builder_, OP_MODULO);
    if (!result_) result_ = consumeToken(builder_, OP_INTERSECTION);
    if (result_) {
      marker_.done(MULTIPLICATIVE_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // STRING_LITERAL
  public static boolean myStringLiteral(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "myStringLiteral")) return false;
    if (!nextTokenIs(builder_, STRING_LITERAL)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, STRING_LITERAL);
    if (result_) {
      marker_.done(MY_STRING_LITERAL);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotations (namedSpecifiedArgument | namedArgumentDeclaration)
  public static boolean namedArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArgument")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<named argument>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && namedArgument_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(NAMED_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // namedSpecifiedArgument | namedArgumentDeclaration
  private static boolean namedArgument_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArgument_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = namedSpecifiedArgument(builder_, level_ + 1);
    if (!result_) result_ = namedArgumentDeclaration(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // objectArgument
  //     | typedMethodOrGetterArgument
  //     | voidOrInferredMethodArgument
  //     | inferredGetterArgument
  public static boolean namedArgumentDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArgumentDeclaration")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<named argument declaration>");
    result_ = objectArgument(builder_, level_ + 1);
    if (!result_) result_ = typedMethodOrGetterArgument(builder_, level_ + 1);
    if (!result_) result_ = voidOrInferredMethodArgument(builder_, level_ + 1);
    if (!result_) result_ = inferredGetterArgument(builder_, level_ + 1);
    if (result_) {
      marker_.done(NAMED_ARGUMENT_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "{" namedArgument* (sequencedArgument | comprehension)? "}"
  public static boolean namedArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArguments")) return false;
    if (!nextTokenIs(builder_, OP_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LBRACE);
    result_ = result_ && namedArguments_1(builder_, level_ + 1);
    result_ = result_ && namedArguments_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RBRACE);
    if (result_) {
      marker_.done(NAMED_ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // namedArgument*
  private static boolean namedArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArguments_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!namedArgument(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "namedArguments_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (sequencedArgument | comprehension)?
  private static boolean namedArguments_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArguments_2")) return false;
    namedArguments_2_0(builder_, level_ + 1);
    return true;
  }

  // sequencedArgument | comprehension
  private static boolean namedArguments_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedArguments_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = sequencedArgument(builder_, level_ + 1);
    if (!result_) result_ = comprehension(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // memberNameDeclaration specifier ";"
  public static boolean namedSpecifiedArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namedSpecifiedArgument")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<named specified argument>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<named specified argument>");
    result_ = memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && specifier(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(NAMED_SPECIFIED_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // unaryMinusOrComplementOperator 
  //       negationComplementExpression
  //     | exponentiationExpression
  public static boolean negationComplementExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "negationComplementExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<negation complement expression>");
    result_ = negationComplementExpression_0(builder_, level_ + 1);
    if (!result_) result_ = exponentiationExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(NEGATION_COMPLEMENT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // unaryMinusOrComplementOperator 
  //       negationComplementExpression
  private static boolean negationComplementExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "negationComplementExpression_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = unaryMinusOrComplementOperator(builder_, level_ + 1);
    result_ = result_ && negationComplementExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "nonempty" (impliedVariable | specifiedVariable) | booleanCondition
  public static boolean nonemptyCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nonemptyCondition")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<nonempty condition>");
    result_ = nonemptyCondition_0(builder_, level_ + 1);
    if (!result_) result_ = booleanCondition(builder_, level_ + 1);
    if (result_) {
      marker_.done(NONEMPTY_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // "nonempty" (impliedVariable | specifiedVariable)
  private static boolean nonemptyCondition_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nonemptyCondition_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_NONEMPTY);
    result_ = result_ && nonemptyCondition_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // impliedVariable | specifiedVariable
  private static boolean nonemptyCondition_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nonemptyCondition_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = impliedVariable(builder_, level_ + 1);
    if (!result_) result_ = specifiedVariable(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // NATURAL_LITERAL | FLOAT_LITERAL | QUOTED_LITERAL | CHAR_LITERAL
  public static boolean nonstringLiteral(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nonstringLiteral")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<nonstring literal>");
    result_ = consumeToken(builder_, NATURAL_LITERAL);
    if (!result_) result_ = consumeToken(builder_, FLOAT_LITERAL);
    if (!result_) result_ = consumeToken(builder_, QUOTED_LITERAL);
    if (!result_) result_ = consumeToken(builder_, CHAR_LITERAL);
    if (result_) {
      marker_.done(NONSTRING_LITERAL);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "!"
  public static boolean notOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "notOperator")) return false;
    if (!nextTokenIs(builder_, OP_NOT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_NOT);
    if (result_) {
      marker_.done(NOT_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "object" 
  //       memberNameDeclaration
  //       ( 
  //         extendedType
  //       )?
  //       ( 
  //         satisfiedTypes
  //       )?
  //       (
  //         classBody
  //       | 
  //         ";"
  //       )
  public static boolean objectArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument")) return false;
    if (!nextTokenIs(builder_, KW_OBJECT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_OBJECT);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && objectArgument_2(builder_, level_ + 1);
    result_ = result_ && objectArgument_3(builder_, level_ + 1);
    result_ = result_ && objectArgument_4(builder_, level_ + 1);
    if (result_) {
      marker_.done(OBJECT_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( 
  //         extendedType
  //       )?
  private static boolean objectArgument_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument_2")) return false;
    objectArgument_2_0(builder_, level_ + 1);
    return true;
  }

  // ( 
  //         extendedType
  //       )
  private static boolean objectArgument_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = extendedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ( 
  //         satisfiedTypes
  //       )?
  private static boolean objectArgument_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument_3")) return false;
    objectArgument_3_0(builder_, level_ + 1);
    return true;
  }

  // ( 
  //         satisfiedTypes
  //       )
  private static boolean objectArgument_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument_3_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = satisfiedTypes(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // classBody
  //       | 
  //         ";"
  private static boolean objectArgument_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectArgument_4")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = classBody(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "object" memberNameDeclaration extendedType? satisfiedTypes? (classBody | ";")
  public static boolean objectDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_OBJECT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_OBJECT);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && objectDeclaration_2(builder_, level_ + 1);
    result_ = result_ && objectDeclaration_3(builder_, level_ + 1);
    result_ = result_ && objectDeclaration_4(builder_, level_ + 1);
    if (result_) {
      marker_.done(OBJECT_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // extendedType?
  private static boolean objectDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectDeclaration_2")) return false;
    extendedType(builder_, level_ + 1);
    return true;
  }

  // satisfiedTypes?
  private static boolean objectDeclaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectDeclaration_3")) return false;
    satisfiedTypes(builder_, level_ + 1);
    return true;
  }

  // classBody | ";"
  private static boolean objectDeclaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "objectDeclaration_4")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = classBody(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "package" packagePath ";"
  public static boolean packageDescriptor(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "packageDescriptor")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<package descriptor>");
    result_ = consumeToken(builder_, "package");
    result_ = result_ && packagePath(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (result_) {
      marker_.done(PACKAGE_DESCRIPTOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // LIDENTIFIER | UIDENTIFIER
  public static boolean packageName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "packageName")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<package name>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<package name>");
    result_ = consumeToken(builder_, LIDENTIFIER);
    if (!result_) result_ = consumeToken(builder_, UIDENTIFIER);
    if (result_) {
      marker_.done(PACKAGE_NAME);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // packageName ("." packageName)*
  public static boolean packagePath(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "packagePath")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<package path>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<package path>");
    result_ = packageName(builder_, level_ + 1);
    result_ = result_ && packagePath_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(PACKAGE_PATH);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // ("." packageName)*
  private static boolean packagePath_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "packagePath_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!packagePath_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "packagePath_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "." packageName
  private static boolean packagePath_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "packagePath_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_DOT);
    result_ = result_ && packageName(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "(" 
  //       assignmentExpression
  //       ")"
  public static boolean parExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parExpression")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && assignmentExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(PAR_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // parameterType memberName (parameters+ | valueParameter?) specifier?
  public static boolean parameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter")) return false;
    if (!nextTokenIs(builder_, KW_VOID) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<parameter>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<parameter>");
    result_ = parameterType(builder_, level_ + 1);
    result_ = result_ && memberName(builder_, level_ + 1);
    result_ = result_ && parameter_2(builder_, level_ + 1);
    result_ = result_ && parameter_3(builder_, level_ + 1);
    if (result_) {
      marker_.done(PARAMETER);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // parameters+ | valueParameter?
  private static boolean parameter_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameter_2_0(builder_, level_ + 1);
    if (!result_) result_ = parameter_2_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // parameters+
  private static boolean parameter_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!parameters(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "parameter_2_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // valueParameter?
  private static boolean parameter_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2_1")) return false;
    valueParameter(builder_, level_ + 1);
    return true;
  }

  // specifier?
  private static boolean parameter_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_3")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // compilerAnnotations (parameterRef | annotations parameter)
  public static boolean parameterDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterDeclaration")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<parameter declaration>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && parameterDeclaration_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(PARAMETER_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // parameterRef | annotations parameter
  private static boolean parameterDeclaration_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterDeclaration_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameterRef(builder_, level_ + 1);
    if (!result_) result_ = parameterDeclaration_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // annotations parameter
  private static boolean parameterDeclaration_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterDeclaration_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = annotations(builder_, level_ + 1);
    result_ = result_ && parameter(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // memberName specifier?
  public static boolean parameterRef(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterRef")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberName(builder_, level_ + 1);
    result_ = result_ && parameterRef_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(PARAMETER_REF);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // specifier?
  private static boolean parameterRef_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterRef_1")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // type "..."? | "void"
  public static boolean parameterType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterType")) return false;
    if (!nextTokenIs(builder_, KW_VOID) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<parameter type>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<parameter type>");
    result_ = parameterType_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, KW_VOID);
    if (result_) {
      marker_.done(PARAMETER_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // type "..."?
  private static boolean parameterType_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterType_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    result_ = result_ && parameterType_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "..."?
  private static boolean parameterType_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameterType_0_1")) return false;
    consumeToken(builder_, OP_ELLIPSIS);
    return true;
  }

  /* ********************************************************** */
  // "(" (parameterDeclaration ("," parameterDeclaration)*)? ")"
  public static boolean parameters(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameters")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && parameters_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(PARAMETERS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (parameterDeclaration ("," parameterDeclaration)*)?
  private static boolean parameters_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameters_1")) return false;
    parameters_1_0(builder_, level_ + 1);
    return true;
  }

  // parameterDeclaration ("," parameterDeclaration)*
  private static boolean parameters_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameters_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameterDeclaration(builder_, level_ + 1);
    result_ = result_ && parameters_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ("," parameterDeclaration)*
  private static boolean parameters_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameters_1_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!parameters_1_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "parameters_1_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," parameterDeclaration
  private static boolean parameters_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameters_1_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && parameterDeclaration(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // functionOrExpression
  public static boolean positionalArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArgument")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<positional argument>");
    result_ = functionOrExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(POSITIONAL_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "(" 
  //       ( positionalArgument
  //         (
  //           "," 
  //           (
  //             positionalArgument
  //           | 
  //           )
  //         )* 
  //         (
  //           "..."
  //         )?
  //       )? 
  //       (
  //         comprehension
  //       )?
  //       ")"
  public static boolean positionalArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && positionalArguments_1(builder_, level_ + 1);
    result_ = result_ && positionalArguments_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(POSITIONAL_ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( positionalArgument
  //         (
  //           "," 
  //           (
  //             positionalArgument
  //           | 
  //           )
  //         )* 
  //         (
  //           "..."
  //         )?
  //       )?
  private static boolean positionalArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1")) return false;
    positionalArguments_1_0(builder_, level_ + 1);
    return true;
  }

  // positionalArgument
  //         (
  //           "," 
  //           (
  //             positionalArgument
  //           | 
  //           )
  //         )* 
  //         (
  //           "..."
  //         )?
  private static boolean positionalArguments_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = positionalArgument(builder_, level_ + 1);
    result_ = result_ && positionalArguments_1_0_1(builder_, level_ + 1);
    result_ = result_ && positionalArguments_1_0_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //           "," 
  //           (
  //             positionalArgument
  //           | 
  //           )
  //         )*
  private static boolean positionalArguments_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!positionalArguments_1_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "positionalArguments_1_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," 
  //           (
  //             positionalArgument
  //           | 
  //           )
  private static boolean positionalArguments_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && positionalArguments_1_0_1_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // positionalArgument
  //           | 
  //           
  private static boolean positionalArguments_1_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0_1_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = positionalArgument(builder_, level_ + 1);
    if (!result_) result_ = positionalArguments_1_0_1_0_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  private static boolean positionalArguments_1_0_1_0_1_1(PsiBuilder builder_, int level_) {
    return true;
  }

  // (
  //           "..."
  //         )?
  private static boolean positionalArguments_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0_2")) return false;
    positionalArguments_1_0_2_0(builder_, level_ + 1);
    return true;
  }

  // (
  //           "..."
  //         )
  private static boolean positionalArguments_1_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_1_0_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ELLIPSIS);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //         comprehension
  //       )?
  private static boolean positionalArguments_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_2")) return false;
    positionalArguments_2_0(builder_, level_ + 1);
    return true;
  }

  // (
  //         comprehension
  //       )
  private static boolean positionalArguments_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "positionalArguments_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = comprehension(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // primary 
  //       (
  //         postfixOperator
  //       )*
  public static boolean postfixIncrementDecrementExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfixIncrementDecrementExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<postfix increment decrement expression>");
    result_ = primary(builder_, level_ + 1);
    result_ = result_ && postfixIncrementDecrementExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(POSTFIX_INCREMENT_DECREMENT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         postfixOperator
  //       )*
  private static boolean postfixIncrementDecrementExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfixIncrementDecrementExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!postfixIncrementDecrementExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "postfixIncrementDecrementExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (
  //         postfixOperator
  //       )
  private static boolean postfixIncrementDecrementExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfixIncrementDecrementExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = postfixOperator(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "--" 
  //     | "++"
  public static boolean postfixOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfixOperator")) return false;
    if (!nextTokenIs(builder_, OP_PLUS_PLUS) && !nextTokenIs(builder_, OP_MIN_MIN)
        && replaceVariants(builder_, 2, "<postfix operator>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<postfix operator>");
    result_ = consumeToken(builder_, OP_MIN_MIN);
    if (!result_) result_ = consumeToken(builder_, OP_PLUS_PLUS);
    if (result_) {
      marker_.done(POSTFIX_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "--" | "++"
  public static boolean prefixOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "prefixOperator")) return false;
    if (!nextTokenIs(builder_, OP_PLUS_PLUS) && !nextTokenIs(builder_, OP_MIN_MIN)
        && replaceVariants(builder_, 2, "<prefix operator>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<prefix operator>");
    result_ = consumeToken(builder_, OP_MIN_MIN);
    if (!result_) result_ = consumeToken(builder_, OP_PLUS_PLUS);
    if (result_) {
      marker_.done(PREFIX_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // base (qualifiedReference | arguments | indexOrIndexRange)*
  public static boolean primary(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primary")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<primary>");
    result_ = base(builder_, level_ + 1);
    result_ = result_ && primary_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(PRIMARY);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (qualifiedReference | arguments | indexOrIndexRange)*
  private static boolean primary_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primary_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!primary_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "primary_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // qualifiedReference | arguments | indexOrIndexRange
  private static boolean primary_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primary_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedReference(builder_, level_ + 1);
    if (!result_) result_ = arguments(builder_, level_ + 1);
    if (!result_) result_ = indexOrIndexRange(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // memberSelectionOperator (memberReference | typeReference)
  public static boolean qualifiedReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedReference")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<qualified reference>");
    result_ = memberSelectionOperator(builder_, level_ + 1);
    result_ = result_ && qualifiedReference_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(QUALIFIED_REFERENCE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // memberReference | typeReference
  private static boolean qualifiedReference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedReference_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberReference(builder_, level_ + 1);
    if (!result_) result_ = typeReference(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // supertypeQualifier? typeNameWithArguments ("." typeNameWithArguments)*
  public static boolean qualifiedType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedType_0(builder_, level_ + 1);
    result_ = result_ && typeNameWithArguments(builder_, level_ + 1);
    result_ = result_ && qualifiedType_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(QUALIFIED_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // supertypeQualifier?
  private static boolean qualifiedType_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedType_0")) return false;
    supertypeQualifier(builder_, level_ + 1);
    return true;
  }

  // ("." typeNameWithArguments)*
  private static boolean qualifiedType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedType_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!qualifiedType_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "qualifiedType_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "." typeNameWithArguments
  private static boolean qualifiedType_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qualifiedType_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_DOT);
    result_ = result_ && typeNameWithArguments(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // additiveExpression
  //       (
  //         rangeIntervalEntryOperator 
  //         additiveExpression
  //       )?
  public static boolean rangeIntervalEntryExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rangeIntervalEntryExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<range interval entry expression>");
    result_ = additiveExpression(builder_, level_ + 1);
    result_ = result_ && rangeIntervalEntryExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(RANGE_INTERVAL_ENTRY_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         rangeIntervalEntryOperator 
  //         additiveExpression
  //       )?
  private static boolean rangeIntervalEntryExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rangeIntervalEntryExpression_1")) return false;
    rangeIntervalEntryExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // rangeIntervalEntryOperator 
  //         additiveExpression
  private static boolean rangeIntervalEntryExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rangeIntervalEntryExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = rangeIntervalEntryOperator(builder_, level_ + 1);
    result_ = result_ && additiveExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // ".." 
  //     | ":"
  //     | "->"
  public static boolean rangeIntervalEntryOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rangeIntervalEntryOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<range interval entry operator>");
    result_ = consumeToken(builder_, OP_DOT_DOT);
    if (!result_) result_ = consumeToken(builder_, ":");
    if (!result_) result_ = consumeToken(builder_, OP_ARROW);
    if (result_) {
      marker_.done(RANGE_INTERVAL_ENTRY_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "(" (specifiedVariable | expression)? ")"
  public static boolean resource(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "resource")) return false;
    if (!nextTokenIs(builder_, OP_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LPAREN);
    result_ = result_ && resource_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(RESOURCE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (specifiedVariable | expression)?
  private static boolean resource_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "resource_1")) return false;
    resource_1_0(builder_, level_ + 1);
    return true;
  }

  // specifiedVariable | expression
  private static boolean resource_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "resource_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = specifiedVariable(builder_, level_ + 1);
    if (!result_) result_ = expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "return" functionOrExpression?
  public static boolean returnDirective(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnDirective")) return false;
    if (!nextTokenIs(builder_, KW_RETURN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_RETURN);
    result_ = result_ && returnDirective_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(RETURN_DIRECTIVE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // functionOrExpression?
  private static boolean returnDirective_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnDirective_1")) return false;
    functionOrExpression(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "satisfies" qualifiedType ("&" qualifiedType)*
  public static boolean satisfiedTypes(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiedTypes")) return false;
    if (!nextTokenIs(builder_, KW_SATISFIES)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_SATISFIES);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    result_ = result_ && satisfiedTypes_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(SATISFIED_TYPES);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("&" qualifiedType)*
  private static boolean satisfiedTypes_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiedTypes_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!satisfiedTypes_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "satisfiedTypes_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "&" qualifiedType
  private static boolean satisfiedTypes_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiedTypes_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_INTERSECTION);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "satisfies" qualifiedType
  public static boolean satisfiesCaseCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiesCaseCondition")) return false;
    if (!nextTokenIs(builder_, KW_SATISFIES)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_SATISFIES);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (result_) {
      marker_.done(SATISFIES_CASE_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "satisfies" (qualifiedType qualifiedType)?
  public static boolean satisfiesCondition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiesCondition")) return false;
    if (!nextTokenIs(builder_, KW_SATISFIES)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_SATISFIES);
    result_ = result_ && satisfiesCondition_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(SATISFIES_CONDITION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (qualifiedType qualifiedType)?
  private static boolean satisfiesCondition_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiesCondition_1")) return false;
    satisfiesCondition_1_0(builder_, level_ + 1);
    return true;
  }

  // qualifiedType qualifiedType
  private static boolean satisfiesCondition_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "satisfiesCondition_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = qualifiedType(builder_, level_ + 1);
    result_ = result_ && qualifiedType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "this"
  //     | "super" 
  //     | "outer"
  public static boolean selfReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "selfReference")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<self reference>");
    result_ = consumeToken(builder_, KW_THIS);
    if (!result_) result_ = consumeToken(builder_, KW_SUPER);
    if (!result_) result_ = consumeToken(builder_, KW_OUTER);
    if (result_) {
      marker_.done(SELF_REFERENCE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotations expressions "..."?
  public static boolean sequencedArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequencedArgument")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<sequenced argument>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && expressions(builder_, level_ + 1);
    result_ = result_ && sequencedArgument_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(SEQUENCED_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // "..."?
  private static boolean sequencedArgument_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequencedArgument_2")) return false;
    consumeToken(builder_, OP_ELLIPSIS);
    return true;
  }

  /* ********************************************************** */
  // "assign" memberNameDeclaration (block | ";")
  public static boolean setterDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "setterDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_ASSIGN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_ASSIGN);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && setterDeclaration_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(SETTER_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // block | ";"
  private static boolean setterDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "setterDeclaration_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = block(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // variable specifier?
  public static boolean specifiedVariable(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specifiedVariable")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<specified variable>");
    result_ = variable(builder_, level_ + 1);
    result_ = result_ && specifiedVariable_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(SPECIFIED_VARIABLE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // specifier?
  private static boolean specifiedVariable_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specifiedVariable_1")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "=" functionOrExpression
  public static boolean specifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specifier")) return false;
    if (!nextTokenIs(builder_, OP_EQUALS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_EQUALS);
    result_ = result_ && functionOrExpression(builder_, level_ + 1);
    if (result_) {
      marker_.done(SPECIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // directiveStatement | controlStatement | expressionOrSpecificationStatement
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<statement>");
    result_ = directiveStatement(builder_, level_ + 1);
    if (!result_) result_ = controlStatement(builder_, level_ + 1);
    if (!result_) result_ = expressionOrSpecificationStatement(builder_, level_ + 1);
    if (result_) {
      marker_.done(STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // stringTemplate | myStringLiteral
  public static boolean stringExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringExpression")) return false;
    if (!nextTokenIs(builder_, STRING_LITERAL)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = stringTemplate(builder_, level_ + 1);
    if (!result_) result_ = myStringLiteral(builder_, level_ + 1);
    if (result_) {
      marker_.done(STRING_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // myStringLiteral
  //       (
  //         expression myStringLiteral
  //       )+
  public static boolean stringTemplate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringTemplate")) return false;
    if (!nextTokenIs(builder_, STRING_LITERAL)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = myStringLiteral(builder_, level_ + 1);
    result_ = result_ && stringTemplate_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(STRING_TEMPLATE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (
  //         expression myStringLiteral
  //       )+
  private static boolean stringTemplate_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringTemplate_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = stringTemplate_1_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!stringTemplate_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "stringTemplate_1");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // expression myStringLiteral
  private static boolean stringTemplate_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "stringTemplate_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && myStringLiteral(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // typeName "::"
  public static boolean supertypeQualifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "supertypeQualifier")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "::");
    if (result_) {
      marker_.done(SUPERTYPE_QUALIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // switchHeader cases
  public static boolean switchCaseElse(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switchCaseElse")) return false;
    if (!nextTokenIs(builder_, KW_SWITCH)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = switchHeader(builder_, level_ + 1);
    result_ = result_ && cases(builder_, level_ + 1);
    if (result_) {
      marker_.done(SWITCH_CASE_ELSE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // "switch" "(" expression? ")"
  public static boolean switchHeader(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switchHeader")) return false;
    if (!nextTokenIs(builder_, KW_SWITCH)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_SWITCH);
    result_ = result_ && consumeToken(builder_, OP_LPAREN);
    result_ = result_ && switchHeader_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_RPAREN);
    if (result_) {
      marker_.done(SWITCH_HEADER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // expression?
  private static boolean switchHeader_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switchHeader_2")) return false;
    expression(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // disjunctionExpression
  //       (
  //         thenElseOperator 
  //         disjunctionExpression
  //       )*
  public static boolean thenElseExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "thenElseExpression")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<then else expression>");
    result_ = disjunctionExpression(builder_, level_ + 1);
    result_ = result_ && thenElseExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(THEN_ELSE_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (
  //         thenElseOperator 
  //         disjunctionExpression
  //       )*
  private static boolean thenElseExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "thenElseExpression_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!thenElseExpression_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "thenElseExpression_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // thenElseOperator 
  //         disjunctionExpression
  private static boolean thenElseExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "thenElseExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = thenElseOperator(builder_, level_ + 1);
    result_ = result_ && disjunctionExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "else" 
  //     | "then"
  public static boolean thenElseOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "thenElseOperator")) return false;
    if (!nextTokenIs(builder_, KW_ELSE) && !nextTokenIs(builder_, KW_THEN)
        && replaceVariants(builder_, 2, "<then else operator>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<then else operator>");
    result_ = consumeToken(builder_, KW_ELSE);
    if (!result_) result_ = consumeToken(builder_, KW_THEN);
    if (result_) {
      marker_.done(THEN_ELSE_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "throw" expression?
  public static boolean throwDirective(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "throwDirective")) return false;
    if (!nextTokenIs(builder_, KW_THROW)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_THROW);
    result_ = result_ && throwDirective_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(THROW_DIRECTIVE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // expression?
  private static boolean throwDirective_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "throwDirective_1")) return false;
    expression(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "try" (resource controlBlock | block)
  public static boolean tryBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryBlock")) return false;
    if (!nextTokenIs(builder_, KW_TRY)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_TRY);
    result_ = result_ && tryBlock_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(TRY_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // resource controlBlock | block
  private static boolean tryBlock_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryBlock_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = tryBlock_1_0(builder_, level_ + 1);
    if (!result_) result_ = block(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // resource controlBlock
  private static boolean tryBlock_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryBlock_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = resource(builder_, level_ + 1);
    result_ = result_ && controlBlock(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // tryBlock catchBlock* finallyBlock?
  public static boolean tryCatchFinally(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryCatchFinally")) return false;
    if (!nextTokenIs(builder_, KW_TRY)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = tryBlock(builder_, level_ + 1);
    result_ = result_ && tryCatchFinally_1(builder_, level_ + 1);
    result_ = result_ && tryCatchFinally_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(TRY_CATCH_FINALLY);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // catchBlock*
  private static boolean tryCatchFinally_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryCatchFinally_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!catchBlock(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "tryCatchFinally_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // finallyBlock?
  private static boolean tryCatchFinally_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tryCatchFinally_2")) return false;
    finallyBlock(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // unionType
  public static boolean type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = unionType(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // type
  //       ( 
  //         "..."
  //       )?
  public static boolean typeArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArgument")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    result_ = result_ && typeArgument_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( 
  //         "..."
  //       )?
  private static boolean typeArgument_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArgument_1")) return false;
    typeArgument_1_0(builder_, level_ + 1);
    return true;
  }

  // ( 
  //         "..."
  //       )
  private static boolean typeArgument_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArgument_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ELLIPSIS);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "<"
  //       typeArgument 
  //       (
  //         ","
  //         (
  //           typeArgument
  //           | 
  //         )
  //       )* 
  //       ">"
  public static boolean typeArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArguments")) return false;
    if (!nextTokenIs(builder_, OP_LT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LT);
    result_ = result_ && typeArgument(builder_, level_ + 1);
    result_ = result_ && typeArguments_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_GT);
    if (result_) {
      marker_.done(TYPE_ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (
  //         ","
  //         (
  //           typeArgument
  //           | 
  //         )
  //       )*
  private static boolean typeArguments_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArguments_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!typeArguments_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "typeArguments_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ","
  //         (
  //           typeArgument
  //           | 
  //         )
  private static boolean typeArguments_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArguments_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && typeArguments_2_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeArgument
  //           | 
  //         
  private static boolean typeArguments_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeArguments_2_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeArgument(builder_, level_ + 1);
    if (!result_) result_ = typeArguments_2_0_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  private static boolean typeArguments_2_0_1_1(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // compilerAnnotations "given" typeNameDeclaration parameters? caseTypes? /*metatypes?*/ satisfiedTypes? abstractedType?
  public static boolean typeConstraint(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraint")) return false;
    if (!nextTokenIs(builder_, OP_ANNOTATION) && !nextTokenIs(builder_, KW_GIVEN)
        && replaceVariants(builder_, 2, "<type constraint>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<type constraint>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, KW_GIVEN);
    result_ = result_ && typeNameDeclaration(builder_, level_ + 1);
    result_ = result_ && typeConstraint_3(builder_, level_ + 1);
    result_ = result_ && typeConstraint_4(builder_, level_ + 1);
    result_ = result_ && typeConstraint_5(builder_, level_ + 1);
    result_ = result_ && typeConstraint_6(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_CONSTRAINT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // parameters?
  private static boolean typeConstraint_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraint_3")) return false;
    parameters(builder_, level_ + 1);
    return true;
  }

  // caseTypes?
  private static boolean typeConstraint_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraint_4")) return false;
    caseTypes(builder_, level_ + 1);
    return true;
  }

  // satisfiedTypes?
  private static boolean typeConstraint_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraint_5")) return false;
    satisfiedTypes(builder_, level_ + 1);
    return true;
  }

  // abstractedType?
  private static boolean typeConstraint_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraint_6")) return false;
    abstractedType(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // typeConstraint+
  public static boolean typeConstraints(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeConstraints")) return false;
    if (!nextTokenIs(builder_, OP_ANNOTATION) && !nextTokenIs(builder_, KW_GIVEN)
        && replaceVariants(builder_, 2, "<type constraints>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<type constraints>");
    result_ = typeConstraint(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!typeConstraint(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "typeConstraints");
        break;
      }
      offset_ = next_offset_;
    }
    if (result_) {
      marker_.done(TYPE_CONSTRAINTS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // UIDENTIFIER
  public static boolean typeName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeName")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, UIDENTIFIER);
    if (result_) {
      marker_.done(TYPE_NAME);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // typeName | memberName
  public static boolean typeNameDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeNameDeclaration")) return false;
    if (!nextTokenIs(builder_, LIDENTIFIER) && !nextTokenIs(builder_, UIDENTIFIER)
        && replaceVariants(builder_, 2, "<type name declaration>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<type name declaration>");
    result_ = typeName(builder_, level_ + 1);
    if (!result_) result_ = memberName(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_NAME_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // typeName typeArguments?
  public static boolean typeNameWithArguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeNameWithArguments")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeName(builder_, level_ + 1);
    result_ = result_ && typeNameWithArguments_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_NAME_WITH_ARGUMENTS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // typeArguments?
  private static boolean typeNameWithArguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeNameWithArguments_1")) return false;
    typeArguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "is"
  //     | "extends"
  //     | "satisfies"
  public static boolean typeOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeOperator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<type operator>");
    result_ = consumeToken(builder_, KW_IS);
    if (!result_) result_ = consumeToken(builder_, KW_EXTENDS);
    if (!result_) result_ = consumeToken(builder_, KW_SATISFIES);
    if (result_) {
      marker_.done(TYPE_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // variance? typeNameDeclaration "..."?
  public static boolean typeParameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameter")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<type parameter>");
    result_ = typeParameter_0(builder_, level_ + 1);
    result_ = result_ && typeNameDeclaration(builder_, level_ + 1);
    result_ = result_ && typeParameter_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_PARAMETER);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // variance?
  private static boolean typeParameter_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameter_0")) return false;
    variance(builder_, level_ + 1);
    return true;
  }

  // "..."?
  private static boolean typeParameter_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameter_2")) return false;
    consumeToken(builder_, OP_ELLIPSIS);
    return true;
  }

  /* ********************************************************** */
  // "<" typeParameter ("," typeParameter)* ">"
  public static boolean typeParameters(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameters")) return false;
    if (!nextTokenIs(builder_, OP_LT)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_LT);
    result_ = result_ && typeParameter(builder_, level_ + 1);
    result_ = result_ && typeParameters_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_GT);
    if (result_) {
      marker_.done(TYPE_PARAMETERS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ("," typeParameter)*
  private static boolean typeParameters_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameters_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!typeParameters_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "typeParameters_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // "," typeParameter
  private static boolean typeParameters_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeParameters_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_COMMA);
    result_ = result_ && typeParameter(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // typeName typeArguments?
  public static boolean typeReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeReference")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typeName(builder_, level_ + 1);
    result_ = result_ && typeReference_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_REFERENCE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // typeArguments?
  private static boolean typeReference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeReference_1")) return false;
    typeArguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "=" type
  public static boolean typeSpecifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typeSpecifier")) return false;
    if (!nextTokenIs(builder_, OP_EQUALS)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_EQUALS);
    result_ = result_ && type(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPE_SPECIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // type memberNameDeclaration
  //     (
  //         typeParameters? parameters+ /*metatypes?*/ typeConstraints? (block | specifier? ";")
  //       | (specifier | initializer)? ";"
  //       | block
  //     )
  public static boolean typedMethodOrAttributeDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && typedMethodOrAttributeDeclaration_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPED_METHOD_OR_ATTRIBUTE_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // typeParameters? parameters+ /*metatypes?*/ typeConstraints? (block | specifier? ";")
  //       | (specifier | initializer)? ";"
  //       | block
  private static boolean typedMethodOrAttributeDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typedMethodOrAttributeDeclaration_2_0(builder_, level_ + 1);
    if (!result_) result_ = typedMethodOrAttributeDeclaration_2_1(builder_, level_ + 1);
    if (!result_) result_ = block(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeParameters? parameters+ /*metatypes?*/ typeConstraints? (block | specifier? ";")
  private static boolean typedMethodOrAttributeDeclaration_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typedMethodOrAttributeDeclaration_2_0_0(builder_, level_ + 1);
    result_ = result_ && typedMethodOrAttributeDeclaration_2_0_1(builder_, level_ + 1);
    result_ = result_ && typedMethodOrAttributeDeclaration_2_0_2(builder_, level_ + 1);
    result_ = result_ && typedMethodOrAttributeDeclaration_2_0_3(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeParameters?
  private static boolean typedMethodOrAttributeDeclaration_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_0")) return false;
    typeParameters(builder_, level_ + 1);
    return true;
  }

  // parameters+
  private static boolean typedMethodOrAttributeDeclaration_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!parameters(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "typedMethodOrAttributeDeclaration_2_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeConstraints?
  private static boolean typedMethodOrAttributeDeclaration_2_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_2")) return false;
    typeConstraints(builder_, level_ + 1);
    return true;
  }

  // block | specifier? ";"
  private static boolean typedMethodOrAttributeDeclaration_2_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_3")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = block(builder_, level_ + 1);
    if (!result_) result_ = typedMethodOrAttributeDeclaration_2_0_3_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // specifier? ";"
  private static boolean typedMethodOrAttributeDeclaration_2_0_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_3_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typedMethodOrAttributeDeclaration_2_0_3_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // specifier?
  private static boolean typedMethodOrAttributeDeclaration_2_0_3_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_0_3_1_0")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  // (specifier | initializer)? ";"
  private static boolean typedMethodOrAttributeDeclaration_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typedMethodOrAttributeDeclaration_2_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (specifier | initializer)?
  private static boolean typedMethodOrAttributeDeclaration_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_1_0")) return false;
    typedMethodOrAttributeDeclaration_2_1_0_0(builder_, level_ + 1);
    return true;
  }

  // specifier | initializer
  private static boolean typedMethodOrAttributeDeclaration_2_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrAttributeDeclaration_2_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = specifier(builder_, level_ + 1);
    if (!result_) result_ = initializer(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // type 
  //       memberNameDeclaration
  //       ( 
  //         (
  //           parameters
  //         )+
  //       )?
  //       block
  public static boolean typedMethodOrGetterArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrGetterArgument")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && typedMethodOrGetterArgument_2(builder_, level_ + 1);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(TYPED_METHOD_OR_GETTER_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( 
  //         (
  //           parameters
  //         )+
  //       )?
  private static boolean typedMethodOrGetterArgument_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrGetterArgument_2")) return false;
    typedMethodOrGetterArgument_2_0(builder_, level_ + 1);
    return true;
  }

  // (
  //           parameters
  //         )+
  private static boolean typedMethodOrGetterArgument_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrGetterArgument_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = typedMethodOrGetterArgument_2_0_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!typedMethodOrGetterArgument_2_0_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "typedMethodOrGetterArgument_2_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //           parameters
  //         )
  private static boolean typedMethodOrGetterArgument_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedMethodOrGetterArgument_2_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "-" 
  //     | "+"
  public static boolean unaryMinusOrComplementOperator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unaryMinusOrComplementOperator")) return false;
    if (!nextTokenIs(builder_, OP_PLUS) && !nextTokenIs(builder_, OP_MINUS)
        && replaceVariants(builder_, 2, "<unary minus or complement operator>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<unary minus or complement operator>");
    result_ = consumeToken(builder_, OP_MINUS);
    if (!result_) result_ = consumeToken(builder_, OP_PLUS);
    if (result_) {
      marker_.done(UNARY_MINUS_OR_COMPLEMENT_OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // intersectionType
  //       ( 
  //         (
  //           "|"
  //           (
  //             intersectionType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  //       )?
  public static boolean unionType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionType")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = intersectionType(builder_, level_ + 1);
    result_ = result_ && unionType_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(UNION_TYPE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // ( 
  //         (
  //           "|"
  //           (
  //             intersectionType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  //       )?
  private static boolean unionType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionType_1")) return false;
    unionType_1_0(builder_, level_ + 1);
    return true;
  }

  // (
  //           "|"
  //           (
  //             intersectionType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  //         )+
  private static boolean unionType_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionType_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = unionType_1_0_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!unionType_1_0_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "unionType_1_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "|"
  //           (
  //             intersectionType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  private static boolean unionType_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionType_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_UNION);
    result_ = result_ && unionType_1_0_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //             intersectionType
  // //          | { displayRecognitionError(getTokenNames(), 
  // //                new MismatchedTokenException(UIDENTIFIER, input)); }
  //           )
  private static boolean unionType_1_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionType_1_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = intersectionType(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // intersectionTypeExpression (("|" intersectionTypeExpression)+)?
  public static boolean unionTypeExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionTypeExpression")) return false;
    if (!nextTokenIs(builder_, UIDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = intersectionTypeExpression(builder_, level_ + 1);
    result_ = result_ && unionTypeExpression_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(UNION_TYPE_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (("|" intersectionTypeExpression)+)?
  private static boolean unionTypeExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionTypeExpression_1")) return false;
    unionTypeExpression_1_0(builder_, level_ + 1);
    return true;
  }

  // ("|" intersectionTypeExpression)+
  private static boolean unionTypeExpression_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionTypeExpression_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = unionTypeExpression_1_0_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!unionTypeExpression_1_0_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "unionTypeExpression_1_0");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // "|" intersectionTypeExpression
  private static boolean unionTypeExpression_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unionTypeExpression_1_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_UNION);
    result_ = result_ && intersectionTypeExpression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // "->" type memberName
  public static boolean valueParameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "valueParameter")) return false;
    if (!nextTokenIs(builder_, OP_ARROW)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_ARROW);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && memberName(builder_, level_ + 1);
    if (result_) {
      marker_.done(VALUE_PARAMETER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // (type | "void" | "function" | "value") memberName parameters* | memberName | memberName parameters+
  public static boolean var(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<var>");
    result_ = var_0(builder_, level_ + 1);
    if (!result_) result_ = memberName(builder_, level_ + 1);
    if (!result_) result_ = var_2(builder_, level_ + 1);
    if (result_) {
      marker_.done(VAR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // (type | "void" | "function" | "value") memberName parameters*
  private static boolean var_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = var_0_0(builder_, level_ + 1);
    result_ = result_ && memberName(builder_, level_ + 1);
    result_ = result_ && var_0_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // type | "void" | "function" | "value"
  private static boolean var_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = type(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, KW_VOID);
    if (!result_) result_ = consumeToken(builder_, KW_FUNCTION);
    if (!result_) result_ = consumeToken(builder_, KW_VALUE);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // parameters*
  private static boolean var_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_0_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!parameters(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "var_0_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // memberName parameters+
  private static boolean var_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = memberName(builder_, level_ + 1);
    result_ = result_ && var_2_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // parameters+
  private static boolean var_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_2_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!parameters(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "var_2_1");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // compilerAnnotations var
  public static boolean variable(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<variable>");
    result_ = compilerAnnotations(builder_, level_ + 1);
    result_ = result_ && var(builder_, level_ + 1);
    if (result_) {
      marker_.done(VARIABLE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // "in" | "out"
  public static boolean variance(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variance")) return false;
    if (!nextTokenIs(builder_, KW_IN) && !nextTokenIs(builder_, KW_OUT)
        && replaceVariants(builder_, 2, "<variance>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<variance>");
    result_ = consumeToken(builder_, KW_IN);
    if (!result_) result_ = consumeToken(builder_, KW_OUT);
    if (result_) {
      marker_.done(VARIANCE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // (
  //         "void"
  //       |
  //         "function"
  //       ) 
  //       memberNameDeclaration 
  //       (
  //         parameters
  //       )*
  //       block
  public static boolean voidOrInferredMethodArgument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodArgument")) return false;
    if (!nextTokenIs(builder_, KW_FUNCTION) && !nextTokenIs(builder_, KW_VOID)
        && replaceVariants(builder_, 2, "<void or inferred method argument>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<void or inferred method argument>");
    result_ = voidOrInferredMethodArgument_0(builder_, level_ + 1);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && voidOrInferredMethodArgument_2(builder_, level_ + 1);
    result_ = result_ && block(builder_, level_ + 1);
    if (result_) {
      marker_.done(VOID_OR_INFERRED_METHOD_ARGUMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // "void"
  //       |
  //         "function"
  private static boolean voidOrInferredMethodArgument_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodArgument_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_VOID);
    if (!result_) result_ = consumeToken(builder_, KW_FUNCTION);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (
  //         parameters
  //       )*
  private static boolean voidOrInferredMethodArgument_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodArgument_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!voidOrInferredMethodArgument_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "voidOrInferredMethodArgument_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (
  //         parameters
  //       )
  private static boolean voidOrInferredMethodArgument_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodArgument_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = parameters(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // ("void" | "function") memberNameDeclaration typeParameters? parameters* /*metatypes? */ typeConstraints? (block | specifier? ";")
  public static boolean voidOrInferredMethodDeclaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration")) return false;
    if (!nextTokenIs(builder_, KW_FUNCTION) && !nextTokenIs(builder_, KW_VOID)
        && replaceVariants(builder_, 2, "<void or inferred method declaration>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<void or inferred method declaration>");
    result_ = voidOrInferredMethodDeclaration_0(builder_, level_ + 1);
    result_ = result_ && memberNameDeclaration(builder_, level_ + 1);
    result_ = result_ && voidOrInferredMethodDeclaration_2(builder_, level_ + 1);
    result_ = result_ && voidOrInferredMethodDeclaration_3(builder_, level_ + 1);
    result_ = result_ && voidOrInferredMethodDeclaration_4(builder_, level_ + 1);
    result_ = result_ && voidOrInferredMethodDeclaration_5(builder_, level_ + 1);
    if (result_) {
      marker_.done(VOID_OR_INFERRED_METHOD_DECLARATION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // "void" | "function"
  private static boolean voidOrInferredMethodDeclaration_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_VOID);
    if (!result_) result_ = consumeToken(builder_, KW_FUNCTION);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // typeParameters?
  private static boolean voidOrInferredMethodDeclaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_2")) return false;
    typeParameters(builder_, level_ + 1);
    return true;
  }

  // parameters*
  private static boolean voidOrInferredMethodDeclaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_3")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!parameters(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "voidOrInferredMethodDeclaration_3");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // typeConstraints?
  private static boolean voidOrInferredMethodDeclaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_4")) return false;
    typeConstraints(builder_, level_ + 1);
    return true;
  }

  // block | specifier? ";"
  private static boolean voidOrInferredMethodDeclaration_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_5")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = block(builder_, level_ + 1);
    if (!result_) result_ = voidOrInferredMethodDeclaration_5_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // specifier? ";"
  private static boolean voidOrInferredMethodDeclaration_5_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_5_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = voidOrInferredMethodDeclaration_5_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OP_SEMI_COLUMN);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // specifier?
  private static boolean voidOrInferredMethodDeclaration_5_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "voidOrInferredMethodDeclaration_5_1_0")) return false;
    specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // "while" conditions controlBlock
  public static boolean whileBlock(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whileBlock")) return false;
    if (!nextTokenIs(builder_, KW_WHILE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, KW_WHILE);
    result_ = result_ && conditions(builder_, level_ + 1);
    result_ = result_ && controlBlock(builder_, level_ + 1);
    if (result_) {
      marker_.done(WHILE_BLOCK);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // whileBlock
  public static boolean whileLoop(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "whileLoop")) return false;
    if (!nextTokenIs(builder_, KW_WHILE)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = whileBlock(builder_, level_ + 1);
    if (result_) {
      marker_.done(WHILE_LOOP);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  final static Parser ceylon_statement_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return ceylon_statement_recover(builder_, level_ + 1);
    }
  };
  final static Parser compilationUnit_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return compilationUnit(builder_, level_ + 1);
    }
  };
}
