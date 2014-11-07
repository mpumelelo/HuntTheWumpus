import java.util.ArrayList;
import java.util.List;

/**
 * This class stores the belief state for a cell.
 */
public class KbCell {
	private pState stench;  // YES if visited and stench percept received, NO if visited and stench not received, UKNOWN if cell not visited   
	private pState breeze;  // YES if visited and breeze percept received, NO if visited and stench not received, UKNOWN if cell not visited 
	private wpState pit;	 // initially UKNOWN, update-able to YES, NO or MAYBE representing if cell believed to contain pit
	private wpState wumpus;  // initially UKNOWN, update-able to YES, NO or MAYBE representing if cell believed to contain wumpus 
	private pState glitter;   // true if glitter percept received, else false 
	public boolean visited; // true if cell visited, else false 
	private BoardCoordinate location; // object with x and y coordinates of cell
	private int distFrom; // value indicating distance from starting cell
	public List<BoardCoordinate> neighbors; //list of cells that are one move away
	
	//constructor 
	public KbCell(BoardCoordinate location) {
		this.visited = false;
		this.stench = pState.UNKNOWN;
		this.breeze = pState.UNKNOWN;
		this.pit = wpState.UNKNOWN;
		this.wumpus = wpState.UNKNOWN;
		this.glitter = pState.UNKNOWN;
		this.distFrom = -100;
		this.neighbors= new ArrayList<BoardCoordinate>();
		this.location=location;
	}

	public pState getStench() {
		return stench;
	}

	public void setStench(pState stench) {
		this.stench = stench;
	}

	public pState getBreeze() {
		return breeze;
	}

	public void setBreeze(pState breeze) {
		this.breeze = breeze;
	}

	public wpState getPit() {
		return pit;
	}

	public void setPit(wpState pit) {
		this.pit = pit;
	}

	public wpState getWumpus() {
		return wumpus;
	}

	public void setWumpus(wpState wumpus) {
		this.wumpus = wumpus;
	}

	public pState isGlitter() {
		return glitter;
	}

	public void setGlitter(pState glitter) {
		this.glitter = glitter;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public BoardCoordinate getLocation() {
		return location;
	}

	public void setLocation(BoardCoordinate location) {
		this.location = location;
	}

	// if stench, breeze or no pit and no wumpus returns true, else returns false 
	public boolean isSafe(){
		boolean pit = this.pit==wpState.YES;
		boolean wumpus = this.wumpus==wpState.YES;
		boolean stench = this.stench==pState.YES;
		boolean breeze= this.breeze==pState.YES;
		return stench || breeze || (!pit && !wumpus);
		
	}; 
	
	public int getDistFrom() {
		return this.distFrom;
	}

	public void setDistFrom(int distFromStart) {
		this.distFrom = distFromStart;
	}
	
	public void print() {
		String values = " ";
		if(this.breeze==pState.YES){
			values += "B";
		}
		if(this.stench==pState.YES){
			values += "S";
		}
		if(this.pit==wpState.YES){
			values += "P";
		}
		if(this.pit==wpState.MAYBE){
			values += "P?";
		}
		if(this.glitter==pState.YES){
			values += "G";
		}
		if(this.wumpus==wpState.YES){
			values += "W";
		}
		if(this.wumpus==wpState.MAYBE){
			values += "W?";
		}
		if(values.equalsIgnoreCase("")){
			values = "  ";
		}
		System.out.printf("%-6s",values);
	}

}
