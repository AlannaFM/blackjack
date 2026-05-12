package blackjack;

import blackjack.rmi.IJogoBlackJack;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;

/**
 * janela principal
 * usa um javax.swing.Timer para polling em vez de threads separadas
 */
public class Janela extends JFrame {

    // ── Campos de rede/RMI ────────────────────────────────────────────────────
    private Servidor servidorLocal;          // não-null quando este processo é o host
    private IJogoBlackJack jogo;             // stub RMI compartilhado
    private Registry registro;
    private boolean ehServidor;
    private String ip;
    private int porta;

    // ── Estado do jogo ───────────────────────────────────────────────────────
    private String nomeJogador1;
    private String nomeJogador2;
    private GerenciadorDeBaralhos gerenciador;
    private Partida partidaAtual;
    private boolean[] cartasViradasJ1;
    private boolean[] cartasViradasJ2;
    private boolean jogador1Plantado;
    private boolean jogador2Plantado;
    private int contadorPartidas;

    // ── Timer de polling (substituindo threads) ─────────────────────────────────
    private Timer timerPolling;
    private static final int INTERVALO_POLLING_MS = 200;

    // ── Componentes Swing ─────────────────────────────────────────────────────
    private JComboBox<String> comboBaralho;
    private JButton btnNovaPartida, btnPedirCarta, btnPlantar;
    private JButton btnCriarSala, btnEntrar, btnEnviar;
    private JLabel lblStatus, lblPing, lblPartidas;
    private JPanel painelJ1, painelJ2;
    private JPanel contJ1, contJ2;
    private JScrollPane scrollJ1, scrollJ2;
    private JTextArea areaMensagens;
    private JTextField txtMensagem;
    private JTextPane txtStatusConexao;
    private JScrollPane scrollStatusConexao;

    public static Color laranjaEscuro = new Color(219, 124, 0);
    public static Color verdeEscuro   = new Color(6, 166, 54);

    // ── Getters/Setters usados pelos diálogos ─────────────────────────────────
    public String getNomeJogador1() { return nomeJogador1; }
    public void   setNomeJogador1(String n) {
        nomeJogador1 = n;
        atualizarTituloPainel(contJ1, n);
    }
    public String getIp()   { return ip; }
    public void   setIp(String ip) { this.ip = ip; }
    public int    getPorta(){ return porta; }
    public void   setPorta(int p) { this.porta = p; }

    // ─────────────────────────────────────────────────────────────────────────
    public Janela() {
        inicializarComponentes();
        configurarJanela();
    }

