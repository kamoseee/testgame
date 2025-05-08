import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class BykinGame extends JPanel implements KeyListener, MouseMotionListener, ActionListener {

    private Bykin bykin;
    private Stage stage;
    private final int charX = 300, charY = 220;
    private int dx = 0, dy = 0;
    private Timer timer;
    private List<Enemy> enemies; // 敵のリスト
    private boolean showStatus = false; // ステータス表示フラグ
    private boolean skillOnCooldown = false;
    private int cooldownMax = 3000; // 3000ms = 3秒間クールダウン
    private long skillUsedTime = 0;
    private boolean isGameOver = false;
    // private Font gameOverFont = new Font("MS Gothic", Font.BOLD, 48);
    private boolean isGameStarted = false; // スタート画面制御
    private GameState gameState;
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastAttackTime = 0; // 攻撃の最後の発射時間
    private Point mousePos = new Point(0, 0);
    private int mouseX = 0;
    private int mouseY = 0;
    private List<DamageDisplay> damageDisplays = new ArrayList<>();

    public BykinGame() {
        // Tabキーのフォーカス移動を無効化（Tabキーイベントを受け取れるようにする）
        setFocusTraversalKeysEnabled(false);

        bykin = new Bykin(100, 200);
        stage = new Stage(2000, 2000);
        addMouseMotionListener(this);

        enemies = new ArrayList<>();

        // 画像, 位置, ステータス（レベル, 攻撃, 防御, 速度, HP）
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /*
                 * // プレイヤーの位置からマウス位置への角度を計算
                 * double angle = Math.atan2(e.getY() - bykin.getY(), e.getX() - bykin.getX());
                 * // 攻撃を発射
                 * // 発射位置をプレイヤーの位置に設定
                 * int startX = bykin.getX() + bykin.getWidth() / 2;
                 * int startY = bykin.getY() + bykin.getHeight() / 2;
                 * 
                 * // デバッグ: 角度と発射位置を出力
                 * System.out.println("Angle: " + angle);
                 * System.out.println("Player Position: (" + startX + ", " + startY + ")");
                 * 
                 * projectiles.add(new Projectile(bykin.getX(), bykin.getY(), angle,
                 * "assets/attack.png"));
                 */
            }
        });
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this); // 約60FPS
        timer.start();

        gameState = GameState.START; // 初期状態をスタート画面に設定
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // マウスがドラッグされた時の処理を書く（今回は何もしない）
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景やキャラクターを先に描画
        int offsetX = bykin.getX() - charX;
        int offsetY = bykin.getY() - charY;

        stage.draw(g, offsetX, offsetY);
        bykin.draw(g, charX, charY);
        // enemies の中で消える演出が終わっていない敵だけ描画する
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            // HPが0以下でまだフェードアウト開始していなければ、開始
            if (enemy.getCurrentHp() <= 0 && !enemy.isDying()) {
                enemy.startDying();
            }

            // フェードアウト中の場合
            if (enemy.isDying()) {
                if (enemy.updateDying()) {
                    iterator.remove(); // 完全に消滅したら削除
                    continue; // 描画スキップ
                }
            }

            // 敵とHPバーを描画（通常 or 半透明）
            enemy.draw(g, offsetX, offsetY);
            drawEnemyHealthBar(g, enemy, offsetX, offsetY);
        }

        // 攻撃を描画
        for (Projectile projectile : projectiles) {
            projectile.move(); // 攻撃を動かす
            projectile.draw(g, offsetX, offsetY);
        }
        // ダメージ表示を描画
        g.setColor(Color.RED);
        // フォントサイズを大きくする
        Font originalFont = g.getFont();
        g.setFont(new Font("Arial", Font.BOLD, 32)); // フォントサイズを24に変更

        Graphics2D g2d = (Graphics2D) g; // Graphics2D にキャスト
        originalFont = g2d.getFont();
        g2d.setFont(new Font("Arial", Font.BOLD, 32)); // フォントを大きく

        Iterator<DamageDisplay> iter = damageDisplays.iterator();
        while (iter.hasNext()) {
            DamageDisplay damage = iter.next();

            if (damage.isExpired()) {
                iter.remove(); // 表示時間を超えたら削除
                continue;
            }

            int alpha = damage.getAlpha(); // 時間に応じた透明度
            g2d.setColor(new Color(255, 0, 0, alpha)); // 赤色＋透明度

            int drawX = damage.getX() - offsetX; // 敵の中心に合わせる
            int drawY = damage.getY() - offsetY - 20; // 敵の上に表示
            g2d.drawString("-" + damage.getDamage(), drawX, drawY);
            //System.out.println("ダメージ表示座標: X=" + drawX + ", Y=" + drawY);


        }

        g2d.setFont(originalFont); // フォントを戻す
        // 元のフォントに戻す
        g.setFont(originalFont);

        drawHealthBar(g, 10, 10);
        drawCoordinates(g);
        drawSkillIcons(g);

        // ステータスパネルを最前面に描画
        if (showStatus) {
            drawStatusPanel(g);
        }
        switch (gameState) {
            case START:
                new StartScreen().draw(g, getWidth(), getHeight());
                break;
            case GAME:
                // drawGame(g);
                break;
            case GAME_OVER:
                new GameOverScreen().draw(g, getWidth(), getHeight());
                break;
        }
    }

    private void drawStatusPanel(Graphics g) {
        int panelX = getWidth() - 220;
        g.setFont(new Font("MS Gothic", Font.PLAIN, 16));
        int panelY = 10;
        int panelWidth = 200;
        int panelHeight = 120;

        // パネル背景
        g.setColor(Color.BLACK); // 黒色
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // ステータステキスト
        g.setColor(Color.WHITE);
        Status s = bykin.getStatus();

        g.drawString("レベル: " + s.getLevel(), panelX + 10, panelY + 30);
        g.drawString("攻撃: " + s.getAttack(), panelX + 10, panelY + 50);
        g.drawString("防御: " + s.getDefense(), panelX + 10, panelY + 70);
        g.drawString("速度: " + s.getSpeed(), panelX + 10, panelY + 90);
        g.drawString("HP: " + s.getCurrentHp() + "/" + s.getMaxHp(), panelX + 10, panelY + 110);
    }

    private void drawSkillIcons(Graphics g) {
        int padding = 10;
        int iconSize = 64;
        int screenHeight = getHeight();

        Image skillImage = bykin.getSkillImage();
        Image specialImage = bykin.getSpecialImage();

        int skillX = padding;
        int skillY = screenHeight - iconSize - padding - 30;

        int specialX = skillX + iconSize + padding;
        int specialY = skillY;

        if (skillImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            // 丸型クリップ
            g2.setClip(new Ellipse2D.Float(skillX, skillY, iconSize, iconSize));
            g2.drawImage(skillImage, skillX, skillY, iconSize, iconSize, this);
            g2.setClip(null);

            if (skillOnCooldown) {
                long elapsed = System.currentTimeMillis() - skillUsedTime;
                if (elapsed >= cooldownMax) {
                    skillOnCooldown = false;
                } else {
                    double cooldownRatio = (double) elapsed / cooldownMax;
                    int angle = (int) (360 * (1 - cooldownRatio)); // 扇形の角度

                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillArc(skillX, skillY, iconSize, iconSize, 90, angle);
                }
            }

            g2.dispose();
        }
        if (specialImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setClip(new Ellipse2D.Float(specialX, specialY, iconSize, iconSize));
            g2.drawImage(specialImage, specialX, specialY, iconSize, iconSize, this);
            g2.setClip(null);

            g2.dispose();
        }
    }

    private void drawCoordinates(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("MS Gothic", Font.PLAIN, 16));
        int x = bykin.getX();
        int y = bykin.getY();
        String coordText = "座標: (" + x + ", " + y + ")";
        g.drawString(coordText, 10, getHeight() - 10);
    }

    private void useSkill() {
        if (skillOnCooldown)
            return; // クールダウン中なら発動不可

        System.out.println("スキル発動！");
        skillOnCooldown = true;
        skillUsedTime = System.currentTimeMillis();
    }

    private void useSpecial() {
        System.out.println("必殺技発動！");
        // 強力な技の処理をここに追加
    }

    private void drawHealthBar(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("HP:", x, y + 15);

        g.setColor(Color.BLACK);
        g.fillRect(x + 50, y, 200, 20);

        int currentHp = bykin.getStatus().getCurrentHp();
        int maxHp = bykin.getStatus().getMaxHp();
        int barWidth = (int) (200 * ((double) currentHp / maxHp));
        g.setColor(Color.GREEN);
        g.fillRect(x + 50, y, barWidth, 20);

        g.setColor(Color.BLACK);
        g.drawString(currentHp + "/" + maxHp, x + 260, y + 15);
    }

    // 敵の体力バー描画メソッド
    private void drawEnemyHealthBar(Graphics g, Enemy enemy, int offsetX, int offsetY) {
        // スクロール位置を考慮した描画座標
        int x = enemy.getX() - offsetX;
        int y = enemy.getY() - offsetY;

        // 体力バーの表示位置（敵の上）
        int barWidth = 150; // 幅を大きく
        int barHeight = 18; // 高さも調整
        int barX = x + enemy.getWidth() / 2 - barWidth / 2; // 敵の中心に合わせてバーを中央に
        int barY = y - 10; // 敵の上に表示

        int currentHp = enemy.getCurrentHp();
        int maxHp = enemy.getMaxHp();

        int filledWidth = (int) (barWidth * ((double) currentHp / maxHp));

        // 背景（黒）
        g.setColor(Color.BLACK);
        g.fillRect(barX, barY, barWidth, barHeight);

        // HPバー（緑）
        Color hpColor = Color.GREEN;
        if (currentHp <= maxHp * 0.5) hpColor = Color.YELLOW;
        if (currentHp <= maxHp * 0.25) hpColor = Color.RED;

        g.setColor(hpColor);
        g.fillRect(barX, barY, filledWidth, barHeight);


        // 枠線
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
        // レベル表示
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.BLACK);
        g.drawString("Lv." + enemy.getLevel(), x + 220, y + 15);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        projectiles.removeIf(p -> p.getX() < 0 ||
                p.getY() < 0 ||
                p.getX() > stage.getStageWidth() ||
                p.getY() > stage.getStageHeight());

        if (!showStatus && !isGameOver) { // ゲームオーバー時は処理しない
            bykin.move(dx, dy);

            for (Enemy enemy : enemies) {
                enemy.move(getWidth(), getHeight());

                // ピクセル単位の当たり判定
                BufferedImage bykinImg = bykin.getMaskImage();
                BufferedImage enemyImg = enemy.getMaskImage();
                
                if (isPixelCollision(bykinImg, bykin.getX(), bykin.getY(),
                        enemyImg, enemy.getX(), enemy.getY())) {
                    if (!bykin.isInvincible()) {
                        bykin.takeDamage(enemy.getAttack());
                        bykin.setInvincible(true);

                        if (bykin.getStatus().getCurrentHp() <= 0) {
                            isGameOver = true;
                            gameState = GameState.GAME_OVER; // ゲームオーバー状態に変更
                        }
                    }
                }
            }
            for (Iterator<Projectile> it = projectiles.iterator(); it.hasNext();) {
                Projectile projectile = it.next();
                for (Enemy enemy : enemies) {
                    if (projectile.getBounds().intersects(
                            new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()))) {
                                int actualDamage = enemy.takeDamage(bykin.getStatus().getAttack());
                                int damageX = enemy.getX() + enemy.getWidth() / 2; // 敵の中心
                                int damageY = enemy.getY() - 10; // 敵の上

                                damageDisplays.add(new DamageDisplay(actualDamage, damageX, damageY));

                                
                                
                        it.remove(); // イテレータを使って削除
                        break;
                    }
                }
            }

            // 攻撃を自動で発射
            if (System.currentTimeMillis() - lastAttackTime >= 2000) { // 2秒ごとに攻撃
                int centerX = bykin.getX() + bykin.getWidth() / 2;
                int centerY = bykin.getY() + bykin.getHeight() / 2;

                int offsetX = bykin.getX() - charX;
                int offsetY = bykin.getY() - charY;

                // マウスの画面上の座標を、ステージ上の座標に変換
                int worldMouseX = mouseX + offsetX;
                int worldMouseY = mouseY + offsetY;
                double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);

                projectiles.add(new Projectile(centerX, centerY, angle, "assets/attack.png"));
                lastAttackTime = System.currentTimeMillis();
            }

        }
        // 古いダメージ表示を削除
        damageDisplays.removeIf(d -> System.currentTimeMillis() - d.getTimestamp() > 1000); // 1秒後に消去

        repaint();
    }
    
    private void restartGame() {
        bykin = new Bykin(100, 200);
        enemies.clear();
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));
        isGameOver = false;
        skillOnCooldown = false;
        dx = 0;
        dy = 0;
        gameState = GameState.GAME; // ゲームをすぐ再開
        repaint();
    }

    // ピクセル単位のマスク判定（画像の重なりをチェック）
    private boolean isPixelCollision(BufferedImage img1, int x1, int y1,
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
                    return true; // 両方の画像が不透明 → 衝突
                }
            }
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (showStatus) {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                showStatus = false;
                repaint();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> dx = -1;
            case KeyEvent.VK_RIGHT -> dx = 1;
            case KeyEvent.VK_UP -> dy = -1;
            case KeyEvent.VK_DOWN -> dy = 1;
            case KeyEvent.VK_E -> useSkill();
            case KeyEvent.VK_Q -> useSpecial();
            case KeyEvent.VK_TAB -> {
                showStatus = true;
                repaint();
            }
            case KeyEvent.VK_SPACE -> {
                if (isGameOver) {
                    restartGame();
                    gameState = GameState.START; // ゲームオーバー後にスタート画面に戻す
                    
                } else if (!isGameStarted) {
                    gameState = GameState.GAME;
                    isGameStarted = true;  // ←追加

                    repaint();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> dx = 0;
            case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> dy = 0;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bykin Scroll Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.add(new BykinGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

enum GameState {
    START,
    GAME,
    GAME_OVER
}
