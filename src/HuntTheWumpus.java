import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HuntTheWumpus {

	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter path to Wumpus world layout input file: ");
		String pathToWumpusInputFile = "";
		try {
			pathToWumpusInputFile = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Game newGame= new Game(pathToWumpusInputFile);
		newGame.gameBoard.print();
		newGame.playGame();
	}

}
