import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
	public boolean wumpusAlive; // indicates if wumpus is alive or dead
	public Board gameBoard; // Board object used to store game info
	public KnowledgeBase gameKB; // Knowledge base for agent
	public boolean gameOver;
	private final String WUMPUS = "W"; // string constants used to match input file characters 
	private final String BREEZY = "B";
	private final String STENCH = "S";
	private final String GOLD = "G";
	private final String PIT = "P";
	private final String ENTER = "E";
	private enum pState { YES, NO, UNKNOWN} // belief states for percept 
	
	//constructor 
	public Game() {
		gameOver=false;
		wumpusAlive=true;
		initGame();
	}

	// parse input file and initialize board and cells objects 
	public void initGame (){	
		File inputFile = new File("C:/wumpus.txt"); // location and file name of input file used
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new FileReader(inputFile));
		    String inputText = null;
		    int counter = 0;
		    while ((inputText = reader.readLine()) != null) {
		        if(counter == 0){
		        	initBoardAndKB(inputText); // read board size from input file then call board and KB constructors 
		        	counter++;
		        } else {
		        	initCell(inputText); // read cell info from input file and initialize corresponding cell 
		        	counter++;
		        }
		    }
		    this.gameBoard.setAllCellDistFrmStarts(); // set distance from start value for all cells. Outside of while loop above to make sure startTile is set.
		    
		} catch (FileNotFoundException e) { 
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
	}

	// read board size from input file and call board constructor 
	private void initBoardAndKB(String text){
		int rows;
		int columns;
	    String pattern = "[S|s]ize\\s*=\\s*(\\d+)\\s*,\\s*(\\d+)";
	    // Create a Pattern object
	    Pattern r = Pattern.compile(pattern);
	    // Now create matcher object.
	    Matcher m = r.matcher(text);
	    if (m.find()) {
	    	rows = Integer.parseInt(m.group(1));
	    	columns = Integer.parseInt(m.group(2));  
	    	this.gameBoard= new Board(rows,columns); //initialize gameBoard 
	    	this.gameKB = new KnowledgeBase(rows,columns); //initialize gameKB
	    } else {
	        System.out.println("Error: Unable to read board size from file.");
	    }	    
	}

	// read cell info from input file and initialize corresponding cell 
	private void initCell(String text){
	      String splitBy = ",";
	      String[] cellStuff = text.split(splitBy);
	      int rowNumber=-1; // value reflects error if not changed in if statements below
	      int columnNumber=-1; // value reflects error if not changed in if statements below
	      for(int i = 0; i < cellStuff.length; i++){
	    	  if(i == 0){
	    		  rowNumber = Integer.parseInt(cellStuff[i]);
	    	  }
	    	  if(i == 1){
	    		  columnNumber = Integer.parseInt(cellStuff[i]);
	    	  } else {
	    		  if(cellStuff[i].equalsIgnoreCase(WUMPUS)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setWumpus(true);
	    			  continue;
	    		  } else if (cellStuff[i].equalsIgnoreCase(BREEZY)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setBreeze(true);
	    			  continue;
	    		  } else if (cellStuff[i].equalsIgnoreCase(STENCH)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setStench(true);
	    			 continue; 
	    		  } else if (cellStuff[i].equalsIgnoreCase(PIT)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setPit(true);
	    			  continue;
	    		  } else if (cellStuff[i].equalsIgnoreCase(ENTER)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setEnter(true);
	    			  gameBoard.setStartTile(new BoardCoordinate(rowNumber, columnNumber));
	    			  continue;	  
	    		  } else if (cellStuff[i].equalsIgnoreCase(GOLD)){
	    			  gameBoard.tiles[rowNumber][columnNumber].setGlitter(true);
	    			  continue;
	    		  }
	    	  }
	      }
	}

}

