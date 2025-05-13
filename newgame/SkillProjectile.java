
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
    double dx = Math.cos(angle);
    double dy = Math.sin(angle);
    
    double magnitude = Math.sqrt(dx * dx + dy * dy);
    x += (dx / magnitude) * speed;
    y += (dy / magnitude) * speed;
}


    @Override
    public boolean canPassThroughEnemies() {
        return true; // 貫通する
    }
    public boolean hasHit(Enemy enemy) {
        return enemy != null && hitEnemies.contains(enemy);
    }

    public void registerHit(Enemy enemy) {
        if (enemy != null && !hitEnemies.contains(enemy)) {
        hitEnemies.add(enemy);
    }
    }
    public void resetHits() {
        hitEnemies.clear();
    }

}
