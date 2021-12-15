import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class GameFrame2 extends JFrame {
	private int heartNum = 0; //���� ���� �ٸ� ��Ʈ�� ������ �����ϱ� ����
	private StartPanel startPanel = new StartPanel();
	private ScorePanel scorePanel = new ScorePanel(heartNum);
	private PlantPanel plantPanel = new PlantPanel(scorePanel);
	private int userLan = 0; //����ڰ� ������ ���
	private int userLevel = 0; //����ڰ� ������ ����
	private String[] userInfo = { "0", "0", "0" }; //����� �̸�, ���, ���� ���� ����
	private Clip clip; //������� ����� ����
	private GamePanel gamePanel = new GamePanel(scorePanel, plantPanel, userInfo);

	public GameFrame2() {
		setTitle("Ÿ���� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setContentPane(startPanel);
		setVisible(true);
	}

	public class StartPanel extends JPanel {
		private Font basicFont = new Font("���� ��������", 1, 18); //�⺻ �۾�
		private Font titleFont = new Font("Segoe print", Font.BOLD, 40); //���� �۾�
		private Font btnFont = new Font("���� ��������", 1, 15); //��ư �۾�

		private JLabel title = new JLabel("TYPING GAME");
		private JLabel nameLabel = new JLabel("�̸�");
		private JTextField name = new JTextField(30);
		private JLabel lanLabel = new JLabel("���");
		private String[] language = { "�ѱ�", "����" };
		private ButtonGroup lanGroup = new ButtonGroup(); //������ư �ϳ��� ���ð����ϵ��� �׷��� �����Ͽ� ����
		private JRadioButton[] lanSelect = new JRadioButton[2];
		private JLabel levelLabel = new JLabel("����");
		private String[] level = { "Lv.1", "Lv.2", "Lv.3", "Lv.4", "Lv.5" };
		private JComboBox<String> levelSelect = new JComboBox<String>(level);

		private JButton gameStartBtn = new JButton("���ӽ���");
		private JButton rankingBtn = new JButton("��ŷ����");
		private JButton addKorWordBtn = new JButton("�ѱ۴ܾ��߰�");
		private JButton addEngWordBtn = new JButton("����ܾ��߰�");

		public StartPanel() {
			this.setLayout(null);

			title.setFont(titleFont);
			title.setBounds(150, 85, 500, 50);
			add(title);

			nameLabel.setFont(basicFont);
			nameLabel.setForeground(Color.LIGHT_GRAY);
			name.setFont(basicFont);
			nameLabel.setBounds(50, 190, 200, 30);
			name.setBounds(120, 190, 200, 30);
			add(nameLabel);
			add(name);

			lanLabel.setFont(basicFont);
			lanLabel.setForeground(Color.LIGHT_GRAY);
			lanLabel.setBounds(50, 255, 200, 30);
			for (int i = 0; i < lanSelect.length; i++) {
				lanSelect[i] = new JRadioButton(language[i]);
				lanSelect[i].setFont(basicFont);
				lanSelect[i].setOpaque(false);
				lanGroup.add(lanSelect[i]);
			}
			lanSelect[0].setBounds(120, 255, 100, 30);
			lanSelect[1].setBounds(220, 255, 100, 30);
			lanSelect[0].setSelected(true);
			add(lanLabel);
			add(lanSelect[0]);
			add(lanSelect[1]);

			levelLabel.setFont(basicFont);
			levelLabel.setForeground(Color.LIGHT_GRAY);
			levelSelect.setFont(basicFont);
			levelLabel.setBounds(50, 320, 200, 30);
			levelSelect.setBounds(120, 320, 200, 30);
			add(levelLabel);
			add(levelSelect);

			gameStartBtn.setFont(basicFont);
			gameStartBtn.setBounds(50, 450, 500, 40);
			add(gameStartBtn);

			rankingBtn.setFont(btnFont);
			rankingBtn.setBounds(410, 250, 135, 30);
			//rankingBtn.setBorderPainted(false);
			addKorWordBtn.setFont(btnFont);
			addKorWordBtn.setBounds(410, 290, 135, 30);
			//addKorWordBtn.setBorderPainted(false);
			addEngWordBtn.setFont(btnFont);
			addEngWordBtn.setBounds(410, 320, 135, 30);
			//addEngWordBtn.setBorderPainted(false);
			add(rankingBtn);
			add(addKorWordBtn);
			add(addEngWordBtn);
			musicPlay("intro.wav");

			gameStartBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					clip.stop(); //������� ����
					clip.close();
					userInfo[0] = name.getText(); //����� �̸� �迭�� ����
					if (lanSelect[0].isSelected()) //�ѱ��� ������ ���
						userLan = 0;
					else
						userLan = 1; //��� ������ ���
					userInfo[1] = Integer.toString(userLan); //����� ��� ������ ���� �������� �ٲپ �迭�� ����
					userLevel = levelSelect.getSelectedIndex(); //levelSelect �޺��ڽ����� ���õ� index ����
					userInfo[2] = Integer.toString(userLevel); //����� ���� ������ ���� �������� �ٲپ �迭�� ����
					//System.out.println("userLan : " + userLan + ", userLevel : " + userLevel);
					if (userLevel < 2)
						heartNum = 2; //��Ʈ�� ���� ���Ͽ� ScorePanel�� ����
					else
						heartNum = 4;
					scorePanel = new ScorePanel(heartNum);
					plantPanel = new PlantPanel(scorePanel);
					gamePanel = new GamePanel(scorePanel, plantPanel, userInfo);
					closePanel();
					getContentPane().setLayout(new BorderLayout());
					splitPane();
					setResizable(false);
					repaint();
					scorePanel.startTimer(); //timer������ ����
					gamePanel.startGame(); //gamePanel�� ������ ����
				}
			});

			rankingBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int first, second; //������ �����ϰų� ã�� �� �̸��� �����ϱ� ����
					if (lanSelect[0].isSelected())
						first = 0; //�ѱ��� ���õ� ��� ������ ù ���ڴ� 0
					else
						first = 1;
					second = levelSelect.getSelectedIndex(); //������ �� ��° ����
					
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(
								new FileInputStream(Integer.toString(first) + Integer.toString(second) + ".txt"),
								"MS949")); //�ѱ� �ϼ��� ���ڵ� ��� ����
						String line = "";
						
						int num = 0; //������ Line �� ����
						int[] sortTime = new int[1000];
						String[] sortName = new String[1000];
						for (int i = 0; (line = br.readLine()) != null; i++) {
							String sortInfo[] = line.split(" "); //������ �̸��� ���� �ð� �и�
							sortTime[i] = Integer.parseInt(sortInfo[1]);
							sortName[i] = sortInfo[0];
							num++;
						}
						br.close();
						
						for (int i = 0; i < num; i++) { //���� �ð��� ���� ������ �����ϱ� ����
							for (int j = i + 1; j < num; j++) {
								if (sortTime[j] > sortTime[i]) {
									int temp = sortTime[i];
									sortTime[i] = sortTime[j];
									sortTime[j] = temp;
									String str = sortName[i]; //���� �ð��� ���� �̸��� �Բ� ����
									sortName[i] = sortName[j];
									sortName[j] = str;
								}
							}
						}
						
						String result = null; //�ɼ� �гο� ����� ����
						for (int i = 0; i < 10; i++) {
							if(sortName[i] == null) break; //������ ���̰� 10���� ���� �ʴ� ���
							
							if(i == 0)
								result = String.format(sortName[i] + " " + sortTime[i] + "\n");
							else
								result = String.format(result + sortName[i] + " " + sortTime[i] + "\n"); //�� ��° �ٺ��ʹ� ���� ������ ���ļ� ����
						}
						
						int rank = JOptionPane.showConfirmDialog(null, result, "���� �ð� ����", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			addKorWordBtn.addActionListener(new ActionListener() { //�ѱ� �ܾ� �߰��ϱ� ��ư
				@Override
				public void actionPerformed(ActionEvent e) {
					String addWord = JOptionPane.showInputDialog("�߰��� �ѱ� �ܾ �Է����ּ���.");
					try {
						FileWriter file = new FileWriter("�ѱ�.txt", true); //���� �б�
						file.write("\n" + addWord); //�����ٿ� �߰��� �ܾ� ����
						file.flush(); //buffer�� �����ִ� ���� ���Ͽ� ����
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			addEngWordBtn.addActionListener(new ActionListener() { //���� �ܾ� �߰��ϱ� ��ư
				@Override
				public void actionPerformed(ActionEvent e) {
					String addWord = JOptionPane.showInputDialog("�߰��� ���� �ܾ �Է����ּ���.");
					try {
						FileWriter file = new FileWriter("words.txt", true);
						file.write("\n" + addWord);
						file.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

		}

		public void closePanel() { //���� �г� �ݱ�
			title.setVisible(false);
			nameLabel.setVisible(false);
			name.setVisible(false);
			lanLabel.setVisible(false);
			lanSelect[0].setVisible(false);
			lanSelect[1].setVisible(false);
			levelLabel.setVisible(false);
			levelSelect.setVisible(false);
			gameStartBtn.setVisible(false);
			rankingBtn.setVisible(false);
			addKorWordBtn.setVisible(false);
			addEngWordBtn.setVisible(false);
		}

		public void openPanel() { //���� �г� ����
			title.setVisible(true);
			nameLabel.setVisible(true);
			name.setVisible(true);
			lanLabel.setVisible(true);
			lanSelect[0].setVisible(true);
			lanSelect[1].setVisible(true);
			levelLabel.setVisible(true);
			levelSelect.setVisible(true);
			gameStartBtn.setVisible(true);
			rankingBtn.setVisible(true);
			addKorWordBtn.setVisible(true);
			addEngWordBtn.setVisible(true);
		}

		@Override
		public void paintComponent(Graphics g) { //���� ȭ�� ��� �׸���
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("pngegg (5).png");
			g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
			setOpaque(false);
		}
	}

	private void splitPane() { //�и��� �г� ���� ȭ�鿡 ����
		JSplitPane hPane = new JSplitPane();
		getContentPane().add(hPane, BorderLayout.CENTER);
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		hPane.setDividerLocation(400);
		hPane.setEnabled(false);
		hPane.setLeftComponent(gamePanel);

		JSplitPane pPane = new JSplitPane();
		pPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pPane.setDividerLocation(350);
		pPane.setTopComponent(scorePanel);
		pPane.setBottomComponent(plantPanel);
		hPane.setRightComponent(pPane);
	}
	
	public void musicPlay(String fileName) { //����ȭ�� ������� ���
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName)); 
			clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();
		} catch (Exception ex) {
		}
	}
}
