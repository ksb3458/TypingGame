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
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private TextSource textSource = new TextSource("words.txt"); // 단어 벡터 생성
	private CreateThread createTh = null;
	private FallingThread fallingTh = null;
	private boolean umbrella = false;
	private int lifeNum = 4;
	private Vector<JLabel> labelVector = new Vector<JLabel>();
	private Vector<JLabel> iconVector = new Vector<JLabel>();
	private Vector<Integer> scoreVector = new Vector<Integer>();

	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;

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
							scorePanel.increase();
						else if (scoreVector.get(i) == 1)
							scorePanel.sun();
						else if (scoreVector.get(i) == 2) {
							umbrella = true;
						} else if (scoreVector.get(i) == 3) {

						} else if (scoreVector.get(i) == 4) {
							scorePanel.trash();
							lifeNum--;
							if(lifeNum == 0) {
								stopGame();
								scorePanel.stopTimer();
								tf.setText("");
								break;
							}
						}
						tf.setText("");
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
		createTh = new CreateThread(labelVector, iconVector, scoreVector);
		fallingTh = new FallingThread(labelVector, iconVector); // 게임 스레드
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
			int xLocation = (int) (Math.random() * 290) + 1;
			ImageIcon oriIcon = null;

			if (effectWord < 50) {
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
			label.setFont(new Font("Tahoma", Font.ITALIC, 20)); // 레이블 글자의 폰트를 설정한다.

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
		private long delay = 200;
		private Vector<JLabel> labelVector = null;
		private Vector<JLabel> iconVector = null;

		public FallingThread(Vector<JLabel> labelVector, Vector<JLabel> iconVector) {
			this.labelVector = labelVector;
			this.iconVector = iconVector;
		}

		public void run() {
			while (true) {
				for (int i = 0; i < labelVector.size(); i++) {
					int x = labelVector.get(i).getX();
					int y = labelVector.get(i).getY() + 5;
					if (y >= gameGroundPanel.getHeight() - labelVector.get(i).getHeight()) {
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
					} else
						sleep(delay);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
}
