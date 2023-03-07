package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jogotabuleiro.Peca;
import jogotabuleiro.Posicao;
import jogotabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Dama;
import xadrez.pecas.Peao;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaXadrez enPassantVulneravel;
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCA;
		configuracaoInicial();
	}

	// Esse metodo percorre toda a matriz de peca do tabuleiro e retorna uma matriz
	// com as posicoes de cada peca
	public PecaXadrez[][] getPecas() {

		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];

		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public PecaXadrez getEnPassantVulneravel() {
		return enPassantVulneravel;
	}

	public PecaXadrez executarJogada(PosicaoXadrez posicaoDeOrigem, PosicaoXadrez posicaoDeDestino) {
		Posicao origem = posicaoDeOrigem.paraPosicao();
		Posicao destino = posicaoDeDestino.paraPosicao();
		validarPosicaoDeOrigem(origem);
		validarPosicaoDeDestino(origem, destino);
		Peca pecaCapturada = fazerMovimento(origem, destino);

		if (testeCheck(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Voce nao pode se colocar em check");
		}

		PecaXadrez pecaQueMoveu = (PecaXadrez) tabuleiro.peca(destino);

		check = (testeCheck(oponente(jogadorAtual))) ? true : false;

		if (testeCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}

		// #Movimento especial en passant
		if (pecaQueMoveu instanceof Peao
				&& (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantVulneravel = pecaQueMoveu;
		} else {
			enPassantVulneravel = null;
		}

		return (PecaXadrez) pecaCapturada;
	}

	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(origem);
		p.aumentarContagemDeMovimento();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.colocarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);

		}

		// #Movimento especial Roque pequeno
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarContagemDeMovimento();
		}

		// #Movimento especial Roque grande
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarContagemDeMovimento();
		}

		// #Movimento especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCA) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
				pecasNoTabuleiro.remove(pecaCapturada);
				pecasCapturadas.add(pecaCapturada);
			}
		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(destino);
		p.diminuirContagemDeMovimento();
		tabuleiro.colocarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.colocarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// #Movimento especial Roque pequeno
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirContagemDeMovimento();
		}

		// #Movimento especial Roque grande
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirContagemDeMovimento();
		}

		// #Movimento especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVulneravel) {
				PecaXadrez peao = (PecaXadrez) tabuleiro.removerPeca(destino);
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCA) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocarPeca(peao, posicaoPeao);
			}
		}
	}

	private void validarPosicaoDeOrigem(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezException("Nao existe peca na posicao de origem");
		}
		if (jogadorAtual != ((PecaXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A peca escolhida nao e sua");
		}
		if (!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
			throw new XadrezException("Nao ha movimentos possiveis para a peca escolhida");
		}
	}

	private void validarPosicaoDeDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).possivelMover(destino)) {
			throw new XadrezException("A peca escolhida nao pode se mover para a posicao de destino");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
	}

	public boolean[][] movimentosPossiveis(PosicaoXadrez posicaoXadrex) {
		Posicao posicao = posicaoXadrex.paraPosicao();
		validarPosicaoDeOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
	}

	private PecaXadrez rei(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : lista) {
			if (p instanceof Rei) {
				return (PecaXadrez) p;
			}
		}
		throw new IllegalStateException("Nao existe o Rei da cor " + cor + "no tabuleiro");
	}

	// Metodo que verifica se o Rei esta em CHECK
	private boolean testeCheck(Cor cor) {
		Posicao posicaoDoRei = rei(cor).getPosicaoXadrez().paraPosicao();
		List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecasDoOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoDoRei.getLinha()][posicaoDoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	// Metodo que verifica se existe algum movimento possivel que tire o Rei do
	// CHECK
	// se nao houver, informa que foi checkmate.
	private boolean testeCheckMate(Cor cor) {
		if (!testeCheck(cor)) {
			return false;
		}
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : lista) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < mat.length; i++) {
				for (int j = 0; j < mat[0].length; j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadrez) p).getPosicaoXadrez().paraPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = fazerMovimento(origem, destino);
						boolean testeCheck = testeCheck(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// Metodo para colocar uma peca passando as informacoes da coluna e linha do
	// tabuleiro de xadrez
	public void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).paraPosicao());
		pecasNoTabuleiro.add(peca);
	}

	// Esse metodo coloca as pecas na configuracao inicial para iniciar a partida
	public void configuracaoInicial() {
		colocarNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('d', 1, new Dama(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCA));
		colocarNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCA, this));
		colocarNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCA, this));

		colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETA));
		colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETA));
		colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETA));
		colocarNovaPeca('d', 8, new Dama(tabuleiro, Cor.PRETA));
		colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETA));
		colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETA));
		colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETA));
		colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETA, this));
		colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETA, this));

	}
}
