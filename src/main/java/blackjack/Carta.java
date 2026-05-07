package blackjack;

import java.io.Serializable;


public class Carta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String imagem;
    private float valor;

    public Carta(String imagem, float valor) {
        this.imagem = imagem;
        this.valor = valor;
    }

    public String getImagem() { return imagem; }
    public float getValor()   { return valor; }
    public void setValor(float valor) { this.valor = valor; }
}