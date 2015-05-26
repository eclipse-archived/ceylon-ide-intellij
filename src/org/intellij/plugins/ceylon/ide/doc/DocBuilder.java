package org.intellij.plugins.ceylon.ide.doc;

import com.github.rjeschke.txtmark.BlockEmitter;
import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import com.github.rjeschke.txtmark.SpanEmitter;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.*;

import java.util.List;

/**
 * TODO
 */
public class DocBuilder {

    public static String getPackageFor(Declaration declaration) {
        com.redhat.ceylon.model.typechecker.model.Package pkg = declaration.getUnit().getPackage();

        return pkg.getNameAsString();
    }

    public static String getModifiers(Declaration declaration) {
        StringBuilder builder = new StringBuilder();

        for (Annotation annotation : declaration.getAnnotations()) {
            if (annotation.getNamedArguments().isEmpty() && annotation.getPositionalArguments().isEmpty()) {
                builder.append(annotation.getName()).append(' ');
            }
        }

        return builder.toString();
    }

    //////////////////////////////////////////////////////////////
    // Methods below are extracted from DocumentationHover.java //
    //////////////////////////////////////////////////////////////

    public static void appendDocAnnotationContent(Tree.AnnotationList annotationList,
                                                  StringBuilder documentation, Scope linkScope) {
        if (annotationList != null) {
            Tree.AnonymousAnnotation aa = annotationList.getAnonymousAnnotation();
            if (aa != null) {
                documentation.append(markdown(aa.getStringLiteral().getText(), linkScope,
                        annotationList.getUnit()));
            }
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("doc".equals(name)) {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList != null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                Tree.PositionalArgument a = args.get(0);
                                if (a instanceof Tree.ListedArgument) {
                                    String text = ((Tree.ListedArgument) a).getExpression()
                                            .getTerm().getText();
                                    if (text != null) {
                                        documentation.append(markdown(text, linkScope,
                                                annotationList.getUnit()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static String markdown(String text, final Scope linkScope, final Unit unit) {
        if (text == null || text.length() == 0) {
            return text;
        }

//	    String unquotedText = text.substring(1, text.length()-1);

        Configuration.Builder builder = Configuration.builder().forceExtentedProfile();
        builder.setCodeBlockEmitter(new CeylonBlockEmitter());
        if (linkScope != null) {
            builder.setSpecialLinkEmitter(new SpanEmitter() {
                @Override
                public void emitSpan(StringBuilder out, String content) {
                    String linkName;
                    String linkTarget;

                    int indexOf = content.indexOf("|");
                    if (indexOf == -1) {
                        linkName = content;
                        linkTarget = content;
                    } else {
                        linkName = content.substring(0, indexOf);
                        linkTarget = content.substring(indexOf + 1, content.length());
                    }

                    String href = resolveLink(linkTarget, linkScope, unit);
                    if (href != null) {
                        out.append("<a ").append(href).append(">");
                    }
                    out.append("<code>");
                    int sep = linkName.indexOf("::");
                    out.append(sep < 0 ? linkName : linkName.substring(sep + 2));
                    out.append("</code>");
                    if (href != null) {
                        out.append("</a>");
                    }
                }
            });
        }
        return Processor.process(text, builder.build());
    }

    // TODO
    public static String resolveLink(String linkTarget, Scope linkScope, Unit unit) {
        return linkTarget;
    }

    public static final class CeylonBlockEmitter implements BlockEmitter {

        @Override
        public void emitBlock(StringBuilder out, List<String> lines, String meta) {
            if (!lines.isEmpty()) {
                out.append("<pre>");
                /*if (meta == null || meta.length() == 0) {
                    out.append("<pre>");
                } else {
                    out.append("<pre class=\"brush: ").append(meta).append("\">");
                }*/
                StringBuilder code = new StringBuilder();
                for (String s : lines) {
                    code.append(s).append('\n');
                }
                String highlighted;
                if (meta == null || meta.length() == 0 || "ceylon".equals(meta)) {
                    highlighted = highlightLine(code.toString());
                } else {
                    highlighted = code.toString();
                }
                out.append(highlighted);
                out.append("</pre>\n");
            }
        }
    }

    public static String highlightLine(String s) {
        return s;
    }
}
