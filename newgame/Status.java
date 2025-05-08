package newgame;
public class Status {
    private int level;
    private int attack;
    private int defense;
    private int speed;
    private int currentHp;
    private int maxHp;
    private int experience; // 経験値
    private int experienceToNextLevel; // 次のレベルまでの経験値
    private BykinGame game; // BykinGame の参照を追加

    public Status(int level, int attack, int defense, int speed, int maxHp, BykinGame game) {
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.experience = 0;
        this.experienceToNextLevel = level * 100; // 初期値: レベル × 100
        this.game = game; // BykinGame の参照をセット
    }

    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    // 経験値を追加し、レベルアップ判定
    public void addExperience(int exp) {
        experience += exp;
        while (experience >= experienceToNextLevel) {
            levelUp();
        }
    }

    // レベルアップ処理
    private void levelUp() {
        experience -= experienceToNextLevel;
        level++;
        experienceToNextLevel = level * 100; // 次のレベルまでの経験値を更新
    
        // ステータス上昇
        attack += 2;
        defense += 1;
        speed += 1;
        maxHp += 10;
        currentHp = maxHp; // レベルアップ時にHP全回復
    
        System.out.println("レベルアップ！ 新しいレベル: " + level);
    
        game.setGameState(GameState.SHOW_STATS); // まずステータス変化画面を表示
    game.repaint();

    }

    public void takeDamage(int damage) {
        currentHp -= Math.max(0, damage - defense);
        if (currentHp < 0)
            currentHp = 0;
    }

    public void heal(int amount) {
        currentHp += Math.max(0, amount);
        if (currentHp > maxHp)
            currentHp = maxHp;
    }

    public void setCurrentHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp)); // 0〜maxHpの範囲に収める
    }
}
