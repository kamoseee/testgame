import java.awt.*;

public class GameOverScreen {
    public void draw(Graphics g, int width, int height) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.RED);
        g.setFont(new Font("MS Gothic", Font.BOLD, 48));
        g.drawString("ゲームオーバー", width / 2 - 150, height / 2);
        g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        g.drawString("スペースキーでリスタート", width / 2 - 130, height / 2 + 40);
    }
}
