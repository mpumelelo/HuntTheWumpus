
public class BoardCoordinate {
	
	private int xCoordinate;
	private int yCoordinate;
	
	// constructor 
	public BoardCoordinate(int xCoordinate, int yCoordinate) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}
	
	public int getX() {
		return xCoordinate;
	}
	public void setX(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	public int getY() {
		return yCoordinate;
	}
	public void setY(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	
	@Override // define comparison of BoardCoordinate objects 
	public boolean equals (Object other){
		if (!(other instanceof BoardCoordinate)){
			return false;
		}
		BoardCoordinate that = (BoardCoordinate) other;
		return (this.xCoordinate==that.getX()) && (this.yCoordinate==that.getY());
	}
	
	@Override
	public String toString() {
		return "Coordinates: ["+ xCoordinate + ","+ yCoordinate + "]";
	}
		
	
}
