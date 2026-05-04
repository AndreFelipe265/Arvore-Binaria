package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class AVLService {

    private final Tree tree;
    private final List<PassoAVL> passos;

    public AVLService(Tree tree) {
        this.tree = tree;
        this.passos = new ArrayList<>();
    }

    public ResultadoInsercao inserirComPassos(Long valor) {
        passos.clear();
        recalcularAlturas(tree.root);

        List<Long> caminho = new ArrayList<>();

        if (tree.root == null) {
            No novo = criarNo(valor);
            tree.root = novo;
            caminho.add(valor);

            String caminhoTexto = formatarCaminho(caminho);
            String posicaoInsercao = "raiz, profundidade 0";
            adicionarPasso("Inserção como raiz", valor, caminhoTexto, posicaoInsercao,
                    TipoRotacao.NENHUMA, "nenhuma", novo.item, 0);

            return new ResultadoInsercao(passos,
                    criarLinhaHistorico(valor, posicaoInsercao, novo.item, 0,
                            TipoRotacao.NENHUMA.getDescricao(), "nenhuma"));
        }

        List<No> caminhoNos = new ArrayList<>();
        No atual = tree.root;
        No pai = null;

        while (atual != null) {
            pai = atual;
            caminhoNos.add(atual);
            caminho.add(atual.item);

            if (valor < atual.item) {
                atual = atual.esq;
            } else if (valor > atual.item) {
                atual = atual.dir;
            } else {
                return new ResultadoInsercao(passos, "");
            }
        }

        No novo = criarNo(valor);
        caminho.add(valor);
        int profundidadeInsercao = caminho.size() - 1;
        String posicaoInsercao;
        if (valor < pai.item) {
            pai.esq = novo;
            posicaoInsercao = "lado esquerdo, profundidade " + profundidadeInsercao;
        } else {
            pai.dir = novo;
            posicaoInsercao = "lado direito, profundidade " + profundidadeInsercao;
        }

        atualizarAlturasCaminho(caminhoNos);

        String caminhoTexto = formatarCaminho(caminho);
        Balanceamento balanceamento = encontrarBalanceamento(caminhoNos);

        if (!balanceamento.desbalanceado) {
            adicionarPasso("Inserção sem rotação", valor, caminhoTexto, posicaoInsercao,
                    TipoRotacao.NENHUMA, "nenhuma", balanceamento.no.item, balanceamento.fator);

            return new ResultadoInsercao(passos,
                    criarLinhaHistorico(valor, posicaoInsercao,
                            balanceamento.no.item, balanceamento.fator,
                            TipoRotacao.NENHUMA.getDescricao(), "nenhuma"));
        }

        TipoRotacao tipoRotacao = identificarRotacao(balanceamento.no, valor);
        String rotacoesFeitas = descreverRotacoes(tipoRotacao, balanceamento.no);
        adicionarPasso("Desbalanceamento encontrado", valor, caminhoTexto, posicaoInsercao,
                tipoRotacao, rotacoesFeitas, balanceamento.no.item, balanceamento.fator);
        adicionarPasso("Executando rotação", valor, caminhoTexto, posicaoInsercao,
                tipoRotacao, rotacoesFeitas, balanceamento.no.item, balanceamento.fator);

        No novaRaizSubarvore = aplicarRotacao(balanceamento.no, tipoRotacao);
        conectarSubarvoreRotacionada(caminhoNos, balanceamento.indice, novaRaizSubarvore);
        atualizarAlturasAcima(caminhoNos, balanceamento.indice);

        adicionarPasso("Rotação aplicada", valor, caminhoTexto, posicaoInsercao,
                tipoRotacao, rotacoesFeitas, novaRaizSubarvore.item,
                tree.fatorBalanceamento(novaRaizSubarvore));

        return new ResultadoInsercao(passos,
                criarLinhaHistorico(valor, posicaoInsercao,
                        balanceamento.no.item, balanceamento.fator,
                        tipoRotacao.getDescricao(), rotacoesFeitas));
    }

    private No criarNo(Long valor) {
        No novo = new No();
        novo.item = valor;
        novo.altura = 1;
        return novo;
    }

    private void atualizarAlturasCaminho(List<No> caminhoNos) {
        for (int i = caminhoNos.size() - 1; i >= 0; i--) {
            atualizarAltura(caminhoNos.get(i));
        }
    }

    private void atualizarAlturasAcima(List<No> caminhoNos, int indiceRotacao) {
        for (int i = indiceRotacao - 1; i >= 0; i--) {
            atualizarAltura(caminhoNos.get(i));
        }
    }

    private int recalcularAlturas(No no) {
        if (no == null) {
            return 0;
        }

        no.altura = 1 + Math.max(recalcularAlturas(no.esq), recalcularAlturas(no.dir));
        return no.altura;
    }

    private void atualizarAltura(No no) {
        no.altura = 1 + Math.max(tree.altura(no.esq), tree.altura(no.dir));
    }

    private Balanceamento encontrarBalanceamento(List<No> caminhoNos) {
        No maiorFator = caminhoNos.get(caminhoNos.size() - 1);
        int fatorMaior = tree.fatorBalanceamento(maiorFator);
        int indiceMaior = caminhoNos.size() - 1;

        for (int i = caminhoNos.size() - 1; i >= 0; i--) {
            No no = caminhoNos.get(i);
            int fator = tree.fatorBalanceamento(no);

            if (Math.abs(fator) > Math.abs(fatorMaior)) {
                maiorFator = no;
                fatorMaior = fator;
                indiceMaior = i;
            }

            if (Math.abs(fator) > 1) {
                return new Balanceamento(no, fator, i, true);
            }
        }

        return new Balanceamento(maiorFator, fatorMaior, indiceMaior, false);
    }

    private TipoRotacao identificarRotacao(No no, Long valor) {
        int fator = tree.fatorBalanceamento(no);

        if (fator > 1 && valor < no.esq.item) {
            return TipoRotacao.SIMPLES_DIREITA;
        }

        if (fator < -1 && valor > no.dir.item) {
            return TipoRotacao.SIMPLES_ESQUERDA;
        }

        if (fator > 1 && valor > no.esq.item) {
            return TipoRotacao.DUPLA_DIREITA;
        }

        return TipoRotacao.DUPLA_ESQUERDA;
    }

    private No aplicarRotacao(No no, TipoRotacao tipoRotacao) {
        return switch (tipoRotacao) {
            case SIMPLES_DIREITA -> tree.rotacaoSimplesDireita(no);
            case SIMPLES_ESQUERDA -> tree.rotacaoSimplesEsquerda(no);
            case DUPLA_DIREITA -> tree.rotacaoDuplaParaDireita(no);
            case DUPLA_ESQUERDA -> tree.rotacaoDuplaParaEsquerda(no);
            case NENHUMA -> no;
        };
    }

    private String descreverRotacoes(TipoRotacao tipoRotacao, No no) {
        return switch (tipoRotacao) {
            case SIMPLES_DIREITA -> "girou a subárvore do nó " + no.item + " para a direita";
            case SIMPLES_ESQUERDA -> "girou a subárvore do nó " + no.item + " para a esquerda";
            case DUPLA_DIREITA -> "girou a subárvore do nó " + no.esq.item
                    + " para a esquerda e depois a subárvore do nó " + no.item + " para a direita";
            case DUPLA_ESQUERDA -> "girou a subárvore do nó " + no.dir.item
                    + " para a direita e depois a subárvore do nó " + no.item + " para a esquerda";
            case NENHUMA -> "nenhuma";
        };
    }

    private void conectarSubarvoreRotacionada(List<No> caminhoNos, int indiceRotacao, No novaRaizSubarvore) {
        if (indiceRotacao == 0) {
            tree.root = novaRaizSubarvore;
            return;
        }

        No pai = caminhoNos.get(indiceRotacao - 1);
        No noRotacionado = caminhoNos.get(indiceRotacao);

        if (pai.esq == noRotacionado) {
            pai.esq = novaRaizSubarvore;
        } else {
            pai.dir = novaRaizSubarvore;
        }
    }

    private void adicionarPasso(String titulo, Long valor, String caminho, String posicaoInsercao,
                                TipoRotacao rotacao, String rotacoesFeitas,
                                Long noBalanceamento, int fatorBalanceamento) {
        passos.add(new PassoAVL(
                tree.copiar(tree.root),
                titulo,
                valor,
                caminho,
                posicaoInsercao,
                rotacao.getDescricao(),
                rotacoesFeitas,
                noBalanceamento,
                fatorBalanceamento
        ));
    }

    private String criarLinhaHistorico(Long valor, String posicaoInsercao,
                                       Long noBalanceamento, int fatorBalanceamento,
                                       String tipoRotacao,
                                       String rotacoesFeitas) {
        String balanceamento = "FB(" + noBalanceamento + ")=" + fatorBalanceamento;

        if (rotacoesFeitas.equals("nenhuma")) {
            return "Nó " + valor + " inserido.\n"
                    + "Posição de inserção: " + posicaoInsercao + ".\n"
                    + "Balanceamento: " + balanceamento + ".\n"
                    + "Rotação: nenhuma.";
        }

        return "Nó " + valor + " inserido.\n"
                + "Posição de inserção: " + posicaoInsercao + ".\n"
                + "Balanceamento: " + balanceamento + ".\n"
                + "Rotação: " + tipoRotacao + " - " + rotacoesFeitas + ".";
    }

    private String formatarCaminho(List<Long> caminho) {
        StringJoiner joiner = new StringJoiner(" -> ");
        for (Long valor : caminho) {
            joiner.add(String.valueOf(valor));
        }
        return joiner.toString();
    }

    private enum TipoRotacao {
        NENHUMA("Nenhuma"),
        SIMPLES_DIREITA("Simples à direita (LL)"),
        SIMPLES_ESQUERDA("Simples à esquerda (RR)"),
        DUPLA_DIREITA("Dupla à direita (LR)"),
        DUPLA_ESQUERDA("Dupla à esquerda (RL)");

        private final String descricao;

        TipoRotacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    private static class Balanceamento {
        private final No no;
        private final int fator;
        private final int indice;
        private final boolean desbalanceado;

        private Balanceamento(No no, int fator, int indice, boolean desbalanceado) {
            this.no = no;
            this.fator = fator;
            this.indice = indice;
            this.desbalanceado = desbalanceado;
        }
    }

    public static class ResultadoInsercao {
        private final List<PassoAVL> passos;
        private final String linhaHistorico;

        private ResultadoInsercao(List<PassoAVL> passos, String linhaHistorico) {
            this.passos = new ArrayList<>(passos);
            this.linhaHistorico = linhaHistorico;
        }

        public List<PassoAVL> getPassos() {
            return passos;
        }

        public String getLinhaHistorico() {
            return linhaHistorico;
        }
    }

    public static class PassoAVL {
        private final No root;
        private final String titulo;
        private final Long valorInserido;
        private final String caminho;
        private final String posicaoInsercao;
        private final String rotacao;
        private final String rotacoesFeitas;
        private final Long noBalanceamento;
        private final int fatorBalanceamento;

        private PassoAVL(No root, String titulo, Long valorInserido, String caminho, String posicaoInsercao,
                         String rotacao, String rotacoesFeitas,
                         Long noBalanceamento, int fatorBalanceamento) {
            this.root = root;
            this.titulo = titulo;
            this.valorInserido = valorInserido;
            this.caminho = caminho;
            this.posicaoInsercao = posicaoInsercao;
            this.rotacao = rotacao;
            this.rotacoesFeitas = rotacoesFeitas;
            this.noBalanceamento = noBalanceamento;
            this.fatorBalanceamento = fatorBalanceamento;
        }

        public No getRoot() {
            return root;
        }

        public String getTitulo() {
            return titulo;
        }

        public Long getValorInserido() {
            return valorInserido;
        }

        public String getCaminho() {
            return caminho;
        }

        public String getPosicaoInsercao() {
            return posicaoInsercao;
        }

        public String getRotacao() {
            return rotacao;
        }

        public String getRotacoesFeitas() {
            return rotacoesFeitas;
        }

        public Long getNoBalanceamento() {
            return noBalanceamento;
        }

        public int getFatorBalanceamento() {
            return fatorBalanceamento;
        }
    }
}
