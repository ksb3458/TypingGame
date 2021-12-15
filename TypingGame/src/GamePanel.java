import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel {
	private JTextField input = new JTextField(30); //����ڰ� �ܾ �Է��ϴ� textfield
	private ScorePanel scorePanel = null;
	private PlantPanel plantPanel = null;
	private int userLan, userLevel; //������� ���� ���� ����
	private String userName; //����� �̸�
	private String[] userInfo = new String[3]; //�̸�, ���, ���� ������ ������ �迭
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private TextSource textSource = null; // �ܾ� ���� ����
	private CreateThread createTh = null; //�ܾ� ���� ������
	private FallingThread fallingTh = null; //�ܾ �������� ������
	private boolean umbrella = false; //���� �Բ� �ִ� �ܾ �Է��ߴ��� üũ
	private int lifeNum; //���� ��Ʈ�� �� ����
	private int fallingDelay; //�ܾ �������� �ӵ� ����
	private boolean dcLife = false; //�ܾ �ٴڿ� �������� �� ��Ʈ�� ���ҽ�ų �� Ȯ��
	private Vector<JLabel> labelVector = new Vector<JLabel>(); //������ �ܾ� ����
	private Vector<JLabel> iconVector = new Vector<JLabel>(); //�ܾ�� �Բ� �����̴� �׸� ����
	private Vector<Integer> scoreVector = new Vector<Integer>(); //�ܾ ���߾��� �� ���� ȿ���� �˾ƿ��� ���� ����
	private Clip rightClip, wrongClip; //�ܾ �ùٸ��� �Է����� �� ����Ǵ� �Ҹ�, �ٸ��� �Է����� �� ����Ǵ� �Ҹ�

	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel, String[] userInfo) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;
		this.userInfo = userInfo;
		userName = userInfo[0];
		userLan = Integer.parseInt(userInfo[1]);
		userLevel = Integer.parseInt(userInfo[2]); //�迭�� ����� �̸�, ���, ���� ����

		setLayout(new BorderLayout());
		add(gameGroundPanel, BorderLayout.CENTER);
		add(new InputPanel(), BorderLayout.SOUTH);

		input.addActionListener(new ActionListener() { //����ڰ� �ܾ �Է��Ͽ� ������ ���
			public void actionPerformed(ActionEvent e) {
				JTextField tf = (JTextField) (e.getSource());
				String inWord = tf.getText(); //textfield�� �Էµ� ����
				for (int i = 0; i < labelVector.size(); i++) {
					if (labelVector.get(i).getText().equals(inWord)) { // ���߱� ����
						musicPlay("correct.wav"); //���� �Ҹ� ���
						if (scoreVector.get(i) == 0) //�� �׸��� �Բ� �ִ� �ܾ ���߾��� ���
							scorePanel.increase(10); //10�� ����
						else if (scoreVector.get(i) == 1) { //�¾� �׸��� �Բ� �ִ� �ܾ ���߾��� ���
							scorePanel.sun(); //10�� ����
							scorePanel.increase(10);
						}
						else if (scoreVector.get(i) == 2) { //��� �׸��� �Բ� �ִ� �ܾ ���߾��� ���
							umbrella = true; //10�ʰ� ȭ�� ����
							scorePanel.increase(10);
						} else if (scoreVector.get(i) == 3) { //������ �׸��� �Բ� �ִ� �ܾ ���߾��� ���
							int count = labelVector.size(); //���� ȭ���� �ִ� �ܾ� ���� ����
							stopGame(); //������ ����, ���Ͱ� ���� ����
							scorePanel.increase(10*count); //��µ� �ܾ��� �� * 10�� ����
							startGame(); //������ �����
							tf.setText(""); //�Էµ� �� �����
							break;
						} else if (scoreVector.get(i) == 4) { //������ �׸��� �Բ� �ִ� �ܾ ���߾��� ���
							controlLife(); //��Ʈ �ϳ� ����
						}
						tf.setText(""); //�Էµ� �� �����
						if(scorePanel.getScore() >= 300) { //������ 300�� �̻��� ���
							try {
								FileWriter file = new FileWriter(Integer.toString(userLan) + Integer.toString(userLevel) + ".txt", true);
								file.write(userName + " " +scorePanel.getTime()+"\n");
								file.flush(); //���Ͽ� "������̸� �����ð�" ���·� ����
								scorePanel.winGame(); //scorePanel���� �¸����� ����� �г� ����
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
						scoreVector.remove(i); //���Ϳ� ����� ���� gameGroundPanel�� ǥ�õ� �� ����
						gameGroundPanel.repaint();
					}
				}
			}
		});
	}

	public void startGame() { //���� ����
		if(userLan == 0) { //����� �� ���� textSource �ٸ��� ����
			textSource = new TextSource("�ѱ�.txt");
		}
		else
			textSource = new TextSource("words.txt");
		
		if(userLevel == 0 || userLevel == 2) { //�ܾ� �������� �ӵ� �������� �ٸ��� ����
			fallingDelay = 200;
		}
		
		else if (userLevel == 1 || userLevel == 3) {
			fallingDelay = 100;
		}
		
		if(userLevel == 0 || userLevel == 1) { //�ܾ �ٴڿ� ����� �� ��Ʈ ���� ���ο� ��Ʈ ���� �������� �ٸ��� ����
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
		fallingTh.start(); //������ ����
	}

	public void stopGame() { //���� ����
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
	
	public void controlLife() { //������ �׸��� �Բ� �ִ� �ܾ �Է��� ���
		scorePanel.trash();
		lifeNum--;
		if(lifeNum == 0) { //�����ִ� ��Ʈ �׸��� ���� ���
			stopGame();
			scorePanel.stopTimer();
			scorePanel.loseGame();
			return;
		}
	}
	
	public void musicPlay(String fileName) { //ȿ���� ���
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
			Clip clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();
		} catch (Exception ex) {
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

	public class CreateThread extends Thread { //�ܾ� ���� ������
		private long delay = 3000; //�ܾ �����Ǵ� ���� �ð�
		private Vector<JLabel> labelVector = null; //������ �ܾ �����ϴ� ����
		private Vector<JLabel> iconVector = null; //������ �̹����� �����ϴ� ����
		private Vector<Integer> scoreVector = null; //������ �̹����� ���ڷ� �����Ͽ� �����ϴ� ����
		private int effectScore = 0; //������ �̹����� ���ڷ� ����

		void createWord() {
			int effectWord = (int) (Math.random() * 100); //�������� ���� �ٸ� �̹��� ����
			int xLocation = (int) (Math.random() * 260) + 31; //�ܾ �����Ǵ� ��ġ
			ImageIcon oriIcon = null; //�̹��� ����

			if (effectWord < 10) { //�̹����� �����Ǵ� Ȯ���� �ٸ��� �ϱ� ����
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
			Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); //�̹����� ũ�� ����
			ImageIcon icon = new ImageIcon(img2);

			JLabel label = new JLabel("");
			String fallingWord = textSource.get(); //textSource���� �������� �ܾ� ��������
			label.setText(fallingWord);
			label.setSize(200, 30); // ���̺� ũ��
			label.setLocation(xLocation, 0); // ���̺� ��ġ
			label.setForeground(Color.MAGENTA); // ���̺��� ���� ���� �����Ѵ�.
			label.setFont(new Font("�������", Font.ITALIC, 20)); // ���̺� ������ ��Ʈ�� �����Ѵ�.

			JLabel labelIcon = new JLabel();
			labelIcon.setIcon(icon);
			labelIcon.setSize(30, 30);
			labelIcon.setLocation(xLocation - 30, 0); //������ ���̺� ��ġ

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
				createWord(); //�ܾ�� �̹��� �����ϴ� �Լ� ȣ��
				try {
					if (umbrella == true) {
						sleep(10000); //10�� ���� ���߱�
						umbrella = false;
					} else
						sleep(delay);
				} catch (InterruptedException e) {
					return;
				}
				gameGroundPanel.repaint();
			}
		}
	}

	public class FallingThread extends Thread { //�ܾ �������� ������
		private int delay; //���� �ð�
		private boolean dcLife; //�ܾ ���� ������ ��Ʈ ������ �� üũ
		private Vector<JLabel> labelVector = null; //�ܾ� ����
		private Vector<JLabel> iconVector = null; //������ ����

		public FallingThread(Vector<JLabel> labelVector, Vector<JLabel> iconVector, int delay, boolean dcLife) {
			this.labelVector = labelVector;
			this.iconVector = iconVector;
			this.delay = delay;
			this.dcLife = dcLife;
		}

		public void run() {
			while (true) {
				for (int i = 0; i < labelVector.size(); i++) { //�ܾ� ������ ������ ��ŭ ����
					int x = labelVector.get(i).getX();
					int y = labelVector.get(i).getY() + 5; //�ܾ� ������ y�� 5 �����Ͽ� ����
					if (y >= gameGroundPanel.getHeight() - labelVector.get(i).getHeight()) { //�ܾ ���� ���� ���
						if(dcLife == true && scoreVector.get(i) != 4) //������ �������� �ƴ����� �Բ� üũ
							controlLife();
						gameGroundPanel.remove(labelVector.get(i));
						labelVector.remove(i);
						gameGroundPanel.remove(iconVector.get(i));
						iconVector.remove(i);
						scoreVector.remove(i);
						continue;
					}
					iconVector.get(i).setLocation(x - 30, y); //������ ��ġ ����
					labelVector.get(i).setLocation(x, y); //�ܾ� ��ġ ����
					gameGroundPanel.repaint();
				}
				try {
					if (umbrella == true) { //10�ʰ� ����
						sleep(10000);
						umbrella = false;
					} else {
						if(delay == 0) { //���� 5�� ��츦 ����
							delay = (int)(Math.random()*100) + 100; //���� 1 ~ 2������ �ӵ� ���� ����
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
