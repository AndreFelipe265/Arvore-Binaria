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
                System.out.print("    ");
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
                    throw new RuntimeException("Número já existe na árvore: " + v);
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
        if (no.esq == null && no.dir == null) return true;
        if (no.esq != null && no.dir != null) {
            return isCheia(no.esq) && isCheia(no.dir);
        }
        return false;
    }

    private boolean isCompleta(No no, int index, int numNos) {
        if (no == null) return true;
        if (index >= numNos) return false;

        return isCompleta(no.esq, 2 * index + 1, numNos)
                && isCompleta(no.dir, 2 * index + 2, numNos);
    }

    private boolean isIncompleta(No no) {
        if (no == null) return true;
        if (no.esq != null && no.dir != null) return false;
        return isIncompleta(no.esq) && isIncompleta(no.dir);
    }

    public String obterTipoArvore() {
        if (root == null) return "Árvore Vazia";

        int numNos = contarNos(root);
        int altura = calcAltura(root);

        boolean cheia = isCheia(root);
        boolean completa = isCompleta(root, 0, numNos);
        boolean perfeita = (numNos == (Math.pow(2, altura + 1) - 1));
        boolean incompleta = isIncompleta(root);

        if (perfeita) return "Perfeita";
        if (cheia) return "Cheia";
        if (completa) return "Completa";
        if (incompleta) return "Incompleta";
        return "Árvore binária comum";
    }

    public void limpar() {
        root = null;
    }

    public void carregarDeParenteses(String conteudo) {
        // Remover o cabeçalho se houver
        if (conteudo.contains("Árvore em parênteses alinhados:")) {
            conteudo = conteudo.substring(conteudo.indexOf(":\n\n") + 3).trim();
        }

        this.root = parsingParenteses(conteudo);
    }

    private No parsingParenteses(String str) {
        str = str.trim();
        if (str.equals("()") || str.isEmpty()) {
            return null;
        }

        // Remover os parênteses externos: (10 (5 () ()) (15 () ())) -> 10 (5 () ()) (15 () ())
        if (str.startsWith("(") && str.endsWith(")")) {
            str = str.substring(1, str.length() - 1).trim();
        }

        // Localizar o primeiro espaço após o item para separar o valor dos filhos
        int primeiroEspaco = str.indexOf(' ');
        if (primeiroEspaco == -1) {
            // É um nó folha sem filhos representados: "10"
            No no = new No();
            no.item = Long.parseLong(str);
            no.esq = null;
            no.dir = null;
            return no;
        }

        String itemStr = str.substring(0, primeiroEspaco);
        No no = new No();
        no.item = Long.parseLong(itemStr);

        String resto = str.substring(primeiroEspaco).trim(); // "(5 () ()) (15 () ())"

        // Separar os dois blocos de parênteses (filho esquerdo e filho direito)
        int balance = 0;
        int divisor = -1;
        for (int i = 0; i < resto.length(); i++) {
            if (resto.charAt(i) == '(') balance++;
            else if (resto.charAt(i) == ')') balance--;

            if (balance == 0) {
                divisor = i;
                break;
            }
        }

        if (divisor != -1) {
            String esqStr = resto.substring(0, divisor + 1);
            String dirStr = resto.substring(divisor + 1).trim();

            no.esq = parsingParenteses(esqStr);
            no.dir = parsingParenteses(dirStr);
        }

        return no;
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