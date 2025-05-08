import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class GameLogic {
    private BykinGame game;

    public GameLogic(BykinGame game) {
        this.game = game;
    }

    public void updateGame() {
        if (game.getGameState() == GameState.LEVEL_UP) {
            return; // レベルアップ画面のときは処理を停止
        }

        game.getProjectiles().removeIf(p -> p.getX() < 0 ||
                p.getY() < 0 ||
                p.getX() > game.getStage().getStageWidth() ||
                p.getY() > game.getStage().getStageHeight());

        if (!game.isShowStatus() && !game.isGameOver()) {
            game.getBykin().move(game.getDx(), game.getDy());

            for (Enemy enemy : game.getEnemies()) {
                enemy.move(game.getWidth(), game.getHeight());

                BufferedImage bykinImg = game.getBykin().getMaskImage();
                BufferedImage enemyImg = enemy.getMaskImage();

                if (checkPixelCollision(bykinImg, game.getBykin().getX(), game.getBykin().getY(),
                        enemyImg, enemy.getX(), enemy.getY())) {
                    if (!game.getBykin().isInvincible()) {
                        game.getBykin().takeDamage(enemy.getAttack());
                        game.getBykin().setInvincible(true);

                        if (game.getBykin().getStatus().getCurrentHp() <= 0) {
                            game.setGameOver(true);
                            game.setGameState(GameState.GAME_OVER);
                        }
                    }
                }
            }

            handleProjectileCollisions();
            handleAutoAttack();
        }

        game.getDamageDisplays().removeIf(d -> System.currentTimeMillis() - d.getTimestamp() > 1000);
    }

    private void handleProjectileCollisions() {
        for (Iterator<Enemy> it = game.getEnemies().iterator(); it.hasNext();) {
            Enemy enemy = it.next();
            for (Iterator<Projectile> projIt = game.getProjectiles().iterator(); projIt.hasNext();) {
                Projectile projectile = projIt.next();
                if (projectile.getBounds().intersects(
                        new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()))) {
                    int actualDamage = enemy.takeDamage(game.getBykin().getStatus().getAttack());
                    game.getDamageDisplays().add(new DamageDisplay(actualDamage, enemy.getX(), enemy.getY()));

                    if (enemy.getCurrentHp() <= 0) {
                        game.getBykin().getStatus().addExperience(enemy.getLevel() * 20); // 経験値を加算
                        enemy.startDying(); // フェードアウト開始
                    }

                    projIt.remove();
                    break;
                }
            }
        }
        game.getEnemies().removeIf(enemy -> enemy.isDying() && enemy.updateDying());
    }

    private void handleAutoAttack() {
        if (System.currentTimeMillis() - game.getLastAttackTime() >= 2000) {
            int centerX = game.getBykin().getX() + game.getBykin().getWidth() / 2;
            int centerY = game.getBykin().getY() + game.getBykin().getHeight() / 2;

            int offsetX = game.getBykin().getX() - game.getCharX();
            int offsetY = game.getBykin().getY() - game.getCharY();

            int worldMouseX = game.getMouseX() + offsetX;
            int worldMouseY = game.getMouseY() + offsetY;
            double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);

            game.getProjectiles().add(new Projectile(centerX, centerY, angle, "assets/attack.png"));
            game.setLastAttackTime(System.currentTimeMillis());
        }
    }

    public boolean checkPixelCollision(BufferedImage img1, int x1, int y1,
                                       BufferedImage img2, int x2, int y2) {
        int top = Math.max(y1, y2);
        int bottom = Math.min(y1 + img1.getHeight(), y2 + img2.getHeight());
        int left = Math.max(x1, x2);
        int right = Math.min(x1 + img1.getWidth(), x2 + img2.getWidth());

        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                int pixel1 = img1.getRGB(x - x1, y - y1);
                int pixel2 = img2.getRGB(x - x2, y - y2);
                if (((pixel1 >> 24) & 0xFF) > 0 && ((pixel2 >> 24) & 0xFF) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
