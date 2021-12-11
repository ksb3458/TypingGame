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
	private TextSource textSource = new TextSource("words.txt"); // �ܾ� ���� ����
	private FallingThread thread = null; 
	private String fallingWord = null;
	private JLabel label = new JLabel(); // �������� �ܾ� 
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
				
				if(label.getText().equals(inWord)) { // ���߱� ����
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
		// �ܾ� �� �� ����		
		ImageIcon oriIcon = new ImageIcon("rain.png");
		Image img = oriIcon.getImage();
		Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img2);
		
		fallingWord = textSource.get();
		label.setText(fallingWord);
		label.setSize(200, 30); // ���̺� ũ��
		label.setLocation(xLocation, 0); // ���̺� ��ġ
		label.setForeground(Color.MAGENTA); //���̺��� ���� ���� �����Ѵ�.				
		label.setFont(new Font("Tahoma", Font.ITALIC, 20)); // ���̺� ������ ��Ʈ�� �����Ѵ�.
		
		labelIcon.setIcon(icon);
		labelIcon.setSize(30, 30); // ���̺� ũ��
		labelIcon.setLocation(xLocation - 30, 0); // ���̺� ��ġ

		thread = new FallingThread(this, label, labelIcon); // ���� ������
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
			return; // �����尡 ���� 
		thread.interrupt(); // ������ ���� ����
		thread = null;
		gameOn = false;
	}
	
	public void stopSelfAndNewGame() { // �����尡 �ٴڿ� ��Ƽ� ������ �� ȣ��
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
		private JLabel label; //���� ���ڸ� ����ϴ� ���̺�
		private JLabel labelIcon;
		private long delay = 200; // ���� �ð��� �ʱ갪 = 200
		private boolean falling = false; // �������� �ִ���. �ʱ갪 = false
		
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
					int y = label.getY() + 5; //5�ȼ� �� �Ʒ��� �̵�
					if(y >= panel.getHeight()-label.getHeight()) {
						falling = false;
						label.setText("");
						panel.stopSelfAndNewGame();
						break; // ������ ����
					}
					labelIcon.setLocation(label.getX() - 30, y);
					label.setLocation(label.getX(), y);
					GamePanel.this.repaint();
				} catch (InterruptedException e) {
					falling = false;
					return; // ������ ����
				}
			}
		}	
	}
}
