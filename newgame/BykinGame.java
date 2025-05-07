import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.geom.Ellipse2D;


public class BykinGame extends JPanel implements KeyListener, ActionListener {

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
    private Font gameOverFont = new Font("MS Gothic", Font.BOLD, 48);
    private boolean isGameStarted = false; // スタート画面制御



    public BykinGame() {
        // Tabキーのフォーカス移動を無効化（Tabキーイベントを受け取れるようにする）
        setFocusTraversalKeysEnabled(false);

        bykin = new Bykin(100, 200);
        stage = new Stage(2000, 2000);

        enemies = new ArrayList<>();

        // 画像, 位置, ステータス（レベル, 攻撃, 防御, 速度, HP）
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));
        
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this); // 約60FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // 背景やキャラクターを先に描画
        int offsetX = bykin.getX() - charX;
        int offsetY = bykin.getY() - charY;
    
        stage.draw(g, offsetX, offsetY);
        bykin.draw(g, charX, charY);
        for (Enemy enemy : enemies) {
            enemy.draw(g, offsetX, offsetY);
        }
    
        drawHealthBar(g, 10, 10);
        drawCoordinates(g);
        drawSkillIcons(g);
    
        // ステータスパネルを最前面に描画
        if (showStatus) {
            drawStatusPanel(g);
        }
        if (!isGameStarted) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(gameOverFont);
            g.drawString("バイキン強化計画", getWidth() / 2 - 180, getHeight() / 2 - 50);
            g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
            g.drawString("スペースキーでスタート", getWidth() / 2 - 130, getHeight() / 2 + 30);
            return;
        }
        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
        
            g.setColor(Color.RED);
            g.setFont(gameOverFont);
            g.drawString("ゲームオーバー", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            g.drawString("スペースキーでリスタート", getWidth() / 2 - 130, getHeight() / 2 + 40);
            return;
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
        Font japaneseFont = new Font("MS Gothic", Font.PLAIN, 16);
    
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
        if (skillOnCooldown) return; // クールダウン中なら発動不可
    
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

        @Override
public void actionPerformed(ActionEvent e) {
    if (!showStatus) {
        bykin.move(dx, dy);
      // 敵をランダムに移動させる
      for (Enemy enemy : enemies) {
        // 当たり判定
    Rectangle bykinRect = new Rectangle(bykin.getX(), bykin.getY(), 64, 64);
    Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 64, 64);

    if (bykinRect.intersects(enemyRect)) {
        // ダメージを受ける（1秒に1回まで）
        if (!bykin.isInvincible()) {
            bykin.takeDamage(enemy.getAttack());
            bykin.setInvincible(true);
            if (bykin.getStatus().getCurrentHp() <= 0) {
                isGameOver = true;
            }
        }
    }
        enemy.move(getWidth(), getHeight()); // 画面サイズを渡す
    }
    }
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
    repaint();
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
            if (!isGameStarted) {
                isGameStarted = true;
                repaint();
            } else if (isGameOver) {
                restartGame();
            }
        }
    }
    }
    
@Override
public void keyReleased(KeyEvent e) {
    if (showStatus) return;

    switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> dx = 0;
        case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> dy = 0;
    }
}


    @Override
    public void keyTyped(KeyEvent e) {}

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
