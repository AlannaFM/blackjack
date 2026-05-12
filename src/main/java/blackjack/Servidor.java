package blackjack;

import blackjack.rmi.IJogoBlackJack;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * implementação RMI do servidor
 * mantém todo o estado da partida. O cliente consulta via polling
 */
public class Servidor extends UnicastRemoteObject implements IJogoBlackJack {

    private String nomeJogador1;
    private String nomeJogador2;

    private GerenciadorDeBaralhos gerenciador;
    private Partida partidaAtual;
    private String nomeBaralho;

    private boolean jogador1Plantado;
    private boolean jogador2Plantado;

    // ultimos eventos pendentes de leitura pelo cliente
    private volatile String ultimaCartaJogador1;
    private volatile String ultimaCartaJogador2;
    private final java.util.Queue<String> filaMensagensJ1 = new java.util.LinkedList<>();
    private final java.util.Queue<String> filaMensagensJ2 = new java.util.LinkedList<>();

    // ping
    private volatile long pingMs = 0;
    private volatile long tempoPing;

    public Servidor(String nomeJogador1) throws RemoteException {
        super();
        this.nomeJogador1 = nomeJogador1;
    }

    @Override
    public synchronized String entrarNaSala(String nomeJogador2) throws RemoteException {
        this.nomeJogador2 = nomeJogador2;
        return nomeJogador1;
    }

    @Override
    public synchronized void novaPartida(String nomeBaralho) throws RemoteException {
        this.nomeBaralho = nomeBaralho;
        jogador1Plantado = false;
        jogador2Plantado = false;
        ultimaCartaJogador1 = null;
        ultimaCartaJogador2 = null;
        filaMensagensJ1.clear();
        filaMensagensJ2.clear();

        TiposDeBaralho BaralhoTipo = TiposDeBaralho.buscarBaralho(nomeBaralho);
        gerenciador = new GerenciadorDeBaralhos(BaralhoTipo);

        if ("BlackJack".equals(nomeBaralho)) {
            partidaAtual = new Partida(
                    new JogadorBlackJack(nomeJogador1),
                    new JogadorBlackJack(nomeJogador2),
                    BaralhoTipo.getLimite()
            );
        } else {
            partidaAtual = new Partida(
                    new Jogador(nomeJogador1),
                    new Jogador(nomeJogador2),
                    BaralhoTipo.getLimite()
            );
        }

        // carta inicial para cada jogador
        Carta c1 = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador1().darCarta(c1);
        ultimaCartaJogador1 = c1.getImagem();

        Carta c2 = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador2().darCarta(c2);
        ultimaCartaJogador2 = c2.getImagem();
    }

    @Override
    public synchronized String pedirCartaJogador1() throws RemoteException {
        Carta c = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador1().darCarta(c);
        ultimaCartaJogador1 = c.getImagem();
        return c.getImagem();
    }

    @Override
    public synchronized String pedirCartaJogador2() throws RemoteException {
        Carta c = gerenciador.cartaAleatoria(partidaAtual.getJogador1(), partidaAtual.getJogador2());
        partidaAtual.getJogador2().darCarta(c);
        ultimaCartaJogador2 = c.getImagem();
        return c.getImagem();
    }

    @Override
    public synchronized void plantarJogador1() throws RemoteException {
        jogador1Plantado = true;
    }

    @Override
    public synchronized void plantarJogador2() throws RemoteException {
        jogador2Plantado = true;
    }

    @Override
    public synchronized void enviarMensagem(String remetente, String texto) throws RemoteException {
        String msg = remetente + ": " + texto;
        filaMensagensJ1.add(msg);
        filaMensagensJ2.add(msg);
    }

    @Override
    public synchronized String getUltimaCartaJogador1() throws RemoteException {
        return ultimaCartaJogador1;
    }

    @Override
    public synchronized String getUltimaCartaJogador2() throws RemoteException {
        return ultimaCartaJogador2;
    }

    @Override
    public synchronized boolean isJogador1Plantado() throws RemoteException {
        return jogador1Plantado;
    }  

    @Override
    public synchronized boolean isJogador2Plantado() throws RemoteException {
        return jogador2Plantado;
    }

    @Override
    public synchronized String getUltimaMensagem() throws RemoteException {
        return null; // não usar mais esse 
    }

    @Override
    public synchronized String getNomeBaralho() throws RemoteException {
        return nomeBaralho;
    }

    @Override
    public synchronized void consumirCartaJogador1() throws RemoteException {
        ultimaCartaJogador1 = null;
    }

    @Override
    public synchronized void consumirCartaJogador2() throws RemoteException {
        ultimaCartaJogador2 = null;
    }

    @Override
    public synchronized void consumirMensagem() throws RemoteException {
      //  filaMensagens.poll(); // remove apenas quando o cliente confirma leitura
    }
    

    @Override
    public long getPing() throws RemoteException {
        return pingMs;
    }

    @Override
    public void pong() throws RemoteException {
        pingMs = System.currentTimeMillis() - tempoPing;
        tempoPing = System.currentTimeMillis(); // prepara próximo ping
    }

    // cchamado internamente pelo servidor para iniciar a medição de ping
    public void iniciarPing() {
        tempoPing = System.currentTimeMillis();
    }

    @Override
    public String getNomeJogador2() throws RemoteException {
        return nomeJogador2;
    }

    public Partida getPartidaAtual() {
        return partidaAtual;
    }
    
    public synchronized String getMensagemJogador1() throws RemoteException {
        return filaMensagensJ1.peek();
    }

    public synchronized String getMensagemJogador2() throws RemoteException {
        return filaMensagensJ2.peek();
    }

    public synchronized void consumirMensagemJogador1() throws RemoteException {
        filaMensagensJ1.poll();
    }

    public synchronized void consumirMensagemJogador2() throws RemoteException {
        filaMensagensJ2.poll();
    }
}