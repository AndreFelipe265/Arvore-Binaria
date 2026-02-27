package org.example;

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
