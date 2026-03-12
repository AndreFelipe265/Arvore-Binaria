package org.example;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    public No root;

    public void exibir() {
        System.out.print("\n Exibindo em ordem: ");
        inOrder(root);
    }

    public void exibirArvore() {
        System.out.println("\nÁrvore:");
        exibirArvore(root, 0);
    }

    private void exibirArvore(No atual, int nivel) {
        if (atual != null) {
            exibirArvore(atual.dir, nivel + 1);

            for (int i = 0; i < nivel; i++) {
                System.out.print("    "); // 4 espaços por nível
            }

            System.out.println(atual.item);

            exibirArvore(atual.esq, nivel + 1);
        }
    }

    public void inOrder(No atual) {
        if (atual != null) {
            inOrder(atual.esq);
            System.out.print(atual.item + " ");
            inOrder(atual.dir);
        }
    }

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

    public void inserir(Long v){
        No novo = new No();
        novo.item = v;
        novo.dir = null;
        novo.esq = null;

        if(root == null) root = novo;
        else{
            No atual = root;
            No anterior;

            while(true){
                anterior = atual;

                if (v.equals(atual.item)) {
                    throw new RuntimeException("Numéro já existe na árvore: " + v);
                }

                if(v < atual.item){
                    atual = atual.esq;
                    if(atual == null){
                        anterior.esq = novo;
                        return;
                    }
                }else{
                    atual = atual.dir;
                    if(atual == null){
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

}
