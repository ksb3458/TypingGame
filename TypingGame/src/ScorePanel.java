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
	private int score = 0; //사용자의 점수 표시
	private int lifeNum = 0; //사용자의 남은 하트 개수 저장
	private JLabel textLabel = new JLabel("점수");
	private JLabel scoreLabel = new JLabel(Integer.toString(score)); //현재 점수가 표시되는 JLabel
	private ImageIcon heartImage = new ImageIcon("heart.png");
	private JLabel timerLabel = new JLabel(); //남은 시간이 표시되는 JLabel
	private TimerNum timerNum = new TimerNum(120, timerLabel); //타이머 스레드
	private JLabel[] life = null;
	private boolean checkSun = false; //시간을 10초 증가시키기 위한 체크 포인트
	private GameFrame2 gameFrame = null;
	
	public ScorePanel(int heartNum) {
		lifeNum = heartNum; //하트가 다 사라져서 게임이 종료되는 경우를 위함
		life = new JLabel[lifeNum + 1]; //출력되는 하트의 개수는 lifeNum + 1개
		setLayout(null);	
		Image img = heartImage.getImage();
		Image img2 = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(img2);
		for(int i = 0; i < life.length; i++) { //배열로 패널에 하트그림 추가
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
	
	public void increase(int addScore) { //GamePanel에서 점수 추가 위함
		score += addScore;
		scoreLabel.setText(Integer.toString(score));
	}
	
	public void trash() { //게임 진행 중 쓰레기와 함께 있는 단어를 맞추었을 경우 - 하트 1개 감소
		life[lifeNum].setVisible(false); //제일 끝 하트 안보이게
		lifeNum--; //남은 하트 개수 감소
	}
	
	public void sun() { //게임 진행 중 태양과 함께 있는 단어를 맞추었을 경우 - 타이머 10초 증가
		checkSun = true;
	}
	
	public void startTimer() { //타이머 스레드 시작. GameFrame2에서 "start game" 버튼 눌릴때 호출
		timerNum.start();
	}
	
	public void stopTimer() { //타이머 스레드 종료
		timerNum.interrupt();
	}
	
	public void winGame() { //제한 시간 안에 300점에 도달했을 경우
		int result = JOptionPane.showConfirmDialog(this, "성공하였습니다.\n다시 도전하시겠습니까?", "게임 성공", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) { //재도전을 원하지 않거나 panel을 닫은 경우
			System.exit(0); //시스템 종료
		}
		else if(result == JOptionPane.YES_OPTION) { //재도전을 원한 경우
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public void loseGame() { //시간 초과 혹은 하트 그림이 모두 사라진 경우
		int result = JOptionPane.showConfirmDialog(this, "실패하였습니다.\n다시 도전하시겠습니까?", "게임 실패", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION) { //재도전을 원하지 않거나 panel을 닫은 경우
			System.exit(0);
		}
		else if(result == JOptionPane.YES_OPTION) { //재도전을 원한 경우
			GameFrame2 gameFrame = new GameFrame2();
			setVisible(false);
		}
	}
	
	public int getScore() { //GamePanel에서 현재 점수를 확인할 때 호출
		return score;
	}
	
	public String getTime() { //GamePanel에서 현재 시간을 확인할 때 호출
		String[] time = timerLabel.getText().split(" ");
		int second = (Integer.parseInt(time[0]) * 60 ) + Integer.parseInt(time[2]); //"분:초"의 형태 로 되어있는 시간을 초로 바꾸어서 전달
		return Integer.toString(second);
	}
	
	class TimerNum extends Thread { //타이머 스레드
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
				if(checkSun == true) { //해와 함께 있는 그림을 맞추었을 경우
					second += 10;
					checkSun = false;
				}
				
				if(second % 60 < 10) //분 : 초 형식에서 초가 한 글자라면 앞에 0 추가
					showSecond = "0" + Integer.toString(second % 60);
				else
					showSecond = Integer.toString(second % 60);
				timerLabel.setText(Integer.toString(second / 60) + " : " + showSecond); //초를 분 : 초 형식으로 변경하여 출력
				second--;
							
				if(second < 0) { //시간 초과의 경우
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
