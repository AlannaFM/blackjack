package blackjack;

import java.util.regex.Pattern;
import javax.swing.*;

/**
 * Diálogo para o jogador 2 entrar em uma sala via RMI.
 */
public class EntrarNaSala extends JDialog {

    private final Janela pai;

    public EntrarNaSala(JFrame pai, boolean modal) {
        super(pai, modal);
        this.pai = (Janela) pai;
        inicializarComponentes();
        if (this.pai.getIp()     != null) txtIp.setText(this.pai.getIp());
        if (this.pai.getPorta()  != 0)    txtPorta.setText(String.valueOf(this.pai.getPorta()));
        if (this.pai.getNomeJogador1() != null) txtNome.setText(this.pai.getNomeJogador1());
        setLocationRelativeTo(pai);
    }

    private JTextField txtIp, txtPorta, txtNome;
    private JButton btnEntrar, btnCancelar;

    private void inicializarComponentes() {
        setTitle("Entrar na Sala");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtIp    = new JTextField(14);
        txtPorta = new JTextField(6);
        txtNome  = new JTextField(12);

        btnEntrar   = new JButton("Entrar");
        btnCancelar = new JButton("Cancelar");

        btnEntrar.addActionListener(e   -> executar());
        btnCancelar.addActionListener(e -> dispose());
        txtNome.addActionListener(e     -> executar());
        txtPorta.addActionListener(e    -> executar());
        txtIp.addActionListener(e       -> executar());

        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JPanel linhaIp = new JPanel();
        linhaIp.add(new JLabel("IP:"));    linhaIp.add(txtIp);
        linhaIp.add(new JLabel("Porta:")); linhaIp.add(txtPorta);

        JPanel linhaNome = new JPanel();
        linhaNome.add(new JLabel("Nome:")); linhaNome.add(txtNome);

        JPanel linhaBotoes = new JPanel();
        linhaBotoes.add(btnEntrar); linhaBotoes.add(btnCancelar);

        painel.add(linhaIp);
        painel.add(linhaNome);
        painel.add(linhaBotoes);
        add(painel);
        pack();
    }

    private void executar() {
        String ip    = txtIp.getText().trim();
        String porta = txtPorta.getText().trim();
        String nome  = txtNome.getText().trim();

        String regexIp  = "(([0-9]{1,2}|(1[0-9]{2})|(2[0-4][0-9])|255)\\.){3}"
                + "([0-9]{1,2}|(1[0-9]{2})|(2[0-4][0-9])|255)";
        String regexHost = "([a-z\\-]+\\.[a-z\\-]+)+(\\.[a-z\\-]+)*";

        if (!Pattern.matches(regexIp, ip) && !Pattern.matches(regexHost, ip)) {
            JOptionPane.showMessageDialog(this, "Informe um IP ou endereço válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtIp.requestFocus(); txtIp.selectAll(); return;
        }
        if (!Pattern.matches("[0-9]{1,5}", porta)) {
            JOptionPane.showMessageDialog(this, "Informe uma porta válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtPorta.requestFocus(); txtPorta.selectAll(); return;
        }
        if (!Pattern.matches("[A-Za-z0-9]+([ _][A-Za-z0-9]*)*", nome)) {
            JOptionPane.showMessageDialog(this, "Informe um nome válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtNome.requestFocus(); txtNome.selectAll(); return;
        }

        pai.setIp(ip);
        pai.setPorta(Integer.parseInt(porta));
        pai.setNomeJogador1(nome);
        pai.entrarNaSala();
        dispose();
    }
}