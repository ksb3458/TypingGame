import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlantPanel extends JPanel {
	
	private ScorePanel scorePanel = null;
	private int score = 0; //현재 점수
	private ImageIcon plant1 = new ImageIcon("1.png");
	private ImageIcon plant2 = new ImageIcon("2.png");
	private ImageIcon plant3 = new ImageIcon("3.png");
	private ImageIcon plant4 = new ImageIcon("4.png");
	private ImageIcon plant5 = new ImageIcon("5.png"); //점수에 따라 바뀌는 이미지들 저장
	private ImageIcon plant = plant1; //패널에 출력할 이미지 저장
	
	public void paintComponent(Graphics g) { // 이미지 그리기
		super.paintComponent(g);
		g.drawImage(plant.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		setOpaque(false);
	}		
	
	public PlantPanel(ScorePanel scorePanel) {
		this.scorePanel = scorePanel;
		this.setLayout(new FlowLayout());
		GrowPlant growPlant = new GrowPlant();
		growPlant.start(); //이미지를 변경해서 그려줄 스레드 시작
	}
	
	class GrowPlant extends Thread { //이미지를 변경하는 스레드
		public GrowPlant() {
		}

		@Override
		public void run() {
			while (true) {
				score = scorePanel.getScore(); //현재 점수 알아오기
				if(score < 60) //점수에 따라 이미지 교체
					plant = plant1;
				else if(score >= 60 && score < 120)
					plant = plant2;
				else if(score >= 120 && score < 180)
					plant = plant3;
				else if(score >= 180 && score < 240)
					plant = plant4;
				else
					plant = plant5;
				
				repaint(); //다시 그리기
				try {
					sleep(1000);
				} catch (Exception e) {
					return;
				}
			}
		}
	}
}