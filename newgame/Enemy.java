import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Enemy {
    private int x, y;
    private Image image;

    // 敵のステータス
    private int level = 1;
    private int attack = 5;
    private int defense = 1;
    private int speed = 3; // 速度
    private int maxHp = 30;
    private int currentHp = 30;

    private Random rand = new Random();
    
    // 敵が最後に移動した時間
    private long lastMoveTime;
    // 移動間隔（ミリ秒）
    private static final long MOVE_INTERVAL = 500; // 0.5秒

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastMoveTime = System.currentTimeMillis(); // 初期化

        try {
            // 画像を読み込む
            image = ImageIO.read(new File("assets/virus01.png"));
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

    // 敵のHPを減らす処理
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp < 0) {
            currentHp = 0;
        }
    }

    // ランダムに移動するメソッド（間隔を調整）
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
        // 敵の位置をオフセットを考慮して描画
        g.drawImage(image, x - offsetX, y - offsetY, null);
    }

    // 位置を取得するゲッター
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
