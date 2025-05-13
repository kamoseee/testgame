package newgame;
public class Status {
    private int level;
    private int attack;
    private int defense;
    private int speed = 5; // 初期速度を5に設定
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
        while (experience >= experienceToNextLevel && level < 100) { // 上限を設定
            levelUp();
        }

    }

    // レベルアップ処理
    private void levelUp() {
        experience -= experienceToNextLevel;
        level++;
        experienceToNextLevel = (int) (100 * Math.pow(1.2, level)); // 1.2倍ずつ増加
    
        // ステータス上昇
        attack += 2;
        defense += 1;
        if (level % 5 == 0) { // レベルが5の倍数のときだけ速度を上昇
            speed += 1;
        }
        maxHp += 10;
        currentHp = maxHp; // レベルアップ時にHP全回復
    
        System.out.println("レベルアップ！ 新しいレベル: " + level);
    
        game.setGameState(GameState.SHOW_STATS); // まずステータス変化画面を表示
    game.repaint();

    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense); // 最低1ダメージは受ける
        currentHp -= actualDamage;
        if (currentHp < 0)
            currentHp = 0;
    }


    public void heal(int amount) {
    int actualHeal = Math.min(amount, maxHp - currentHp); // 最大HPを超えないように制限
    currentHp += actualHeal;
}


    public void setCurrentHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp)); // 0〜maxHpの範囲に収める
    }
}
