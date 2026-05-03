package org.example;

public class No {
    public long item;
    public No dir;
    public No esq;
    public int altura;

    public No() {
        altura = 1;
    }

    public No getEsquerda() {
        return esq;
    }

    public No getDireita() {
        return dir;
    }

    @Override
    public String toString() {
        return "[Nó " + item + "]";
    }
}
