package blackjack;

import java.util.ArrayList;

public class Jogador extends ArrayList<Carta> { //o próprio jogador é sua mão de cartas

    private final String nome;

    public Jogador(String nome) { this.nome = nome; }

    public String getNome() { return nome; }

    public boolean darCarta(Carta carta) { return this.add(carta); } //adiciona uma carta à mão (delega para add)

    public Carta[] getCartas() { // retorna array de cartas ou null se a mão estiver vazia
        if (this.isEmpty()) return null;
        return this.toArray(new Carta[0]);
    }

    public float somaCartas() {  //itera sobre todas as cartas somando os valores e  retorna o total

        float total = 0f;
        for (Carta c : this) total += c.getValor();
        return total;
    }
}