import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class Bykin {
    private BufferedImage image;
    private int x, y;
    private Status status;
    private Image skillImage;
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
    

    public void takeDamage(int damage) {
        status.takeDamage(damage);
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
