package jogotermo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GerenciadorDeJogadas implements Runnable {

    private ObjectInputStream entradaJogador;
    private ObjectOutputStream saidaOponente;

    public GerenciadorDeJogadas(ObjectInputStream entradaJogador, ObjectOutputStream saidaOponente) {
        this.entradaJogador = entradaJogador;
        this.saidaOponente = saidaOponente;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String tentativa = (String) entradaJogador.readObject();
                saidaOponente.writeObject(tentativa);
                saidaOponente.flush();
            }
        } catch (Exception e) {
            System.out.println("Conexão encerrada.");
        }
    }
}
