
package newgame;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SkillProjectile extends Projectile {
    public SkillProjectile(int x, int y, double angle, String imagePath) {
        super(x, y, angle, imagePath);
    }

    @Override
    public void move() {
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
    }

    @Override
    public boolean canPassThroughEnemies() {
        return true; // 貫通する
    }
}
