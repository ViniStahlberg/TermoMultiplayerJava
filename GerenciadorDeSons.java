package jogotermo;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
        try {
        File arquivoSom = new File(caminho);
        if (!arquivoSom.exists()) {
            throw new FileNotFoundException("Arquivo de som não encontrado: " + caminho);
        }
        
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivoSom);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        System.err.println("Erro ao carregar som: " + caminho);
        throw e;
         }
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