import java.util.List;
import java.util.ArrayList;

/**
 * This class acts as the agent knowledge base
 */
public class KnowledgeBase {
	private int numRows;      // number of board rows
	private int numColumns;   // number of board cells
	private List<BoardCoordinate> stenchTiles; //list of all cells that we learn have stench
	public KbCell[][] kbTiles;  // two-dimensional array of cell/tile objects 
	
	// constructor 
	public KnowledgeBase(int numRows, int numColumns) {
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.kbTiles= new KbCell[numRows][numColumns];
		this.stenchTiles= new ArrayList<BoardCoordinate>();
		
		// initialize each tile with a Cell object
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				this.kbTiles[i][j]= new KbCell(new BoardCoordinate(i,j));
			}
		}
	}

	// update knowledge base with breeze, stench and glitter info learned from current cell 
	public void tell(Board gBoard, BoardCoordinate currentCell){
		
		this.kbTiles[currentCell.getX()][currentCell.getY()].setVisited(true); // set current cell as visited 
		
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

	// set all coordinates received to no wumpus 
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
	
	// set all coordinates received to possible wumpus if current status is unknown
	private void setNeighborsPossWump(List<BoardCoordinate> neighbors){
		for(BoardCoordinate neighbor : neighbors){
			if(kbTiles[neighbor.getX()][neighbor.getY()].getWumpus()==wpState.UNKNOWN) // only set to maybe if current state is unknown
				kbTiles[neighbor.getX()][neighbor.getY()].setWumpus(wpState.MAYBE);
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
			line+="-------";
		}
		System.out.println(line);
	}
}
