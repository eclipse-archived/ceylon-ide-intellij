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
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals {
    GrLiteral
}
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path {
    GrMethodCallExpression
}
import org.jetbrains.plugins.groovy.lang.psi.api.util {
    GrStatementOwner
}

// Mainly adapted from org.jetbrains.kotlin.idea.configuration.KotlinWithGradleConfigurator
object groovyFileManipulator {
    value sourceSet = "main.java.srcDirs += 'src/main/ceylon'\n";
    value ceylonAndroidDep = "classpath 'com.redhat.ceylon.gradle:android:0.0.4'\n";
    value repositoryName = "jcenter()\n";

    GrClosableBlock getAndroidBlock(GroovyFile buildFile)
        => getBlockOrCreate(buildFile, "android");

    GrClosableBlock getSourceSetsBlock(GrStatementOwner parent)
        => getBlockOrCreate(parent, "sourceSets");

    GrClosableBlock getCeylonBlock(GrStatementOwner parent)
        => getBlockOrCreate(parent, "ceylon");

    Boolean addLastExpressionInBlockIfNeeded(String text, GrClosableBlock block)
        => addExpressionInBlockIfNeeded(text, block, false);

    shared Boolean addApplyDirective(GroovyFile file, String applyPluginDirective) {
        if (!containsDirective(file.text, applyPluginDirective)) {
            value apply = GroovyPsiElementFactory.getInstance(file.project)
                .createExpressionFromText(javaString(applyPluginDirective));

            if (exists applyStatement = getApplyStatement(file)) {
                file.addAfter(apply, applyStatement);
            } else {
                if (exists buildScript = getBlockByName(file, "buildscript")) {
                    file.addAfter(apply, buildScript.parent);
                } else {
                    value statements = file.statements;
                    if (statements.size > 0) {
                        file.addAfter(apply, statements.get(statements.size - 1));
                    } else {
                        file.addAfter(apply, file.firstChild);
                    }
                }
            }

            return true;
        }

        return false;
    }

    shared Boolean configureRepository(GroovyFile file) {
        value buildScriptBlock = getBlockOrCreate(file, "buildscript", true);
        value repositoriesBlock = getRepositoriesBlock(buildScriptBlock);

        variable Boolean wasModified = false;

        if (!isRepositoryConfigured(repositoriesBlock)) {
            wasModified ||= addLastExpressionInBlockIfNeeded(repositoryName, repositoriesBlock);
        }

        GrClosableBlock dependenciesBlock = getDependenciesBlock(buildScriptBlock);
        wasModified ||=addExpressionInBlockIfNeeded(ceylonAndroidDep, dependenciesBlock, false);

        return wasModified;
    }

    shared Boolean configureSourceSet(GroovyFile buildFile) {
        value androidBlock = getAndroidBlock(buildFile);
        value sourceSets = getSourceSetsBlock(androidBlock);

        return addLastExpressionInBlockIfNeeded(sourceSet, sourceSets);
    }

    shared Boolean configureLint(GroovyFile buildFile) {
        value androidBlock = getAndroidBlock(buildFile);
        value lintOptions = getBlockOrCreate(androidBlock, "lintOptions");

        return addLastExpressionInBlockIfNeeded("disable 'InvalidPackage'", lintOptions);
    }

    shared Boolean configureCeylonModule(GroovyFile buildFile) {
        if (exists version = findModuleName(buildFile)) {
            value ceylonBlock = getCeylonBlock(buildFile);
            return addLastExpressionInBlockIfNeeded(
               "module \"``version``\"", ceylonBlock);
        }

        return false;
    }

    shared String? findAndroidVersion(GroovyFile file) {
        if (exists blck = getBlockByName(file, "android"),
            exists call = getCallExpressionByName(blck, "compileSdkVersion"),
            exists arg = call.argumentList.allArguments.get(0)) {

            return arg.text;
        }

        return null;
    }

    String? findModuleName(GroovyFile file) {
        if (exists blck = getBlockByName(file, "android"),
            exists blck2 = getBlockByName(blck, "defaultConfig")) {

            String? name;
            String? version;

            if (exists call = getCallExpressionByName(blck2, "applicationId"),
                is GrLiteral arg = call.argumentList.allArguments.get(0)) {

                name = arg.text[1..arg.text.size - 2];
            } else {
                name = null;
            }

            if (exists call = getCallExpressionByName(blck2, "versionName"),
                is GrLiteral arg = call.argumentList.allArguments.get(0)) {

                version = arg.text[1..arg.text.size - 2];
            } else {
                version = null;
            }

            if (exists name, exists version) {
                return name + "/" + version;
            }
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

    GrClosableBlock getBlockOrCreate(GrStatementOwner parent, String name, Boolean first = false) {
        if (exists block = getBlockByName(parent, name)) {
            return block;
        } else {
            value factory = GroovyPsiElementFactory.getInstance(parent.project);
            value indents = if (is GroovyFile parent) then "" else "    ";
            value newBlock = factory.createExpressionFromText(
                javaString(name + "{\n" + indents + "}\n"));
            value statements = parent.statements;
            value add = if (first) then parent.addBefore else parent.addAfter;

            if (statements.size > 0) {
                add(newBlock, first then statements.get(0) else statements.get(statements.size - 1));
            } else {
                add(newBlock, parent.firstChild);
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

    GrClosableBlock getRepositoriesBlock(GrStatementOwner file)
            => getBlockOrCreate(file, "repositories");

    GrClosableBlock getDependenciesBlock(GrStatementOwner file)
            => getBlockOrCreate(file, "dependencies");

    Boolean isRepositoryConfigured(GrClosableBlock repositoriesBlock)
            => repositoriesBlock.text.contains(repositoryName);

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
