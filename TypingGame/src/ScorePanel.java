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
	private int score = 0; //������� ���� ǥ��
	private int lifeNum = 0; //������� ���� ��Ʈ ���� ����
	private JLabel textLabel = new JLabel("����");
	private JLabel scoreLabel = new JLabel(Integer.toString(score)); //���� ������ ǥ�õǴ� JLabel
	private ImageIcon heartImage = new ImageIcon("heart.png");
	private JLabel timerLabel = new JLabel(); //���� �ð��� ǥ�õǴ� JLabel
	private TimerNum timerNum = new TimerNum(120, timerLabel); //Ÿ�̸� ������
	private JLabel[] life = null;
	private boolean checkSun = false; //�ð��� 10�� ������Ű�� ���� üũ ����Ʈ
	private GameFrame2 gameFrame = null;
	
	public ScorePanel(int heartNum) {
		lifeNum = heartNum; //��Ʈ�� �� ������� ������ ����Ǵ� ��츦 ����
		life = new JLabel[lifeNum + 1]; //��µǴ� ��Ʈ�� ������ lifeNum + 1��
		setLayout(null);	
		Image img = heartImage.getImage();
		Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(img2);
		for(int i = 0; i < life.length; i++) { //�迭�� �гο� ��Ʈ�׸� �߰�
			life[i] = new JLabel();
			life[i].setIcon(icon);
			life[i].setBounds(10 + i*30, 10, 30, 30);
			add(life[i]);
		}
		
		timerLabel.setOpaque(true);
		timerLabel.setBounds(20, 50, 80, 30);
		timerLabel.setForeground(Color.BLUE);
		timerLabel.setText("2 : 00");
		timerLabel.setFont(new Font("���� ���", 1, 20));
		add(timerLabel);
		
		textLabel.setSize(50, 20);
		textLabel.setLocation(15, 100);
		textLabel.setFont(new Font("���� ���", 1, 15));
		add(textLabel);
		
		scoreLabel.setSize(50, 20);
		scoreLabel.setLocation(65, 100);
		scoreLabel.setFont(new Font("���� ���", 1, 15));
		add(scoreLabel);
	}
	
	public void increase(int addScore) { //GamePanel���� ���� �߰� ����
		score += addScore;
		scoreLabel.setText(Integer.toString(score));
	}
	
	public void trash() { //���� ���� �� ������� �Բ� �ִ� �ܾ ���߾��� ��� - ��Ʈ 1�� ����
		life[lifeNum].setVisible(false); //���� �� ��Ʈ �Ⱥ��̰�
		lifeNum--; //���� ��Ʈ ���� ����
	}
	
	public void sun() { //���� ���� �� �¾�� �Բ� �ִ� �ܾ ���߾��� ��� - Ÿ�̸� 10�� ����
		checkSun = true;
	}
	
	public void startTimer() { //Ÿ�̸� ������ ����. GameFrame2���� "start game" ��ư ������ ȣ��
		timerNum.start();
	}
	
	public void stopTimer() { //Ÿ�̸� ������ ����
		timerNum.interrupt();
	}
	
	public void winGame() { //���� �ð� �ȿ� 300���� �������� ���
		int result = JOptionPane.showConfirmDialog(this, "�����Ͽ����ϴ�.\n�ٽ� �����Ͻðڽ��ϱ�?", "���� ����", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) { //�絵���� ������ �ʰų� panel�� ���� ���
			System.exit(0); //�ý��� ����
		}
		else if(result == JOptionPane.YES_OPTION) { //�絵���� ���� ���
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public void loseGame() { //�ð� �ʰ� Ȥ�� ��Ʈ �׸��� ��� ����� ���
		int result = JOptionPane.showConfirmDialog(this, "�����Ͽ����ϴ�.\n�ٽ� �����Ͻðڽ��ϱ�?", "���� ����", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) { //�絵���� ������ �ʰų� panel�� ���� ���
			System.exit(0);
		}
		else if(result == JOptionPane.YES_OPTION) { //�絵���� ���� ���
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public int getScore() { //GamePanel���� ���� ������ Ȯ���� �� ȣ��
		return score;
	}
	
	public String getTime() { //GamePanel���� ���� �ð��� Ȯ���� �� ȣ��
		String[] time = timerLabel.getText().split(" ");
		int second = (Integer.parseInt(time[0]) * 60 ) + Integer.parseInt(time[2]); //"��:��"�� ���� �� �Ǿ��ִ� �ð��� �ʷ� �ٲپ ����
		return Integer.toString(second);
	}
	
	class TimerNum extends Thread { //Ÿ�̸� ������
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
				if(checkSun == true) { //�ؿ� �Բ� �ִ� �׸��� ���߾��� ���
					second += 10;
					checkSun = false;
				}
				
				if(second % 60 < 10) //�� : �� ���Ŀ��� �ʰ� �� ���ڶ�� �տ� 0 �߰�
					showSecond = "0" + Integer.toString(second % 60);
				else
					showSecond = Integer.toString(second % 60);
				timerLabel.setText(Integer.toString(second / 60) + " : " + showSecond); //�ʸ� �� : �� �������� �����Ͽ� ���
				second--;
							
				if(second < 0) { //�ð� �ʰ��� ���
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
