package newgame;

import java.awt.*;

public class AOEEffect {
    private int centerX, centerY, radius;
    private long startTime;
    private long duration;

    public AOEEffect(int centerX, int centerY, int radius, long duration) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
    int drawX = centerX - offsetX;
    int drawY = centerY - offsetY;

    //System.out.println("エフェクト描画: " + drawX + ", " + drawY + ", 半径: " + radius); // デバッグ用
    g.setColor(new Color(255, 0, 0, 100)); // 半透明の赤色
    g.fillOval(drawX - radius, drawY - radius, radius * 2, radius * 2); // 中心座標を考慮
}

}
