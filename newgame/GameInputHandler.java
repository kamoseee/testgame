package newgame;
import newgame.GameState; // GameState をインポート

import java.awt.event.KeyEvent;

public class GameInputHandler {
    private BykinGame game;

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
            case KeyEvent.VK_LEFT -> game.setDx(-1);
            case KeyEvent.VK_RIGHT -> game.setDx(1);
            case KeyEvent.VK_UP -> game.setDy(-1);
            case KeyEvent.VK_DOWN -> game.setDy(1);
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
    
    private void handleSkillSelection(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1 -> game.getBykin().setSelectedSkill(SkillType.AREA_ATTACK);
            case KeyEvent.VK_2 -> game.getBykin().setSelectedSkill(SkillType.PIERCING_SHOT);
            case KeyEvent.VK_3 -> game.getBykin().setSelectedSkill(SkillType.RAPID_FIRE);
        }

        game.setGameState(GameState.GAME);
        game.repaint();
    }
}
