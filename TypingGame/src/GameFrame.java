import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

public class GameFrame extends JFrame {
	// ��ư�� ���� �̹��� �ε� �Ͽ� ������ �����
	private ImageIcon normalIcon = new ImageIcon("normal.gif");
	private ImageIcon pressedIcon = new ImageIcon("pressed.gif");
	private ImageIcon overIcon = new ImageIcon("over.gif");
	
	private JMenuItem startItem = new JMenuItem("start");
	private JMenuItem stopItem = new JMenuItem("stop");
	private JButton startBtn = new JButton(normalIcon);
	private JButton stopBtn = new JButton("stop");
	
	private ScorePanel scorePanel = new ScorePanel();
	private PlantPanel plantPanel = new PlantPanel();
	private GamePanel gamePanel = new GamePanel(scorePanel, plantPanel);
	
	public GameFrame() {
		setTitle("Ÿ���� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		splitPane(); // JSplitPane�� �����Ͽ� ����Ʈ���� CENTER�� ����
		makeMenu();
		makeToolBar();
		setResizable(false);
		setVisible(true);
	}

	private void splitPane() {
		JSplitPane hPane = new JSplitPane();
		getContentPane().add(hPane, BorderLayout.CENTER);
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		hPane.setDividerLocation(550);
		hPane.setEnabled(false);
		hPane.setLeftComponent(gamePanel);
		
		JSplitPane pPane = new JSplitPane();
		pPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pPane.setDividerLocation(300);
		pPane.setTopComponent(scorePanel);
		pPane.setBottomComponent(plantPanel);
		hPane.setRightComponent(pPane);
	}
	private void makeMenu() {
		JMenuBar mBar = new JMenuBar();
		setJMenuBar(mBar);
		JMenu fileMenu = new JMenu("Game");
		fileMenu.add(startItem);
		fileMenu.add(stopItem);
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem("exit"));
		mBar.add(fileMenu);
		
		startItem.addActionListener(new StartAction());
	}
	
	private void makeToolBar() {
		JToolBar tBar = new JToolBar();
		tBar.add(startBtn);
		tBar.add(stopBtn);
		getContentPane().add(tBar, BorderLayout.NORTH);
		
		startBtn.addActionListener(new StartAction());
		
		startBtn.setRolloverIcon(overIcon);
		startBtn.setPressedIcon(pressedIcon);
	}
	
	private class StartAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gamePanel.startGame();
		}
	}
	
}