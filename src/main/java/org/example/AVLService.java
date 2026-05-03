package org.example;

import java.util.ArrayList;
import java.util.List;

public class AVLService {

    private Tree tree;
    private List<No> passos;

    public AVLService(Tree tree) {
        this.tree = tree;
        this.passos = new ArrayList<>();
    }

    public List<No> inserirComPassos(Long valor) {
        System.out.println("\n[Nó Inserido: " + valor + "]");
        passos.clear();

        tree.root = inserirAVLComPassos(tree.root, valor);
        System.out.println("[FIM DA INSERÇÃO] Árvore balanceada.\n");
        return passos;
    }

    private No inserirAVLComPassos(No no, Long valor) {
        if (no == null) {
            System.out.println("   - Nó nulo encontrado. Inserindo " + valor + " aqui.");
            No novo = new No();
            novo.item = valor;
            novo.altura = 1;

            passos.add(tree.copiar(tree.root));

            return novo;
        }

        if (valor < no.item) {
            System.out.println("   - " + valor + " < " + no.item + ": Descendo à esquerda do nó " + no.item);
            no.esq = inserirAVLComPassos(no.esq, valor);
        } else if (valor > no.item) {
            System.out.println("   - " + valor + " > " + no.item + ": Descendo à direita do nó " + no.item);
            no.dir = inserirAVLComPassos(no.dir, valor);
        } else {
            System.out.println("   - Valor " + valor + " já existe na árvore. Nenhuma alteração feita.");
            return no;
        }

        no.altura = 1 + Math.max(
                tree.altura(no.esq),
                tree.altura(no.dir)
        );

        int fb = tree.fatorBalanceamento(no);

        if (fb > 1 && valor < no.esq.item) {
            System.out.println("   - Fator de balanceamento (" + fb + ") exige Rotação Simples à Direita.");
            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesDireita(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb < -1 && valor > no.dir.item) {
            System.out.println("   - Fator de balanceamento (" + fb + ") exige Rotação Simples à Esquerda.");
            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesEsquerda(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb > 1 && valor > no.esq.item) {
            System.out.println("   - Fator de balanceamento (" + fb + ") exige Rotação Dupla à Direita no pivô: " + no.item);
            passos.add(tree.copiar(tree.root));

            no.esq = tree.rotacaoSimplesEsquerda(no.esq);

            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesDireita(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb < -1 && valor < no.dir.item) {
            System.out.println("   - Fator de balanceamento (" + fb + ") exige Rotação Dupla à Esquerda no pivô: " + no.item);
            passos.add(tree.copiar(tree.root));

            no.dir = tree.rotacaoSimplesDireita(no.dir);

            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesEsquerda(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        }

        return no;
    }
}
