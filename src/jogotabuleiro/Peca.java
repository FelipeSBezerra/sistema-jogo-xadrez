package jogotabuleiro;

public class Peca {

	protected Posicao posicao;
	private Tabuleiro tabuleiro;

	// A posicao inicial de uma peca é nula(null), mas pertence a um tabuleiro.
	public Peca(Tabuleiro tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

	public Tabuleiro getTabuleiro() {
		return tabuleiro;
	}

}
