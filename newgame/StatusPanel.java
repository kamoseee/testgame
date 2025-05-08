package newgame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

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

        int panelX = 10;
        int panelY = 10;
        int panelWidth = 200;
        int panelHeight = 120;

        // パネル背景
        g.setColor(Color.BLACK);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // ステータステキスト
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));

        if (bykin != null) {
            Status s = bykin.getStatus();
            g.drawString("レベル: " + s.getLevel(), 10, 30);
            g.drawString("攻撃: " + s.getAttack(), 10, 50);
            g.drawString("防御: " + s.getDefense(), 10, 70);
            g.drawString("速度: " + s.getSpeed(), 10, 90);
            g.drawString("HP: " + s.getCurrentHp() + "/" + s.getMaxHp(), 10, 110);
        }
        }
}
