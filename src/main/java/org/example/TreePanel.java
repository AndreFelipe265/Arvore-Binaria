package org.example;

import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {

    private No root;
    private final int NODE_RADIUS = 25;
    private final int LEVEL_HEIGHT = 80;

    public TreePanel(No root) {
        this.root = root;
        setBackground(new Color(43,43,43));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (root != null) {

            int altura = calcularAltura(root);


            int largura = (int) Math.pow(2, altura) * 40;
            int alturaPainel = altura * LEVEL_HEIGHT + 100;

            setPreferredSize(new Dimension(largura, alturaPainel));
            revalidate();

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            desenharArvore(g2, root, getWidth() / 2, 50, getWidth() / 6);
        }
    }

    private void desenharArvore(Graphics2D g, No no, int x, int y, int distanciaX) {

        if (no == null) return;

        int childY = y + LEVEL_HEIGHT;

        g.setColor(Color.WHITE);
        if (no.esq != null) {
            g.drawLine(x, y, x - distanciaX, childY);
            desenharArvore(g, no.esq, x - distanciaX, childY, distanciaX / 2);
        }

        g.setColor(Color.WHITE);
        if (no.dir != null) {
            g.drawLine(x, y, x + distanciaX, childY);
            desenharArvore(g, no.dir, x + distanciaX, childY, distanciaX / 2);
        }

        g.setColor(new Color(122, 12, 166)); // roxo
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

    private int calcularAltura(No no) {
        if (no == null) return 0;
        return 1 + Math.max(calcularAltura(no.esq), calcularAltura(no.dir));
    }

    public void setRoot(No root) {
        this.root = root;
    }

}
