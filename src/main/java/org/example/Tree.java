package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Tree {
    public No root;

    public String caminhoLNR(No raiz) {
        StringBuilder caminho = new StringBuilder();

        if (raiz != null) {
            caminho.append(caminhoLNR(raiz.esq));
            caminho.append(raiz.item).append(" ");
            caminho.append(caminhoLNR(raiz.dir));
        }

        return caminho.toString();
    }

    public String caminhoNLR(No raiz) {
        StringBuilder caminho = new StringBuilder();

        if (raiz != null) {
            caminho.append(raiz.item).append(" ");
            caminho.append(caminhoNLR(raiz.esq));
            caminho.append(caminhoNLR(raiz.dir));
        }

        return caminho.toString();
    }

    public String caminhoLRN(No raiz) {
        StringBuilder caminho = new StringBuilder();

        if (raiz != null) {
            caminho.append(caminhoLRN(raiz.esq));
            caminho.append(caminhoLRN(raiz.dir));
            caminho.append(raiz.item).append(" ");
        }

        return caminho.toString();
    }

    public void inserir(Long v) {
        No novo = new No();
        novo.item = v;
        novo.dir = null;
        novo.esq = null;

        if (root == null) {
            root = novo;
        } else {
            No atual = root;
            No anterior;

            while (true) {
                anterior = atual;

                if (v.equals(atual.item)) {
                    throw new RuntimeException("Numero ja existe na arvore: " + v);
                }

                if (v < atual.item) {
                    atual = atual.esq;
                    if (atual == null) {
                        anterior.esq = novo;
                        return;
                    }
                } else {
                    atual = atual.dir;
                    if (atual == null) {
                        anterior.dir = novo;
                        return;
                    }
                }
            }
        }
    }

    public No buscar(Long valor) {
        No atual = root;

        while (atual != null) {
            if (valor.equals(atual.item)) {
                return atual;
            }

            if (valor < atual.item) {
                atual = atual.esq;
            } else {
                atual = atual.dir;
            }
        }

        return null;
    }

    public int calcProfundidade(Long valor) {
        No atual = root;
        int profundidade = 0;

        while (atual != null) {
            if (valor.equals(atual.item)) {
                return profundidade;
            }

            if (valor < atual.item) {
                atual = atual.esq;
            } else {
                atual = atual.dir;
            }

            profundidade++;
        }

        return -1;
    }

    public int calcAltura(No no) {
        if (no == null) return -1;

        int alturaEsq = calcAltura(no.esq);
        int alturaDir = calcAltura(no.dir);

        return 1 + Math.max(alturaEsq, alturaDir);
    }

    public int contarNos(No no) {
        if (no == null) return 0;
        return 1 + contarNos(no.esq) + contarNos(no.dir);
    }

    private boolean isCheia(No no) {
        if (no == null) return true;

        int altura = calcAltura(no);
        return isCheia(no, 0, altura);
    }

    private boolean isCheia(No no, int nivel, int altura) {
        if (no == null) return true;

        if (no.esq == null && no.dir == null) {
            return nivel == altura;
        }

        if (no.esq == null || no.dir == null) {
            return false;
        }

        return isCheia(no.esq, nivel + 1, altura) && isCheia(no.dir, nivel + 1, altura);
    }

    private boolean isCompleta(No no) {
        if (no == null) return true;

        Queue<No> fila = new LinkedList<>();
        fila.add(no);
        boolean encontrouEspacoVazio = false;

        while (!fila.isEmpty()) {
            No atual = fila.poll();

            if (atual.esq != null) {
                if (encontrouEspacoVazio) return false;
                fila.add(atual.esq);
            } else {
                encontrouEspacoVazio = true;
            }

            if (atual.dir != null) {
                if (encontrouEspacoVazio) return false;
                fila.add(atual.dir);
            } else {
                encontrouEspacoVazio = true;
            }
        }

        return true;
    }

    public String obterTipoArvore() {
        if (root == null) return "Arvore vazia";

        if (isCheia(root)) return "Cheia";
        if (isCompleta(root)) return "Completa";
        return "Incompleta";
    }

    public void limpar() {
        root = null;
    }

    public void carregarParenteses(String conteudo) {
        conteudo = conteudo.trim();
        if (conteudo.isEmpty() || conteudo.equals("()")) {
            root = null;
            return;
        }
        root = parseParenteses(new StringTokenizer(conteudo, "() ", true));
    }

    private No parseParenteses(StringTokenizer st) {
        if (!st.hasMoreTokens()) return null;

        String token = st.nextToken();
        while (token.equals(" ")) {
            if (!st.hasMoreTokens()) return null;
            token = st.nextToken();
        }

        if (token.equals("(")) {
            if (!st.hasMoreTokens()) return null;
            token = st.nextToken();

            while (token.equals(" ")) {
                if (!st.hasMoreTokens()) return null;
                token = st.nextToken();
            }

            if (token.equals(")")) {
                return null;
            }

            No no = new No();
            no.item = Long.parseLong(token);

            no.esq = parseParenteses(st);
            no.dir = parseParenteses(st);

            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.equals(")")) break;
            }

            return no;
        }

        return null;
    }

    public String gerarParenteses() {
        return gerarParenteses(root);
    }

    private String gerarParenteses(No no) {
        if (no == null) {
            return "()";
        }

        return "("
                + no.item + " "
                + gerarParenteses(no.esq) + " "
                + gerarParenteses(no.dir)
                + ")";
    }

    public Tree copiar() {
        Tree nova = new Tree();
        nova.root = copiarNo(this.root);
        return nova;
    }

    private No copiarNo(No no) {
        if (no == null) return null;

        No novo = new No();
        novo.item = no.item;
        novo.esq = copiarNo(no.esq);
        novo.dir = copiarNo(no.dir);

        return novo;
    }
}
