package blackjack;

import java.util.ArrayList;

/**
 * gerencia o baralho padrão de BlackJack (52 cartas)
 *
 * fornece sorteio sem repetição entre as cartas já distribuídas
 */
public class GerenciadorDeBaralhos extends ArrayList<Carta> {

    public GerenciadorDeBaralhos() {
        inicializarCartas();
    }

    private void inicializarCartas() {
        String[] imagens = TiposDeBaralho.getImagensCartas();
        float[]  valores = TiposDeBaralho.getValores();
        int naipeSize    = valores.length; // 13

        for (int i = 0; i < imagens.length; i++) {
            this.add(new Carta(imagens[i], valores[i % naipeSize]));
        }
    }

    public Carta buscarCartaPorUrl(String url) {
        for (Carta c : this)
            if (c.getImagem().equals(url)) return c;
        return null;
    }

    // sorteia uma carta que ainda não está na mão de nenhum dos jogadores

    public Carta cartaAleatoria(Jogador j1, Jogador j2) {
        int total = TiposDeBaralho.totalDeCartas();
        ArrayList<Carta> emJogo = new ArrayList<>();
        if (j1.getCartas() != null)
            for (Carta c : j1.getCartas()) emJogo.add(c);
        if (j2.getCartas() != null)
            for (Carta c : j2.getCartas()) emJogo.add(c);
        int rand;
        do {
            rand = (int) (Math.random() * total);
        } while (emJogo.contains(this.get(rand)));
        return this.get(rand);
    }
}