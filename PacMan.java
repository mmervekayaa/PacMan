import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Blok {
        int x;
        int y;
        int genislik;
        int yukseklik;
        Image resim;

        int baslangicX;
        int baslangicY;
        char yon = 'Y'; // Y A S D (Yukari, Asagi, Sol, Sag)
        int hizX = 0;
        int hizY = 0;

        Blok(Image resim, int x, int y, int genislik, int yukseklik) {
            this.resim = resim;
            this.x = x;
            this.y = y;
            this.genislik = genislik;
            this.yukseklik = yukseklik;
            this.baslangicX = x;
            this.baslangicY = y;
        }

        void yonGuncelle(char yon) {
            char oncekiYon = this.yon;
            this.yon = yon;
            hizGuncelle();
            this.x += this.hizX;
            this.y += this.hizY;
            for (Blok duvar : duvarlar) {
                if (carpisma(this, duvar)) {
                    this.x -= this.hizX;
                    this.y -= this.hizY;
                    this.yon = oncekiYon;
                    hizGuncelle();
                }
            }
        }

        void hizGuncelle() {
            if (this.yon == 'Y') {
                this.hizX = 0;
                this.hizY = -kareBoyutu / 4;
            } else if (this.yon == 'A') {
                this.hizX = 0;
                this.hizY = kareBoyutu / 4;
            } else if (this.yon == 'S') {
                this.hizX = -kareBoyutu / 4;
                this.hizY = 0;
            } else if (this.yon == 'D') {
                this.hizX = kareBoyutu / 4;
                this.hizY = 0;
            }
        }

        void sifirla() {
            this.x = this.baslangicX;
            this.y = this.baslangicY;
        }
    }

    private int satirSayisi = 21;
    private int sutunSayisi = 19;
    private int kareBoyutu = 32;
    private int tahtaGenislik = sutunSayisi * kareBoyutu;
    private int tahtaYukseklik = satirSayisi * kareBoyutu;

    private Image duvarResmi;
    private Image maviHayaletResmi;
    private Image turuncuHayaletResmi;
    private Image pembeHayaletResmi;
    private Image kirmiziHayaletResmi;

    private Image pacmanYukariResmi;
    private Image pacmanAsagiResmi;
    private Image pacmanSolResmi;
    private Image pacmanSagResmi;

    // X = duvar, O = atla, P = pacman, ' ' = yemek
    // Hayaletler: b = mavi, o = turuncu, p = pembe, r = kirmizi
    private String[] harita = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Blok> duvarlar;
    HashSet<Blok> yemekler;
    HashSet<Blok> hayaletler;
    Blok pacman;

    Timer oyunDongusu;
    char[] yonler = {'Y', 'A', 'S', 'D'}; // Yukari, Asagi, Sol, Sag
    Random rastgele = new Random();
    int puan = 0;
    int can = 3;
    boolean oyunBitti = false;

    PacMan() {
        setPreferredSize(new Dimension(tahtaGenislik, tahtaYukseklik));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Resimleri yukle
        duvarResmi = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        maviHayaletResmi = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        turuncuHayaletResmi = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pembeHayaletResmi = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        kirmiziHayaletResmi = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanYukariResmi = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanAsagiResmi = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanSolResmi = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanSagResmi = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        haritayiYukle();
        for (Blok hayalet : hayaletler) {
            char yeniYon = yonler[rastgele.nextInt(4)];
            hayalet.yonGuncelle(yeniYon);
        }
        // Zamanlayici baslat
        oyunDongusu = new Timer(50, this); // 20 FPS (1000/50)
        oyunDongusu.start();

    }

    public void haritayiYukle() {
        duvarlar = new HashSet<Blok>();
        yemekler = new HashSet<Blok>();
        hayaletler = new HashSet<Blok>();

        for (int r = 0; r < satirSayisi; r++) {
            for (int c = 0; c < sutunSayisi; c++) {
                String satir = harita[r];
                char haritaKarakteri = satir.charAt(c);

                int x = c * kareBoyutu;
                int y = r * kareBoyutu;

                if (haritaKarakteri == 'X') { // Duvar
                    Blok duvar = new Blok(duvarResmi, x, y, kareBoyutu, kareBoyutu);
                    duvarlar.add(duvar);
                } else if (haritaKarakteri == 'b') { // Mavi hayalet
                    Blok hayalet = new Blok(maviHayaletResmi, x, y, kareBoyutu, kareBoyutu);
                    hayaletler.add(hayalet);
                } else if (haritaKarakteri == 'o') { // Turuncu hayalet
                    Blok hayalet = new Blok(turuncuHayaletResmi, x, y, kareBoyutu, kareBoyutu);
                    hayaletler.add(hayalet);
                } else if (haritaKarakteri == 'p') { // Pembe hayalet
                    Blok hayalet = new Blok(pembeHayaletResmi, x, y, kareBoyutu, kareBoyutu);
                    hayaletler.add(hayalet);
                } else if (haritaKarakteri == 'r') { // Kirmizi hayalet
                    Blok hayalet = new Blok(kirmiziHayaletResmi, x, y, kareBoyutu, kareBoyutu);
                    hayaletler.add(hayalet);
                } else if (haritaKarakteri == 'P') { // Pacman
                    pacman = new Blok(pacmanSagResmi, x, y, kareBoyutu, kareBoyutu);
                } else if (haritaKarakteri == ' ') { // Yemek
                    Blok yemek = new Blok(null, x + 14, y + 14, 4, 4);
                    yemekler.add(yemek);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ciz(g);
    }

    public void ciz(Graphics g) {
        g.drawImage(pacman.resim, pacman.x, pacman.y, pacman.genislik, pacman.yukseklik, null);

        for (Blok hayalet : hayaletler) {
            g.drawImage(hayalet.resim, hayalet.x, hayalet.y, hayalet.genislik, hayalet.yukseklik, null);
        }

        for (Blok duvar : duvarlar) {
            g.drawImage(duvar.resim, duvar.x, duvar.y, duvar.genislik, duvar.yukseklik, null);
        }

        g.setColor(Color.WHITE);
        for (Blok yemek : yemekler) {
            g.fillRect(yemek.x, yemek.y, yemek.genislik, yemek.yukseklik);
        }
        // Puan
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (oyunBitti) {
            g.drawString("Oyun Bitti: " + String.valueOf(puan), kareBoyutu / 2, kareBoyutu / 2);
        } else {
            g.drawString("x" + String.valueOf(can) + " Puan: " + String.valueOf(puan), kareBoyutu / 2, kareBoyutu / 2);
        }
    }

    public void hareketEt() {
        pacman.x += pacman.hizX;
        pacman.y += pacman.hizY;

        // Duvar carpisma kontrolu
        for (Blok duvar : duvarlar) {
            if (carpisma(pacman, duvar)) {
                pacman.x -= pacman.hizX;
                pacman.y -= pacman.hizY;
                break;
            }
        }

        // Hayalet carpisma kontrolu
        for (Blok hayalet : hayaletler) {
            if (carpisma(hayalet, pacman)) {
                can -= 1;
                if (can == 0) {
                    oyunBitti = true;
                    return;
                }
                pozisyonlariSifirla();
            }

            if (hayalet.y == kareBoyutu * 9 && hayalet.yon != 'Y' && hayalet.yon != 'A') {
                hayalet.yonGuncelle('Y');
            }
            hayalet.x += hayalet.hizX;
            hayalet.y += hayalet.hizY;
            for (Blok duvar : duvarlar) {
                if (carpisma(hayalet, duvar) || hayalet.x <= 0 || hayalet.x + hayalet.genislik >= tahtaGenislik) {
                    hayalet.x -= hayalet.hizX;
                    hayalet.y -= hayalet.hizY;
                    char yeniYon = yonler[rastgele.nextInt(4)];
                    hayalet.yonGuncelle(yeniYon);
                }
            }
        }

        // Yemek carpisma kontrolu
        Blok yenenYemek = null;
        for (Blok yemek : yemekler) {
            if (carpisma(pacman, yemek)) {
                yenenYemek = yemek;
                puan += 10;
            }
        }
        yemekler.remove(yenenYemek);

        if (yemekler.isEmpty()) {
            haritayiYukle();
            pozisyonlariSifirla();
        }
    }

    public boolean carpisma(Blok a, Blok b) {
        return  a.x < b.x + b.genislik &&
                a.x + a.genislik > b.x &&
                a.y < b.y + b.yukseklik &&
                a.y + a.yukseklik > b.y;
    }

    public void pozisyonlariSifirla() {
        pacman.sifirla();
        pacman.hizX = 0;
        pacman.hizY = 0;
        for (Blok hayalet : hayaletler) {
            hayalet.sifirla();
            char yeniYon = yonler[rastgele.nextInt(4)];
            hayalet.yonGuncelle(yeniYon);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hareketEt();
        repaint();
        if (oyunBitti) {
            oyunDongusu.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (oyunBitti) {
            haritayiYukle();
            pozisyonlariSifirla();
            can = 3;
            puan = 0;
            oyunBitti = false;
            oyunDongusu.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.yonGuncelle('Y');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.yonGuncelle('A');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.yonGuncelle('S');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.yonGuncelle('D');
        }

        if (pacman.yon == 'Y') {
            pacman.resim = pacmanYukariResmi;
        } else if (pacman.yon == 'A') {
            pacman.resim = pacmanAsagiResmi;
        } else if (pacman.yon == 'S') {
            pacman.resim = pacmanSolResmi;
        } else if (pacman.yon == 'D') {
            pacman.resim = pacmanSagResmi;
        }
    }
}
