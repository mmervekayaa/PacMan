import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int satirSayisi = 21;
        int sutunSayisi = 19;
        int karoBoyutu = 32;
        int tahtaGenisligi = sutunSayisi * karoBoyutu;
        int tahtaYuksekligi = satirSayisi * karoBoyutu;

        JFrame pencere = new JFrame("PacMan");
        // pencere.setVisible(true);
        pencere.setSize(tahtaGenisligi, tahtaYuksekligi);
        pencere.setLocationRelativeTo(null);
        pencere.setResizable(false);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanOyunu = new PacMan();
        pencere.add(pacmanOyunu);
        pencere.pack();
        pacmanOyunu.requestFocus();
        pencere.setVisible(true);

    }
}
