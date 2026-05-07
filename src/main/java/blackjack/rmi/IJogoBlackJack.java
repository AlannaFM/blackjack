package blackjack.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface remota RMI do jogo
 * expõe todas as ações que o cliente pode invocar no servidor
 */
public interface IJogoBlackJack extends Remote {

    // Registra o jogador 2 (cliente) e retorna o nome do jogador 1 (servidor).
    String entrarNaSala(String nomeJogador2) throws RemoteException;

    // Inicia uma nova partida com a Baralho indicada. Só o servidor pode chamar.
    void novaPartida(String nomeBaralho) throws RemoteException;

    // Jogador 1 pede uma carta; retorna a URL da imagem sorteada.
    String pedirCartaJogador1() throws RemoteException;

    // Jogador 2 pede uma carta; retorna a URL da imagem sorteada.
    String pedirCartaJogador2() throws RemoteException;

    // Jogador 1 se planta.
    void plantarJogador1() throws RemoteException;

    // Jogador 2 se planta.
    void plantarJogador2() throws RemoteException;

    // Envia uma mensagem de chat.
    void enviarMensagem(String remetente, String texto) throws RemoteException;

    // ── Estado consultável pelo cliente via polling ──────────────────────────

    // Retorna a URL da última carta dada ao jogador 1, ou null se não houver novidade.
    String getUltimaCartaJogador1() throws RemoteException;

    // Retorna a URL da última carta dada ao jogador 2, ou null se não houver novidade.
    String getUltimaCartaJogador2() throws RemoteException;

    // Retorna true se o jogador 1 está plantado.
    boolean isJogador1Plantado() throws RemoteException;

    // Retorna true se o jogador 2 está plantado.
    boolean isJogador2Plantado() throws RemoteException;

    // Retorna a última mensagem de chat recebida (formato "Remetente: texto") ou null.
    String getUltimaMensagem() throws RemoteException;

    // Retorna o nome da Baralho da partida em andamento, ou null se sem partida.
    String getNomeBaralho() throws RemoteException;

    // Limpa a última carta do jogador 1 após o cliente ter lido.
    void consumirCartaJogador1() throws RemoteException;

    // Limpa a última carta do jogador 2 após o cliente ter lido.
    void consumirCartaJogador2() throws RemoteException;

    // Limpa a última mensagem após o cliente ter lido.
    void consumirMensagem() throws RemoteException;

    // Retorna o ping em ms calculado pelo servidor.
    long getPing() throws RemoteException;

    // Chamado pelo cliente para medir o ping (round-trip).
    void pong() throws RemoteException;

    // Retorna o nome do jogador 2 (cliente), ou null se ainda não conectou.
    String getNomeJogador2() throws RemoteException;
}