public class DamageDisplay {
    private int damage;
    private int x, y;
    private long timestamp;
    //private static final int DURATION = 1000; // 表示時間：1秒

    public DamageDisplay(int damage, int x, int y) {
        this.damage = Math.max(1, damage); // ダメージが異常に大きくならないように修正
        this.x = x;  // 敵の位置から渡される x 座標
        this.y = y;  // 敵の位置から渡される y 座標
        this.timestamp = System.currentTimeMillis();
    }

    public int getDamage() {
        return damage;
    }

    // X座標は変わらず、Y座標は時間とともに上に移動
    public int getX() {
        return x;
    }

    public int getY() {
        long elapsed = System.currentTimeMillis() - timestamp;
        return y - (int)(elapsed / 15); // 1000ms で 約67px 浮く（より自然）
    }
    

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > DURATION+200; // 200ms の余裕を持たせる
    }

    private static final int DURATION = 1500; // 表示時間を1.5秒に延長

public int getAlpha() {
    long elapsed = System.currentTimeMillis() - timestamp;
    int alpha = 255 - (int)(255 * elapsed / DURATION);
    return Math.max(0, alpha); // 最低0
}

}
