import javax.swing {
    Icon
}
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.icons {
    AllIcons
}

// TODO merge this with Icons.java?
shared object ideaIcons {
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.gif");
    shared Icon packages => IconLoader.getIcon("/icons/package.png");
    shared Icon modules => AllIcons.Nodes.\iArtifact;
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon refinement => AllIcons.General.\iImplementingMethod;
    shared Icon param => AllIcons.Nodes.\iParameter;
    shared Icon local => AllIcons.Nodes.\iVariable;
    shared Icon anonymousFunction => AllIcons.Nodes.\iFunction;
}