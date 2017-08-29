//package org.intellij.plugins.ceylon.ide.editor;
//
//import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
//import com.intellij.openapi.Disposable;
//import com.intellij.openapi.actionSystem.DataContext;
//import com.intellij.openapi.actionSystem.DataKeys;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
//import com.intellij.openapi.editor.impl.EditorImpl;
//import com.intellij.openapi.ui.AbstractPainter;
//import com.intellij.openapi.wm.IdeGlassPaneUtil;
//import com.intellij.openapi.wm.WindowManager;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.ArrayList;
//
//public class Particles extends TypedActionHandlerBase implements Disposable {
//
//    public static boolean ENABLED = false;
//
//    private ArrayList<Particle> particles = new ArrayList<>(500);
//    private AbstractPainter painter;
//    @Nullable
//    private TypedActionHandler originalHandler;
//
//    public Particles(@Nullable TypedActionHandler originalHandler) {
//        super(originalHandler);
//        this.originalHandler = originalHandler;
//    }
//
//    private void addParticles(int x, int y) {
//        for (int i = 0; i < 20; i++) {
//            int dx, dy;
//
//            do {
//                dx = newRandom(5, true);
//                dy = newRandom(5, true);
//            } while (dx == 0 || dy == 0);
//
//            int size = newRandom(5);
//            int life = newRandom(120) + 80;
//
//            Color col = new Color(
//                    newRandom(255),
//                    newRandom(255),
//                    newRandom(255),
//                    255 - newRandom(125)
//            );
//            particles.add(new Particle(x, y, dx, dy, size, life, col));
//        }
//    }
//
//    private int newRandom(int coeff) {
//        return newRandom(coeff, false);
//    }
//
//    private int newRandom(int coeff, boolean randomizeSign) {
//        boolean positive = true;
//
//        if (randomizeSign && Math.random() < 0.5) {
//            positive = false;
//        }
//
//        int val = (int) (Math.random() * coeff);
//
//        return positive ? val : -val;
//    }
//
//    private void update() {
//        for (int i = 0; i <= particles.size() - 1; i++) {
//            if (particles.get(i).update()) {
//                particles.remove(i);
//            }
//        }
//    }
//
//    private void renderParticles(Graphics2D g2d) {
//        for (int i = 0; i <= particles.size() - 1; i++) {
//            particles.get(i).render(g2d);
//        }
//    }
//
//    @Override
//    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
//        if (ENABLED) {
//            JFrame win = WindowManager.getInstance().getFrame(DataKeys.PROJECT.getData(dataContext));
//
//            if (editor instanceof EditorImpl) {
//                EditorImpl.CaretRectangle[] locations = ((EditorImpl) editor).getCaretLocations(false);
//
//                if (locations != null) {
//                    for (EditorImpl.CaretRectangle rect : locations) {
//                        Point point = rect.myPoint;
//
//                        Point pWin = win.getLocationOnScreen();
//                        Point pEd = editor.getComponent().getLocationOnScreen();
//
//                        addParticles(
//                                (int) (pEd.getX() - pWin.getX() + point.getX()) + 60,
//                                (int) (pEd.getY() - pWin.getY() + point.getY()) - 5
//                        );
//                    }
//                }
//            }
//
//            if (painter == null) {
//
//                painter = new AbstractPainter() {
//
//                    @Override
//                    public void executePaint(Component component, Graphics2D g) {
//                        if (particles.size() == 0) {
//                            return;
//                        }
//
//                        update();
//                        renderParticles(g);
//                        try {
//                            Thread.sleep(100 / 60);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        setNeedsRepaint(true);
//                    }
//                };
//
//
//                Component component = win.getComponent(0);
//                if (component instanceof JComponent) {
//                    IdeGlassPaneUtil.installPainter((JComponent) component, painter, this);
//                } else {
//                    IdeGlassPaneUtil.installPainter(editor.getComponent(), painter, this);
//                }
//            }
//            painter.setNeedsRepaint(true);
//        }
//
//        if (originalHandler != null) {
//            originalHandler.execute(editor, charTyped, dataContext);
//        }
//    }
//
//    @Override
//    public void dispose() {
//        System.out.println("Particles disposed");
//    }
//}
//
//class Particle {
//
//    private int x;
//    private int y;
//    private int dx;
//    private int dy;
//    private int size;
//    private int life;
//    private Color color;
//
//    Particle(int x, int y, int dx, int dy, int size, int life, Color c) {
//        this.x = x;
//        this.y = y;
//        this.dx = dx;
//        this.dy = dy;
//        this.size = size;
//        this.life = life;
//        this.color = c;
//    }
//
//    public boolean update() {
//        x += dx;
//        y += dy;
//        life--;
//
//        int alpha = color.getAlpha() - 2;
//
//        if (life <= 0 || alpha <= 0) {
//            return true;
//        }
//
//        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
//
//        return false;
//    }
//
//    void render(Graphics2D g) {
//        Graphics2D g2d = (Graphics2D) g.create();
//        g2d.setColor(color);
//
//        g2d.fillRoundRect(x - (size / 2), y - (size / 2), size, size, size / 2, size / 2);
//
//        g2d.dispose();
//
//    }
//}
