package newgame;
import java.awt.*;

public class Stage {
    private int width, height;

    public Stage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // 背景を塗りつぶし
        g.setColor(new Color(220, 220, 220)); // 外側をライトグレーに変更
        g.fillRect(0, 0, width, height);

        // **中心部分をさらに明るい色に**
        int centerX = width / 2;
        int centerY = height / 2;
        int centerSize = Math.min(width, height) / 2; // 中心部分のサイズ

        g.setColor(new Color(245, 245, 245)); // 白に近いグレー
        g.fillRect(centerX - centerSize / 2, centerY - centerSize / 2, centerSize, centerSize);
        // グリッドを描画
        for (int i = 0; i < width; i += 40) {
            for (int j = 0; j < height; j += 40) {
                int screenX = i - offsetX;
                int screenY = j - offsetY;
                if (screenX + 40 > 0 && screenY + 40 > 0 && screenX < width && screenY < height) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(screenX, screenY, 40, 40);
                }
            }
        }
    }
    

    public int getStageWidth() {
        return width;
    }
    
    public int getStageHeight() {
        return height;
    }
    
    

}
