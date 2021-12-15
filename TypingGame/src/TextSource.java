import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Vector;

public class TextSource {
	private Vector<String> wordVector = new Vector<String>();
	private BufferedReader br = null;
	private Scanner scanner = null;

	public TextSource(String fileName) {
		try {
			if (fileName.equals("�ѱ�.txt")) { //�ѱ� ������ ������ ���
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8")); //�ѱ��� �о���� ���Ͽ� utf-8�� ���ڵ�
				String line = "";
				for (int i = 0; (line = br.readLine()) != null; i++) { //���� ������ ����������
					wordVector.add(line); //�ܾ ���Ϳ� ����
					//System.out.println(line);
				}
				br.close();
			} 
			
			else {
				scanner = new Scanner(new FileReader(fileName));
				while (scanner.hasNext()) { // ���� ������ ����
					String word = scanner.nextLine(); // �� ������ �а� '\n'�� ���� ������ ���ڿ��� ����
					wordVector.add(word); // ���ڿ��� ���Ϳ� ����
				}
				scanner.close();
			}
		} catch (IOException e) {
			System.out.println("file not found error");
			System.exit(0);
		}
	}

	public String get() {
		final int WORDMAX = wordVector.size(); // �� �ܾ��� ����
		int index = (int) (Math.random() * WORDMAX); //���� ����
		return wordVector.get(index);
	}
}
