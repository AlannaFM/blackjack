package blackjack;

public class Partida {

    private final Jogador jogador1;
    private final Jogador jogador2;
    private final float limite;

    public Partida(Jogador j1, Jogador j2, float limite) {
        this.jogador1 = j1;
        this.jogador2 = j2;
        this.limite   = limite;
    }

    public Jogador getJogador1() { return jogador1; }
    public Jogador getJogador2() { return jogador2; }
    public float   getLimite()   { return limite; }

    public Jogador getVencedor() {
        if (empatou()) return null;
        if (j1PassouEj2Nao()) return jogador2;
        if (j2PassouEj1Nao()) return jogador1;
        if (nenhumPassouOuAmbosPassaram()) return maisProximoDoLimite();
        return null;
    }

    private boolean empatou() {
        return jogador1.somaCartas() == jogador2.somaCartas();
    }

    private boolean nenhumPassouOuAmbosPassaram() {
        float s1 = jogador1.somaCartas(), s2 = jogador2.somaCartas();
        return (s1 > limite && s2 > limite) || (s1 <= limite && s2 <= limite);
    }

    private boolean j1PassouEj2Nao() {
        return jogador1.somaCartas() > limite && jogador2.somaCartas() <= limite;
    }

    private boolean j2PassouEj1Nao() {
        return jogador2.somaCartas() > limite && jogador1.somaCartas() <= limite;
    }

    private Jogador maisProximoDoLimite() {
        return Math.abs(limite - jogador1.somaCartas()) < Math.abs(limite - jogador2.somaCartas())
                ? jogador1 : jogador2;
    }
}