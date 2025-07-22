package jogotermo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTermo {

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor Termo iniciado: " + servidor + "\n");

        while (true) {
            System.out.println("Aguardando conexão do Jogador 1...");
            Socket socketJogador1 = servidor.accept();
            System.out.println("Jogador 1 conectado: " + socketJogador1.getInetAddress());

            ObjectOutputStream out1 = new ObjectOutputStream(socketJogador1.getOutputStream());
            out1.flush();
            ObjectInputStream in1 = new ObjectInputStream(socketJogador1.getInputStream());

            System.out.println("Aguardando conexão do Jogador 2...");
            Socket socketJogador2 = servidor.accept();
            System.out.println("Jogador 2 conectado: " + socketJogador2.getInetAddress());

            ObjectOutputStream out2 = new ObjectOutputStream(socketJogador2.getOutputStream());
            out2.flush();
            ObjectInputStream in2 = new ObjectInputStream(socketJogador2.getInputStream());

            System.out.println("Recebendo palavras secretas...");
            String palavra1 = (String) in1.readObject();
            String palavra2 = (String) in2.readObject();

            System.out.println("Palavra do Jogador 1: " + palavra1);
            System.out.println("Palavra do Jogador 2: " + palavra2);

            out1.writeObject("true;" + palavra2);
            out1.flush();

            out2.writeObject("false;" + palavra1);
            out2.flush();

            Thread t1 = new Thread(() -> {
                try {
                    repassar(in1, out2);
                } finally {
                    fecharConexao(socketJogador1, in1, out1);
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    repassar(in2, out1);
                } finally {
                    fecharConexao(socketJogador2, in2, out2);
                }
            });

            t1.start();
            t2.start();

            t1.join();
            t2.join();
            System.out.println("Partida encerrada. Reiniciando servidor para nova partida...");
        }
    }

    private static void repassar(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                String tentativa = (String) in.readObject();

                if (tentativa.startsWith("ATUALIZAR_PALAVRA;")) {
                    String novaPalavra = tentativa.split(";")[1];
                    out.writeObject("PALAVRA_ATUALIZADA;" + novaPalavra);
                    out.flush();
                } else {
                    out.writeObject(tentativa);
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Conexão encerrada.");
        }
    }

    private static void fecharConexao(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}