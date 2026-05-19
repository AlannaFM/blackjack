package blackjack.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota RMI do jogo.
 * Expõe todas as ações que o cliente (Angular) pode invocar no servidor via HTTP Bridge ou RMI direto.
 */
public interface IJogoBlackJack extends Remote {

    /** Registra o jogador 2 (cliente) e retorna o nome do jogador 1 (host). */
    String entrarNaSala(String nomeJogador2) throws RemoteException;

    /** Inicia uma nova partida com o baralho indicado. Só o host pode chamar. */
    void novaPartida(String nomeBaralho) throws RemoteException;

    /** Jogador 1 pede uma carta; retorna o nome do arquivo de imagem sorteado. */
    String pedirCartaJogador1() throws RemoteException;

    /** Jogador 2 pede uma carta; retorna o nome do arquivo de imagem sorteado. */
    String pedirCartaJogador2() throws RemoteException;

    /** Jogador 1 se planta. */
    void plantarJogador1() throws RemoteException;

    /** Jogador 2 se planta. */
    void plantarJogador2() throws RemoteException;

    /** Envia uma mensagem de chat para ambos os jogadores. */
    void enviarMensagem(String remetente, String texto) throws RemoteException;

    // ── Estado consultável via polling ───────────────────────────────────────

    /** Retorna a imagem da última carta do jogador 1 ainda não consumida, ou null. */
    String getUltimaCartaJogador1() throws RemoteException;

    /** Retorna a imagem da última carta do jogador 2 ainda não consumida, ou null. */
    String getUltimaCartaJogador2() throws RemoteException;

    /** Retorna true se o jogador 1 está plantado. */
    boolean isJogador1Plantado() throws RemoteException;

    /** Retorna true se o jogador 2 está plantado. */
    boolean isJogador2Plantado() throws RemoteException;

    /** Retorna a última mensagem de chat (legado – não utilizar). */
    String getUltimaMensagem() throws RemoteException;

    /** Retorna o nome do baralho da partida em andamento, ou null. */
    String getNomeBaralho() throws RemoteException;

    /** Consome (limpa) a última carta do jogador 1 após leitura. */
    void consumirCartaJogador1() throws RemoteException;

    /** Consome (limpa) a última carta do jogador 2 após leitura. */
    void consumirCartaJogador2() throws RemoteException;

    /** Consome a última mensagem geral (legado). */
    void consumirMensagem() throws RemoteException;

    /** Retorna o ping atual em ms calculado pelo servidor. */
    long getPing() throws RemoteException;

    /** Chamado pelo cliente para fechar o round-trip de ping. */
    void pong() throws RemoteException;

    /** Retorna o nome do jogador 2, ou null se ainda não conectou. */
    String getNomeJogador2() throws RemoteException;

    // ── Fila de mensagens de chat por jogador ────────────────────────────────

    /** Retorna a próxima mensagem pendente para o jogador 1, sem remover. */
    String getMensagemJogador1() throws RemoteException;

    /** Retorna a próxima mensagem pendente para o jogador 2, sem remover. */
    String getMensagemJogador2() throws RemoteException;

    /** Remove a mensagem lida da fila do jogador 1. */
    void consumirMensagemJogador1() throws RemoteException;

    /** Remove a mensagem lida da fila do jogador 2. */
    void consumirMensagemJogador2() throws RemoteException;

    // ── Estado completo da partida (para o Angular) ──────────────────────────

    /** Retorna um snapshot JSON do estado atual da partida. */
    String getEstadoPartida() throws RemoteException;

    /** Retorna os nomes dos baralhos disponíveis separados por vírgula. */
    String getBaralhosDisponiveis() throws RemoteException;
}
