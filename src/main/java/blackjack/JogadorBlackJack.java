package blackjack;

//Jogador de BlackJack com regra especial do Ás (11 → 1 se ultrapassar 21).

public class JogadorBlackJack extends Jogador {

    public JogadorBlackJack(String nome) { super(nome); }

    @Override
    public boolean darCarta(Carta carta) { return this.add(carta); }

    @Override
    public boolean add(Carta carta) {
        if (carta.getValor() == 11f && carta.getValor() + this.somaCartas() > 21f)
            carta.setValor(1f);
        return super.add(carta);
    }
}