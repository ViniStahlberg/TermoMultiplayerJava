package jogotermo;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class JogoTermo extends JFrame {

    private int TAM = 5;
    private int MAX = 15;

    private String palavraSecreta;
    private String palavraOponente;
    private boolean suaVez;
    private boolean fim = false;

    private int feitas = 0;
    private int recebidas = 0;

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;

    private JTextField campoEntrada;
    private JPanel painelTentativas;
    private JLabel lblStatus;
    private JPanel painelInferior;

    public JogoTermo() throws Exception {
        JanelaJogo();

        conectar();

        definirPalavra();

        receberPalavraOponente();

        atualizarStatus();

        jogar();
    }

    private void conectar() throws Exception {
        try {

            servidorConexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());

            servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
            servidorSaida.flush();

            servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
            throw e;
        }
    }

    private void JanelaJogo() {
        setTitle("Jogo Termo");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        lblStatus = new JLabel("Aguardando...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 28));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblStatus, BorderLayout.NORTH);

        painelTentativas = new JPanel();
        painelTentativas.setLayout(new BoxLayout(painelTentativas, BoxLayout.Y_AXIS));
        painelTentativas.setBackground(new Color(30, 30, 30));
        add(new JScrollPane(painelTentativas), BorderLayout.CENTER);

        campoEntrada = new JTextField();
        campoEntrada.setFont(new Font("Arial", Font.BOLD, 32));
        campoEntrada.setHorizontalAlignment(JTextField.CENTER);
        campoEntrada.setPreferredSize(new Dimension(200, 60));

        campoEntrada.addActionListener(e -> enviarTentativa());

        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 24));
        btnEnviar.addActionListener(e -> enviarTentativa());

        painelInferior = new JPanel(new BorderLayout());
        painelInferior.setBorder(BorderFactory.createEmptyBorder(20, 300, 20, 300));
        painelInferior.add(campoEntrada, BorderLayout.CENTER);
        painelInferior.add(btnEnviar, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void enviarTentativa() {
        if (!fim && suaVez) {
            String tentativa = campoEntrada.getText().trim().toUpperCase();
            campoEntrada.setText("");

            if (tentativa.length() == TAM) {
                try {
                    feitas++;
                    servidorSaida.writeObject(tentativa);
                    mostrarTentativa(tentativa, palavraOponente);

                    if (tentativa.equalsIgnoreCase(palavraOponente)) {
                        // servidorConexao.close();
                        finalizarJogo("Parabéns!\nVocê acertou!");

                    } else if (feitas >= MAX && recebidas >= MAX) {
                        // servidorConexao.close();
                        finalizarJogo("Empate!\nA palavra era: " + palavraOponente);

                    } else {
                        suaVez = false;
                        atualizarStatus();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void definirPalavra() throws Exception {
        String p = "";
        while (p.length() != TAM) {
            p = JOptionPane.showInputDialog(this, "Digite sua palavra secreta de 5 letras:");
            if (p == null) {
                System.exit(0);
            }
            p = p.trim().toUpperCase();
        }
        palavraSecreta = p;
        servidorSaida.writeObject(palavraSecreta);
    }

    private void receberPalavraOponente() throws Exception {
        String msg = (String) servidorEntrada.readObject();

        String[] info = msg.split(";");

        suaVez = Boolean.parseBoolean(info[0]);
        palavraOponente = info[1];
    }

    private void jogar() {
        new Thread(() -> {
            try {
                while (!fim) {
                    if (!suaVez) {
                        String tentativa = (String) servidorEntrada.readObject();
                        recebidas++;

                        if (tentativa.equalsIgnoreCase(palavraSecreta)) {
                            // servidorConexao.close();
                            finalizarJogo("O oponente acertou sua palavra.\nVocê perdeu.");
                        }

                        if (feitas >= MAX && recebidas >= MAX) {
                            // servidorConexao.close();
                            finalizarJogo("Empate!\nA palavra era: " + palavraOponente);
                        }

                        suaVez = true;
                        atualizarStatus();
                    }

                    Thread.sleep(100);
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private void mostrarTentativa(String tentativa, String alvo) {
        JPanel linha = new JPanel(new GridLayout(1, TAM, 4, 4));
        linha.setBackground(new Color(30, 30, 30));
        linha.setMaximumSize(new Dimension(600, 80));

        for (int i = 0; i < TAM; i++) {
            JLabel lbl = new JLabel(String.valueOf(tentativa.charAt(i)), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 40));
            lbl.setOpaque(true);
            lbl.setForeground(Color.WHITE);
            lbl.setPreferredSize(new Dimension(60, 60));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            if (tentativa.charAt(i) == alvo.charAt(i)) {
                lbl.setBackground(new Color(106, 170, 100));
            } else if (alvo.contains(String.valueOf(tentativa.charAt(i)))) {
                lbl.setBackground(new Color(201, 180, 88));
            } else {
                lbl.setBackground(new Color(100, 100, 100));
            }

            linha.add(lbl);
        }

        painelTentativas.add(linha);
        painelTentativas.revalidate();
        painelTentativas.repaint();
    }

    private void atualizarStatus() {
        if (fim) {
            lblStatus.setText("Fim do jogo");
        } else if (suaVez) {
            lblStatus.setText("Sua vez");
        } else {
            lblStatus.setText("Esperando o oponente...");
        }
    }

    private void finalizarJogo(String mensagem) {
        fim = true;
        JOptionPane.showMessageDialog(this, mensagem);

        int opcao = JOptionPane.showConfirmDialog(this, "Deseja jogar novamente?", "Reiniciar", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            reiniciarJogo();
        } else {
            System.exit(0);
        }
    }

    private void reiniciarJogo() {
        try {
            if (servidorEntrada != null)
                servidorEntrada.close();
            if (servidorSaida != null)
                servidorSaida.close();
            if (servidorConexao != null)
                servidorConexao.close();

            palavraSecreta = "";
            palavraOponente = "";
            feitas = 0;
            recebidas = 0;
            fim = false;

            painelTentativas.removeAll();
            painelTentativas.revalidate();
            painelTentativas.repaint();

            Thread.sleep(1000);

            conectar();
            definirPalavra();
            receberPalavraOponente();
            atualizarStatus();
            jogar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao reiniciar: " + e.getMessage());
        }
    }
}
