package newgame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;

public class Projectile {
    protected double x, y; // `protected` に変更
    protected int speed = 10;
    protected double angle;
    private BufferedImage image;
    private BufferedImage maskImage; // ピクセル単位の判定用マスク画像
    private String imagePath;

    public Projectile(int startX, int startY, double angle, String imagePath) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;
        this.imagePath = imagePath;
        loadImages();
    }
    public boolean canPassThroughEnemies() {
        return false; // 通常の弾は貫通しない
    }
    protected  void loadImages() {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                image = ImageIO.read(file);
                maskImage = ImageIO.read(file); // マスク画像も同じものを使用
            } else {
                System.err.println("画像が見つかりません: " + imagePath);
                            generatePlaceholderImage(); // 代替画像を生成
            }
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
            e.printStackTrace();
            generatePlaceholderImage(); // 画像が読み込めない場合にもプレースホルダーを使用
        }
    }
    private void generatePlaceholderImage() {
        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.RED);
        g2.fillOval(0, 0, 16, 16);
        g2.dispose();

        maskImage = image; // 同じプレースホルダーをマスク画像として使用
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getMaskImage() {
        return maskImage;
    }

    public void move() {
    double dx = Math.cos(angle);
    double dy = Math.sin(angle);
    
    double magnitude = Math.sqrt(dx * dx + dy * dy);
    x += (dx / magnitude) * speed;
    y += (dy / magnitude) * speed;
}

    public void draw(Graphics g, int offsetX, int offsetY) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (image != null) {
        g2d.drawImage(image, getX() - offsetX, getY() - offsetY, null);
    } else {
        g2d.setColor(Color.RED);
        g2d.fillOval(getX() - offsetX, getY() - offsetY, 10, 10);
    }
}


    public Rectangle getBounds() {
    int width = (image != null) ? image.getWidth() : 10;
    int height = (image != null) ? image.getHeight() : 10;
    return new Rectangle(getX(), getY(), width, height);
}

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
}
