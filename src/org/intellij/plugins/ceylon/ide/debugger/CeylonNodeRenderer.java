package org.intellij.plugins.ceylon.ide.debugger;

import com.intellij.debugger.DebuggerContext;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.ui.impl.watch.ValueDescriptorImpl;
import com.intellij.debugger.ui.tree.*;
import com.intellij.debugger.ui.tree.render.ChildrenBuilder;
import com.intellij.debugger.ui.tree.render.DescriptorLabelListener;
import com.intellij.debugger.ui.tree.render.NodeRendererImpl;
import com.intellij.psi.PsiExpression;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.List;

public class CeylonNodeRenderer extends NodeRendererImpl {
    @Override
    public void buildChildren(Value value, ChildrenBuilder builder, EvaluationContext evaluationContext) {
        if (value instanceof ObjectReference) {
            final ValueDescriptorImpl parentDescriptor = (ValueDescriptorImpl)builder.getParentDescriptor();
            final NodeManager nodeManager = builder.getNodeManager();
            final NodeDescriptorFactory nodeDescriptorFactory = builder.getDescriptorManager();
            final ObjectReference objRef = (ObjectReference)value;
            final ReferenceType refType = objRef.referenceType();
            List<DebuggerTreeNode> children = new ArrayList<>();

            for (Field field : refType.allFields()) {
                if (field.name().startsWith("$init")) {
                    continue;
                }
                FieldDescriptor descriptor = nodeDescriptorFactory.getFieldDescriptor(parentDescriptor, objRef, field);
                children.add(nodeManager.createNode(descriptor, evaluationContext));
            }

            builder.setChildren(children);
        }
    }

    @Override
    public PsiExpression getChildValueExpression(DebuggerTreeNode node, DebuggerContext context) throws EvaluateException {
        return null;
    }

    @Override
    public boolean isExpandable(Value value, EvaluationContext evaluationContext, NodeDescriptor parentDescriptor) {
        return value instanceof ObjectReference;
    }

    @Override
    public String calcLabel(ValueDescriptor descriptor, EvaluationContext evaluationContext, DescriptorLabelListener listener) throws EvaluateException {
        Type type = descriptor.getValue().type();

        String name = type.name();

        if (name.endsWith("_")) {
            name = name.substring(0, name.length() - 1);
        }

        if (name.equals(ceylon.language.String.class.getName())) {
            ObjectReference objRef = (ObjectReference) descriptor.getValue();
            return objRef.getValue(objRef.referenceType().fieldByName("value")).toString();
        }

        return name;
    }

    @Override
    public String getUniqueId() {
        return "CeylonNodeRenderer";
    }

    @Override
    public boolean isApplicable(Type type) {
        return type instanceof ReferenceType && DebuggerUtils.instanceOf(type, "com.redhat.ceylon.compiler.java.runtime.model.ReifiedType");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
