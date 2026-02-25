package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tree arvore = new Tree();

        while(true){
            System.out.println("\n 1 - Escrever um número  ");
            System.out.println(" 2 - Exibir ordem ");
            System.out.println(" 0 - Sair  ");
            System.out.print(" Escolha uma opção:  ");
            long i = sc.nextLong();

            if (i == 0){
                break;
            }else if (i == 1){
                System.out.println("Escreva um número: ");
                Long num = sc.nextLong();

                arvore.inserir(num);
                System.out.println("Inserido com sucesso!!");
            } else if(i == 2){

                arvore.exibir();

            }else{
                System.out.println("Número invalido");
            }

        }
    }
}
