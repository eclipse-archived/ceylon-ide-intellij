import ceylon.interop.java {
    CeylonIterable
}

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

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlight
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
            value mod = resolveModule(linkScope);
            return mod?.getPackage(linkTarget.spanFrom(8).trimmed);
        } else if (linkTarget.startsWith("module ")) {
            value mod = resolveModule(linkScope);
            Package? pack = mod?.getPackage(linkTarget.spanFrom(7).trimmed);
            return pack?.\imodule;
        }
        
        value pkgSeparatorIndex = linkTarget.firstInclusion("::");
        variable String declName;
        variable Scope? scope;
        
        if (exists pkgSeparatorIndex) {
            value pkgName = linkTarget.spanTo(pkgSeparatorIndex);
            declName = linkTarget.spanFrom(pkgSeparatorIndex + 2);
            if (exists mod = resolveModule(scope)) {
                scope = mod.getPackage(pkgName);
            }
        } else {
            declName = linkTarget;
            scope = linkScope;
        }
        
        if (exists s = scope, !declName.empty) {
            value declNames = declName.split('.'.equals);
            variable Declaration? decl = s.getMemberOrParameter(unit, declNames.first, null, false);
            
            for (name in declNames.skip(1)) {
                if (is Scope d = decl) {
                    decl = (d of Scope).getMember(name, null, false);
                } else {
                    return null;
                }
            }
            
            return decl;
        }
        
        return null;
    }
    
    Module? resolveModule(Scope? scope) {
        if (exists scope) {
            if (is Package scope) {
                return scope.\imodule;
            }
            
            return resolveModule(scope.container);
        }
        
        return null;
    }
}

class CeylonBlockEmitter(Project project) satisfies BlockEmitter {
    
    shared actual void emitBlock(StringBuilder builder, List<JString> lines, String? meta) {
        if (!lines.empty) {
            builder.append("<div style='margin-left: 5px'><pre>");
            
            value code = "\n".join(CeylonIterable(lines)) + "\n";
            
            if (exists meta, (meta.empty || "ceylon".equals(meta))) {
                builder.append(highlight(code.string, project));
            } else {
                builder.append(code.string);
            }
            
            builder.append("</pre></div>\n");
        }
    }
}
