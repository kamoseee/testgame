import java.awt.*;

public class Enemy {
    private int x, y;
    private int width = 40, height = 40; // 敵の大きさ
    private Color color;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.RED;
    }

    // オフセットを考慮して描画
    public void draw(Graphics g, int offsetX, int offsetY) {
        int screenX = x - offsetX;
        int screenY = y - offsetY;

        g.setColor(color);
        g.fillRect(screenX, screenY, width, height);
    }

    // 衝突判定用
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getter（必要に応じて）
    public int getX() { return x; }
    public int getY() { return y; }
}
