package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        getContentPane().setBackground(new Color(30, 30, 30));

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));

        inputField = new JTextField(10);

        JButton insertButton = new JButton("Inserir");
        JButton LNRButton = new JButton("Exibir Caminho LNR");
        JButton LRNButton = new JButton("Exibir Caminho LRN");
        JButton NLRButton = new JButton("Exibir Caminho NLR");
        JButton analisarNoButton = new JButton("Analisar Nó");
        JButton carregarButton = new JButton("Carregar Árvore");
        JButton resetButton = new JButton("Resetar Árvore");

        insertButton.setBackground(Color.WHITE);
        LNRButton.setBackground(Color.WHITE);
        LRNButton.setBackground(Color.WHITE);
        NLRButton.setBackground(Color.WHITE);
        analisarNoButton.setBackground(Color.WHITE);
        carregarButton.setBackground(Color.WHITE);
        resetButton.setBackground(Color.WHITE);

        JLabel labelNumero = new JLabel("Número: ");
        labelNumero.setForeground(Color.WHITE);

        controlPanel.add(labelNumero);
        controlPanel.add(inputField);
        controlPanel.add(insertButton);
        controlPanel.add(LNRButton);
        controlPanel.add(LRNButton);
        controlPanel.add(NLRButton);
        controlPanel.add(analisarNoButton);
        controlPanel.add(carregarButton);
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.NORTH);

        panel = new TreePanel(arvore.root);
        scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(43, 43, 43));
        outputArea.setForeground(Color.WHITE);
        outputArea.setCaretColor(Color.WHITE);

        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.getViewport().setBackground(new Color(43, 43, 43));

        add(outputScroll, BorderLayout.SOUTH);

        insertButton.addActionListener(e -> inserirNumero());
        inputField.addActionListener(e -> inserirNumero());

        LNRButton.addActionListener(e -> exibirCaminhoLNR());
        LRNButton.addActionListener(e -> exibirCaminhoLRN());
        NLRButton.addActionListener(e -> exibirCaminhoNLR());
        analisarNoButton.addActionListener(e -> analisarNo());
        carregarButton.addActionListener(e -> carregarArvore());
        resetButton.addActionListener(e -> resetarArvore());
    }

    private void carregarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar arquivo de árvore (.txt)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de Texto (.txt)", "txt"));
        int escolha = fileChooser.showOpenDialog(this);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();

            try {
                java.util.Scanner scanner = new java.util.Scanner(arquivo);
                StringBuilder conteudo = new StringBuilder();
                while (scanner.hasNextLine()) {
                    conteudo.append(scanner.nextLine()).append("\n");
                }
                scanner.close();

                arvore.carregarDeParenteses(conteudo.toString());
                panel.setRoot(arvore.root);
                panel.revalidate();
                panel.repaint();

                outputArea.setText("Árvore carregada com sucesso do arquivo: " + arquivo.getName());
                outputArea.append("\nTipo da árvore: " + arvore.obterTipoArvore());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar arquivo: " + e.getMessage());
            }
        }
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

            SwingUtilities.invokeLater(() -> {
                int novaLargura = panel.getWidth();
                int novaAltura = panel.getHeight();

                int novoX = centroX - viewport.getWidth() / 2;
                int novoY = centroY - viewport.getHeight() / 2;

                novoX = Math.max(0, Math.min(novoX, Math.max(0, novaLargura - viewport.getWidth())));
                novoY = Math.max(0, Math.min(novoY, Math.max(0, novaAltura - viewport.getHeight())));

                viewport.setViewPosition(new Point(novoX, novoY));
            });

            inputField.setText("");
            inputField.requestFocus();

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

    private void analisarNo() {
        try {
            Long valor = Long.parseLong(inputField.getText());

            No no = arvore.buscar(valor);

            if (no == null) {
                JOptionPane.showMessageDialog(this, "Nó não encontrado!");
                return;
            }

            int profundidade = arvore.calcProfundidade(valor);
            int altura = arvore.calcAltura(no);
            int nivel = profundidade + 1;

            JOptionPane.showMessageDialog(this,
                    "Valor do nó: " + valor +
                            "\nProfundidade: " + profundidade +
                            "\nNível: " + nivel +
                            "\nAltura: " + altura);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um número válido!");
        }

    }

    private void resetarArvore() {
        if (arvore.root == null) {
            JOptionPane.showMessageDialog(this, "A árvore já está vazia!");
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Gostaria de salvar antes de resetar?",
                "Resetar Árvore",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (opcao == JOptionPane.CANCEL_OPTION || opcao == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (opcao == JOptionPane.YES_OPTION) {
            File arquivoImagem = salvarImagemArvore();

            if (arquivoImagem == null) {
                return;
            }

            salvarParentesesArquivo(arquivoImagem);

            String tipo = arvore.obterTipoArvore();

            outputArea.setText("Árvore salva com sucesso!\nTipo da árvore: " + tipo);

            JLabel mensagem = new JLabel(
                    "<html><div style='text-align:center;'>"
                            + "<span style='font-size:18px;'>Árvore salva com sucesso!</span><br><br>"
                            + "<span style='font-size:16px;'>Tipo da árvore: </span>"
                            + "<span style='font-size:22px; color:#228B22; font-weight:bold;'>" + tipo + "</span>"
                            + "</div></html>"
            );
            mensagem.setHorizontalAlignment(SwingConstants.CENTER);

            JOptionPane.showMessageDialog(
                    this,
                    mensagem,
                    "Árvore salva",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        arvore.limpar();
        panel.setRoot(arvore.root);
        panel.revalidate();
        panel.repaint();
        inputField.setText("");
        inputField.requestFocus();

        if (opcao == JOptionPane.NO_OPTION) {
            outputArea.setText("A árvore foi resetada sem salvar.");
        } else {
            outputArea.append("\nA árvore foi resetada.");
        }
    }

    private File salvarImagemArvore() {
        try {
            panel.revalidate();
            panel.repaint();

            Dimension tamanho = panel.getPreferredSize();
            int largura = Math.max(panel.getWidth(), tamanho.width);
            int altura = Math.max(panel.getHeight(), tamanho.height);

            if (largura <= 0 || altura <= 0) {
                largura = 1000;
                altura = 600;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar árvore como imagem");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            String dataHora = LocalDateTime.now().format(formatter);

            fileChooser.setSelectedFile(new File("arvore_" + dataHora + ".png"));

            int escolha = fileChooser.showSaveDialog(this);

            if (escolha != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            File arquivo = fileChooser.getSelectedFile();
            String nomeOriginal = arquivo.getName();

            if (!nomeOriginal.toLowerCase().endsWith(".png")) {
                arquivo = new File(arquivo.getParentFile(), nomeOriginal + ".png");
                nomeOriginal = arquivo.getName();
            }

            if (arquivo.exists()) {
                String nomeBase = nomeOriginal;
                String extensao = "";

                int ultimoPonto = nomeOriginal.lastIndexOf('.');
                if (ultimoPonto != -1) {
                    nomeBase = nomeOriginal.substring(0, ultimoPonto);
                    extensao = nomeOriginal.substring(ultimoPonto);
                }

                File pasta = arquivo.getParentFile();
                int contador = 1;

                File novoArquivo = new File(pasta, nomeBase + "(" + contador + ")" + extensao);

                while (novoArquivo.exists()) {
                    contador++;
                    novoArquivo = new File(pasta, nomeBase + "(" + contador + ")" + extensao);
                }

                arquivo = novoArquivo;
            }

            BufferedImage imagem = new BufferedImage(
                    largura,
                    altura,
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2 = imagem.createGraphics();
            panel.setSize(largura, altura);
            panel.doLayout();
            panel.printAll(g2);
            g2.dispose();

            ImageIO.write(imagem, "png", arquivo);

            return arquivo;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar imagem: " + e.getMessage());
            return null;
        }
    }

    private boolean salvarParentesesArquivo(File arquivoImagem) {
        try {
            String nome = arquivoImagem.getName();
            String nomeBase = nome.substring(0, nome.lastIndexOf('.'));

            File arquivoTxt = new File(arquivoImagem.getParentFile(), nomeBase + ".txt");

            String conteudo = arvore.gerarParenteses();

            java.io.FileWriter writer = new java.io.FileWriter(arquivoTxt);
            writer.write("Árvore em parênteses alinhados:\n\n");
            writer.write(conteudo);
            writer.close();

            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar arquivo de parênteses: " + e.getMessage());
            return false;
        }
    }

}
