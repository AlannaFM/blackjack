package blackjack;

import java.io.Serializable;
// avisa a máquina virtual que o objeto pode ser convertido pra poder trafegar
// na rede via RMI


public class Carta implements Serializable {

    private static final long serialVersionUID = 1L;
    //garante compatibilidade de versão na serialização entre cliente e servidor.

    private String imagem;
    private float valor;

    public Carta(String imagem, float valor) {
        this.imagem = imagem;
        this.valor = valor;
    }

    public String getImagem() { return imagem; }
    public float getValor()   { return valor; }
    public void setValor(float valor) { this.valor = valor; } // permite alterar o valor, usado especificamente para a regra do Ás
}