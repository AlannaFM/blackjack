package blackjack;

import javax.swing.JComponent;

//associa um componente swing a um erro ocorrido

public class MinhaExcecao extends Exception {

    private final JComponent causador;

    public MinhaExcecao(JComponent causador, String mensagem) {
        super(mensagem);
        this.causador = causador;
    }

    public JComponent getCausador() { return causador; }
}