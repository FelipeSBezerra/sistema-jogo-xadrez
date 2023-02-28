package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.Cor;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;

public class UI {

	// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	// https://stackoverflow.com/questions/2979383/java-clear-the-console
	public static void limparTela() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static PosicaoXadrez lerPosicaoXadrez(Scanner sc) {
		try {
			String aux = sc.next();
			char coluna = aux.charAt(0);
			int linha = Integer.parseInt(aux.substring(1));
			return new PosicaoXadrez(coluna, linha);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Posicao nao existe: A posicao deve ser entre a1 e h8");
		}
	}

	// Esse metodo percorre toda matriz de peca de xadrez e imprime cada peca na
	// tela
	public static void printTabuleiro(PecaXadrez[][] peca) {
		System.out.println("======XADREZ=====");
		for (int i = 0; i < peca.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < peca[0].length; j++) {
				printPeca(peca[i][j], false);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	// Esse metodo percorre toda matriz de peca de xadrez e imprime cada peca na
	// tela e seus movimentos possiveis
	public static void printTabuleiro(PecaXadrez[][] peca, boolean[][] movimentosPossiveis) {
		System.out.println("======XADREZ=====");
		for (int i = 0; i < peca.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < peca[0].length; j++) {
				printPeca(peca[i][j], movimentosPossiveis[i][j]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	// Metodo para imprimir uma unica peca ou colore o planoDeFundo com um movimento possivel
	private static void printPeca(PecaXadrez peca, boolean planoDeFundo) {
		if(planoDeFundo) {
			System.out.print(ANSI_GREEN_BACKGROUND);
		}
		if (peca == null) {
			System.out.print("-" + ANSI_RESET);
		} else {
			if (peca.getCor() == Cor.BRANCA) {
				System.out.print(ANSI_WHITE + peca + ANSI_RESET);
			} else {
				System.out.print(ANSI_YELLOW + peca + ANSI_RESET);
			}
		}
		System.out.print(" ");
	}
}
