package newgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import newgame.SkillProjectile;
import newgame.Projectile;
import newgame.Enemy;
import newgame.GameState; // GameState をインポート

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
    private GameState gameState;
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastAttackTime = System.currentTimeMillis(); // 初期値を現在の時間
    private Point mousePos = new Point(0, 0);
    private int mouseX = 0;
    private int mouseY = 0;
    private List<DamageDisplay> damageDisplays = new ArrayList<>();
    
    private GameRenderer renderer;
    private GameLogic logic;
    private GameInputHandler inputHandler;

    public BykinGame() {
        setFocusTraversalKeysEnabled(false);
        inputHandler = new GameInputHandler(this);
        bykin = new Bykin(100, 200, this);
        stage = new Stage(2000, 2000);
        addMouseMotionListener(this);

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

        // 新しいクラスを初期化
        renderer = new GameRenderer(this);
        logic = new GameLogic(this);
        inputHandler = new GameInputHandler(this);
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
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g); // 描画処理を `GameRenderer` に委譲
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
        if (skillOnCooldown) return;

    System.out.println("スキル発動！");
    skillOnCooldown = true;
    skillUsedTime = System.currentTimeMillis();

    switch (bykin.getSelectedSkill()) {
        case AREA_ATTACK -> useAreaAttack();
        case PIERCING_SHOT -> usePiercingShot();
        case RAPID_FIRE -> useRapidFire();
    }

    repaint();
    }
    private void useAreaAttack() {
        int skillRange = 150;
        int centerX = bykin.getX();
        int centerY = bykin.getY();
    
        for (Enemy enemy : enemies) {
            int distance = (int) Math.sqrt(Math.pow(enemy.getX() - centerX, 2) + Math.pow(enemy.getY() - centerY, 2));
            if (distance <= skillRange) {
                int actualDamage = enemy.takeDamage(bykin.getStatus().getAttack() * 2);
                damageDisplays.add(new DamageDisplay(actualDamage, enemy.getX(), enemy.getY()));
    
                if (enemy.getCurrentHp() <= 0) {
                    bykin.getStatus().addExperience(enemy.getLevel() * 20);
                    enemy.startDying();
                }
            }
        }
    }
    private void usePiercingShot() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
        double angle = Math.atan2(mouseY - centerY, mouseX - centerX);
    
        projectiles.add(new SkillProjectile(centerX, centerY, angle, "assets/skill_attack.png"));
    }
    private void useRapidFire() {
        new Thread(() -> {
            long skillDuration = 5000;
            long startTime = System.currentTimeMillis();
    
            while (System.currentTimeMillis() - startTime < skillDuration) {
                int centerX = bykin.getX() + bykin.getWidth() / 2;
                int centerY = bykin.getY() + bykin.getHeight() / 2;
                double angle = Math.atan2(mouseY - centerY, mouseX - centerX);
    
                projectiles.add(new Projectile(centerX, centerY, angle, "assets/attack.png"));
    
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    
            skillOnCooldown = false;
            repaint();
        }).start();
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
