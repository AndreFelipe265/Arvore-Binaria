package org.example;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private Tree arvore;
    private TreePanel panel;
    private JTextField inputField;
    private JTextArea outputArea;
    private JScrollPane scrollPane;

    public MainFrame() {
        arvore = new Tree();

        setTitle("Visualização de Árvore Binária");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();

        inputField = new JTextField(10);
        JButton insertButton = new JButton("Inserir");
        JButton LNRButton = new JButton("Exibir Caminho LNR");
        JButton LRNButton = new JButton("Exibir Caminho LRN");
        JButton NLRButton = new JButton("Exibir Caminho NLR");

        controlPanel.add(new JLabel("Número: "));
        controlPanel.add(inputField);
        controlPanel.add(insertButton);
        controlPanel.add(LNRButton);
        controlPanel.add(LRNButton);
        controlPanel.add(NLRButton);

        add(controlPanel, BorderLayout.NORTH);


        panel = new TreePanel(arvore.root);
        scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);


        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        insertButton.addActionListener(e -> inserirNumero());
        LNRButton.addActionListener(e -> exibirCaminhoLNR());
        LRNButton.addActionListener(e -> exibirCaminhoLRN());
        NLRButton.addActionListener(e -> exibirCaminhoNLR());

    }

    private void inserirNumero() {
        try {
            Long valor = Long.parseLong(inputField.getText());

            JViewport viewport = scrollPane.getViewport();
            Point posicaoAtual = viewport.getViewPosition();
            int centroX = posicaoAtual.x + viewport.getWidth() / 2;
            int centroY = posicaoAtual.y + viewport.getHeight() / 2;

            arvore.inserir(valor);

            panel.setRoot(arvore.root);
            panel.revalidate();
            panel.repaint();

            SwingUtilities.invokeLater(() ->
                    {
                        int novaLargura = panel.getWidth();
                        int novaAltura = panel.getHeight();

                        int novoX = centroX - viewport.getWidth() / 2;
                        int novoY = centroY - viewport.getHeight() / 2;

                        novoX = Math.max(0, Math.min(novoX, novaLargura - viewport.getWidth()));
                        novoY = Math.max(0, Math.min(novoY, novaAltura - viewport.getHeight()));

                        viewport.setViewPosition(new Point(novoX, novoY));
                    }
            );

            inputField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um número válido!");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir valor: " + exception.getMessage());
        }
    }

    private void exibirCaminhoLNR() {
        String resposta = arvore.caminhoLNR(arvore.root);
        JOptionPane.showMessageDialog(this, "Caminho LNR:\n" + resposta);
    }

    private void exibirCaminhoLRN() {
        String resposta = arvore.caminhoLRN(arvore.root);
        JOptionPane.showMessageDialog(this, "Caminho LRN:\n" + resposta);
    }

    private void exibirCaminhoNLR() {
        String resposta = arvore.caminhoNLR(arvore.root);
        JOptionPane.showMessageDialog(this, "Caminho NLR:\n" + resposta);
    }

}
