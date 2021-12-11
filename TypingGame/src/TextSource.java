import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class TextSource {
	private Vector<String> wordVector = new Vector<String>();

	public TextSource(String fileName) {
		try {
			Scanner scanner = new Scanner(new FileReader(fileName));
			while(scanner.hasNext()) { // 파일 끝까지 읽음
				String word = scanner.nextLine(); // 한 라인을 읽고 '\n'을 버린 나머지 문자열만 리턴
				wordVector.add(word); // 문자열을 벡터에 저장
			}
			scanner.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("file not found error");
			System.exit(0);
		}
	}
	
	public String get() {
		final int WORDMAX = wordVector.size(); // 총 단어의 개수
		int index = (int)(Math.random()*WORDMAX);
		return wordVector.get(index);
	}
}
