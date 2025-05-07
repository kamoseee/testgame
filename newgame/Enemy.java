import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import java.awt.image.BufferedImage;  // これを追加


public class Enemy {
    private int x, y;
    private BufferedImage image; 
    // 敵のステータス
        // 宣言だけ残す（初期化は
        private int level;
        private int attack;
        private int defense;  
        private int speed;
        private int maxHp;
        private int currentHp;  // ← これを追加！

        private boolean dying = false; 
        private Random rand = new Random(); 



    private float alpha = 1.0f; // 1.0 

    // 敵が最後に移動した時間
    private long lastMoveTime;
    // 移動間隔（ミリ秒）
    private static final long MOVE_INTERVAL = 500; // 0.5秒

    public Enemy(int x, int y, String imagePath, int level, int attack, int defense, int speed, int maxHp) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.lastMoveTime = System.currentTimeMillis();

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 敵のステータスに関連するゲッター
    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }
    public BufferedImage getImage() {
        return image;
    }
    

    // 敵のHPを減らす処理
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp < 0) {
            currentHp = 0;
        }
    }

        //ランダムに移動するメソッド（間隔を調整）
    public void move(int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        
        // 移動間隔が経過した場合のみ移動
        if (currentTime - lastMoveTime >= MOVE_INTERVAL) {
            int direction = rand.nextInt(4); // 0: 左, 1: 右, 2: 上, 3: 下
            int moveDistance = speed; // 移動距離は速度に基づく

            switch (direction) {
                case 0: // 左
                        
                    if (x - moveDistance >= 0) x -= moveDistance;
                    break;
                case 1: // 右
                        
                    if (x + moveDistance < screenWidth) x += moveDistance;
                    break;
                case 2: // 上
                        
                    if (y - moveDistance >= 0) y -= moveDistance;
                    break;
                case 3: // 下
                    if (y + moveDistance < screenHeight) y += moveDistance;
                    break;
            }

            // 最後に移動した時刻を更新
            lastMoveTime = currentTime;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2d = (Graphics2D) g;
    Composite original = g2d.getComposite();

    if (dying) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }

    g2d.drawImage(image, x - offsetX, y - offsetY, null);

    if (dying) {
        g2d.setComposite(original);
    }
    }
    public void startDying() {
        this.dying = true;
    }
    public boolean isDying() {
        return dying;
    }
    
    public boolean updateDying() {
        if (dying) {
            alpha -= 0.05f; // 徐々に透明に
            if (alpha <= 0) {
                return true; // 消滅完了のサイン
            }
        }
        return false;
    }
    

    // 位置を取得するゲッター
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public BufferedImage getMaskImage() {
        return (BufferedImage) image;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }
    public int getWidth() {
        return image.getWidth();
    }
    
    public int getHeight() {
        return image.getHeight();
    }
    
    
}
