package jogotermo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTermo {

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor Termo iniciado: " + servidor + "\n");

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

        Thread t1 = new Thread(() -> repassar(in1, out2));
        Thread t2 = new Thread(() -> repassar(in2, out1));

        t1.start();
        t2.start();
    }

    private static void repassar(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                String tentativa = (String) in.readObject();
                out.writeObject(tentativa);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Conexão encerrada.");
        }
    }
}

