package newgame;
import java.awt.*;

public class Stage {
    private int width, height;

    public Stage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 背景を塗りつぶし
    g2d.setColor(new Color(220, 220, 220)); 
    g2d.fillRect(0, 0, width, height);

    // **中心部分をさらに明るい色に**
    int centerX = width / 2;
    int centerY = height / 2;
    int centerSize = Math.min(width, height) / 2;

    g2d.setColor(new Color(245, 245, 245));
    g2d.fillRect(centerX - centerSize / 2, centerY - centerSize / 2, centerSize, centerSize);

    // グリッドを描画
    g2d.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i < width; i += 40) {
    for (int j = 0; j < height; j += 40) {
        int screenX = i - offsetX;
        int screenY = j - offsetY;
        Rectangle gridCell = new Rectangle(screenX, screenY, 40, 40);

        if (gridCell.intersects(new Rectangle(0, 0, width, height))) {
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
