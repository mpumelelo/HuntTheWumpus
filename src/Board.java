/**
 * This class stores the Wumpus world state as described by an input file.
 */

public class Board {

	private int numRows;      // number of board rows
	private int numColumns;   // number of board cells
	public Cell[][] tiles;  // two-dimensional array of cell/tile objects 
	private BoardCoordinate startTile; // board coordinates of agent starting position 
		
	public Board(int numRows, int numColumns) {
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.tiles= new Cell[numRows][numColumns];
		
		// initialize each tile with a Cell object
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				this.tiles[i][j]= new Cell(new BoardCoordinate(i,j));
				this.setCellNeighbors(i,j);
			}
		}
	}
	
	public BoardCoordinate getStartTile() {
		return startTile;
	}

	public void setStartTile(BoardCoordinate startTile) {
		this.startTile = startTile;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	private void setCellNeighbors(int cellRow, int cellCol){
		int rowAbove = cellRow-1;
		int rowBelow = cellRow+1;
		int colLeft= cellCol-1;
		int colRight= cellCol+1;
		if (0<=rowAbove && rowAbove<this.numRows){//if there is a cell above add to neighbors list
			this.tiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(rowAbove,cellCol));	
		}
		if (0<=rowBelow && rowBelow<this.numRows){//if there is a cell below add to neighbors list
			this.tiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(rowBelow,cellCol));	
		}
		if (0<=colLeft && colLeft<this.numColumns){//if there is a cell to the left add to neighbors list
			this.tiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(cellRow,colLeft));	
		}
		if (0<=colRight && colRight<this.numColumns){//if there is a cell to the right add to neighbors list
			this.tiles[cellRow][cellCol].neighbors.add(new BoardCoordinate(cellRow,colRight));	
		}
	}
	
	public void setAllCellDistFrmStarts(){
		for(int i = 0; i < this.numRows; i++){
			for(int j = 0; j < this.numColumns; j++){
				int sX= this.startTile.getX();
				int sY= this.startTile.getY();
				int distance= Math.abs(i-sX) + Math.abs(j-sY); // calculate Manhattan distance from start point to current cell
				this.tiles[i][j].setDistFromStart(distance);
			}
		}
	}
	
	public void print(){
		System.out.println("Board:");
		printHline();
		for(int i = 0; i < numRows; i++){
			System.out.print("|");
			for(int j = 0; j < numColumns; j++){
				tiles[i][j].print();
				System.out.print("|");
			}
			System.out.println();
			printHline();	
		}
	}

	private void printHline(){
		String line="";
		for(int count=0; count<numColumns; count++){
			line+="-------";
		}
		System.out.println(line);
	}
 }
