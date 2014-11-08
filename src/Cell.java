import java.util.List;
import java.util.ArrayList;

/**
 * This class stores information relevant to a board cell/tile.
 */
class Cell {
	
	private boolean enter;     // agent start position 
	private boolean stench;    // stench value from input file 
	private boolean breeze;    // breeze value from input file 
	private boolean pit;	   // pit value from input file 
	private boolean wumpus;    // Wumpus value from input file 
	private boolean glitter;   // glitter value from input file 
	private BoardCoordinate location; // object with x and y coordinates of cell
	public List<BoardCoordinate> neighbors; //list of cells that are one move away
	
	//constructor 
	public Cell(BoardCoordinate location) {
		this.enter = false;
		this.stench = false;
		this.breeze = false;
		this.pit = false;
		this.wumpus = false;
		this.glitter = false;
		this.location = location;
		this.neighbors= new ArrayList<BoardCoordinate>();
	}

	
	public boolean isEnter() {
		return enter;
	}

	public void setEnter(boolean enter) {
		this.enter = enter;
	}

	public boolean isStench() {
		return stench;
	}
	
	public void setStench(boolean inStench) {
		this.stench = inStench;
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(boolean inBreeze) {
		this.breeze = inBreeze;
	}

	public boolean isPit() {
		return pit;
	}

	public void setPit(boolean inPit) {
		this.pit = inPit;
	}

	public boolean isWumpus() {
		return wumpus;
	}

	public void setWumpus(boolean inWumpus) {
		this.wumpus = inWumpus;
	}

	public boolean isGlitter() {
		return glitter;
	}

	public void setGlitter(boolean inGlitter) {
		this.glitter = inGlitter;
	}

	public BoardCoordinate getLocation() {
		return location;
	}

	public void setLocation(BoardCoordinate location) {
		this.location = location;
	}

	public void print() {
		String values = " ";
		if(this.isEnter()){
			values += "E";
		}
		if(this.isBreeze()){
			values += "B";
		}
		if(this.isStench()){
			values += "S";
		}
		if(this.isPit()){
			values += "P";
		}
		if(this.isGlitter()){
			values += "G";
		}
		if(this.isWumpus()){
			values += "W";
		}
		if(values.equalsIgnoreCase("")){
			values = "  ";
		}
		System.out.printf("%-8s",values);
	}
}
