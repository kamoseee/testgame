package newgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage; // BufferedImage をインポート
import java.util.ArrayList;
import java.util.List;
import newgame.SkillProjectile;
import newgame.Projectile;
import newgame.Enemy;
import newgame.GameState; // GameState をインポート
import java.util.Iterator;
import newgame.Bykin; // `Bykin` をインポート
import newgame.SkillType; // `SkillType` をインポート

public class BykinGame extends JPanel implements KeyListener, MouseMotionListener, ActionListener {
    private Bykin bykin;
    private Stage stage;
    private final int charX = 300, charY = 220;
    private int dx = 0, dy = 0;
    private Timer timer;
    private List<Enemy> enemies;
    private boolean showStatus = false;
    private boolean skillOnCooldown = false;
    private int cooldownMax = 3000;
    private long skillUsedTime = 0;
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastAttackTime = System.currentTimeMillis(); // 初期値を現在の時間
    private Point mousePos = new Point(0, 0);
    private int mouseX = 0;
    private int mouseY = 0;
    private List<DamageDisplay> damageDisplays = new ArrayList<>();
    private List<AOEEffect> effects = new ArrayList<>(); // 範囲攻撃のエフェクトリスト
    private GameRenderer renderer;
    private GameLogic logic;
    private GameInputHandler inputHandler;
    private GameState gameState = GameState.START; // ゲームの状態を管理
    


