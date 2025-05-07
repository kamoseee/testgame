public class DamageDisplay {
    private int damage;
    private int x, y;
    private long timestamp;

    public DamageDisplay(int damage, int x, int y) {
        this.damage = damage;
        this.x = x;
        this.y = y;
        this.timestamp = System.currentTimeMillis();
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
