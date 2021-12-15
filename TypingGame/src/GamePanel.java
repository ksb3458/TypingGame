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
	private JTextField input = new JTextField(30); //사용자가 단어를 입력하는 textfield
	private ScorePanel scorePanel = null;
	private PlantPanel plantPanel = null;
	private int userLan, userLevel; //사용자의 언어와 레벨 정보
	private String userName; //사용자 이름
	private String[] userInfo = new String[3]; //이름, 언어, 레벨 정보를 저장할 배열
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private TextSource textSource = null; // 단어 벡터 생성
	private CreateThread createTh = null; //단어 생성 스레드
	private FallingThread fallingTh = null; //단어가 내려오는 스레드
	private boolean umbrella = false; //우산과 함께 있는 단어를 입력했는지 체크
	private int lifeNum; //남은 하트의 수 관리
	private int fallingDelay; //단어가 떨어지는 속도 관리
	private boolean dcLife = false; //단어가 바닥에 떨어졌을 때 하트를 감소시킬 지 확인
	private Vector<JLabel> labelVector = new Vector<JLabel>(); //생성된 단어 벡터
	private Vector<JLabel> iconVector = new Vector<JLabel>(); //단어와 함께 움직이는 그림 벡터
	private Vector<Integer> scoreVector = new Vector<Integer>(); //단어를 맞추었을 때 생길 효과를 알아오기 위한 벡터
	private Clip rightClip, wrongClip; //단어를 올바르게 입력했을 때 재생되는 소리, 다르게 입력했을 때 재생되는 소리

	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel, String[] userInfo) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;
		this.userInfo = userInfo;
		userName = userInfo[0];
		userLan = Integer.parseInt(userInfo[1]);
		userLevel = Integer.parseInt(userInfo[2]); //배열에 사용자 이름, 언어, 레벨 저장

		setLayout(new BorderLayout());
		add(gameGroundPanel, BorderLayout.CENTER);
		add(new InputPanel(), BorderLayout.SOUTH);

		input.addActionListener(new ActionListener() { //사용자가 단어를 입력하여 제출한 경우
			public void actionPerformed(ActionEvent e) {
				JTextField tf = (JTextField) (e.getSource());
				String inWord = tf.getText(); //textfield에 입력된 글자
				for (int i = 0; i < labelVector.size(); i++) {
					if (labelVector.get(i).getText().equals(inWord)) { // 맞추기 성공
						musicPlay("correct.wav"); //정답 소리 재생
						if (scoreVector.get(i) == 0) //비 그림과 함께 있는 단어를 맞추었을 경우
							scorePanel.increase(10); //10점 증가
						else if (scoreVector.get(i) == 1) { //태양 그림과 함께 있는 단어를 맞추었을 경우
							scorePanel.sun(); //10초 증가
							scorePanel.increase(10);
						}
						else if (scoreVector.get(i) == 2) { //우산 그림과 함께 있는 단어를 맞추었을 경우
							umbrella = true; //10초간 화면 정지
							scorePanel.increase(10);
						} else if (scoreVector.get(i) == 3) { //무지개 그림과 함께 있는 단어를 맞추었을 경우
							int count = labelVector.size(); //현재 화면의 있는 단어 개수 저장
							stopGame(); //스레드 종료, 벡터값 전부 삭제
							scorePanel.increase(10*count); //출력된 단어의 수 * 10점 증가
							startGame(); //스레드 재시작
							tf.setText(""); //입력된 값 지우기
							break;
						} else if (scoreVector.get(i) == 4) { //쓰레기 그림과 함께 있는 단어를 맞추었을 경우
							controlLife(); //하트 하나 감소
						}
						tf.setText(""); //입력된 값 지우기
						if(scorePanel.getScore() >= 300) { //점수가 300점 이상인 경우
							try {
								FileWriter file = new FileWriter(Integer.toString(userLan) + Integer.toString(userLevel) + ".txt", true);
								file.write(userName + " " +scorePanel.getTime()+"\n");
								file.flush(); //파일에 "사용자이름 남은시간" 형태로 저장
								scorePanel.winGame(); //scorePanel에서 승리했을 경우의 패널 생성
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
						scoreVector.remove(i); //백터에 저장된 값과 gameGroundPanel에 표시된 라벨 제거
						gameGroundPanel.repaint();
					}
				}
			}
		});
	}

	public void startGame() { //게임 시작
		if(userLan == 0) { //사용자 언어에 따라 textSource 다르게 생성
			textSource = new TextSource("한글.txt");
		}
		else
			textSource = new TextSource("words.txt");
		
		if(userLevel == 0 || userLevel == 2) { //단어 떨어지는 속도 레벨별로 다르게 설정
			fallingDelay = 200;
		}
		
		else if (userLevel == 1 || userLevel == 3) {
			fallingDelay = 100;
		}
		
		if(userLevel == 0 || userLevel == 1) { //단어가 바닥에 닿았을 때 하트 감소 여부와 하트 개수 레벨별로 다르게 설정
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
		fallingTh.start(); //스레드 시작
	}

	public void stopGame() { //게임 종료
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
	
	public void controlLife() { //쓰레기 그림과 함께 있는 단어를 입력한 경우
		scorePanel.trash();
		lifeNum--;
		if(lifeNum == 0) { //남아있는 하트 그림이 없는 경우
			stopGame();
			scorePanel.stopTimer();
			scorePanel.loseGame();
			return;
		}
	}
	
	public void musicPlay(String fileName) { //효과음 재생
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

	public class CreateThread extends Thread { //단어 생성 스레드
		private long delay = 3000; //단어가 생성되는 지연 시간
		private Vector<JLabel> labelVector = null; //생성된 단어를 저장하는 벡터
		private Vector<JLabel> iconVector = null; //생성된 이미지를 저장하는 벡터
		private Vector<Integer> scoreVector = null; //생성된 이미지를 숫자로 구분하여 저장하는 벡터
		private int effectScore = 0; //생성된 이미지를 숫자로 구분

		void createWord() {
			int effectWord = (int) (Math.random() * 100); //랜덤값에 따라 다른 이미지 생성
			int xLocation = (int) (Math.random() * 260) + 31; //단어가 생성되는 위치
			ImageIcon oriIcon = null; //이미지 저장

			if (effectWord < 10) { //이미지가 생성되는 확률을 다르게 하기 위함
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
			Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); //이미지의 크기 변경
			ImageIcon icon = new ImageIcon(img2);

			JLabel label = new JLabel("");
			String fallingWord = textSource.get(); //textSource에서 랜덤으로 단어 가져오기
			label.setText(fallingWord);
			label.setSize(200, 30); // 레이블 크기
			label.setLocation(xLocation, 0); // 레이블 위치
			label.setForeground(Color.MAGENTA); // 레이블의 글자 색을 설정한다.
			label.setFont(new Font("맑은고딕", Font.ITALIC, 20)); // 레이블 글자의 폰트를 설정한다.

			JLabel labelIcon = new JLabel();
			labelIcon.setIcon(icon);
			labelIcon.setSize(30, 30);
			labelIcon.setLocation(xLocation - 30, 0); //아이콘 레이블 위치

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
				createWord(); //단어와 이미지 생성하는 함수 호출
				try {
					if (umbrella == true) {
						sleep(10000); //10초 동안 멈추기
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

	public class FallingThread extends Thread { //단어가 떨어지는 스레드
		private int delay; //지연 시간
		private boolean dcLife; //단어가 땅에 닿으면 하트 감소할 지 체크
		private Vector<JLabel> labelVector = null; //단어 벡터
		private Vector<JLabel> iconVector = null; //아이콘 벡터

		public FallingThread(Vector<JLabel> labelVector, Vector<JLabel> iconVector, int delay, boolean dcLife) {
			this.labelVector = labelVector;
			this.iconVector = iconVector;
			this.delay = delay;
			this.dcLife = dcLife;
		}

		public void run() {
			while (true) {
				for (int i = 0; i < labelVector.size(); i++) { //단어 벡터의 사이즈 만큼 실행
					int x = labelVector.get(i).getX();
					int y = labelVector.get(i).getY() + 5; //단어 벡터의 y값 5 증가하여 저장
					if (y >= gameGroundPanel.getHeight() - labelVector.get(i).getHeight()) { //단어가 땅에 닿은 경우
						if(dcLife == true && scoreVector.get(i) != 4) //쓰레기 아이콘이 아닌지도 함께 체크
							controlLife();
						gameGroundPanel.remove(labelVector.get(i));
						labelVector.remove(i);
						gameGroundPanel.remove(iconVector.get(i));
						iconVector.remove(i);
						scoreVector.remove(i);
						continue;
					}
					iconVector.get(i).setLocation(x - 30, y); //아이콘 위치 지정
					labelVector.get(i).setLocation(x, y); //단어 위치 지정
					gameGroundPanel.repaint();
				}
				try {
					if (umbrella == true) { //10초간 정지
						sleep(10000);
						umbrella = false;
					} else {
						if(delay == 0) { //레벨 5의 경우를 위함
							delay = (int)(Math.random()*100) + 100; //레벨 1 ~ 2사이의 속도 랜덤 생성
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
