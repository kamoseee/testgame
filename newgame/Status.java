public class Status {
    private int level;
    private int attack;
    private int defense;
    private int speed;
    private int currentHp;
    private int maxHp;

    public Status(int level, int attack, int defense, int speed, int maxHp) {
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
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

    public void takeDamage(int damage) {
        currentHp -= Math.max(0, damage - defense);
        if (currentHp < 0)
            currentHp = 0;
    }

    public void heal(int amount) {
        currentHp += amount;
        if (currentHp > maxHp)
            currentHp = maxHp;
    }

    public void setCurrentHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp)); // 0〜maxHpの範囲に収める
    }

}
