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
import java.io.FileWriter;
import java.io.IOException;
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
	private int userLan, userLevel;
	private String userName;
	private String[] userInfo = new String[3];
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private TextSource textSource = null; // 단어 벡터 생성
	private CreateThread createTh = null;
	private FallingThread fallingTh = null;
	private boolean umbrella = false;
	private int lifeNum;
	private int fallingDelay;
	private boolean dcLife = false;
	private Vector<JLabel> labelVector = new Vector<JLabel>();
	private Vector<JLabel> iconVector = new Vector<JLabel>();
	private Vector<Integer> scoreVector = new Vector<Integer>();

	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel, String[] userInfo) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;
		this.userInfo = userInfo;
		userName = userInfo[0];
		userLan = Integer.parseInt(userInfo[1]);
		userLevel = Integer.parseInt(userInfo[2]);

		setLayout(new BorderLayout());
		add(gameGroundPanel, BorderLayout.CENTER);
		add(new InputPanel(), BorderLayout.SOUTH);

		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField tf = (JTextField) (e.getSource());
				String inWord = tf.getText();
				for (int i = 0; i < labelVector.size(); i++) {
					if (labelVector.get(i).getText().equals(inWord)) { // 맞추기 성공
						if (scoreVector.get(i) == 0)
							scorePanel.increase(10);
						else if (scoreVector.get(i) == 1) {
							scorePanel.sun();
							scorePanel.increase(10);
						}
						else if (scoreVector.get(i) == 2) {
							umbrella = true;
							scorePanel.increase(10);
						} else if (scoreVector.get(i) == 3) {
							int count = labelVector.size();
							stopGame();
							scorePanel.increase(10*count);
							startGame();
							tf.setText("");
							break;
						} else if (scoreVector.get(i) == 4) {
							controlLife();
						}
						tf.setText("");
						if(scorePanel.getScore() >= 300) {
							try {
								FileWriter file = new FileWriter(Integer.toString(userLan) + Integer.toString(userLevel) + ".txt", true);
								file.write(userName + " " +scorePanel.getTime()+"\n");
								file.flush();
								scorePanel.winGame();
							}
							catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						gameGroundPanel.remove(labelVector.get(i));
						gameGroundPanel.remove(iconVector.get(i));
						labelVector.remove(i);
						iconVector.remove(i);
						scoreVector.remove(i);
						gameGroundPanel.repaint();
					}
				}
			}
		});
	}

	public void startGame() {
		if(userLan == 0) {
			textSource = new TextSource("한글.txt");
		}
		else
			textSource = new TextSource("words.txt");
		
		if(userLevel == 0 || userLevel == 2) {
			fallingDelay = 200;
		}
		
		else if (userLevel == 1 || userLevel == 3) {
			fallingDelay = 100;
		}
		
		if(userLevel == 0 || userLevel == 1) {
			dcLife = false;
			lifeNum = 3;
		}
		else {
			dcLife = true;
			lifeNum = 5;
		}
		
		createTh = new CreateThread(labelVector, iconVector, scoreVector);
		fallingTh = new FallingThread(labelVector, iconVector, fallingDelay, dcLife);
		createTh.start();
		fallingTh.start();
	}

	public void stopGame() {
		for (int i = 0; i < labelVector.size(); i++) {
			gameGroundPanel.remove(labelVector.get(i));
			gameGroundPanel.remove(iconVector.get(i));		
			gameGroundPanel.repaint();
		}
		labelVector.clear();
		iconVector.clear();
		scoreVector.clear();
		createTh.interrupt();
		fallingTh.interrupt();
	}
	
	public void controlLife() {
		scorePanel.trash();
		lifeNum--;
		if(lifeNum == 0) {
			stopGame();
			scorePanel.stopTimer();
			//tf.setText("");
			scorePanel.loseGame();
			return;
		}
	}

	class GameGroundPanel extends JPanel {
		public GameGroundPanel() {
			setLayout(null);
		}
	}

	class InputPanel extends JPanel {
		public InputPanel() {
			setLayout(new FlowLayout());
			this.setBackground(Color.CYAN);
			add(input);
		}
	}

	public class CreateThread extends Thread {
		private long delay = 3000;
		private Vector<JLabel> labelVector = null;
		private Vector<JLabel> iconVector = null;
		private Vector<Integer> scoreVector = null;
		private int effectScore = 0;

		void createWord() {
			int effectWord = (int) (Math.random() * 100);
			int colorWord = (int) (Math.random() * 100);
			int xLocation = (int) (Math.random() * 260) + 31;
			ImageIcon oriIcon = null;

			if (effectWord < 10) {
				effectScore = 1;
				oriIcon = new ImageIcon("sun.png");
			} else if (effectWord >= 10 && effectWord < 20) {
				effectScore = 2;
				oriIcon = new ImageIcon("umbrella.png");
			} else if (effectWord >= 20 && effectWord < 30) {
				effectScore = 4;
				oriIcon = new ImageIcon("trash.png");
			} else if (effectWord >= 30 && effectWord < 35) {
				effectScore = 3;
				oriIcon = new ImageIcon("rainbow.png");
			} else {
				effectScore = 0;
				oriIcon = new ImageIcon("rain.png");
			}

			Image img = oriIcon.getImage();
			Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(img2);

			JLabel label = new JLabel("");
			String fallingWord = textSource.get();
			label.setText(fallingWord);
			label.setSize(200, 30); // 레이블 크기
			label.setLocation(xLocation, 0); // 레이블 위치
			label.setForeground(Color.MAGENTA); // 레이블의 글자 색을 설정한다.
			label.setFont(new Font("맑은고딕", Font.ITALIC, 20)); // 레이블 글자의 폰트를 설정한다.

			JLabel labelIcon = new JLabel();
			labelIcon.setIcon(icon);
			labelIcon.setSize(30, 30);
			labelIcon.setLocation(xLocation - 30, 0);

			labelVector.addElement(label);
			iconVector.addElement(labelIcon);
			scoreVector.addElement(effectScore);
			gameGroundPanel.add(label);
			gameGroundPanel.add(labelIcon);
		}

		public CreateThread(Vector<JLabel> labelVector, Vector<JLabel> iconVector, Vector<Integer> scoreVector) {
			this.labelVector = labelVector;
			this.iconVector = iconVector;
			this.scoreVector = scoreVector;
		}

		public void run() {
			while (true) {
				createWord();
				gameGroundPanel.repaint();
				try {
					if (umbrella == true) {
						sleep(10000);
						umbrella = false;
					} else
						sleep(delay);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public class FallingThread extends Thread {
		private int delay;
		private boolean dcLife;
		private Vector<JLabel> labelVector = null;
		private Vector<JLabel> iconVector = null;

		public FallingThread(Vector<JLabel> labelVector, Vector<JLabel> iconVector, int delay, boolean dcLife) {
			this.labelVector = labelVector;
			this.iconVector = iconVector;
			this.delay = delay;
			this.dcLife = dcLife;
		}

		public void run() {
			while (true) {
				for (int i = 0; i < labelVector.size(); i++) {
					int x = labelVector.get(i).getX();
					int y = labelVector.get(i).getY() + 5;
					if (y >= gameGroundPanel.getHeight() - labelVector.get(i).getHeight()) {
						if(dcLife == true && scoreVector.get(i) != 4)
							controlLife();
						gameGroundPanel.remove(labelVector.get(i));
						labelVector.remove(i);
						gameGroundPanel.remove(iconVector.get(i));
						iconVector.remove(i);
						scoreVector.remove(i);
						continue;
					}
					iconVector.get(i).setLocation(x - 30, y);
					labelVector.get(i).setLocation(x, y);
					gameGroundPanel.repaint();
				}
				try {
					if (umbrella == true) {
						sleep(10000);
						umbrella = false;
					} else {
						if(delay == 0) {
							delay = (int)(Math.random()*100) + 100;
							sleep(delay);
							delay = 0;
						}
						else sleep(delay);		
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
}
