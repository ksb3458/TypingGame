import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlantPanel extends JPanel {
	private ImageIcon plant1 = new ImageIcon("1.png");
	private ImageIcon plant2 = new ImageIcon("2.png");
	private ImageIcon plant3 = new ImageIcon("3.png");
	private ImageIcon plant4 = new ImageIcon("4.png");
	private ImageIcon plant5 = new ImageIcon("5.png");
	
	public void paintComponent(Graphics g) { // 배경이미지 설정
		super.paintComponent(g);
		g.drawImage(plant1.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		setOpaque(false);
	}		
	
	public PlantPanel() {
		this.setLayout(new FlowLayout());
		//this.setBackground(Color.YELLOW);
	}
}