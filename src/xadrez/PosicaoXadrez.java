package xadrez;

import jogotabuleiro.Posicao;
import jogotabuleiro.TabuleiroException;

public class PosicaoXadrez {

	char coluna;
	int linha;
	
	public PosicaoXadrez(char coluna, int linha) {
		if(coluna < 'a' || coluna > 'h' && linha < 1 || linha > 8) {
			throw new TabuleiroException("Posicao não existe: A posicao deve ser entre a1 e h8");
		}
		this.coluna = coluna;
		this.linha = linha;
	}
	
	public char getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}

	// Metodo para, a partir de uma posicao de xadrez, passar para uma posicao de matriz
	protected Posicao paraPosicao() {
		return new Posicao(8 - linha, coluna - 'a');
	}
	
	// Metodo para, a partir de uma posicao de matriz, passar para uma posicao de xadrez
	protected static PosicaoXadrez daPosicao(Posicao posicao) {
		return new PosicaoXadrez((char)('a' - posicao.getColuna()), posicao.getLinha() - 8);
	}
	
	@Override
	public String toString() {
		return "" + coluna + linha;
	}
	
	
}
