import com.github.rjeschke.txtmark {
    SpanEmitter,
    BlockEmitter
}
import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Scope,
    Referenceable,
    Function,
    Module,
    Package,
    Declaration
}

import java.lang {
    StringBuilder,
    JString=String
}
import java.util {
    List
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}


object unlinkedSpanEmitter satisfies SpanEmitter {
    
    shared actual void emitSpan(StringBuilder? builder, String? content) {
        if (exists content, exists builder) {
            value pipe = content.firstOccurrence('|');
            
            if (exists pipe) {
                builder.append(content.spanTo(pipe - 1));
            } else {
                builder.append("<code>").append(content).append("</code>");
            }
        }
    }
}

class CeylonSpanEmitter(Scope scope, Unit unit, String buildUrl(Referenceable model)) satisfies SpanEmitter {
    
    shared actual void emitSpan(StringBuilder builder, String content) {
        value pipe = content.firstOccurrence('|');
        variable String linkDescription;
        variable String linkTarget;
        
        if (exists pipe) {
            linkDescription = content.spanTo(pipe - 1);
            linkTarget = content.spanFrom(pipe + 1);
        } else {
            value sep = content.firstInclusion("::");
            if (exists sep) {
                linkDescription = content.spanFrom(sep + 2);
            } else {
                linkDescription = content;
            }
            linkTarget = content;
        }
        
        value decl = resolveLink(linkTarget, scope, unit);
        value href = if (exists decl) then "href='``psiProtocol``doc:``buildUrl(decl)``'" else null;
        
        if (exists href) {
            builder.append("<a ").append(href).append(">");
        }
        if (exists pipe) {
            builder.append("<code>").append(linkDescription);
            if (is Function decl) {
                builder.append("()");
            }
            builder.append("</code>");
        } else {
            builder.append(linkDescription);
        }
        
        if (exists href) {
            builder.append("</a>");
        }
    }
    
    Referenceable? resolveLink(String linkTarget, Scope linkScope, Unit unit) {
        if (linkTarget.startsWith("package ")) {
            return resolveModule(linkScope)
                ?.getPackage(linkTarget[8...].trimmed);
        } else if (linkTarget.startsWith("module ")) {
            return resolveModule(linkScope)
                ?.getPackage(linkTarget[7...].trimmed)
                ?.\imodule;
        }
        
        value pkgSeparatorIndex = linkTarget.firstInclusion("::");
        String declName;
        Scope? scope;
        if (exists pkgSeparatorIndex) {
            value pkgName = linkTarget[0:pkgSeparatorIndex];
            declName = linkTarget[pkgSeparatorIndex+2...];
            if (exists mod = resolveModule(linkScope)) {
                scope = mod.getPackage(pkgName);
            } else {
                scope = null;
            }
        } else {
            declName = linkTarget;
            scope = linkScope;
        }
        
        if (exists scope, !declName.empty) {
            value declNames = declName.split('.'.equals);
            variable Declaration? decl
                    = scope.getMemberOrParameter(unit, declNames.first, null, false);
            
            for (name in declNames.skip(1)) {
                if (is Scope s = decl) {
                    decl = s.getMember(name, null, false);
                    if (!decl exists) {
                        //a parameter
                        decl = s.getDirectMember(name, null, false);
                    }
                } else {
                    return null;
                }
            }
            
            return decl;
        }
        
        return null;
    }
    
    Module? resolveModule(Scope? scope)
            => switch (scope)
            case (null) null
            case (is Package) scope.\imodule
            else resolveModule(scope.container);
}

class CeylonBlockEmitter(Project project) satisfies BlockEmitter {
    
    shared actual void emitBlock(StringBuilder builder, List<JString> lines, String? meta) {
        if (!lines.empty) {
            builder.append("<div style='margin-left: 5px'><pre>");
            
            value code = "\n".join { *lines } + "\n";
            
            if (exists meta, meta.empty || "ceylon"==meta) {
                builder.append(highlighter.highlight(code.string, project));
            } else {
                builder.append(code.string);
            }
            
            builder.append("</pre></div>\n");
        }
    }
}
