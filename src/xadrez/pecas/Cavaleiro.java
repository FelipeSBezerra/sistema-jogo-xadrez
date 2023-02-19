package xadrez.pecas;

import jogotabuleiro.Tabuleiro;
import xadrez.Cor;
import xadrez.PecaXadrez;

public class Cavaleiro extends PecaXadrez {

	public Cavaleiro(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro, cor);
	}

	@Override
	public String toString() {
		return "C";
	}

	// logica ainda sera implementada
	@Override
	public boolean[][] movimentosPossiveis() {
		return null;
	}
}
