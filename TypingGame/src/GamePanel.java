import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel {
	private JTextField input = new JTextField(30);
	private ScorePanel scorePanel = null;
	private PlantPanel plantPanel = null;
	private TextSource textSource = new TextSource("words.txt"); // 단어 벡터 생성
	private FallingThread thread = null; 
	private String fallingWord = null;
	private JLabel label = new JLabel(); // 떨어지는 단어 
	private JLabel labelIcon = new JLabel();
	private ImageIcon icon = null;
	private boolean gameOn = false;
	
	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;

		setLayout(new BorderLayout());
		add(new GameGroundPanel(), BorderLayout.CENTER);
		add(new InputPanel(), BorderLayout.SOUTH);
		
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField tf = (JTextField)(e.getSource());
				String inWord = tf.getText();
				/*if(!isGameOn())
					return;
					
				boolean match = matchWord(inWord);*/
				
				if(label.getText().equals(inWord)) { // 맞추기 성공
					scorePanel.increase();
					tf.setText("");
					thread.interrupt();
					startGame();
				}
			}
		});
		
		startGame();
	}
	
	public void startGame() {
		int xLocation = (int)(Math.random()*290) + 1;
		// 단어 한 개 선택		
		ImageIcon oriIcon = new ImageIcon("rain.png");
		Image img = oriIcon.getImage();
		Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img2);
		
		fallingWord = textSource.get();
		label.setText(fallingWord);
		label.setSize(200, 30); // 레이블 크기
		label.setLocation(xLocation, 0); // 레이블 위치
		label.setForeground(Color.MAGENTA); //레이블의 글자 색을 설정한다.				
		label.setFont(new Font("Tahoma", Font.ITALIC, 20)); // 레이블 글자의 폰트를 설정한다.
		
		labelIcon.setIcon(icon);
		labelIcon.setSize(30, 30); // 레이블 크기
		labelIcon.setLocation(xLocation - 30, 0); // 레이블 위치

		thread = new FallingThread(this, label, labelIcon); // 게임 스레드
		thread.start();
	}
	
	class GameGroundPanel extends JPanel {
		public GameGroundPanel() {
			setLayout(null);
			add(label);
			add(labelIcon);
		}
	}
	
	class InputPanel extends JPanel {
		public InputPanel() {
			setLayout(new FlowLayout());
			this.setBackground(Color.CYAN);
			add(input);
		}
	}
	
	public boolean isGameOn() {
		return gameOn;
	}
	
	public void stopGame() {
		if(thread == null)
			return; // 스레드가 없음 
		thread.interrupt(); // 스레드 강제 종료
		thread = null;
		gameOn = false;
	}
	
	public void stopSelfAndNewGame() { // 스레드가 바닥에 닿아서 실패할 때 호출
		startGame();			
	}
	
	public boolean matchWord(String text) {
		if(fallingWord != null && fallingWord.equals(text))
			return true;
		else
			return false;
	}
	
	class FallingThread extends Thread {
		private GamePanel panel;
		private JLabel label; //게임 숫자를 출력하는 레이블
		private JLabel labelIcon;
		private long delay = 200; // 지연 시간의 초깃값 = 200
		private boolean falling = false; // 떨이지고 있는지. 초깃값 = false
		
		public FallingThread(GamePanel panel, JLabel label, JLabel labelIcon) {
			this.panel = panel;
			this.label = label;
			this.labelIcon = labelIcon;
		}
		
		public boolean isFalling() {
			return falling; 
		}	
		
		@Override
		public void run() {
			falling = true;
			while(true) {
				try {
					sleep(delay);
					int y = label.getY() + 5; //5픽셀 씩 아래로 이동
					if(y >= panel.getHeight()-label.getHeight()) {
						falling = false;
						label.setText("");
						panel.stopSelfAndNewGame();
						break; // 스레드 종료
					}
					labelIcon.setLocation(label.getX() - 30, y);
					label.setLocation(label.getX(), y);
					GamePanel.this.repaint();
				} catch (InterruptedException e) {
					falling = false;
					return; // 스레드 종료
				}
			}
		}	
	}
}
