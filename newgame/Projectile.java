import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;  // ImageIconクラスをインポート


class Projectile {
    private int x, y;
    private int speed = 10;  // 攻撃の速さ
    private double angle;    // 攻撃方向（ラジアン）
    private BufferedImage image;

    public Projectile(int startX, int startY, double angle, String imagePath) {
        this.x = startX;   // プレイヤーのX座標を使う
        this.y = startY;   // プレイヤーのY座標を使う
        this.angle = angle;
        
        try {
            // BufferedImageとして画像を読み込む
            image = ImageIO.read(getClass().getResource(imagePath)); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     public BufferedImage getImage() {
        return image;
    }
    public void move() {
        // 角度に基づいて移動
        x += (int) (speed * Math.cos(angle));
        y += (int) (speed * Math.sin(angle));
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        g.drawImage(image, x - offsetX, y - offsetY, null);
    }
    

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
