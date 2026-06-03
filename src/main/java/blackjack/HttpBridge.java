package blackjack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Bridge HTTP que expõe o servidor RMI Servidorcomo uma API REST/JSON
 *
 * <p>O angular (ou qualquer cliente HTTP) faz requisições para esta bridge ee
 * ela repassa as chamadas ao objeto RMI local e devolve JSON</p>
 *
 * <h3>endpoints:</h3>
 * <pre>
 *  GET  /estado            → getEstadoPartida()
 *  GET  /ping              → getPing()
 *  GET  /jogador2          → getNomeJogador2()
 *  GET  /carta/jogador1    → getUltimaCartaJogador1()
 *  GET  /carta/jogador2    → getUltimaCartaJogador2()
 *  GET  /mensagem/jogador1 → getMensagemJogador1()
 *  GET  /mensagem/jogador2 → getMensagemJogador2()
 *
 *  POST /entrar            → entrarNaSala(nome)
 *  POST /nova-partida      → novaPartida()
 *  POST /pedir/jogador1    → pedirCartaJogador1()
 *  POST /pedir/jogador2    → pedirCartaJogador2()
 *  POST /plantar/jogador1  → plantarJogador1()
 *  POST /plantar/jogador2  → plantarJogador2()
 *  POST /chat              → enviarMensagem(remetente, texto)
 *  POST /consumir/carta/j1 → consumirCartaJogador1()
 *  POST /consumir/carta/j2 → consumirCartaJogador2()
 *  POST /consumir/msg/j1   → consumirMensagemJogador1()
 *  POST /consumir/msg/j2   → consumirMensagemJogador2()
 *  POST /pong              → pong()
 * </pre>
 *
 */
public class HttpBridge {

    private final Servidor  servidor;
    private final int       porta;
    private       HttpServer httpServer;

    public HttpBridge(Servidor servidor, int porta) {
        this.servidor = servidor;
        this.porta    = porta;
    }

