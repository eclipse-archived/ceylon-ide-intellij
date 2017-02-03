import com.intellij.debugger {
    DebuggerContext
}
import com.intellij.debugger.engine {
    DebuggerUtils
}
import com.intellij.debugger.engine.evaluation {
    EvaluationContext
}
import com.intellij.debugger.ui.tree {
    ...
}
import com.intellij.debugger.ui.tree.render {
    ChildrenBuilder,
    DescriptorLabelListener,
    NodeRendererImpl
}
import com.sun.jdi {
    ...
}
import java.util {
    ArrayList
}

shared class CeylonNodeRenderer() extends NodeRendererImpl() {

    shared actual void buildChildren(Value val, ChildrenBuilder builder, EvaluationContext evaluationContext) {
        if (is ObjectReference objRef = val) {
            value parentDescriptor = builder.parentDescriptor;
            value nodeManager = builder.nodeManager;
            value nodeDescriptorFactory = builder.descriptorManager;
            value refType = objRef.referenceType();
            value children = ArrayList<DebuggerTreeNode>();

            for (Field field in refType.allFields()) {
                if (field.name().startsWith("$init")) {
                    continue;
                }
                value descriptor = nodeDescriptorFactory.getFieldDescriptor(parentDescriptor, objRef, field);
                children.add(nodeManager.createNode(descriptor, evaluationContext));
            }
            builder.setChildren(children);
        }
    }

    getChildValueExpression(DebuggerTreeNode node, DebuggerContext context)
            => null;

    isExpandable(Value val, EvaluationContext evaluationContext, NodeDescriptor parentDescriptor)
            => val is ObjectReference;

    shared actual String calcLabel(ValueDescriptor descriptor, EvaluationContext evaluationContext,
        DescriptorLabelListener listener) {

        Type type = descriptor.\ivalue.type();
        variable String name = type.name();
        if (name.endsWith("_")) {
            name = name.substring(0, name.size - 1);
        }
        if (name.equals(`String`.declaration.name)) {
            assert (is ObjectReference objRef = descriptor.\ivalue);
            return objRef.getValue(objRef.referenceType().fieldByName("value")).string;
        }
        return name;
    }

    uniqueId => "CeylonNodeRenderer";

    isApplicable(Type type) =>
            type is ReferenceType
                && DebuggerUtils.instanceOf(type, "com.redhat.ceylon.compiler.java.runtime.model.ReifiedType");


    enabled = true;
}
