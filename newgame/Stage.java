import java.awt.*;

public class Stage {
    private int width, height;

    public Stage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // 背景を描画（仮のグリッド）
        for (int i = 0; i < width; i += 40) {
            for (int j = 0; j < height; j += 40) {
                int screenX = i - offsetX;
                int screenY = j - offsetY;
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(screenX, screenY, 40, 40); // グリッド描画
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
