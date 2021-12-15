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
	private int heartNum = 0; //레벨 별로 다른 하트의 개수를 전달하기 위함
	private StartPanel startPanel = new StartPanel();
	private ScorePanel scorePanel = new ScorePanel(heartNum);
	private PlantPanel plantPanel = new PlantPanel(scorePanel);
	private int userLan = 0; //사용자가 선택한 언어
	private int userLevel = 0; //사용자가 선택한 레벨
	private String[] userInfo = { "0", "0", "0" }; //사용자 이름, 언어, 레벨 정보 저장
	private Clip clip; //배경음악 재생을 위함
	private GamePanel gamePanel = new GamePanel(scorePanel, plantPanel, userInfo);

	public GameFrame2() {
		setTitle("타이핑 게임");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setContentPane(startPanel);
		setVisible(true);
	}

	public class StartPanel extends JPanel {
		private Font basicFont = new Font("한컴 말랑말랑", 1, 18); //기본 글씨
		private Font titleFont = new Font("Segoe print", Font.BOLD, 40); //제목 글씨
		private Font btnFont = new Font("한컴 말랑말랑", 1, 15); //버튼 글씨

		private JLabel title = new JLabel("TYPING GAME");
		private JLabel nameLabel = new JLabel("이름");
		private JTextField name = new JTextField(30);
		private JLabel lanLabel = new JLabel("언어");
		private String[] language = { "한글", "영어" };
		private ButtonGroup lanGroup = new ButtonGroup(); //라디오버튼 하나만 선택가능하도록 그룹을 지정하여 관리
		private JRadioButton[] lanSelect = new JRadioButton[2];
		private JLabel levelLabel = new JLabel("레벨");
		private String[] level = { "Lv.1", "Lv.2", "Lv.3", "Lv.4", "Lv.5" };
		private JComboBox<String> levelSelect = new JComboBox<String>(level);

		private JButton gameStartBtn = new JButton("게임시작");
		private JButton rankingBtn = new JButton("랭킹보기");
		private JButton addKorWordBtn = new JButton("한글단어추가");
		private JButton addEngWordBtn = new JButton("영어단어추가");

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
					clip.stop(); //배경음악 종료
					clip.close();
					userInfo[0] = name.getText(); //사용자 이름 배열에 저장
					if (lanSelect[0].isSelected()) //한글을 선택한 경우
						userLan = 0;
					else
						userLan = 1; //영어를 선택한 경우
					userInfo[1] = Integer.toString(userLan); //사용자 언어 정보를 문자 형식으로 바꾸어서 배열에 저장
					userLevel = levelSelect.getSelectedIndex(); //levelSelect 콤보박스에서 선택된 index 저장
					userInfo[2] = Integer.toString(userLevel); //사용자 레벨 정보를 문자 형식으로 바꾸어서 배열에 저장
					//System.out.println("userLan : " + userLan + ", userLevel : " + userLevel);
					if (userLevel < 2)
						heartNum = 2; //하트의 개수 정하여 ScorePanel에 전달
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
					scorePanel.startTimer(); //timer스레드 시작
					gamePanel.startGame(); //gamePanel의 스레드 시작
				}
			});

			rankingBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int first, second; //파일을 생성하거나 찾을 때 이름을 구분하기 위함
					if (lanSelect[0].isSelected())
						first = 0; //한글이 선택된 경우 파일의 첫 글자는 0
					else
						first = 1;
					second = levelSelect.getSelectedIndex(); //파일의 두 번째 글자
					
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(
								new FileInputStream(Integer.toString(first) + Integer.toString(second) + ".txt"),
								"MS949")); //한글 완성형 인코딩 방식 선택
						String line = "";
						
						int num = 0; //파일의 Line 수 저장
						int[] sortTime = new int[1000];
						String[] sortName = new String[1000];
						for (int i = 0; (line = br.readLine()) != null; i++) {
							String sortInfo[] = line.split(" "); //파일의 이름과 남은 시간 분리
							sortTime[i] = Integer.parseInt(sortInfo[1]);
							sortName[i] = sortInfo[0];
							num++;
						}
						br.close();
						
						for (int i = 0; i < num; i++) { //남은 시간이 많은 순으로 정렬하기 위함
							for (int j = i + 1; j < num; j++) {
								if (sortTime[j] > sortTime[i]) {
									int temp = sortTime[i];
									sortTime[i] = sortTime[j];
									sortTime[j] = temp;
									String str = sortName[i]; //남은 시간에 따라 이름도 함께 정렬
									sortName[i] = sortName[j];
									sortName[j] = str;
								}
							}
						}
						
						String result = null; //옵션 패널에 출력할 정보
						for (int i = 0; i < 10; i++) {
							if(sortName[i] == null) break; //파일의 길이가 10줄이 되지 않는 경우
							
							if(i == 0)
								result = String.format(sortName[i] + " " + sortTime[i] + "\n");
							else
								result = String.format(result + sortName[i] + " " + sortTime[i] + "\n"); //두 번째 줄부터는 이전 정보와 합쳐서 저장
						}
						
						int rank = JOptionPane.showConfirmDialog(null, result, "남은 시간 순위", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			addKorWordBtn.addActionListener(new ActionListener() { //한글 단어 추가하기 버튼
				@Override
				public void actionPerformed(ActionEvent e) {
					String addWord = JOptionPane.showInputDialog("추가할 한글 단어를 입력해주세요.");
					try {
						FileWriter file = new FileWriter("한글.txt", true); //파일 읽기
						file.write("\n" + addWord); //다음줄에 추가한 단어 쓰기
						file.flush(); //buffer에 남아있는 내용 파일에 쓰기
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			addEngWordBtn.addActionListener(new ActionListener() { //영어 단어 추가하기 버튼
				@Override
				public void actionPerformed(ActionEvent e) {
					String addWord = JOptionPane.showInputDialog("추가할 영어 단어를 입력해주세요.");
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

		public void closePanel() { //메인 패널 닫기
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

		public void openPanel() { //메인 패널 열기
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
		public void paintComponent(Graphics g) { //메인 화면 배경 그리기
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("pngegg (5).png");
			g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
			setOpaque(false);
		}
	}

	private void splitPane() { //분리된 패널 게임 화면에 부착
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
	
	public void musicPlay(String fileName) { //매인화면 배경음악 재생
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
