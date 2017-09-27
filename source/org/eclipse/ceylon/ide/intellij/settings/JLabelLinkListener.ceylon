import java.awt {
    ...
}
import java.awt.event {
    MouseAdapter,
    MouseEvent
}

import javax.accessibility {
    AccessibleText
}
import javax.swing {
    ...
}
import javax.swing.text {
    SimpleAttributeSet
}
import javax.swing.text.html {
    HTML
}

shared abstract class JLabelLinkListener() extends MouseAdapter() {

    shared formal void onLinkClicked(String href);

    shared actual void mouseClicked(MouseEvent e) {
        if (exists link = getLink(e)) {
            value href = link.getAttribute(HTML.Attribute.href).string;
            onLinkClicked(href);
        }
    }

    shared actual void mouseMoved(MouseEvent e) {
        assert (is JLabel label = e.source);
        label.cursor
            = getLink(e) exists
            then Cursor.getPredefinedCursor(Cursor.handCursor)
            else Cursor.defaultCursor;
    }

    SimpleAttributeSet? getLink(MouseEvent e) {
        value point = e.point;
        assert (is JLabel source = e.source,
                is AccessibleText ctx = source.accessibleContext);
        value pos = ctx.getIndexAtPoint(point);
        value as = ctx.getCharacterAttribute(pos);
        value attribute = as?.getAttribute(HTML.Tag.a);
        assert (is SimpleAttributeSet? attribute);
        return attribute;
    }

}
