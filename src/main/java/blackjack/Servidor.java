package blackjack;

import blackjack.rmi.IJogoBlackJack;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringJoiner;

/**
 * Implementação RMI do servidor de BlackJack.
 *
 * <p>Todo o estado da partida é mantido aqui. O cliente Angular consulta
 * via polling usando o {@link HttpBridge}, que repassa as chamadas para
 * este objeto RMI.</p>
 */
public class Servidor extends UnicastRemoteObject implements IJogoBlackJack {

    // ── Jogadores ────────────────────────────────────────────────────────────

    private final String          nomeJogador1;
    private volatile String       nomeJogador2;

    // ── Estado da partida ────────────────────────────────────────────────────

    private GerenciadorDeBaralhos gerenciador;
    private Partida               partidaAtual;

    private volatile boolean jogador1Plantado;
    private volatile boolean jogador2Plantado;

    // ── Eventos pendentes de leitura ─────────────────────────────────────────

    private volatile String ultimaCartaJogador1;
    private volatile String ultimaCartaJogador2;

    private final Queue<String> filaMensagensJ1 = new LinkedList<>();
    private final Queue<String> filaMensagensJ2 = new LinkedList<>();

    // ── Ping ─────────────────────────────────────────────────────────────────

    private volatile long pingMs    = 0;
    private volatile long tempoPing = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public Servidor(String nomeJogador1) throws RemoteException {
        super();
        this.nomeJogador1 = nomeJogador1;
    }

    // ── Entrada na sala ───────────────────────────────────────────────────────

    @Override
    public synchronized String entrarNaSala(String nomeJogador2) throws RemoteException {
        this.nomeJogador2 = nomeJogador2;
        System.out.println("[RMI] Jogador 2 entrou: " + nomeJogador2);
        return nomeJogador1;
    }

    // ── Controle de partida ───────────────────────────────────────────────────

    @Override
    public synchronized void novaPartida() throws RemoteException {
        jogador1Plantado    = false;
        jogador2Plantado    = false;
        ultimaCartaJogador1 = null;
        ultimaCartaJogador2 = null;
        filaMensagensJ1.clear();
        filaMensagensJ2.clear();

        gerenciador = new GerenciadorDeBaralhos();

        JogadorBlackJack j1 = new JogadorBlackJack(nomeJogador1);
        JogadorBlackJack j2 = new JogadorBlackJack(nomeJogador2 != null ? nomeJogador2 : "Jogador 2");

        partidaAtual = new Partida(j1, j2, TiposDeBaralho.LIMITE);

        // Carta inicial para cada jogador
        Carta c1 = gerenciador.cartaAleatoria(j1, j2);
        j1.darCarta(c1);
        ultimaCartaJogador1 = c1.getImagem();

        Carta c2 = gerenciador.cartaAleatoria(j1, j2);
        j2.darCarta(c2);
        ultimaCartaJogador2 = c2.getImagem();

        System.out.println("[RMI] Nova partida iniciada.");
    }

    // ── Ações dos jogadores ───────────────────────────────────────────────────

    @Override
    public synchronized String pedirCartaJogador1() throws RemoteException {
        garantirPartidaAtiva();
        Carta c = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador1().darCarta(c);
        ultimaCartaJogador1 = c.getImagem();
        return c.getImagem();
    }

    @Override
    public synchronized String pedirCartaJogador2() throws RemoteException {
        garantirPartidaAtiva();
        Carta c = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador2().darCarta(c);
        ultimaCartaJogador2 = c.getImagem();
        return c.getImagem();
    }

    @Override
    public synchronized void plantarJogador1() throws RemoteException {
        jogador1Plantado = true;
        System.out.println("[RMI] Jogador 1 plantou.");
    }

