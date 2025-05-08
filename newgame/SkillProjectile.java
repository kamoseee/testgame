
package newgame;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SkillProjectile extends Projectile {
    private Set<Enemy> hitEnemies = new HashSet<>(); // すでに当たった敵を記録
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
    public boolean hasHit(Enemy enemy) {
        return hitEnemies.contains(enemy);
    }

    public void registerHit(Enemy enemy) {
        hitEnemies.add(enemy);
    }
}
