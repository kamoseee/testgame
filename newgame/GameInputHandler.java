package newgame;
import newgame.GameState; // GameState をインポート
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;


public class GameInputHandler implements KeyListener {
    private BykinGame game;
    private boolean movingUp, movingDown, movingLeft, movingRight;

    public GameInputHandler(BykinGame game) {
        this.game = game;
    }

    public void handleKeyPress(KeyEvent e) {
        if (game.getGameState() == GameState.LEVEL_UP) {
            handleSkillSelection(e.getKeyCode()); // スキル選択処理を追加
            return;
        }

        if (game.getGameState() == GameState.SHOW_STATS && e.getKeyCode() == KeyEvent.VK_SPACE) {
            game.setGameState(GameState.LEVEL_UP); // スペースキーでスキル選択画面へ移行
            game.repaint();
            return;
        }

        if (game.isShowStatus()) {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                game.setShowStatus(false);
                game.repaint();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_E -> game.useSkill();
            case KeyEvent.VK_Q -> game.useSpecial();
            case KeyEvent.VK_TAB -> {
                game.setShowStatus(true);
                game.repaint();
            }
            case KeyEvent.VK_SPACE -> {
                if (game.isGameOver()) {
                    game.restartGame();
                    game.setGameState(GameState.START);
                    game.setGameState(GameState.GAME); // すぐにゲームを開始
                } else if (!game.isGameStarted()) {
                    game.setGameState(GameState.GAME);
                    game.setGameStarted(true);
                    game.repaint();
                }
            }
            case KeyEvent.VK_O -> {
                game.getBykin().getStatus().addExperience(game.getBykin().getStatus().getExperienceToNextLevel());
                game.repaint();
            }
        }
    }
    @Override
public void keyPressed(KeyEvent e) {
    updateMovementFlags(e.getKeyCode(), true);
}

@Override
public void keyReleased(KeyEvent e) {
    updateMovementFlags(e.getKeyCode(), false);
}

private void updateMovementFlags(int keyCode, boolean isPressed) {
    switch (keyCode) {
        case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = isPressed;
        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = isPressed;
        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = isPressed;
        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = isPressed;
    }
}
    
    



    @Override
    public void keyTyped(KeyEvent e) {}

    public void updateMovement() {
        if (game.isPaused()) return; // 🔥 一時停止中なら何もしない
        int speed = game.getBykin().getStatus().getSpeed();
        int dx = (movingLeft ? -1 : 0) + (movingRight ? 1 : 0);
        int dy = (movingUp ? -1 : 0) + (movingDown ? 1 : 0);

        if (dx != 0 || dy != 0) {
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            dx = (int) (dx / magnitude * speed);
            dy = (int) (dy / magnitude * speed);
            game.getBykin().move(dx, dy);
        }
}

    private static final Map<Integer, SkillType> skillMap = Map.of(
        KeyEvent.VK_1, SkillType.AREA_ATTACK,
        KeyEvent.VK_2, SkillType.PIERCING_SHOT,
        KeyEvent.VK_3, SkillType.RAPID_FIRE
    );

    private void handleSkillSelection(int keyCode) {
        SkillType selectedSkill = skillMap.get(keyCode);
        if (selectedSkill != null) {
            game.getBykin().setSelectedSkill(selectedSkill);
            game.setGameState(GameState.GAME);
            game.repaint();
        }
    }
}
