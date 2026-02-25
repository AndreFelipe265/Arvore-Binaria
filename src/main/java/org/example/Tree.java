package org.example;

public class Tree {
    public No root;

    public void exibir() {
        System.out.print("\n Exibindo em ordem: ");
        inOrder(root);
    }

    public void inOrder(No atual) {
        if (atual != null) {
            inOrder(atual.esq);
            System.out.print(atual.item + " ");
            inOrder(atual.dir);
        }
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
}
