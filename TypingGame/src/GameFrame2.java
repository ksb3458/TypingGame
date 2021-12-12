import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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

	private StartPanel startPanel = new StartPanel();
	private ScorePanel scorePanel = new ScorePanel();
	private PlantPanel plantPanel = new PlantPanel();
	private GamePanel gamePanel = new GamePanel(scorePanel, plantPanel);

	public GameFrame2() {
		setTitle("타이핑 게임");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setContentPane(startPanel);
		setVisible(true);
	}

	public class StartPanel extends JPanel {
		private Font basicFont = new Font("돋움", 1, 20);
		private Font titleFont = new Font("돋움", 1, 40);
		private Font btnFont = new Font("돋움", 1, 15);

		private JLabel title = new JLabel("TYPING GAME");
		private JLabel nameLabel = new JLabel("이름");
		private JTextField name = new JTextField(30);
		private JLabel lanLabel = new JLabel("언어");
		private String[] language = { "한글", "영어" };
		private ButtonGroup lanGroup = new ButtonGroup();
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
			title.setBounds(150, 90, 500, 50);
			add(title);
			
			nameLabel.setFont(basicFont);
			name.setFont(basicFont);	
			nameLabel.setBounds(50, 190, 200, 30);
			name.setBounds(120, 190, 200, 30);
			add(nameLabel);
			add(name);

			lanLabel.setFont(basicFont);
			lanLabel.setBounds(50, 250, 200, 30);			
			for (int i = 0; i < lanSelect.length; i++) {
				lanSelect[i] = new JRadioButton(language[i]);
				lanSelect[i].setFont(basicFont);
				lanSelect[i].setOpaque(false);
				lanGroup.add(lanSelect[i]);
			}
			lanSelect[0].setBounds(120, 250, 200, 30);
			lanSelect[1].setBounds(200, 250, 200, 30);
			lanSelect[0].setSelected(true);
			add(lanLabel);
			add(lanSelect[0]);
			add(lanSelect[1]);

			levelLabel.setFont(basicFont);
			levelSelect.setFont(basicFont);
			levelLabel.setBounds(50, 310, 200, 30);
			levelSelect.setBounds(120, 310, 200, 30);
			add(levelLabel);
			add(levelSelect);

			gameStartBtn.setFont(basicFont);
			gameStartBtn.setBounds(50, 450, 500, 40);	
			add(gameStartBtn);
			
			rankingBtn.setFont(btnFont);
			rankingBtn.setBounds(400, 270, 150, 30);
			addKorWordBtn.setFont(btnFont);
			addKorWordBtn.setBounds(400, 310, 75, 75);
			addEngWordBtn.setFont(btnFont);
			addEngWordBtn.setBounds(475, 310, 75, 75);
			add(rankingBtn);
			add(addKorWordBtn);
			add(addEngWordBtn);

			gameStartBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					gamePanel = new GamePanel(scorePanel, plantPanel);
					closePanel();
					getContentPane().setLayout(new BorderLayout());
					splitPane();
					setResizable(false);
					repaint();
					scorePanel.startTimer();
					gamePanel.startGame();
				}
			});

			rankingBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closePanel();
				}
			});
			
			addKorWordBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
				}
			});

			addEngWordBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
				}
			});

		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("pngegg (5).png");
			g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
			setOpaque(false);
		}

		public void closePanel() {
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

		public void openPanel() {
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

		private void splitPane() {
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
	}
}
