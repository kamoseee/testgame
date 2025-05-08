package newgame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bykin {
    private BufferedImage image;
    private int x, y;
    private Status status;
    private Image skillImage;
    private boolean invincible = false;
    private long lastDamageTime = 0;
    private static final int INVINCIBLE_TIME = 1000; // 1秒無敵
    private Image specialImage;
    private BykinGame game; // `game` 変数を追加
    private SkillType selectedSkill = SkillType.AREA_ATTACK; // デフォルトは範囲攻撃

    public Bykin(int startX, int startY, BykinGame game) {

        this.x = startX;
        this.y = startY;
        this.game = game; // `game` をセット
        this.status = new Status(1, 10, 5, 3, 100, game); // `Status` を適切に初期化
    
        try {
            image = ImageIO.read(new File("assets/bykin.png"));
            skillImage = ImageIO.read(new File("assets/E.png"));
            specialImage = ImageIO.read(new File("assets/Q.png"));
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + e.getMessage());
            e.printStackTrace();
        }

    }
    public SkillType getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(SkillType skill) {
        this.selectedSkill = skill;
    }
    // 画像の幅を取得
    public int getWidth() {
        return (image != null) ? image.getWidth() : 0;
    }
    
    public int getHeight() {
        return (image != null) ? image.getHeight() : 0;
    }
    

    public void move(int dx, int dy) {
        int moveDistance = Math.min(status.getSpeed(), 10); // 最大移動距離を制限
        x += dx * moveDistance;
        y += dy * moveDistance;
    }
    

    public boolean isInvincible() {
        long now = System.currentTimeMillis();
        if (invincible && now - lastDamageTime >= INVINCIBLE_TIME) {
            invincible = false;
        }
        return invincible;
    }

    public void setInvincible(boolean b) {
        invincible = b;
        lastDamageTime = System.currentTimeMillis();
    }

    public void takeDamage(int damage) {
        if (isInvincible()) {
            //System.out.println("無敵状態のためダメージなし！");
            return;
        }
        
        int reduced = Math.max(1, damage - status.getDefense()); // 最低1ダメージ
        status.setCurrentHp(status.getCurrentHp() - reduced);
        System.out.println("ダメージを受けた！ 残HP: " + status.getCurrentHp());
    }
    

    public void heal(int amount) {
        status.heal(amount);
    }

    public void draw(Graphics g, int screenX, int screenY) {
        if (image != null) {
            g.drawImage(image, screenX, screenY, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(screenX, screenY, 50, 50); // 赤い四角を描画（エラー表示）
            g.setColor(Color.WHITE);
            g.drawString("画像なし", screenX + 5, screenY + 25);
        }
    }
    

    public Image getSkillImage() {
        return skillImage;
    }

    public Image getSpecialImage() {
        return specialImage;
    }

    public BufferedImage getMaskImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Status getStatus() {
        return status;
    }
}