    private void configurarJanela() {
        setTitle("BlackJack Multiplayer – RMI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        lblPing.setText("");
        lblPartidas.setText("");
        txtMensagem.setEnabled(false);
        btnEnviar.setEnabled(false);
        setStatusConexao(Color.RED, "Sem partida");
        btnPedirCarta.setEnabled(false);
        btnPlantar.setEnabled(false);
        btnNovaPartida.setEnabled(false);
        comboBaralho.setModel(new DefaultComboBoxModel<>(TiposDeBaralho.nomesBaralho()));
        setVisible(true);
        btnNovaPartida.requestFocus();
    }

    // ── Iniciar servidor (hospedar) ────────────────────────────────────────────
    public void iniciarServidor(int portaDesejada) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");//ip
            servidorLocal = new Servidor(nomeJogador1);
            registro = LocateRegistry.createRegistry(portaDesejada == 0 ? Registry.REGISTRY_PORT : portaDesejada);
            porta = portaDesejada == 0 ? Registry.REGISTRY_PORT : portaDesejada;
            registro.rebind("JogoBlackJack", servidorLocal);
            jogo = servidorLocal;
            ehServidor = true;
            setStatusConexao(laranjaEscuro, "Aguardando jogador na porta " + porta + "...");
            btnCriarSala.setText("Aguardando...");
            btnEntrar.setEnabled(false);

            // Poll para detectar quando o cliente conectou
            Timer espera = new Timer(500, null);
            espera.addActionListener(e -> {
                try {
                    String nomeJ2 = jogo.getNomeJogador2();
                    if (nomeJ2 != null) {
                        espera.stop();
                        nomeJogador2 = nomeJ2;
                        atualizarTituloPainel(contJ2, nomeJogador2);
                        conectadoComo(true);
                    }
                } catch (Exception ex) { espera.stop(); resetarJanela(); }
            });
            espera.start();
        } catch (Exception e) {
            resetarJanela();
            JOptionPane.showMessageDialog(this,
                    "Não foi possível criar a sala: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Entrar na sala como cliente ───────────────────────────────────────────
    public void entrarNaSala() {
        btnEntrar.setText("Conectando...");
        btnCriarSala.setEnabled(false);
        try {
            registro = LocateRegistry.getRegistry(ip, porta);
            jogo = (IJogoBlackJack) registro.lookup("JogoBlackJack");
            nomeJogador2 = jogo.entrarNaSala(nomeJogador1); // retorna nome do servidor
            // Troca de perspectiva: do ponto de vista do cliente, J1=servidor, J2=ele mesmo
            String temp = nomeJogador1;
            nomeJogador1 = nomeJogador2; // nome do host → J1 na tela
            nomeJogador2 = temp;          // nome próprio  → J2 na tela
            atualizarTituloPainel(contJ1, nomeJogador1);
            atualizarTituloPainel(contJ2, nomeJogador2);
            conectadoComo(false);
        } catch (Exception e) {
            resetarJanela();
            JOptionPane.showMessageDialog(this,
                    "Não foi possível conectar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Pós-conexão ───────────────────────────────────────────────────────────
    private void conectadoComo(boolean servidor) {
        ehServidor = servidor;
        String enderecoInfo = servidor ? "porta " + porta : ip + ":" + porta;
        setStatusConexao(verdeEscuro, (servidor ? "[Servidor]" : "[Cliente]") + " Conectado – " + enderecoInfo);
        btnCriarSala.setText("Desconectar");
        btnEntrar.setText("Desconectar");
        btnCriarSala.setEnabled(true);
        btnEntrar.setEnabled(true);
        areaMensagens.setText("");
        txtMensagem.setEnabled(true);
        btnEnviar.setEnabled(true);
        if (servidor) {
            btnNovaPartida.setEnabled(true);
            comboBaralho.setEnabled(true);
        } else {
            btnNovaPartida.setEnabled(false);
            comboBaralho.setEnabled(false);
        }
        iniciarPolling();
    }

    // ── Timer de polling (sem threads!) ──────────────────────────────────────
    private void iniciarPolling() {
        if (timerPolling != null) timerPolling.stop();
        timerPolling = new Timer(INTERVALO_POLLING_MS, e -> fazerPolling());
        timerPolling.start();
    }

    private void fazerPolling() {
        if (jogo == null) return;
        try {
            // Ping
            long inicio = System.currentTimeMillis();
            jogo.pong();
            int ping = (int)(System.currentTimeMillis() - inicio);
            atualizarPing(ping);

            // Nova partida iniciada pelo servidor
            String nomeBaralho = jogo.getNomeBaralho();
            if (nomeBaralho != null && partidaAtual == null) {
                iniciarPartidaLocal(nomeBaralho);
            }

            if (partidaAtual == null) return;

            String cartaJ1 = jogo.getUltimaCartaJogador1();
            if (cartaJ1 != null && partidaAtual != null) {
                jogo.consumirCartaJogador1();
                if (ehServidor) adicionarCartaAoPainel(painelJ1, cartaJ1, partidaAtual.getJogador1(), true);
                else adicionarCartaAoPainel(painelJ1, cartaJ1, partidaAtual.getJogador1(), false);
            }

            // Carta para J2
            String cartaJ2 = jogo.getUltimaCartaJogador2();
            if (cartaJ2 != null && partidaAtual != null) {
                jogo.consumirCartaJogador2();
                if (ehServidor) adicionarCartaAoPainel(painelJ2, cartaJ2, partidaAtual.getJogador2(), false);
                else adicionarCartaAoPainel(painelJ2, cartaJ2, partidaAtual.getJogador2(), true);  
            }

            // Plantadas
            boolean j1P = jogo.isJogador1Plantado();
            boolean j2P = jogo.isJogador2Plantado();
            if (j1P && !jogador1Plantado) { jogador1Plantado = true; verificarFimDePartida(); }
            if (j2P && !jogador2Plantado) { jogador2Plantado = true; verificarFimDePartida(); }

            // Chat
            String msg;
            if (ehServidor) {
                msg = jogo.getMensagemJogador1();
                if (msg != null) {
                    jogo.consumirMensagemJogador1();
                    String meuPrefixo = nomeJogador1;
                    if (!msg.startsWith(meuPrefixo)) {
                        areaMensagens.append(msg + "\n");
                        areaMensagens.setCaretPosition(areaMensagens.getDocument().getLength());
                    }
                }
            } else {
                msg = jogo.getMensagemJogador2();
                if (msg != null) {
                    jogo.consumirMensagemJogador2();
                    String meuPrefixo = nomeJogador2;
                    if (!msg.startsWith(meuPrefixo)) {
                        areaMensagens.append(msg + "\n");
                        areaMensagens.setCaretPosition(areaMensagens.getDocument().getLength());
                    }
                }
            }  

        } catch (java.rmi.RemoteException ex) {
            timerPolling.stop();
            resetarJanela();
            setStatusConexao(Color.RED, "Conexão perdida");
            JOptionPane.showMessageDialog(this, "Conexão perdida com o servidor.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Lógica de jogo local ─────────────────────────────────────────────────
    private void iniciarPartidaLocal(String nomeBaralho) {
        contadorPartidas++;
        lblPartidas.setText(contadorPartidas + "ª Partida");
        jogador1Plantado = false;
        jogador2Plantado = false;
        TiposDeBaralho tipo = TiposDeBaralho.buscarBaralho(nomeBaralho);
        gerenciador = new GerenciadorDeBaralhos(tipo);
        if ("BlackJack".equals(nomeBaralho)) {
            partidaAtual = new Partida(new JogadorBlackJack(nomeJogador1),
                    new JogadorBlackJack(nomeJogador2), tipo.getLimite());
        } else {
            partidaAtual = new Partida(new Jogador(nomeJogador1),
                    new Jogador(nomeJogador2), tipo.getLimite());
        }
        cartasViradasJ1 = new boolean[tipo.totalDeCartas()];
        cartasViradasJ2 = new boolean[tipo.totalDeCartas()];
        painelJ1.removeAll(); painelJ2.removeAll();
        painelJ1.revalidate(); painelJ1.repaint();
        painelJ2.revalidate(); painelJ2.repaint();
        if (!ehServidor) {
            btnPedirCarta.setEnabled(false);
            btnPlantar.setEnabled(false);
        }
    }

    private void adicionarCartaAoPainel(JPanel painel, String urlImagem, Jogador jogador, boolean visivel) {
        Carta carta = gerenciador.buscarCartaPorUrl(urlImagem);
        if (carta == null) return;
        jogador.darCarta(carta);

        JLabel lblCarta = new JLabel();
        String imgUrl = visivel ? urlImagem : TiposDeBaralho.getReverso();
        lblCarta.setIcon(new ImageIcon(getClass().getResource(imgUrl)));
        lblCarta.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) { virarCarta(lblCarta, painel, urlImagem); }
            @Override public void mouseEntered(MouseEvent e)  { lblCarta.setLocation(lblCarta.getX()-2, lblCarta.getY()-2); }
            @Override public void mouseExited(MouseEvent e)   { lblCarta.setLocation(lblCarta.getX()+2, lblCarta.getY()+2); }
        });
        painel.add(lblCarta);
        int n = painel.getComponentCount();
        painel.setLayout(new GridLayout((n/4)+((n%4!=0)?1:0), Math.min(n,4), 5, 12));
        painel.revalidate(); painel.repaint();

        // Habilitar ações se for o painel do próprio jogador e ele ainda não plantou
        boolean ehPainelPropio = (ehServidor && painel == painelJ1) || (!ehServidor && painel == painelJ2);
        boolean jaPlantoui = ehServidor ? jogador1Plantado : jogador2Plantado;
        String meuNome = ehServidor ? nomeJogador1 : nomeJogador2;

        if (ehPainelPropio && !jaPlantoui) {
            btnPedirCarta.setEnabled(true);
            btnPlantar.setEnabled(true);
            lblStatus.setText("Sua vez, " + meuNome);
        }
    }

    private void virarCarta(JLabel lbl, JPanel painel, String urlImagem) {
        String atual = ((ImageIcon) lbl.getIcon()).getDescription();
        boolean mostrando = urlImagem.equals(atual);
        lbl.setIcon(new ImageIcon(getClass().getResource(mostrando ? TiposDeBaralho.getReverso() : urlImagem)));
    }

    private void verificarFimDePartida() {
        if (!jogador1Plantado || !jogador2Plantado) return;
        btnPedirCarta.setEnabled(false);
        btnPlantar.setEnabled(false);
        Jogador vencedor = partidaAtual.getVencedor();
        if (vencedor == null) {
            float pts = partidaAtual.getJogador1().somaCartas();
            lblStatus.setText("Empate com " + formatarPontos(pts) + " pontos!");
        } else {
            lblStatus.setText("Vencedor: " + vencedor.getNome() + " com " + formatarPontos(vencedor.somaCartas()) + " pontos!");
        }
        if (ehServidor) btnNovaPartida.setEnabled(true);
    }

    private String formatarPontos(float pts) {
        return (pts == (int) pts) ? String.valueOf((int) pts) : String.valueOf(pts);
    }

    private void atualizarPing(int ms) {
        lblPing.setForeground(ms < 100 ? verdeEscuro : ms < 200 ? laranjaEscuro : Color.RED);
        lblPing.setText("Ping: " + ms + " ms");
    }

    // ── Ações dos botões ─────────────────────────────────────────────────────
    private void novaPartida() {
        if (!ehServidor) return;
        try {
            String nomeBaralho = (String) comboBaralho.getSelectedItem();
            jogo.novaPartida(nomeBaralho);
            iniciarPartidaLocal(nomeBaralho);

            // Cartas iniciais já foram criadas no servidor; busca e consome imediatamente
            String c1 = jogo.getUltimaCartaJogador1();
            if (c1 != null) { jogo.consumirCartaJogador1(); adicionarCartaAoPainel(painelJ1, c1, partidaAtual.getJogador1(), true); }
            String c2 = jogo.getUltimaCartaJogador2();
            if (c2 != null) { jogo.consumirCartaJogador2(); adicionarCartaAoPainel(painelJ2, c2, partidaAtual.getJogador2(), false); }

            btnNovaPartida.setEnabled(false);
            lblStatus.setText("Partida iniciada! Sua vez, " + nomeJogador1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar partida: " + e.getMessage());
        }
    }

    private void pedirCarta() {
        try {
            String url;
            if (ehServidor) {
                url = jogo.pedirCartaJogador1();
                jogo.consumirCartaJogador1(); // evita reprocessamento no polling
                adicionarCartaAoPainel(painelJ1, url, partidaAtual.getJogador1(), true);
                btnPedirCarta.setEnabled(false);
                btnPlantar.setEnabled(false);
                lblStatus.setText("Aguardando " + nomeJogador2 + "...");
            } else {
                url = jogo.pedirCartaJogador2();
                jogo.consumirCartaJogador2(); // 
                adicionarCartaAoPainel(painelJ2, url, partidaAtual.getJogador2(), true);
                btnPedirCarta.setEnabled(false);
                btnPlantar.setEnabled(false);
                lblStatus.setText("Aguardando " + nomeJogador1 + "...");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao pedir carta: " + e.getMessage());
        }
    }

    private void plantar() {
        try {
            if (ehServidor) { jogo.plantarJogador1(); jogador1Plantado = true; }
            else             { jogo.plantarJogador2(); jogador2Plantado = true; }
            btnPedirCarta.setEnabled(false);
            btnPlantar.setEnabled(false);
            lblStatus.setText("Você se plantou. Aguardando adversário...");
            verificarFimDePartida();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void enviarMensagem() {
        String texto = txtMensagem.getText().trim();
        if (texto.isEmpty()) return;
        try {
            String remetente = ehServidor ? nomeJogador1 : nomeJogador2;
            String hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":"
                    + String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE));
            jogo.enviarMensagem(remetente + " (" + hora + ")", texto);
            areaMensagens.append(remetente + " (" + hora + "): " + texto + "\n");
            areaMensagens.setCaretPosition(areaMensagens.getDocument().getLength());
            txtMensagem.setText("");
        } catch (Exception e) { /* ignora */ }
    }

    // ── Desconexão / reset ────────────────────────────────────────────────────
    private void desconectar() {
        if (timerPolling != null) timerPolling.stop();
        try { if (registro != null) UnicastRemoteObject.unexportObject(servidorLocal, true); } catch (Exception ignore) {}
        jogo = null; servidorLocal = null; registro = null;
        partidaAtual = null; gerenciador = null;
        resetarJanela();
        setStatusConexao(Color.RED, "Desconectado");
    }

    public void resetarJanela() {
        SwingUtilities.invokeLater(() -> {
            lblPing.setText("");
            lblPartidas.setText("");
            btnCriarSala.setEnabled(true); btnCriarSala.setText("Criar Sala");
            btnEntrar.setEnabled(true);    btnEntrar.setText("Entrar");
            btnNovaPartida.setEnabled(false);
            btnPedirCarta.setEnabled(false);
            btnPlantar.setEnabled(false);
            txtMensagem.setEnabled(false);
            btnEnviar.setEnabled(false);
            painelJ1.removeAll(); painelJ1.revalidate(); painelJ1.repaint();
            painelJ2.removeAll(); painelJ2.revalidate(); painelJ2.repaint();
            comboBaralho.setEnabled(true);
            partidaAtual = null;
        });
    }

    // ── Auxiliares visuais ─────────────────────────────────────────────────────
    private void setStatusConexao(Color cor, String texto) {
        MutableAttributeSet attr = new SimpleAttributeSet(txtStatusConexao.getParagraphAttributes());
        StyleConstants.setForeground(attr, cor);
        txtStatusConexao.setParagraphAttributes(attr, true);
        txtStatusConexao.setText(texto);
    }

    private void atualizarTituloPainel(JPanel painel, String nome) {
        SwingUtilities.invokeLater(() -> {
            ((TitledBorder) painel.getBorder()).setTitle(nome);
            painel.repaint();
        });
    }

    // ── Construção da interface ────────────────────────────────────────────────
    private void inicializarComponentes() {
        setLayout(new BorderLayout(6, 6));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Topo – Baralho + status + controles de rede
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        comboBaralho = new JComboBox<>();
        topo.add(new JLabel("Baralho:"));
        topo.add(comboBaralho);

        btnCriarSala = new JButton("Criar Sala");
        btnEntrar    = new JButton("Entrar");
        btnCriarSala.addActionListener(e -> {
            if ("Criar Sala".equals(btnCriarSala.getText())) new CriarSala(this, true).setVisible(true);
            else desconectar();
        });
        btnEntrar.addActionListener(e -> {
            if ("Entrar".equals(btnEntrar.getText())) new EntrarNaSala(this, true).setVisible(true);
            else desconectar();
        });
        topo.add(btnCriarSala);
        topo.add(btnEntrar);

        lblPing = new JLabel();
        topo.add(lblPing);

        scrollStatusConexao = new JScrollPane();
        txtStatusConexao    = new JTextPane();
        txtStatusConexao.setEditable(false);
        txtStatusConexao.setFocusable(false);
        txtStatusConexao.setPreferredSize(new Dimension(260, 36));
        scrollStatusConexao.setViewportView(txtStatusConexao);
        scrollStatusConexao.setBorder(null);

        SimpleAttributeSet aln = new SimpleAttributeSet();
        StyleConstants.setAlignment(aln, StyleConstants.ALIGN_RIGHT);
        txtStatusConexao.setParagraphAttributes(aln, true);
        topo.add(scrollStatusConexao);

        add(topo, BorderLayout.NORTH);

        // Centro – mesas dos jogadores
        Color verde = new Color(20, 154, 72);
        contJ2 = criarPainelJogador("Jogador 2", verde);
        contJ1 = criarPainelJogador("Jogador 1", verde);
        painelJ1 = (PainelRolavel) ((JScrollPane)((BorderLayout)contJ1.getLayout() != null
                ? contJ1.getComponent(0) : null) != null ? null : contJ1.getComponent(0));
        // Rebuscamos via helper:
        painelJ1 = extrairPainelRolavel(contJ1);
        painelJ2 = extrairPainelRolavel(contJ2);

        JPanel mesas = new JPanel(new GridLayout(2, 1, 4, 4));
        mesas.add(contJ2);
        mesas.add(contJ1);

        // Direita – chat
        areaMensagens = new JTextArea(8, 22);
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        areaMensagens.setWrapStyleWord(true);
        areaMensagens.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane scrollChat = new JScrollPane(areaMensagens);

        txtMensagem = new JTextField(16);
        btnEnviar   = new JButton("Enviar");
        txtMensagem.addActionListener(e -> enviarMensagem());
        btnEnviar.addActionListener(e   -> enviarMensagem());
        JPanel linhaMensagem = new JPanel(new BorderLayout(4, 0));
        linhaMensagem.add(txtMensagem, BorderLayout.CENTER);
        linhaMensagem.add(btnEnviar,   BorderLayout.EAST);

        JPanel painelChat = new JPanel(new BorderLayout(4, 4));
        painelChat.add(scrollChat,    BorderLayout.CENTER);
        painelChat.add(linhaMensagem, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(6, 0));
        centro.add(mesas,      BorderLayout.CENTER);
        centro.add(painelChat, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        // Rodapé – ações de jogo
        lblStatus   = new JLabel("Em espera");
        lblPartidas = new JLabel();
        btnNovaPartida = new JButton("Nova Partida");
        btnPedirCarta  = new JButton("Pedir Carta");
        btnPlantar     = new JButton("Plantar");
        btnNovaPartida.addActionListener(e -> novaPartida());
        btnPedirCarta.addActionListener(e  -> pedirCarta());
        btnPlantar.addActionListener(e     -> plantar());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rodape.add(btnNovaPartida);
        rodape.add(btnPedirCarta);
        rodape.add(btnPlantar);
        rodape.add(lblPartidas);
        rodape.add(lblStatus);
        add(rodape, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(820, 400));
    }

    /** Cria um painel contenedor de jogador com ScrollPane e PainelRolavel. */
    private JPanel criarPainelJogador(String titulo, Color fundo) {
        PainelRolavel pr = new PainelRolavel();
        pr.setBackground(fundo);
        pr.setPreferredSize(new Dimension(354, 105));

        JScrollPane scroll = new JScrollPane(pr);
        scroll.setBackground(fundo);
        scroll.setBorder(null);

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBorder(BorderFactory.createTitledBorder(titulo));
        cont.setBackground(fundo);
        cont.setPreferredSize(new Dimension(370, 128));
        cont.add(scroll, BorderLayout.CENTER);
        return cont;
    }

    private PainelRolavel extrairPainelRolavel(JPanel cont) {
        JScrollPane scroll = (JScrollPane) cont.getComponent(0);
        return (PainelRolavel) scroll.getViewport().getView();
    }

    // ── main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        SwingUtilities.invokeLater(Janela::new);
    }
}