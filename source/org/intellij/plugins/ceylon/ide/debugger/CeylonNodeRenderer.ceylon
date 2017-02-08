import ceylon.language {
    langNull=null
}

import com.intellij.debugger {
    DebuggerContext
}
import com.intellij.debugger.engine {
    DebuggerUtils
}
import com.intellij.debugger.engine.evaluation {
    EvaluationContext,
    EvaluationContextImpl
}
import com.intellij.debugger.ui.impl.watch {
    ValueDescriptorImpl
}
import com.intellij.debugger.ui.tree {
    ...
}
import com.intellij.debugger.ui.tree.render {
    ChildrenBuilder,
    DescriptorLabelListener,
    NodeRendererImpl
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiExpression
}
import com.sun.jdi {
    ...
}

import java.util {
    ArrayList
}

"Used to enhance the presentation of Ceylon stack frames by:

 * hiding internal variables
 * grouping reified types under a single node

 "
shared class CeylonNodeRenderer() extends NodeRendererImpl() {

    shared actual void buildChildren(Value val, ChildrenBuilder builder, EvaluationContext evaluationContext) {
        if (is ObjectReference objRef = val) {
            value parentDescriptor = builder.parentDescriptor;
            value nodeManager = builder.nodeManager;
            value nodeDescriptorFactory = builder.descriptorManager;
            value refType = objRef.referenceType();
            value children = ArrayList<DebuggerTreeNode>();
            value reifiedTypes = ArrayList<DebuggerTreeNode>();

            for (Field field in refType.allFields()) {
                if (field.name().startsWith("$reified$")) {
                    value descriptor = nodeDescriptorFactory.getFieldDescriptor(parentDescriptor, objRef, field);
                    reifiedTypes.add(nodeManager.createNode(descriptor, evaluationContext));
                    continue;
                }
                if (field.name().startsWith("$init")
                    || field.name().endsWith("$this$")) {
                    continue;
                }
                value descriptor = nodeDescriptorFactory.getFieldDescriptor(parentDescriptor, objRef, field);
                children.add(nodeManager.createNode(descriptor, evaluationContext));
            }

            if (!reifiedTypes.empty) {
                children.add(0, nodeManager.createNode(
                    ReifiedTypesNode(evaluationContext.project, reifiedTypes), evaluationContext));
            }

            builder.setChildren(children);
        } else if (is ReifiedTypesValue val) {
            builder.setChildren(val.reifiedTypes);
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
            (type is ReferenceType
                && DebuggerUtils.instanceOf(type, "com.redhat.ceylon.compiler.java.runtime.model.ReifiedType"))
            || type is ReifiedTypesType;


    enabled = true;
}

"This only exists to be referenced in [[CeylonNodeRenderer.isApplicable]]."
class ReifiedTypesType() satisfies Type {
    name() => "name";

    signature() => "signature";

    string => "string";

    virtualMachine() => langNull;
}

"The value shown in the [[ReifiedTypesNode]]."
class ReifiedTypesValue(shared ArrayList<DebuggerTreeNode> reifiedTypes) satisfies Value {
    string => "Ceylon reified types";

    type() => ReifiedTypesType();

    virtualMachine() => langNull;
}

"The node that groups reified types."
class ReifiedTypesNode(Project project, ArrayList<DebuggerTreeNode> reifiedTypes)
        extends ValueDescriptorImpl(project) {

    calcRepresentation(EvaluationContextImpl? context, DescriptorLabelListener? labelListener)
            => name;

    expandable => true;

    calcValue(EvaluationContextImpl? evaluationContext) => ReifiedTypesValue(reifiedTypes);

    shared actual PsiExpression? getDescriptorEvaluation(DebuggerContext? context) => langNull;

    shared actual void setValueLabel(String? label) {}

    name => "Reified types";
}
