import ceylon.interop.java {
    javaClass,
    javaString
}

import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.codeStyle {
    CodeStyleManager
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import org.jetbrains.plugins.groovy.lang.psi {
    GroovyPsiElementFactory,
    GroovyFile
}
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks {
    GrClosableBlock
}
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions {
    GrApplicationStatement,
    GrMethodCall
}
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path {
    GrMethodCallExpression
}
import org.jetbrains.plugins.groovy.lang.psi.api.util {
    GrStatementOwner
}

// Mainly adapted from org.jetbrains.kotlin.idea.configuration.KotlinWithGradleConfigurator
object groovyFileManipulator {
    value mavenBintray = "url \"http://dl.bintray.com/fromage/maven\"\n";
    value ceylonAndroidDep = "classpath 'com.redhat.ceylon.gradle:android:0.0.3'";


    shared GrClosableBlock getAndroidBlock(GroovyFile buildFile)
        => getBlockOrCreate(buildFile, "android");

    shared GrClosableBlock getSourceSetsBlock(GrStatementOwner parent)
        => getBlockOrCreate(parent, "sourceSets");

    shared Boolean addLastExpressionInBlockIfNeeded(String text, GrClosableBlock block)
        => addExpressionInBlockIfNeeded(text, block, false);

    shared Boolean addApplyDirective(GroovyFile file, String applyPluginDirective) {
        variable Boolean wasModified = false;

        if (!containsDirective(file.text, applyPluginDirective)) {
            value apply = GroovyPsiElementFactory.getInstance(file.project)
                .createExpressionFromText(javaString(applyPluginDirective));

            if (exists applyStatement = getApplyStatement(file)) {
                file.addAfter(apply, applyStatement);
                wasModified = true;
            } else {
                if (exists buildScript = getBlockByName(file, "buildscript")) {
                    file.addAfter(apply, buildScript.parent);
                    wasModified = true;
                } else {
                    value statements = file.statements;
                    if (statements.size > 0) {
                        file.addAfter(apply, statements.get(statements.size - 1));
                    } else {
                        file.addAfter(apply, file.firstChild);
                    }
                    wasModified = true;
                }
            }
        }

        return wasModified;
    }

    shared void configureRepository(GroovyFile file) {
        value buildScriptBlock = getBlockOrCreate(file, "buildscript");
        value repositoriesBlock = getRepositoriesBlock(buildScriptBlock);
        value mavenBlock = getBlockOrCreate(repositoriesBlock, "maven");

        if (!isRepositoryConfigured(mavenBlock)) {
            addLastExpressionInBlockIfNeeded(mavenBintray, mavenBlock);
        }

        GrClosableBlock dependenciesBlock = getDependenciesBlock(buildScriptBlock);
        addExpressionInBlockIfNeeded(ceylonAndroidDep, dependenciesBlock, false);
    }

    shared String? findAndroidVersion(GroovyFile file) {
        if (exists blck = getBlockByName(file, "android"),
            exists call = getCallExpressionByName(blck, "compileSdkVersion"),
            exists arg = call.argumentList.allArguments.get(0)) {

            return arg.text;
        }

        return null;
    }

    GrMethodCall? getCallExpressionByName(GrStatementOwner parent, String name) {
        if (exists allExpressions = PsiTreeUtil.getChildrenOfType(parent, javaClass<GrMethodCall>())) {
            for (expression in allExpressions) {
                if (expression.invokedExpression.text == name) {
                    return expression;
                }
            }
        }

        return null;
    }

    GrClosableBlock getBlockOrCreate(GrStatementOwner parent, String name) {
        if (exists block = getBlockByName(parent, name)) {
            return block;
        } else {
            value factory = GroovyPsiElementFactory.getInstance(parent.project);
            value newBlock = factory.createExpressionFromText(javaString(name + "{\n}\n"));
            value statements = parent.statements;
            if (statements.size > 0) {
                parent.addAfter(newBlock, statements.get(statements.size - 1));
            } else {
                parent.addAfter(newBlock, parent.firstChild);
            }

            if (exists block = getBlockByName(parent, name)) {
                return block;
            }
            throw AssertionError("Block should be non-null because it is created");
        }
    }

    GrClosableBlock? getBlockByName(PsiElement parent, String name) {
        if (exists allExpressions = PsiTreeUtil.getChildrenOfType(parent, javaClass<GrMethodCallExpression>())) {
            for (GrMethodCallExpression expression in allExpressions) {
                value invokedExpression = expression.invokedExpression;
                if (expression.closureArguments.size == 0) {
                    continue;
                }
                String expressionText = invokedExpression.text;
                if (expressionText.equals(name)) {
                    return expression.closureArguments.get(0);
                }
            }
        }
        return null;
    }

    Boolean addExpressionInBlockIfNeeded(String text, GrClosableBlock block, Boolean isFirst) {
        if (block.text.contains(text)) {
            return false;
        }
        value newStatement = GroovyPsiElementFactory.getInstance(block.project)
            .createExpressionFromText(javaString(text));
        CodeStyleManager.getInstance(block.project).reformat(newStatement);
        value statements = block.statements;

        if (!isFirst, statements.size > 0) {
            if (exists lastStatement = statements.get(statements.size - 1)) {
                block.addAfter(newStatement, lastStatement);
            }
        } else {
            if (exists firstChild = block.firstChild) {
                block.addAfter(newStatement, firstChild);
            }
        }
        return true;
    }

    GrClosableBlock getRepositoriesBlock(GrStatementOwner file) {
        return getBlockOrCreate(file, "repositories");
    }

    GrClosableBlock getDependenciesBlock(GrStatementOwner file) {
        return getBlockOrCreate(file, "dependencies");
    }

    Boolean isRepositoryConfigured(GrClosableBlock repositoriesBlock) {
        return repositoriesBlock.text.contains(mavenBintray);
    }

    Boolean containsDirective(String fileText, String directive) {
        return fileText.contains(directive)
            || fileText.contains(directive.replace("\"", "'"))
            || fileText.contains(directive.replace("'", "\""));
    }

    GrApplicationStatement? getApplyStatement(GroovyFile file) {
        if (exists applyStatement = PsiTreeUtil.getChildrenOfType(file, javaClass<GrApplicationStatement>())) {
            for (callExpression in applyStatement) {
                value invokedExpression = callExpression.invokedExpression;
                if (invokedExpression.text.equals("apply")) {
                    return callExpression;
                }
            }
        }
        return null;
    }
}
