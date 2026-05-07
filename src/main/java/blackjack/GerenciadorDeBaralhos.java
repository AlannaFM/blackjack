package blackjack;

import java.util.ArrayList;

//Gerencia a baralho do jogo, fornecendo cartas aleatórias

public class GerenciadorDeBaralhos extends ArrayList<Carta> {

    private final TiposDeBaralho baralho;

    public GerenciadorDeBaralhos(TiposDeBaralho baralho) {
        this.baralho = baralho;
        inicializarCartas();
    }

    public TiposDeBaralho getbaralho() { return baralho; }

    private void inicializarCartas() {
        int total = baralho.totalDeCartas();
        Float[] valores = baralho.getValores();
        String[] imagens = baralho.getImagensCartas();
        int v = valores.length;
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < v; j++) {
                if (this.size() >= total) break;
                this.add(new Carta(imagens[i + j], valores[j]));
            }
            i += v - 1;
        }
    }

    public Carta buscarCartaPorUrl(String url) {
        for (Carta c : this)
            if (c.getImagem().equals(url)) return c;
        return null;
    }

    public Carta cartaAleatoria(Jogador j1, Jogador j2) {
        int total = baralho.totalDeCartas();
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