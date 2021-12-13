import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ScorePanel extends JPanel {
	private int score = 0;
	private int lifeNum = 0;
	private JLabel textLabel = new JLabel("점수");
	private JLabel scoreLabel = new JLabel(Integer.toString(score));
	private ImageIcon heartImage = new ImageIcon("heart.png");
	private JLabel timerLabel = new JLabel();
	private TimerNum timerNum = new TimerNum(120, timerLabel);
	private JLabel[] life = null;
	private boolean checkSun = false;
	private GameFrame2 gameFrame = null;
	
	public ScorePanel(int heartNum) {
		lifeNum = heartNum;
		life = new JLabel[lifeNum + 1];
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
		timerLabel.setFont(new Font("맑은 고딕", 1, 20));
		add(timerLabel);
		
		textLabel.setSize(50, 20);
		textLabel.setLocation(15, 100);
		textLabel.setFont(new Font("맑은 고딕", 1, 15));
		add(textLabel);
		
		scoreLabel.setSize(50, 20);
		scoreLabel.setLocation(65, 100);
		scoreLabel.setFont(new Font("맑은 고딕", 1, 15));
		add(scoreLabel);
	}
	
	public void increase(int addScore) {
		score += addScore;
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
	
	public void stopTimer() {
		timerNum.interrupt();
	}
	
	public void winGame() {
		int result = JOptionPane.showConfirmDialog(this, "성공하였습니다.\n다시 도전하시겠습니까?", "게임 성공", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) {
			System.exit(0);
		}
		else if(result == JOptionPane.YES_OPTION) {
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public void loseGame() {
		int result = JOptionPane.showConfirmDialog(this, "실패하였습니다.\n다시 도전하시겠습니까?", "게임 실패", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) {
			System.exit(0);
		}
		else if(result == JOptionPane.YES_OPTION) {
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public int getScore() {
		return score;
	}
	
	public String getTime() {
		String[] time = timerLabel.getText().split(" ");
		int second = (Integer.parseInt(time[0]) * 60 ) + Integer.parseInt(time[2]);
		return Integer.toString(second);
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
				if(checkSun == true) {
					second += 10;
					checkSun = false;
				}
				
				if(second % 60 < 10)
					showSecond = "0" + Integer.toString(second % 60);
				else
					showSecond = Integer.toString(second % 60);
				timerLabel.setText(Integer.toString(second / 60) + " : " + showSecond);
				second--;
							
				if(second < 0) {
					loseGame();
					return;
				}
				
				try {
					sleep(1000);
				} catch (Exception e) {
					return;
				}
			}
		}
	}
}
