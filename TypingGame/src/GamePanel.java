import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel {
	private JTextField input = new JTextField(30);
	private JLabel text = new JLabel("Ÿ�����غ�����"); 
	private ScorePanel scorePanel = null;
	private PlantPanel plantPanel = null;
	private TextSource textSource = new TextSource("words.txt"); // �ܾ� ���� ����
	
	public GamePanel(ScorePanel scorePanel, PlantPanel plantPanel) {
		this.scorePanel = scorePanel;
		this.plantPanel = plantPanel;
		
		setLayout(new BorderLayout());
		add(new GameGroundPanel(), BorderLayout.CENTER);
		add(new InputPanel(), BorderLayout.SOUTH);
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField t = (JTextField)(e.getSource());
				String inWord = t.getText();
				if(text.getText().equals(inWord)) { // ���߱� ����
					// ���� �ø���
					scorePanel.increase();
					startGame();
					
					//input â �����
					t.setText("");
				}
			}
		});
	}
	
	public void startGame() {
		// �ܾ� �� �� ����
		String newWord = textSource.get();
		text.setText(newWord);
		text.setBackground(Color.GREEN);
		text.setOpaque(true);
	}
	
	class GameGroundPanel extends JPanel {
		public GameGroundPanel() {
			setLayout(null);
			text.setSize(100, 30);
			text.setLocation(100,  10);
			add(text);
		}
	}
	
	class InputPanel extends JPanel {
		public InputPanel() {
			setLayout(new FlowLayout());
			this.setBackground(Color.CYAN);
			add(input);
		}
	}
}
