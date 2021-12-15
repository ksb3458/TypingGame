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
			if (fileName.equals("한글.txt")) { //한글 버전을 선택한 경우
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8")); //한글을 읽어오기 위하여 utf-8로 인코딩
				String line = "";
				for (int i = 0; (line = br.readLine()) != null; i++) { //다음 라인이 없을때까지
					wordVector.add(line); //단어를 벡터에 저장
					//System.out.println(line);
				}
				br.close();
			} 
			
			else {
				scanner = new Scanner(new FileReader(fileName));
				while (scanner.hasNext()) { // 파일 끝까지 읽음
					String word = scanner.nextLine(); // 한 라인을 읽고 '\n'을 버린 나머지 문자열만 리턴
					wordVector.add(word); // 문자열을 벡터에 저장
				}
				scanner.close();
			}
		} catch (IOException e) {
			System.out.println("file not found error");
			System.exit(0);
		}
	}

	public String get() {
		final int WORDMAX = wordVector.size(); // 총 단어의 개수
		int index = (int) (Math.random() * WORDMAX); //랜덤 생성
		return wordVector.get(index);
	}
}
