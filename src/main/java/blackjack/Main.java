package blackjack;

import blackjack.rmi.IJogoBlackJack;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Ponto de entrada do servidor de BlackJack.
 *
 * <p>Inicia:</p>
 * <ol>
 *   <li>O RMI Registry na porta {@code RMI_PORT} (padrão 1099).</li>
 *   <li>O objeto {@link Servidor} registrado como {@code "BlackJackServidor"}.</li>
 *   <li>A {@link HttpBridge} na porta {@code HTTP_PORT} (padrão 8080),
 *       que permite ao Angular consumir a API via HTTP/JSON.</li>
 * </ol>
 *
 * <p>Uso:</p>
 * <pre>
 *   java -jar blackjack-backend.jar [nomeJogador1] [rmiPort] [httpPort]
 * </pre>
 */
public class Main {

    /** Nome com o qual o servidor é registrado no RMI Registry. */
    public static final String NOME_RMI = "BlackJackServidor";

    /** Porta padrão do RMI Registry. */
    public static final int RMI_PORT_DEFAULT = 1099;

    /** Porta padrão da HTTP Bridge (acessada pelo Angular). */
    public static final int HTTP_PORT_DEFAULT = 8080;

    public static void main(String[] args) throws Exception {

        // ── Argumentos opcionais ─────────────────────────────────────────────
        String nomeJogador1 = args.length > 0 ? args[0] : lerNomeConsole();
        int rmiPort  = args.length > 1 ? Integer.parseInt(args[1]) : RMI_PORT_DEFAULT;
        int httpPort = args.length > 2 ? Integer.parseInt(args[2]) : HTTP_PORT_DEFAULT;

        // ── RMI ──────────────────────────────────────────────────────────────
        System.setProperty("java.rmi.server.hostname", "SEU_IP_LOCAL_OU_PUBLICO"); //p configurar o ip e nn cair em localhost
        Servidor servidor = new Servidor(nomeJogador1);

        Registry registry = LocateRegistry.createRegistry(rmiPort);
        registry.rebind(NOME_RMI, servidor);

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       BlackJack Backend  –  RMI + HTTP       ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.printf( "║  Jogador 1 : %-30s ║%n", nomeJogador1);
        System.out.printf( "║  RMI       : rmi://localhost:%d/%s ║%n", rmiPort, NOME_RMI);
        System.out.printf( "║  HTTP API  : http://localhost:%d             ║%n", httpPort);
        System.out.println("╚══════════════════════════════════════════════╝");

        // ── HTTP Bridge ───────────────────────────────────────────────────────
        HttpBridge bridge = new HttpBridge(servidor, httpPort);
        bridge.iniciar();

        // Mantém o processo vivo
        System.out.println("\nServidor rodando. Pressione ENTER para encerrar.");
        new Scanner(System.in).nextLine();

        bridge.parar();
        registry.unbind(NOME_RMI);
        System.out.println("Servidor encerrado.");
    }

    private static String lerNomeConsole() {
        System.out.print("Nome do Jogador 1 (host): ");
        return new Scanner(System.in).nextLine().trim();
    }
}
