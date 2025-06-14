package newgame;
import newgame.GameState; // GameState をインポート

public class GameStateManager {
    private BykinGame game;

    public GameStateManager(BykinGame game) {
        this.game = game;
    }
    private static final Enemy[] INITIAL_ENEMIES = {
        new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30),
        new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40),
        new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60),
        new Enemy(500, 900, "assets/virus01.png", 1, 5, 1, 3, 30),
        new Enemy(200, 500, "assets/virus02.png", 2, 7, 2, 3, 40),
        new Enemy(1000, 1000, "assets/virus03.png", 3, 10, 3, 3, 60)
    };
    public void restartGame() {
        game.setBykin(new Bykin(100, 200, game));
        game.getEnemies().clear();
        for (Enemy enemy : INITIAL_ENEMIES) {
            game.getEnemies().add(enemy);
        }
        game.setGameOver(false);
        game.setSkillOnCooldown(false);
        game.setDx(0);
        game.setDy(0);
        game.setGameState(GameState.GAME);
        game.repaint();
    }

    public void setGameState(GameState newState) {
        game.setGameState(newState);
        game.repaint();
    }

    public void handleGameOver() {
        game.setGameOver(true);
        setGameState(GameState.GAME_OVER);
    }

    public void handleLevelUp() {
        setGameState(GameState.LEVEL_UP);
    }

    public void startGame() {
        game.setGameStarted(true);
        setGameState(GameState.GAME);
    }
}
