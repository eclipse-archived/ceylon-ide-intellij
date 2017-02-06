import ceylon.collection {
    ArrayList
}

import com.intellij.debugger.engine {
    JavaStackFrame
}
import com.intellij.debugger.jdi {
    LocalVariableProxyImpl
}
import com.intellij.debugger.ui.impl.watch {
    StackFrameDescriptorImpl
}
import com.redhat.ceylon.ide.common.debug {
    fixVariableName
}

import java.util {
    List
}

"Overrides the default Java stack frame to hide internal or duplicate variables like
 `i$1`, `i$2` etc."
class CeylonStackFrame(StackFrameDescriptorImpl descriptor, Boolean update)
        extends JavaStackFrame(descriptor, update) {


    shared actual List<LocalVariableProxyImpl> visibleVariables {
        value visibleVariables = super.visibleVariables;
        value it = visibleVariables.iterator();
        value processedNames = ArrayList<String>();

        while (it.hasNext()) {
            value var = it.next();
            value name = fixVariableName(var.name(), true, false);

            if (name.contains("$")) {
                it.remove();
            }
            if (processedNames.contains(name)) {
                it.remove();
            } else {
                processedNames.add(name);
            }
        }

        return visibleVariables;
    }
}
