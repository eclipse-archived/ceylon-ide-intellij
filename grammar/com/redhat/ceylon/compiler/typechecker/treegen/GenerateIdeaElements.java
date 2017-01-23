package com.redhat.ceylon.compiler.typechecker.treegen;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;

import java.io.*;

public class GenerateIdeaElements {

    private static final String GENERATED_PACKAGE_DIR = "org/intellij/plugins/ceylon/ide/psi/";

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        File outputDirectory = new File(args[1], GENERATED_PACKAGE_DIR);
        outputDirectory.mkdirs();
        ideaPsiIntf(file, outputDirectory);
        ideaPsiImpl(file, outputDirectory);
        ideaPsiFactory(file, outputDirectory);
        ideaNodeToIElementTypeMap(file, outputDirectory);
        ideaAstTypes(file, outputDirectory);
    }
    
    private static void ideaAstTypes(File file, File outputDirectory) throws Exception {
        ANTLRInputStream input = getAntlrInputStream(file);
        IdeaAstTypesGenLexer lexer = new IdeaAstTypesGenLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        IdeaAstTypesGenParser parser = new IdeaAstTypesGenParser(tokens);
        setOut("CeylonTypes", outputDirectory);
        parser.nodeList();
    }

    private static void ideaPsiIntf(File file, File outputDirectory) throws Exception {
        ANTLRInputStream input = getAntlrInputStream(file);
        PsiIntfGenLexer lexer = new PsiIntfGenLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PsiIntfGenParser parser = new PsiIntfGenParser(tokens);
        setOut("CeylonPsi", outputDirectory);
        parser.nodeList();
    }

    private static void ideaPsiImpl(File file, File outputDirectory) throws Exception {
        ANTLRInputStream input = getAntlrInputStream(file);
        PsiImplGenLexer lexer = new PsiImplGenLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PsiImplGenParser parser = new PsiImplGenParser(tokens);
        setOut("CeylonPsiImpl", outputDirectory);
        parser.nodeList();
    }

    private static void ideaPsiFactory(File file, File outputDirectory) throws Exception {
        ANTLRInputStream input = getAntlrInputStream(file);
        PsiFactoryGenLexer lexer = new PsiFactoryGenLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PsiFactoryGenParser parser = new PsiFactoryGenParser(tokens);
        setOut("CeylonPsiFactory", outputDirectory);
        parser.nodeList();
    }

    private static void ideaNodeToIElementTypeMap(File file, File outputDirectory) throws Exception {
        ANTLRInputStream input = getAntlrInputStream(file);
        NodeToIElementTypeMapGenLexer lexer = new NodeToIElementTypeMapGenLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NodeToIElementTypeMapGenParser parser = new NodeToIElementTypeMapGenParser(tokens);
        setOut("NodeToIElementTypeMap", outputDirectory);
        parser.nodeList();
    }

    private static void setOut(String fileNameBase, File outputDirectory) throws IOException {
        File out = new File(outputDirectory, fileNameBase + ".java");
        out.createNewFile();
        Util.out=new PrintStream(out);
    }

    private static ANTLRInputStream getAntlrInputStream(File file) throws IOException {InputStream is = new FileInputStream(file);
        return new ANTLRInputStream(is);
    }
}
