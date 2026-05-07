package blackjack;

import java.util.ArrayList;

public class Jogador extends ArrayList<Carta> {

    private final String nome;

    public Jogador(String nome) { this.nome = nome; }

    public String getNome() { return nome; }

    public boolean darCarta(Carta carta) { return this.add(carta); }

    public Carta[] getCartas() {
        if (this.isEmpty()) return null;
        return this.toArray(new Carta[0]);
    }

    public float somaCartas() {
        float total = 0f;
        for (Carta c : this) total += c.getValor();
        return total;
    }
}