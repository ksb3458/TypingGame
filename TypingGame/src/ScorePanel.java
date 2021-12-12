import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScorePanel extends JPanel {
	private int score = 0;
	private int lifeNum = 4;
	private JLabel textLabel = new JLabel("Á¡¼ö");
	private JLabel scoreLabel = new JLabel(Integer.toString(score));
	private ImageIcon heartImage = new ImageIcon("heart.png");
	private JLabel timerLabel = new JLabel();
	private TimerNum timerNum = new TimerNum(120, timerLabel);
	private JLabel[] life = new JLabel[5];
	private boolean checkSun = false;
	
	public ScorePanel() {
		setLayout(null);	
		Image img = heartImage.getImage();
		Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(img2);
		for(int i = 0; i < life.length; i++) {
			life[i] = new JLabel();
			life[i].setIcon(icon);
			life[i].setBounds(10 + i*30, 10, 30, 30);
			add(life[i]);
		}
		
		timerLabel.setOpaque(true);
		timerLabel.setBounds(20, 50, 80, 30);
		timerLabel.setForeground(Color.BLUE);
		timerLabel.setText("2 : 00");
		timerLabel.setFont(new Font("¸¼Àº °íµñ", 1, 20));
		add(timerLabel);
		
		textLabel.setSize(50, 20);
		textLabel.setLocation(15, 100);
		textLabel.setFont(new Font("¸¼Àº °íµñ", 1, 15));
		add(textLabel);
		
		scoreLabel.setSize(50, 20);
		scoreLabel.setLocation(65, 100);
		scoreLabel.setFont(new Font("¸¼Àº °íµñ", 1, 15));
		add(scoreLabel);
	}
	
	public void increase() {
		score += 10;
		scoreLabel.setText(Integer.toString(score));
	}
	
	public void increase20() {
		score += 20;
		scoreLabel.setText(Integer.toString(score));
	}
	
	public void trash() {
		life[lifeNum].setVisible(false);
		lifeNum--;
	}
	
	public void sun() {
		checkSun = true;
	}
	
	public void startTimer() {
		timerNum.start();
	}
	
	class TimerNum extends Thread {
		private int second;
		private String showSecond;
		private JLabel timerLabel;

		public TimerNum(int second, JLabel timerLabel) {
			this.second = second;
			this.timerLabel = timerLabel;
		}

		@Override
		public void run() {
			while (true) {
				if(second % 60 < 10)
					showSecond = "0" + Integer.toString(second % 60);
				else
					showSecond = Integer.toString(second % 60);
				timerLabel.setText(Integer.toString(second / 60) + " : " + showSecond);
				second--;
				
				if(checkSun == true) {
					second += 10;
					checkSun = false;
				}
				
				if(second < 0) {
					return;
				}
				
				try {
					Thread.sleep(1000);	// 1ÃÊ
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
