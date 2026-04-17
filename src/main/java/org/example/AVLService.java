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
        passos.clear();

        tree.root = inserirAVLComPassos(tree.root, valor);
        return passos;
    }

    private No inserirAVLComPassos(No no, Long valor) {
        if (no == null) {
            No novo = new No();
            novo.item = valor;
            novo.altura = 1;

            passos.add(tree.copiar(tree.root));

            return novo;
        }

        if (valor < no.item) {
            no.esq = inserirAVLComPassos(no.esq, valor);
        } else if (valor > no.item) {
            no.dir = inserirAVLComPassos(no.dir, valor);
        } else {
            return no;
        }

        no.altura = 1 + Math.max(
                tree.altura(no.esq),
                tree.altura(no.dir)
        );

        int fb = tree.fatorBalanceamento(no);

        if (fb > 1 && valor < no.esq.item) {

            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesDireita(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb < -1 && valor > no.dir.item) {

            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesEsquerda(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb > 1 && valor > no.esq.item) {
            passos.add(tree.copiar(tree.root));

            no.esq = tree.rotacaoSimplesEsquerda(no.esq);

            passos.add(tree.copiar(tree.root));

            No novo = tree.rotacaoSimplesDireita(no);

            passos.add(tree.copiar(tree.root));

            return novo;
        } else if (fb < -1 && valor < no.dir.item) {

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
