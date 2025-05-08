package newgame; // パッケージを統一

public enum GameState {
    START,      // ゲーム開始画面
    GAME,       // ゲームプレイ中
    SHOW_STATS, // ステータス変化画面
    LEVEL_UP,   // レベルアップ画面
    GAME_OVER   // ゲームオーバー画面
}
