package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.List;

public class MainFrame extends JFrame {

    private Tree arvore;
    private TreePanel panel;
    private JTextField inputField;
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    private boolean isAVL;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private boolean emAnimacao = false;

    private Stack<Tree> undoStack;
    private Stack<Tree> redoStack;

    public MainFrame() {
        arvore = new Tree();
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        statusLabel = new JLabel("Executando balanceamento, aguarde...");
        statusLabel.setForeground(Color.ORANGE);
        statusLabel.setVisible(false);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        setTitle("Visualização de Árvore Binária");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(30, 30, 30));

        controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));

        escolherTipoArvore();

        inputField = new JTextField(10);

        JButton caminhoButton = new JButton("Exibir Caminho");
        JButton analisarButton = new JButton("Analisar");
        JButton desfazerButton = new JButton("Desfazer");
        JButton refazerButton = new JButton("Refazer");
        JButton carregarButton = new JButton("Carregar Árvore");
        JButton inverterButton = new JButton("Inverter Árvore");
        JButton resetButton = new JButton("Resetar Árvore");

        caminhoButton.setBackground(Color.WHITE);
        analisarButton.setBackground(Color.WHITE);
        desfazerButton.setBackground(Color.WHITE);
        refazerButton.setBackground(Color.WHITE);
        carregarButton.setBackground(Color.WHITE);
        inverterButton.setBackground(Color.WHITE);
        resetButton.setBackground(Color.WHITE);

        JLabel labelNumero = new JLabel("Número: ");
        labelNumero.setForeground(Color.WHITE);

        controlPanel.add(labelNumero);
        controlPanel.add(inputField);
        controlPanel.add(caminhoButton);
        controlPanel.add(analisarButton);
        controlPanel.add(desfazerButton);
        controlPanel.add(refazerButton);
        controlPanel.add(carregarButton);
        controlPanel.add(inverterButton);
        controlPanel.add(resetButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 30));

        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

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

        inputField.addActionListener(e -> inserirNumero());
        caminhoButton.addActionListener(e -> abrirMenuCaminhos());
        analisarButton.addActionListener(e -> abrirMenuAnalise());
        desfazerButton.addActionListener(e -> desfazer());
        refazerButton.addActionListener(e -> refazer());
        carregarButton.addActionListener(e -> carregarArvore());
        inverterButton.addActionListener(e -> inverterArvore());
        resetButton.addActionListener(e -> resetarArvore());
    }

    private void setUIEnabled(boolean enabled) {
        for (Component c : controlPanel.getComponents()) {
            c.setEnabled(enabled);
        }

        inputField.setEnabled(enabled);
    }

    private void entrarModoAnimacao() {
        controlPanel.setVisible(false);
        statusLabel.setVisible(true);
    }

    private void sairModoAnimacao() {
        controlPanel.setVisible(true);
        statusLabel.setVisible(false);
    }

    private void inverterArvore() {
        if (arvore.root == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia!");
            return;
        }

        salvarEstadoParaUndo();
        arvore.inverterSubarvores();
        atualizarVisualizacao();

        StringBuilder sb = new StringBuilder();
        sb.append("Árvore invertida com sucesso!\n");
        sb.append("Caminho LNR (Inorder): ").append(arvore.caminhoLNR(arvore.root)).append("\n");
        sb.append("Caminho NLR (Preorder): ").append(arvore.caminhoNLR(arvore.root)).append("\n");
        sb.append("Caminho LRN (Postorder): ").append(arvore.caminhoLRN(arvore.root));

        outputArea.setText(sb.toString());
    }

    private void salvarEstadoParaUndo() {
        undoStack.push(arvore.copiar());
        redoStack.clear();
    }

    private void atualizarVisualizacao() {
        panel.setRoot(arvore.root);
        panel.revalidate();
        panel.repaint();
        inputField.setText("");
        inputField.requestFocus();
    }

    private void inserirNumero() {
        try {
            if (emAnimacao) {
                return;
            }

            Long valor = Long.parseLong(inputField.getText());

            if (arvore.buscar(valor) != null) {
                JOptionPane.showMessageDialog(this, "Número já existe na árvore: " + valor);
                return;
            }

            JViewport viewport = scrollPane.getViewport();
            Point posicaoAtual = viewport.getViewPosition();
            int centroX = posicaoAtual.x + viewport.getWidth() / 2;
            int centroY = posicaoAtual.y + viewport.getHeight() / 2;

            salvarEstadoParaUndo();
            if (isAVL) {
                AVLService avl = new AVLService(arvore);
                List<No> passos = avl.inserirComPassos(valor);
                animar(passos);
            } else {
                arvore.inserir(valor);
                panel.setRoot(arvore.root);
                panel.revalidate();
                panel.repaint();
            }

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
            outputArea.setText("Número " + valor + " inserido com sucesso.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um número válido!");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir valor: " + exception.getMessage());
        }
    }

    private void animar(List<No> passos) {

        emAnimacao = true;
        setUIEnabled(false);
        entrarModoAnimacao();

        Timer timer = new Timer(1000, null);

        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < passos.size()) {
                panel.setRoot(passos.get(index[0]));
                panel.repaint();
                index[0]++;
            } else {
                timer.stop();
                panel.setRoot(arvore.root);
                panel.repaint();

                emAnimacao = false;
                setUIEnabled(true);
                sairModoAnimacao();

                SwingUtilities.invokeLater(() -> {
                    inputField.requestFocusInWindow();
                });
            }
        });

        timer.start();
    }

    private void abrirMenuCaminhos() {
        JDialog dialog = new JDialog(this, "Escolha o Caminho");
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new FlowLayout());

        JButton btnLNR = new JButton("LNR");
        JButton btnNLR = new JButton("NLR");
        JButton btnLRN = new JButton("LRN");

        dialog.add(btnLNR);
        dialog.add(btnNLR);
        dialog.add(btnLRN);

        btnLNR.addActionListener(e -> mostrarResultado(arvore.caminhoLNR(arvore.root)));
        btnNLR.addActionListener(e -> mostrarResultado(arvore.caminhoNLR(arvore.root)));
        btnLRN.addActionListener(e -> mostrarResultado(arvore.caminhoLRN(arvore.root)));

        dialog.setVisible(true);
    }

    private void mostrarResultado(String resultado) {
        JTextArea area = new JTextArea(resultado);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Resultado do Caminho",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void abrirMenuAnalise() {
        JDialog dialog = new JDialog(this, "Tipo de Análise");
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new FlowLayout());

        JButton btnNo = new JButton("Analisar Nó");
        JButton btnArvore = new JButton("Analisar Árvore");

        dialog.add(btnNo);
        dialog.add(btnArvore);

        btnNo.addActionListener(e -> {
            dialog.dispose();
            abrirAnaliseNoDialog();
        });

        btnArvore.addActionListener(e -> {
            dialog.dispose();
            analisarArvore();
        });

        dialog.setVisible(true);
    }

    private void abrirAnaliseNoDialog() {
        JDialog dialog = new JDialog(this, "Analisar Nó", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new FlowLayout());

        JLabel label = new JLabel("Valor do nó:");
        JTextField campo = new JTextField(10);
        JButton btnAnalisar = new JButton("Analisar");

        dialog.add(label);
        dialog.add(campo);
        dialog.add(btnAnalisar);

        btnAnalisar.addActionListener(e -> {
            try {
                Long valor = Long.parseLong(campo.getText());

                No no = arvore.buscar(valor);

                if (no == null) {
                    JOptionPane.showMessageDialog(dialog, "Nó não encontrado!");
                    return;
                }

                int profundidade = arvore.calcProfundidade(valor);
                int altura = arvore.calcAltura(no);
                int nivel = profundidade;

                int grau = 0;
                if (no.getEsquerda() != null) grau++;
                if (no.getDireita() != null) grau++;

                JOptionPane.showMessageDialog(dialog,
                        "Valor do nó: " + valor +
                                "\nProfundidade: " + profundidade +
                                "\nNível: " + nivel +
                                "\nGrau (Nº de filhos): " + grau +
                                "\nAltura: " + altura);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Digite um número válido!");
            }
        });

        dialog.setVisible(true);
    }

    private void analisarArvore() {
        if (arvore.root == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia!");
            return;
        }

        int altura = arvore.calcAltura(arvore.root);
        int nivel = altura;
        int profMaxima = altura;
        int totalNos = arvore.contarNos(arvore.root);
        String tipo = arvore.obterTipoArvore();

        JOptionPane.showMessageDialog(this,
                "Análise da Árvore:\n\n" +
                        "Altura: " + altura +
                        "\nNível: " + nivel +
                        "\nProfundidade: " + profMaxima +
                        "\nQuantidade de nós: " + totalNos +
                        "\nTipo: " + tipo);
    }

    private void desfazer() {
        if (undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nada para desfazer!");
            return;
        }

        redoStack.push(arvore.copiar());
        arvore = undoStack.pop();

        atualizarVisualizacao();
        outputArea.setText("Última ação desfeita.");
    }

    private void refazer() {
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nada para refazer!");
            return;
        }

        undoStack.push(arvore.copiar());
        arvore = redoStack.pop();

        atualizarVisualizacao();
        outputArea.setText("Ação refeita.");
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

        salvarEstadoParaUndo();
        arvore.limpar();
        atualizarVisualizacao();

        if (opcao == JOptionPane.NO_OPTION) {
            outputArea.setText("A árvore foi resetada sem salvar.");
        } else {
            outputArea.append("\nA árvore foi resetada.");
        }

        int trocarTipo = JOptionPane.showConfirmDialog(
                this,
                "Deseja escolher outro tipo de árvore?",
                "Alterar tipo",
                JOptionPane.YES_NO_OPTION
        );

        if (trocarTipo == JOptionPane.YES_OPTION) {
            escolherTipoArvore();
            outputArea.append("\nTipo de árvore alterado.");
        }
    }

    private void carregarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar árvore de arquivo TXT");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));

        int escolha = fileChooser.showOpenDialog(this);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            try {
                java.util.Scanner scanner = new java.util.Scanner(arquivo);
                StringBuilder conteudo = new StringBuilder();

                if (scanner.hasNextLine()) scanner.nextLine();
                if (scanner.hasNextLine()) scanner.nextLine();
                
                while (scanner.hasNextLine()) {
                    conteudo.append(scanner.nextLine());
                }
                scanner.close();

                salvarEstadoParaUndo();
                arvore.carregarParenteses(conteudo.toString());
                atualizarVisualizacao();
                
                outputArea.setText("Árvore carregada com sucesso de: " + arquivo.getName());
                
                String tipo = arvore.obterTipoArvore();
                outputArea.append("\nTipo da árvore carregada: " + tipo);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar arquivo: " + e.getMessage());
            }
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

    private void escolherTipoArvore() {
        String[] opcoes = {"Árvore Binária de Busca", "Árvore Binária AVL"};

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Escolha o tipo de árvore:",
                "Tipo",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (escolha == null) {
            JOptionPane.showMessageDialog(this, "Você precisa escolher um tipo de árvore.");
            return;
        }

        isAVL = escolha.equals("Árvore Binária AVL");
    }

}
