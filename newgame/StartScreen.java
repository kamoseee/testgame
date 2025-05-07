import java.awt.*;

public class StartScreen {
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("MS Gothic", Font.BOLD, 48));
        g.drawString("バイキン強化計画", width / 2 - 180, height / 2 - 50);
        g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        g.drawString("スペースキーでスタート", width / 2 - 130, height / 2 + 30);
    }
}
