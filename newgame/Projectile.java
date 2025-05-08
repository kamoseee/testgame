import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon; // ImageIconクラスをインポート

class Projectile {
    private double x, y; // x, y を double に変更
    private int speed = 10; // 攻撃の速さ
    private double angle; // 攻撃方向（ラジアン）
    private BufferedImage image;

    public Projectile(int startX, int startY, double angle, String imagePath) {
        this.x = startX; // プレイヤーのX座標を使う
        this.y = startY; // プレイヤーのY座標を使う
        this.angle = angle;

        try {
            java.net.URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                image = ImageIO.read(imageUrl);
            } else {
                System.err.println("画像が見つかりません: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
            e.printStackTrace();
        }
        
    }

    public BufferedImage getImage() {
        return image;
    }

    public void move() {
    x += speed * Math.cos(angle);
    y += speed * Math.sin(angle);
}

public void draw(Graphics g, int offsetX, int offsetY) {
    if (image != null) {
        g.drawImage(image, getX() - offsetX, getY() - offsetY, null);
    } else {
        g.setColor(Color.RED);
        g.fillOval(getX() - offsetX, getY() - offsetY, 10, 10); // 赤い円を描画（エラー表示）
    }
}


public Rectangle getBounds() {
    if (image != null) {
        return new Rectangle(getX(), getY(), image.getWidth(), image.getHeight());
    } else {
        return new Rectangle(getX(), getY(), 10, 10); // デフォルトサイズ
    }
}


    public int getX() {
        return (int) x; // 描画時に int に変換
    }
    
    public int getY() {
        return (int) y;
    }
}