    public BykinGame() {
        setFocusTraversalKeysEnabled(false);
        
        // **GameLogic を最初に初期化**
        logic = new GameLogic(this); 
    
        inputHandler = new GameInputHandler(this);
        bykin = new Bykin(100, 200, this);
        stage = new Stage(2000, 2000);
        
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow(); // フォーカスを設定
        addKeyListener(inputHandler);
    
        // 移動を定期的に更新するタイマー
        Timer movementTimer = new Timer(32, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputHandler.updateMovement();
                repaint();
            }
        });
        movementTimer.start();
    
        enemies = new ArrayList<>();
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));
    
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    
        timer = new Timer(16, this);
        timer.start();
    
        gameState = GameState.START;
    
        // **GameRenderer を初期化**
        renderer = new GameRenderer(this);
    }
    
    public List<AOEEffect> getEffects() {
        return effects;
    }
    public long getLastAttackTime() {
        return lastAttackTime;
    }
    
    public void setLastAttackTime(long time) {
        lastAttackTime = time;
    }
    
    public int getMouseX() {
        return mouseX;
    }
    
    public int getMouseY() {
        return mouseY;
    }

    public void setBykin(Bykin bykin) {
        this.bykin = bykin;
    }
    public void updateGame() {
        if (logic != null) {
            logic.updateGame(); // `logic` が `null` でない場合のみ実行
        } else {
            System.err.println("エラー: GameLogic が初期化されていません！");
        }
    }
    private void drawStatsScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("レベルアップ！", getWidth() / 2 - 150, getHeight() / 2 - 100);
    
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        Status s = bykin.getStatus();
        g.drawString("新しいレベル: " + s.getLevel(), getWidth() / 2 - 100, getHeight() / 2 - 50);
        g.drawString("攻撃力: " + s.getAttack(), getWidth() / 2 - 100, getHeight() / 2 - 20);
        g.drawString("防御力: " + s.getDefense(), getWidth() / 2 - 100, getHeight() / 2 + 10);
        g.drawString("速度: " + s.getSpeed(), getWidth() / 2 - 100, getHeight() / 2 + 40);
        g.drawString("最大HP: " + s.getMaxHp(), getWidth() / 2 - 100, getHeight() / 2 + 70);
        g.drawString("スペースキーで続行", getWidth() / 2 - 120, getHeight() / 2 + 120);
    }
    
    private void drawLevelUpScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("スキルを選択してください", getWidth() / 2 - 150, getHeight() / 2 - 100);
    
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        g.drawString("1: 範囲攻撃", getWidth() / 2 - 100, getHeight() / 2);
        g.drawString("2: 貫通弾", getWidth() / 2 - 100, getHeight() / 2 + 30);
        g.drawString("3: 連続攻撃", getWidth() / 2 - 100, getHeight() / 2 + 60);
    }
    public void useAOEAttack() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
        int attackRadius = 200; // 範囲攻撃の半径
    
        System.out.println("範囲攻撃エフェクト追加: " + centerX + ", " + centerY + " 半径: " + attackRadius); // デバッグ用    
        getEffects().add(new AOEEffect(centerX, centerY, attackRadius, 2000)); // 2秒間表示
    
        System.out.println("現在のエフェクト数: " + getEffects().size()); // デバッグ用
    
        int attackRadiusSquared = attackRadius * attackRadius; // 範囲の二乗を計算（高速化）
    
        for (Enemy enemy : getEnemies()) {
            BufferedImage enemyImage = enemy.getImage(); // 敵の画像を取得
            int enemyWidth = enemyImage.getWidth();
            int enemyHeight = enemyImage.getHeight();
            boolean damageApplied = false; // ダメージ適用済みかどうかのフラグ
    
            for (int x = 0; x < enemyWidth; x++) {
                for (int y = 0; y < enemyHeight; y++) {
                    int pixel = enemyImage.getRGB(x, y);
    
                    // 透明ピクセルは無視
                    if ((pixel >> 24) == 0) {
                        continue;
                    }
    
                    int worldX = enemy.getX() + x;
                    int worldY = enemy.getY() + y;
    
                    int distanceSquared = (worldX - centerX) * (worldX - centerX) +
                                          (worldY - centerY) * (worldY - centerY);
    
                    if (distanceSquared <= attackRadiusSquared) { // 範囲内ならダメージ適用
                        if (!damageApplied) { // まだダメージを適用していない場合のみ
                            System.out.println("敵にダメージ適用: " + enemy.getX() + ", " + enemy.getY()); // デバッグ用
                            int actualDamage = enemy.takeDamage(bykin.getStatus().getAttack() * 2);
                            getDamageDisplays().add(new DamageDisplay(actualDamage, worldX, worldY)); // ダメージ表示
    
                            if (enemy.getCurrentHp() <= 0) {
                                bykin.getStatus().addExperience(enemy.getLevel() * 20);
                                enemy.startDying();
                            }
    
                            damageApplied = true; // ダメージ適用済みにする
                        }
                        break; // 1つでも当たり判定があればダメージ適用
                    }
                }
                if (damageApplied) break; // すでにダメージを適用したらループを抜ける
            }
        }
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // プレイヤーのワールド座標から画面座標へのオフセットを計算
        int offsetX = bykin.getX() - getCharX();
        int offsetY = bykin.getY() - getCharY();

        // エフェクトを描画
        for (AOEEffect effect : effects) {
            effect.draw(g, offsetX, offsetY); // 正しい引数を渡す
        }


        switch (getGameState()) {
            case START:
                new StartScreen().draw(g, getWidth(), getHeight());
                break;
            case GAME:
                renderer.render(g);
                //System.out.println("エフェクト描画開始: " + getEffects().size()); // デバッグ用
                for (Iterator<AOEEffect> it = getEffects().iterator(); it.hasNext();) {
                    AOEEffect effect = it.next();
                    effect.draw(g, offsetX, offsetY); // 修正: 正しい引数を渡す
                        if (effect.isExpired()) {
                        it.remove();
                    }
                }
                break;
            case SHOW_STATS:
                drawStatsScreen(g);
                break;
            case LEVEL_UP:
                drawLevelUpScreen(g);
                break;
            case GAME_OVER:
                new GameOverScreen().draw(g, getWidth(), getHeight());
                break;
        }
    }
      
    @Override
    public void actionPerformed(ActionEvent e) {
        logic.updateGame(); // ゲームロジックを `GameLogic` に委譲
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        inputHandler.handleKeyPress(e); // 入力処理を `GameInputHandler` に委譲
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> dx = 0;
            case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> dy = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    public Bykin getBykin() {
        return bykin;
    }

    public Stage getStage() {
        return stage;
    }

    public int getCharX() {
        return charX;
    }

    public int getCharY() {
        return charY;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public boolean isShowStatus() {
        return showStatus;
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean isGameStarted) {
        this.isGameStarted = isGameStarted;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<DamageDisplay> getDamageDisplays() {
        return damageDisplays;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public boolean isSkillOnCooldown() {
        return skillOnCooldown;
    }

    public void setSkillOnCooldown(boolean skillOnCooldown) {
        this.skillOnCooldown = skillOnCooldown;
    }

    public long getSkillUsedTime() {
        return skillUsedTime;
    }

    public int getCooldownMax() {
        return cooldownMax;
    }

    public void useSkill() {
        if (skillOnCooldown) {
            System.out.println("スキルはクールダウン中です！");
            return;
        }
    
        SkillType selectedSkill = bykin.getSelectedSkill();
        if (selectedSkill == null) {
            System.out.println("スキルが選択されていません！ 発動不可");
            return;
        }
    
        System.out.println("スキル発動！ 選択されたスキル: " + selectedSkill);
        skillOnCooldown = true;
        skillUsedTime = System.currentTimeMillis();
    
        switch (selectedSkill) {
            case AREA_ATTACK -> useAOEAttack();
            case PIERCING_SHOT -> usePiercingShot();
            case RAPID_FIRE -> useRapidFire();
        }
    
        repaint();
    }
    
    
        
    private void usePiercingShot() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
        // ワールド座標系でのマウス位置を取得
    int offsetX = bykin.getX() - charX;
    int offsetY = bykin.getY() - charY;
    int worldMouseX = mouseX + offsetX;
    int worldMouseY = mouseY + offsetY;
        // 発射角度を計算
    double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);

    // 貫通弾を発射
    projectiles.add(new SkillProjectile(centerX, centerY, angle, "assets/skill_attack.png"));
}
    private void useRapidFire() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
    
        int offsetX = bykin.getX() - charX;
        int offsetY = bykin.getY() - charY;
    
        int worldMouseX = mouseX + offsetX;
        int worldMouseY = mouseY + offsetY;
        double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);
    
        // 通常攻撃の弾
        projectiles.add(new Projectile(centerX, centerY, angle, "assets/attack.png"));
    
        // 追加の2発（少し角度を変える）
        double spreadAngle = Math.toRadians(10); // 10度の角度差
        projectiles.add(new Projectile(centerX, centerY, angle + spreadAngle, "assets/attack.png"));
        projectiles.add(new Projectile(centerX, centerY, angle - spreadAngle, "assets/attack.png"));
    }
    
    
    public void useSpecial() {
        System.out.println("必殺技発動！");
    }

    public void restartGame() {
        bykin = new Bykin(100, 200, this);
        enemies.clear();
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));
        isGameOver = false;
        skillOnCooldown = false;
        dx = 0;
        dy = 0;
        gameState = GameState.GAME;
        repaint();
    }
}
