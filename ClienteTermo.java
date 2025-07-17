package jogotermo;

import javax.swing.SwingUtilities;

public class ClienteTermo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new JogoTermo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
