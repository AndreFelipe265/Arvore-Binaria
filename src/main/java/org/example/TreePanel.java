package org.example;

import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {

    private No root;
    private final int NODE_RADIUS = 25;
    private final int LEVEL_HEIGHT = 80;

    public TreePanel(No root) {
        this.root = root;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (root != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            desenharArvore(g2, root, getWidth() / 2, 60, getWidth() / 4);
        }
    }

    private void desenharArvore(Graphics2D g, No no, int x, int y, int distanciaX) {

        if (no == null) return;

        int childY = y + LEVEL_HEIGHT;

        // ---- DESENHA LINHAS PRIMEIRO ----
        if (no.esq != null) {
            g.drawLine(x, y, x - distanciaX, childY);
            desenharArvore(g, no.esq, x - distanciaX, childY, distanciaX / 2);
        }

        if (no.dir != null) {
            g.drawLine(x, y, x + distanciaX, childY);
            desenharArvore(g, no.dir, x + distanciaX, childY, distanciaX / 2);
        }

        g.setColor(new Color(70, 130, 180)); // azul bonito
        g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        g.setColor(Color.BLACK);
        g.drawOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        String texto = String.valueOf(no.item);
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(texto);
        int textHeight = fm.getAscent();

        g.drawString(texto,
                x - textWidth / 2,
                y + textHeight / 4);
    }
}
