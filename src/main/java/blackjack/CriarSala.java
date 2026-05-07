package blackjack;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.regex.Pattern;
import javax.swing.*;


// diálogo para o jogador 1 criar (hospedar) uma sala via RMI

public class CriarSala extends JDialog {

    private final Janela pai;

    public CriarSala(JFrame pai, boolean modal) {
        super(pai, modal);
        this.pai = (Janela) pai;
        inicializarComponentes();
        if (this.pai.getNomeJogador1() != null)
            txtNome.setText(this.pai.getNomeJogador1());
        if (this.pai.getPorta() != 0)
            txtPorta.setText(String.valueOf(this.pai.getPorta()));
        setLocationRelativeTo(pai);
    }

    private JLabel lblPorta, lblNome;
    private JTextField txtPorta, txtNome;
    private JCheckBox chkQualquerPorta;
    private JButton btnCriar, btnCancelar;

    private void inicializarComponentes() {
        setTitle("Criar Sala");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        lblPorta = new JLabel("Porta:");
        txtPorta = new JTextField(6);
        txtPorta.setEnabled(false);

        lblNome = new JLabel("Nome:");
        txtNome = new JTextField(12);

        chkQualquerPorta = new JCheckBox("Qualquer porta", true);
        chkQualquerPorta.addActionListener(e -> txtPorta.setEnabled(!chkQualquerPorta.isSelected()));

        btnCriar    = new JButton("Criar");
        btnCancelar = new JButton("Cancelar");

        btnCriar.addActionListener(e    -> executar());
        btnCancelar.addActionListener(e -> dispose());
        txtNome.addActionListener(e     -> executar());
        txtPorta.addActionListener(e    -> executar());

        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JPanel linhaPorta = new JPanel();
        linhaPorta.add(chkQualquerPorta);
        linhaPorta.add(lblPorta);
        linhaPorta.add(txtPorta);
        linhaPorta.add(lblNome);
        linhaPorta.add(txtNome);

        JPanel linhaBotoes = new JPanel();
        linhaBotoes.add(btnCriar);
        linhaBotoes.add(btnCancelar);

        painel.add(linhaPorta);
        painel.add(linhaBotoes);
        add(painel);
        pack();
    }

    private void executar() {
        String nomePorta = txtPorta.getText().trim();
        String nome      = txtNome.getText().trim();

        if (!chkQualquerPorta.isSelected() && !Pattern.matches("[0-9]{1,5}", nomePorta)) {
            JOptionPane.showMessageDialog(this, "Informe um número de porta válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtPorta.requestFocus(); txtPorta.selectAll();
            return;
        }
        if (!Pattern.matches("[A-Za-z0-9]+([ _][A-Za-z0-9]*)*", nome)) {
            JOptionPane.showMessageDialog(this, "Informe um nome válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtNome.requestFocus(); txtNome.selectAll();
            return;
        }

        int porta = chkQualquerPorta.isSelected() ? 0 : Integer.parseInt(nomePorta);
        pai.setNomeJogador1(nome);
        pai.iniciarServidor(porta);
        dispose();
    }
}