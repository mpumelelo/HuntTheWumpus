import java.util.List;
import java.util.ArrayList;

/**
 * This class acts as the agent knowledge base
 */
public class KnowledgeBase {
	public boolean wumpusAlive; // indicates if wumpus is alive 
	private int numRows;      // number of board rows
	private int numColumns;   // number of board cells
	private BoardCoordinate startTile; //cell where agent enters board
	private List<BoardCoordinate> stenchTiles; //list of all cells that we learn have stench
	public KbCell[][] kbTiles;  // two-dimensional array of cell/tile objects 
	
	// constructor 
	public KnowledgeBase(BoardCoordinate start, int numRows, int numColumns) {
		this.wumpusAlive=true;
		this.startTile= start;
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.kbTiles= new KbCell[numRows][numColumns];
		this.stenchTiles= new ArrayList<BoardCoordinate>();
		
		// initialize each tile with a kbTile object 
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				this.kbTiles[i][j]= new KbCell(new BoardCoordinate(i,j));
				this.setCellNeighbors(i,j);
			}
		}
		this.setCellDistFrm(start);
	}
	
	public BoardCoordinate getStartTile() {
		return startTile;
	}

	// update knowledge base with breeze, stench and glitter info learned from current cell 
	public void tell(Board gBoard, BoardCoordinate currentCell){
		
		this.kbTiles[currentCell.getX()][currentCell.getY()].setVisited(true); // set current cell as visited 
		this.kbTiles[currentCell.getX()][currentCell.getY()].setWumpus(wpState.NO);
		this.kbTiles[currentCell.getX()][currentCell.getY()].setPit(wpState.NO);
		
		if (gBoard.tiles[currentCell.getX()][currentCell.getY()].isStench()){ // if cell is stench in the real world 
			this.kbTiles[currentCell.getX()][currentCell.getY()].setStench(pState.YES); // set stench YES in KB
			this.stenchTiles.add(currentCell); //add cell to stench list
			this.setNeighborsPossWump(gBoard.tiles[currentCell.getX()][currentCell.getY()].neighbors); // set neighbors of cell as possible wumpus 
		}
		else{
			this.kbTiles[currentCell.getX()][currentCell.getY()].setStench(pState.NO);
			this.setNeighborsNoWumpus(gBoard.tiles[currentCell.getX()][currentCell.getY()].neighbors); //impossible for neighbors to have wumpus if current cell has no stench
			this.tryInferWumpusLocation(gBoard);
		}
		if (gBoard.tiles[currentCell.getX()][currentCell.getY()].isBreeze()){
			this.kbTiles[currentCell.getX()][currentCell.getY()].setBreeze(pState.YES);
			this.setNeighborsPossPit(gBoard.tiles[currentCell.getX()][currentCell.getY()].neighbors);
		}
		else{
			this.kbTiles[currentCell.getX()][currentCell.getY()].setBreeze(pState.NO);
			this.setNeighborsNoPit(gBoard.tiles[currentCell.getX()][currentCell.getY()].neighbors); //impossible for neighbors to have pit if current cell has no breeze
		}
		if (gBoard.tiles[currentCell.getX()][currentCell.getY()].isGlitter()){
			this.kbTiles[currentCell.getX()][currentCell.getY()].setGlitter(pState.YES);
		}
		else{
			this.kbTiles[currentCell.getX()][currentCell.getY()].setGlitter(pState.NO);
		}
		
	}

	// returns a list of safe unvisited tiles  
	public List<BoardCoordinate> getAllSafeUnvisited(){
		//System.out.println("kb.getallsafeunvisited:The following are safe unvisited tiles:"); //DEBUG
		List<BoardCoordinate> safeMoves = new ArrayList<BoardCoordinate>();
		for(int i = 0; i < numRows; i++){
			for(int j = 0; j < numColumns; j++){
				if ( kbTiles[i][j].isSafe() && !(kbTiles[i][j].isVisited()) ){ // if tile safe and unvisited 
					safeMoves.add(kbTiles[i][j].getLocation());
					//kbTiles[i][j].getLocation().print();//DEBUG
				}
			}	
		}
		return safeMoves;
	}
	
	// returns a list of unsafe unvisited tiles  
	public List<BoardCoordinate> getAllPossWump(){
		List<BoardCoordinate> possWumpTiles = new ArrayList<BoardCoordinate>();
		for(int i = 0; i < numRows; i++){
			for(int j = 0; j < numColumns; j++){
				if ( !(kbTiles[i][j].getWumpus()==wpState.NO) && !(kbTiles[i][j].isVisited()) ) // if tile could be wumpus and unvisited 
					possWumpTiles.add(kbTiles[i][j].getLocation());
			}	
		}
		return possWumpTiles;
	}

	// if all neighbors of stench except one is not the wumpus then we can infer the last unknown cell is the wumpus
	private void tryInferWumpusLocation(Board gBoard){
		for (BoardCoordinate stenchTile : this.stenchTiles){
			int numNeighbors = gBoard.tiles[stenchTile.getX()][stenchTile.getY()].neighbors.size();
			int numNeighWithNoWump=0;
			for (BoardCoordinate neighbor : gBoard.tiles[stenchTile.getX()][stenchTile.getY()].neighbors){
				if (kbTiles[neighbor.getX()][neighbor.getY()].getWumpus()==wpState.NO)
					numNeighWithNoWump++;
			}
			if(numNeighbors==(numNeighWithNoWump-1)){
				for (BoardCoordinate neighbor : gBoard.tiles[stenchTile.getX()][stenchTile.getY()].neighbors){
					if (!(kbTiles[neighbor.getX()][neighbor.getY()].getWumpus()==wpState.NO))
						kbTiles[neighbor.getX()][neighbor.getY()].setWumpus(wpState.YES);
						this.setWumpusFound(new BoardCoordinate(neighbor.getX(),neighbor.getY()));
						break;
				}
			}
		}
	}
	
	// since there is only one wumpus, once it is found all other cells can not be the wumpus
	private void setWumpusFound(BoardCoordinate wumpusLocation) {
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				if (!(i==wumpusLocation.getX()&&j==wumpusLocation.getY())) // if not wumpus location set others to No
					kbTiles[i][j].setWumpus(wpState.NO);
			}
		}
	}
	
	// set all cells no wumpus
	public void setWumpusKilled() {
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
					kbTiles[i][j].setWumpus(wpState.NO);
			}
		}
	}

	// set all coordinates passed in parameter list set to no wumpus 
	private void setNeighborsNoWumpus(List<BoardCoordinate> neighbors){
		for(BoardCoordinate neighbor : neighbors)
			kbTiles[neighbor.getX()][neighbor.getY()].setWumpus(wpState.NO); //impossible for neighbors to have wumpus if current cell has no stench
	}
	
	// set all coordinates received to no pit
	private void setNeighborsNoPit(List<BoardCoordinate> neighbors){
		for(BoardCoordinate neighbor : neighbors)
			kbTiles[neighbor.getX()][neighbor.getY()].setPit(wpState.NO); //impossible for neighbors to have pit if current cell has no breeze
	}
	
	// set all coordinates received to possible pit if current status is unknown
	private void setNeighborsPossPit(List<BoardCoordinate> neighbors){
		for(BoardCoordinate neighbor : neighbors){
			if(kbTiles[neighbor.getX()][neighbor.getY()].getPit()==wpState.UNKNOWN) // only set to maybe if current state is unknown
				kbTiles[neighbor.getX()][neighbor.getY()].setPit(wpState.MAYBE);
		}	
	}
	
	// set all coordinates received to possible wumpus if coordinate current status is unknown
	private void setNeighborsPossWump(List<BoardCoordinate> neighbors){
		for(BoardCoordinate neighbor : neighbors){
			if(kbTiles[neighbor.getX()][neighbor.getY()].getWumpus()==wpState.UNKNOWN) // only set to maybe if current state is unknown
				kbTiles[neighbor.getX()][neighbor.getY()].setWumpus(wpState.MAYBE);
		}	
	}
	
	public void setCellDistFrm(BoardCoordinate tile){
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				int sX= tile.getX();
				int sY= tile.getY();
				int distance= Math.abs(i-sX) + Math.abs(j-sY); // calculate Manhattan distance from start point to current cell
				this.kbTiles[i][j].setDistFrom(distance);
			}
		}
	}
	
	private void setCellNeighbors(int cellRow, int cellCol){
		int rowAbove = cellRow-1;
		int rowBelow = cellRow+1;
		int colLeft= cellCol-1;
		int colRight= cellCol+1;
		if (0<=rowAbove && rowAbove<this.numRows){//if there is a cell above add to neighbors list
			this.kbTiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(rowAbove,cellCol));	
		}
		if (0<=rowBelow && rowBelow<this.numRows){//if there is a cell below add to neighbors list
			this.kbTiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(rowBelow,cellCol));	
		}
		if (0<=colLeft && colLeft<this.numColumns){//if there is a cell to the left add to neighbors list
			this.kbTiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(cellRow,colLeft));	
		}
		if (0<=colRight && colRight<this.numColumns){//if there is a cell to the right add to neighbors list
			this.kbTiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(cellRow,colRight));	
		}
	}
	
	//print kB cells
	public void print(){
		System.out.println("Knowledge base:");
		printHline();
		for(int i = 0; i < numRows; i++){
			System.out.print("|");
			for(int j = 0; j < numColumns; j++){
				kbTiles[i][j].print();
				System.out.print("|");
			}
			System.out.println();
			printHline();	
		}
	}

	// print horizontal line
	private void printHline(){
		String line="";
		for(int count=0; count<numColumns; count++){
			line+="---------";
		}
		System.out.println(line);
	}
}
