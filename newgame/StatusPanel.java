package newgame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class StatusPanel extends JPanel {
    private Bykin bykin;

    public StatusPanel(Bykin bykin) {
        this.bykin = bykin;
        setPreferredSize(new Dimension(200, 120));
        setOpaque(false); // 背景を透明に設定
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelX = 5;
        int panelY = 5;
        int panelWidth = Math.max(10, getWidth() - 10); // 最低幅を保証
        int panelHeight = Math.max(10, getHeight() - 10); // 最低高さを保証

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // パネル背景
        g2d.setColor(new Color(0, 0, 0, 200)); // 透明度を200に変更
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);        // ステータステキスト
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 16));

        if (bykin != null) {
            Status s = bykin.getStatus();
            g2d.drawString("レベル: " + s.getLevel(), 10, 30);
            g2d.drawString("攻撃: " + s.getAttack(), 10, 50);
            g2d.drawString("防御: " + s.getDefense(), 10, 70);
            g2d.drawString("速度: " + s.getSpeed(), 10, 90);
            g2d.drawString("HP: " + s.getCurrentHp() + "/" + s.getMaxHp(), 10, 110);
        }
    }
        @Override
public Dimension getPreferredSize() {
    int width = 220;
    int height = Math.max(140, (bykin != null) ? 30 + 20 * 5 : 60); // ステータス数に応じた高さ
    return new Dimension(width, height);
}


}
