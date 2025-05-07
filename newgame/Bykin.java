import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
       

    
    public Bykin(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.status = new Status(1, 10, 5, 3, 100); // 初期値を設定

        try {
            image = ImageIO.read(new File("assets/bykin.png"));
            skillImage = ImageIO.read(new File("assets/E.png"));
        specialImage = ImageIO.read(new File("assets/Q.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void move(int dx, int dy) {
        x += dx * status.getSpeed();
        y += dy * status.getSpeed();
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
        }
        
    }
    public Image getSkillImage() {
        return skillImage;
    }
    
    public Image getSpecialImage() {
        return specialImage;
    }
    

    public int getX() { return x; }
    public int getY() { return y; }
    public Status getStatus() { return status; }
}
