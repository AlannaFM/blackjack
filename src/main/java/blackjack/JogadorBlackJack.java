package blackjack;

//jogador com regra especial do Ás (11 → 1 se ultrapassar 21).

public class JogadorBlackJack extends Jogador {

    public JogadorBlackJack(String nome) { super(nome); }

    //sobrescrito mas apenas delega para add, garantindo que qualquer inserção passe pela verificação do Ás
    @Override
    public boolean darCarta(Carta carta) { return this.add(carta); }

    //sobrescreve o método add para aplicar a regra especial do Ás
    @Override
    public boolean add(Carta carta) {
        //se uma carta tem valor 11  e adicionar ela ultrapassaria 21, seu valor é ajustado para 1 antes de entrar na mão.
        if (carta.getValor() == 11f && carta.getValor() + this.somaCartas() > 21f)
            carta.setValor(1f);
        return super.add(carta);
    }
}