    @Override
    public synchronized void plantarJogador2() throws RemoteException {
        jogador2Plantado = true;
        System.out.println("[RMI] Jogador 2 plantou.");
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    @Override
    public synchronized void enviarMensagem(String remetente, String texto) throws RemoteException {
        String msg = remetente + ": " + texto;
        filaMensagensJ1.add(msg);
        filaMensagensJ2.add(msg);
    }

    // ── Polling – cartas ──────────────────────────────────────────────────────

    @Override
    public synchronized String getUltimaCartaJogador1() throws RemoteException { return ultimaCartaJogador1; }

    @Override
    public synchronized String getUltimaCartaJogador2() throws RemoteException { return ultimaCartaJogador2; }

    @Override
    public synchronized void consumirCartaJogador1() throws RemoteException { ultimaCartaJogador1 = null; }

    @Override
    public synchronized void consumirCartaJogador2() throws RemoteException { ultimaCartaJogador2 = null; }

    // ── Polling – estado ──────────────────────────────────────────────────────

    @Override
    public synchronized boolean isJogador1Plantado() throws RemoteException { return jogador1Plantado; }

    @Override
    public synchronized boolean isJogador2Plantado() throws RemoteException { return jogador2Plantado; }

    @Override
    public synchronized String getNomeJogador2() throws RemoteException { return nomeJogador2; }

    // ── Polling – mensagens ───────────────────────────────────────────────────

    @Override
    public synchronized String getMensagemJogador1() throws RemoteException { return filaMensagensJ1.peek(); }

    @Override
    public synchronized String getMensagemJogador2() throws RemoteException { return filaMensagensJ2.peek(); }

    @Override
    public synchronized void consumirMensagemJogador1() throws RemoteException { filaMensagensJ1.poll(); }

    @Override
    public synchronized void consumirMensagemJogador2() throws RemoteException { filaMensagensJ2.poll(); }

    /** @deprecated Mantido por compatibilidade; use getMensagemJogador1/2. */
    @Override
    @Deprecated
    public synchronized String getUltimaMensagem() throws RemoteException { return null; }

    /** @deprecated Mantido por compatibilidade. */
    @Override
    @Deprecated
    public synchronized void consumirMensagem() throws RemoteException { /* no-op */ }

    // ── Ping ──────────────────────────────────────────────────────────────────

    @Override
    public long getPing() throws RemoteException { return pingMs; }

    @Override
    public void pong() throws RemoteException {
        pingMs    = System.currentTimeMillis() - tempoPing;
        tempoPing = System.currentTimeMillis();
    }

    public void iniciarPing() { tempoPing = System.currentTimeMillis(); }

    // ── Estado completo (JSON para o Angular) ─────────────────────────────────

    @Override
    public synchronized String getEstadoPartida() throws RemoteException {
        if (partidaAtual == null) {
            return "{\"partida\":null}";
        }

        Jogador j1 = partidaAtual.getJogador1();
        Jogador j2 = partidaAtual.getJogador2();

        String vencedor = "null";
        if (jogador1Plantado && jogador2Plantado) {
            Jogador v = partidaAtual.getVencedor();
            vencedor  = v == null ? "\"empate\"" : "\"" + escapar(v.getNome()) + "\"";
        }

        return "{"
                + "\"baralho\":\"" + TiposDeBaralho.NOME + "\","
                + "\"limite\":"   + TiposDeBaralho.LIMITE + ","
                + "\"jogador1\":{"
                +   "\"nome\":\""   + escapar(j1.getNome()) + "\","
                +   "\"pontos\":"   + j1.somaCartas()       + ","
                +   "\"plantado\":" + jogador1Plantado       + ","
                +   "\"cartas\":"   + cartasParaJson(j1)
                + "},"
                + "\"jogador2\":{"
                +   "\"nome\":\""   + escapar(j2.getNome()) + "\","
                +   "\"pontos\":"   + j2.somaCartas()       + ","
                +   "\"plantado\":" + jogador2Plantado       + ","
                +   "\"cartas\":"   + cartasParaJson(j2)
                + "},"
                + "\"vencedor\":"  + vencedor
                + "}";
    }

    // ── Acesso interno ────────────────────────────────────────────────────────

    public Partida getPartidaAtual() { return partidaAtual; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void garantirPartidaAtiva() throws RemoteException {
        if (partidaAtual == null)
            throw new RemoteException("Nenhuma partida em andamento.");
    }

    private String cartasParaJson(Jogador j) {
        Carta[] cartas = j.getCartas();
        if (cartas == null || cartas.length == 0) return "[]";
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (Carta c : cartas)
            sj.add("{\"imagem\":\"" + escapar(c.getImagem()) + "\",\"valor\":" + c.getValor() + "}");
        return sj.toString();
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
