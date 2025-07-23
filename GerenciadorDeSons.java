package jogotermo;

import javax.sound.sampled.*;
import java.io.File;

public class GerenciadorDeSons {
    private Clip somInicio;
    private Clip somVitoria;
    private Clip somDerrota;

    public GerenciadorDeSons() {
        carregarSons();
    }

    private void carregarSons() {
        try {
            // Carrega os arquivos de som
            somInicio = carregarSom("sounds/start.wav");
            somVitoria = carregarSom("sounds/win.wav");
            somDerrota = carregarSom("sounds/lose.wav");
        } catch (Exception e) {
            System.err.println("Erro ao carregar sons: " + e.getMessage());
        }
    }

    private Clip carregarSom(String caminho) throws Exception {
        File arquivoSom = new File(caminho);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivoSom);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }

    public void tocarSomInicio() {
        tocarSom(somInicio);
    }

    public void tocarSomVitoria() {
        tocarSom(somVitoria);
    }

    public void tocarSomDerrota() {
        tocarSom(somDerrota);
    }

    private void tocarSom(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Reinicia o som
            clip.start();
        }
    }
}