    public void iniciar() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(porta), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());

        // GETs
        httpServer.createContext("/estado",            ex -> handle(ex, this::handleEstado));
        httpServer.createContext("/ping",              ex -> handle(ex, this::handleGetPing));
        httpServer.createContext("/jogador2",          ex -> handle(ex, this::handleGetJogador2));
        httpServer.createContext("/carta/jogador1",    ex -> handle(ex, this::handleGetCartaJ1));
        httpServer.createContext("/carta/jogador2",    ex -> handle(ex, this::handleGetCartaJ2));
        httpServer.createContext("/mensagem/jogador1", ex -> handle(ex, this::handleGetMsgJ1));
        httpServer.createContext("/mensagem/jogador2", ex -> handle(ex, this::handleGetMsgJ2));

        // POSTs
        httpServer.createContext("/entrar",            ex -> handle(ex, this::handleEntrar));
        httpServer.createContext("/nova-partida",      ex -> handle(ex, this::handleNovaPartida));
        httpServer.createContext("/pedir/jogador1",    ex -> handle(ex, this::handlePedirJ1));
        httpServer.createContext("/pedir/jogador2",    ex -> handle(ex, this::handlePedirJ2));
        httpServer.createContext("/plantar/jogador1",  ex -> handle(ex, this::handlePlantarJ1));
        httpServer.createContext("/plantar/jogador2",  ex -> handle(ex, this::handlePlantarJ2));
        httpServer.createContext("/chat",              ex -> handle(ex, this::handleChat));
        httpServer.createContext("/consumir/carta/j1", ex -> handle(ex, this::handleConsumirCartaJ1));
        httpServer.createContext("/consumir/carta/j2", ex -> handle(ex, this::handleConsumirCartaJ2));
        httpServer.createContext("/consumir/msg/j1",   ex -> handle(ex, this::handleConsumirMsgJ1));
        httpServer.createContext("/consumir/msg/j2",   ex -> handle(ex, this::handleConsumirMsgJ2));
        httpServer.createContext("/pong",              ex -> handle(ex, this::handlePong));

        httpServer.start();
        System.out.println("[HTTP] Bridge iniciada na porta " + porta);
    }

    public void parar() {
        if (httpServer != null) httpServer.stop(0);
    }

    // ── Dispatcher ────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface Handler {
        String handle(HttpExchange ex) throws Exception;
    }

    private void handle(HttpExchange ex, Handler h) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            enviarResposta(ex, 204, "");
            return;
        }

        try {
            String resposta = h.handle(ex);
            enviarResposta(ex, 200, resposta);
        } catch (Exception e) {
            String erro = "{\"erro\":\"" + escapar(e.getMessage()) + "\"}";
            enviarResposta(ex, 500, erro);
        }
    }

    // ── Handlers GET ──────────────────────────────────────────────────────────

    private String handleEstado(HttpExchange ex) throws RemoteException {
        return servidor.getEstadoPartida();
    }

    private String handleGetPing(HttpExchange ex) throws RemoteException {
        return "{\"ping\":" + servidor.getPing() + "}";
    }

    private String handleGetJogador2(HttpExchange ex) throws RemoteException {
        String nome = servidor.getNomeJogador2();
        return "{\"nome\":" + (nome == null ? "null" : "\"" + escapar(nome) + "\"") + "}";
    }

    private String handleGetCartaJ1(HttpExchange ex) throws RemoteException {
        String carta = servidor.getUltimaCartaJogador1();
        return "{\"carta\":" + (carta == null ? "null" : "\"" + escapar(carta) + "\"") + "}";
    }

    private String handleGetCartaJ2(HttpExchange ex) throws RemoteException {
        String carta = servidor.getUltimaCartaJogador2();
        return "{\"carta\":" + (carta == null ? "null" : "\"" + escapar(carta) + "\"") + "}";
    }

    private String handleGetMsgJ1(HttpExchange ex) throws RemoteException {
        String msg = servidor.getMensagemJogador1();
        return "{\"mensagem\":" + (msg == null ? "null" : "\"" + escapar(msg) + "\"") + "}";
    }

    private String handleGetMsgJ2(HttpExchange ex) throws RemoteException {
        String msg = servidor.getMensagemJogador2();
        return "{\"mensagem\":" + (msg == null ? "null" : "\"" + escapar(msg) + "\"") + "}";
    }

    // ── Handlers POST ─────────────────────────────────────────────────────────

    private String handleEntrar(HttpExchange ex) throws Exception {
        Map<String, String> params = lerParams(ex);
        String nome = params.get("nome");
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Parâmetro 'nome' obrigatório.");
        String nomeJ1 = servidor.entrarNaSala(nome);
        return "{\"jogador1\":\"" + escapar(nomeJ1) + "\"}";
    }

    private String handleNovaPartida(HttpExchange ex) throws Exception {
        servidor.novaPartida();
        return "{\"ok\":true}";
    }

    private String handlePedirJ1(HttpExchange ex) throws RemoteException {
        String imagem = servidor.pedirCartaJogador1();
        return "{\"carta\":\"" + escapar(imagem) + "\"}";
    }

    private String handlePedirJ2(HttpExchange ex) throws RemoteException {
        String imagem = servidor.pedirCartaJogador2();
        return "{\"carta\":\"" + escapar(imagem) + "\"}";
    }

    private String handlePlantarJ1(HttpExchange ex) throws RemoteException {
        servidor.plantarJogador1();
        return "{\"ok\":true}";
    }

    private String handlePlantarJ2(HttpExchange ex) throws RemoteException {
        servidor.plantarJogador2();
        return "{\"ok\":true}";
    }

    private String handleChat(HttpExchange ex) throws Exception {
        Map<String, String> params = lerParams(ex);
        String remetente = params.getOrDefault("remetente", "Anônimo");
        String texto     = params.get("texto");
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("Parâmetro 'texto' obrigatório.");
        servidor.enviarMensagem(remetente, texto);
        return "{\"ok\":true}";
    }

    private String handleConsumirCartaJ1(HttpExchange ex) throws RemoteException {
        servidor.consumirCartaJogador1();
        return "{\"ok\":true}";
    }

    private String handleConsumirCartaJ2(HttpExchange ex) throws RemoteException {
        servidor.consumirCartaJogador2();
        return "{\"ok\":true}";
    }

    private String handleConsumirMsgJ1(HttpExchange ex) throws RemoteException {
        servidor.consumirMensagemJogador1();
        return "{\"ok\":true}";
    }

    private String handleConsumirMsgJ2(HttpExchange ex) throws RemoteException {
        servidor.consumirMensagemJogador2();
        return "{\"ok\":true}";
    }

    private String handlePong(HttpExchange ex) throws RemoteException {
        servidor.pong();
        return "{\"ok\":true}";
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private Map<String, String> lerParams(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String query = ex.getRequestURI().getQuery();
        if (query != null && !query.isBlank()) {
            body = body.isBlank() ? query : body + "&" + query;
        }
        return parseFormData(body);
    }

    private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (data == null || data.isBlank()) return map;
        for (String par : data.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String val = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(key, val);
            }
        }
        return map;
    }

    private void enviarResposta(HttpExchange ex, int status, String body) {
        try {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException ignored) { /* cliente desconectou */ }
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
