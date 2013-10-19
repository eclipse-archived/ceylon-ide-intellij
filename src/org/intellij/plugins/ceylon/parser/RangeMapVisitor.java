package org.intellij.plugins.ceylon.parser;

import com.intellij.openapi.util.TextRange;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matija Mazi <br/>
 * @created 10/16/13 3:55 PM
 */
public class RangeMapVisitor extends Visitor {
    private Map<TextRange, Node> map = new HashMap<>();

    @Override
    public void visitAny(Node that) {
        super.visitAny(that);
        map.put(new TextRange(that.getStartIndex(), that.getStopIndex() + 1), that);
    }

    public Map<TextRange, Node> getMap() {
        return map;
    }
}
