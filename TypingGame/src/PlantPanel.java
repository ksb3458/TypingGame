import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlantPanel extends JPanel {
	
	private ScorePanel scorePanel = null;
	private int score = 0;
	private ImageIcon plant1 = new ImageIcon("1.png");
	private ImageIcon plant2 = new ImageIcon("2.png");
	private ImageIcon plant3 = new ImageIcon("3.png");
	private ImageIcon plant4 = new ImageIcon("4.png");
	private ImageIcon plant5 = new ImageIcon("5.png");
	private ImageIcon plant = plant1;
	
	public void paintComponent(Graphics g) { // 배경이미지 설정
		super.paintComponent(g);
		g.drawImage(plant.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		setOpaque(false);
	}		
	
	public PlantPanel(ScorePanel scorePanel) {
		this.scorePanel = scorePanel;
		this.setLayout(new FlowLayout());
		GrowPlant growPlant = new GrowPlant();
		growPlant.start();
	}
	
	class GrowPlant extends Thread {
		public GrowPlant() {
		}

		@Override
		public void run() {
			while (true) {
				score = scorePanel.getScore();
				if(score < 60)
					plant = plant1;
				else if(score >= 60 && score < 120)
					plant = plant2;
				else if(score >= 120 && score < 180)
					plant = plant3;
				else if(score >= 180 && score < 240)
					plant = plant4;
				else
					plant = plant5;
				
				repaint();
				try {
					sleep(1000);
				} catch (Exception e) {
					return;
				}
			}
		}
	}
}