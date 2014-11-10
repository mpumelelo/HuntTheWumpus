import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Agent {
	enum Turn {LEFT, RIGHT, NONE, DOUBLE} // turn left, turn right, don't turn and double turn/180
	private enum Direction { NORTH, SOUTH, EAST, WEST}
	private BoardCoordinate position; //current position of agent
	private Direction facing; 
	public KnowledgeBase kb;
	private Board world;
	private boolean arrow; //arrow available
	private int numColumns;
	private int numRows;
	//private int score;
	private boolean climb;  //indicate agent trying to exit 
	private boolean shoot;  //indicate agent ready to shoot 
	public Queue<Turn> plan;
	
	public Agent(Board gBoard, BoardCoordinate startPos, int rows, int columns) {
		this.world= gBoard;
		this.kb= new KnowledgeBase(startPos, rows, columns);
		this.position = startPos;
		this.facing = Direction.EAST;
		this.arrow = true;
		this.numColumns= columns;
		this.numRows= rows;
		//this.score=0;
		this.climb=false;
		plan= new LinkedList<Turn>();
	}
	
	private BoardCoordinate getNearestSafeUnvistd(){
		BoardCoordinate closestTile= new BoardCoordinate(-1,-1);
		int shortestDistance=100000;
		List <BoardCoordinate> safeUnvisitedTiles = new ArrayList<BoardCoordinate>();
		safeUnvisitedTiles = kb.getAllSafeUnvisited();
		if (safeUnvisitedTiles.isEmpty()){ // if no safe unvisited tiles 
			return closestTile;
		}
		for (BoardCoordinate tile : safeUnvisitedTiles){
			int distance= Math.abs(position.getX()-tile.getX()) + Math.abs(position.getY()-tile.getY()); //Manhattan distance to tile from current location 
			if(distance<shortestDistance){
				shortestDistance=distance;
				closestTile = tile;
			}
		}
		/*System.out.println("agent.getNearestSafeUnvisited: Nearest safe unvisited tile:");//DEBUG
		closestTile.print(); //DEBUG */
		return closestTile;
	}
	
	private BoardCoordinate getNearestPossWump(){
		List <BoardCoordinate> possWumpusTiles = kb.getAllPossWump();
		BoardCoordinate closestTile= new BoardCoordinate(-1,-1);
		int shortestDistance=100000;
		if (possWumpusTiles.isEmpty()){ // if no safe unvisited tiles 
			return closestTile;
		}
		for (BoardCoordinate tile : possWumpusTiles){
			int distance= Math.abs(position.getX()-tile.getX()) + Math.abs(position.getY()-tile.getY()); //Manhattan distance to tile from current location 
			if(distance<shortestDistance){
				shortestDistance=distance;
				closestTile = tile;
			}
		}
		
		return closestTile;
	}
	
	private Direction planSafeRoute(BoardCoordinate destination, int flag){
		BoardCoordinate start= this.position;
		Direction nowFacing = this.facing;
		BoardCoordinate nearestTile;
		int shortestDistance;
		/*System.out.println("agent.planSafeRoute- Start position followed by destination:"); //DEBUGG
		start.print();//DEBUGG
		destination.print();//DEBUGG
		*/
		//check if destination is current position 
		if (start==destination){
			nearestTile = this.position;
			shortestDistance=0;
		}
		else{
			nearestTile = new BoardCoordinate(-1,-1);
			shortestDistance=100000;
		}
		
		//enter while loop if start!=destination 
		while(shortestDistance>=flag){ //flag = 1 or 2: 1 if plan route to destination, 2 if plan route to neighbor of destination
			for (BoardCoordinate tile : kb.kbTiles[start.getX()][start.getY()].neighbors ){// check each neighbor of current start tile 
				int distance= Math.abs(tile.getX()-destination.getX()) + Math.abs(tile.getY()-destination.getY()); //calculate Manhattan distance to destination from current location 
				if(distance<shortestDistance && kb.kbTiles[tile.getX()][tile.getY()].isSafe()){ // if tile is closer and SAFE 
					shortestDistance=distance; //update shortest distance
					nearestTile = tile; //update nearest safe tile 
				}
				if(distance==0){ // destination may be safe or unsafe tile 
					shortestDistance=distance; //update shortest distance
					nearestTile = tile; //update nearest safe tile 
				}
			}
			//System.out.println("agent.planSafeRoute- tile on safe route path:"); //DEBUGG
			//nearestTile.print();//DEBUGG
			nowFacing = this.addTurnsToFaceNeighToPlan(start, nearestTile, nowFacing); // turn to face nearest tile, add turns to plan and update current direction facing
			start=nearestTile; // update start tile
		}
		return nowFacing;
	}
	
	public Action decide(){
		kb.tell(this.world, this.position);
		//kb.print(); // FOR DEBUGGING 
		if (climb==true && (position.equals(kb.getStartTile())) ){
			return Action.CLIMB;
		}
		if (kb.kbTiles[position.getX()][position.getY()].isGlitter().equals(pState.YES)&& !climb){// if glitter in current tile and climb flag not set
			//System.out.println("agent.decide: grab gold and plan exit route."); //DEBUGG
			this.planSafeRoute(kb.getStartTile(),1); // plan route to start tile 
			this.climb=true; // indicate ready to climb out once back at start tile
			return Action.GRAB;
		}
		if (plan.isEmpty()&&shoot){
			//System.out.println("agent.decide: shoot wumpus."); //DEBUGG
			shoot=false;
			return Action.SHOOT;
		}
		if(plan.isEmpty()){ // if no instructions in plan queue: generate plan to safe tile 
			//System.out.println("agent.decide: generate plan to safe tile."); //DEBUGG
			BoardCoordinate destination = this.getNearestSafeUnvistd(); // get nearest safe unvisited tile 
			if (!(destination.getX()==-1)){ // if safe unvisited tile exists 
				this.planSafeRoute(destination,1); // plan a safe route to nearest safe unvisited tile 
				//System.out.println("agent.decide - turns required returned by agent.planSafeRoute:"); //DEBUGG
				/*for(Turn t: plan){//DEBUGG
					System.out.println(t.toString());
				}*/
				
			}
		}
		if(plan.isEmpty()){ // if couldn't generate plan to safe tile: generate plan to tile adjacent to wumpus
			//System.out.println("agent.decide: generate plan shoot wumpus."); //DEBUGG
			BoardCoordinate destination = this.getNearestPossWump(); // get nearest possible wumpus tile
			if (!(destination.getX()==-1)&& this.arrow){ // if possible wumpus tile exists and have arrow
				if (world.tiles[position.getX()][position.getY()].neighbors.contains(destination)){ // if wumpus is neighbor 
					this.addTurnsToFaceNeighToPlan(position, destination, facing);//face wumpus
					return Action.SHOOT;
				}
				this.planSafeRoute(destination,2); // plan a route to a cell adjacent to possible wumpus 
				this.shoot=true; // flag so that we shoot after plan to neighbor completed
			}
		}
		if (plan.isEmpty()){ // if couldn't generate plan to safe unvisited  or plan to shoot wumpus: head for the exit 
			//System.out.println("agent.decide: give up and go to exit."); //DEBUGG
			if(position.equals(kb.getStartTile())){// if at entrance
				this.climb=true;
				return Action.CLIMB;
			}
			this.planSafeRoute(kb.getStartTile(),1); // plan route to start tile 
			this.climb=true; // indicate ready to climb out once back at start tile
			
		}
		
		return Action.FORWARD;
		
		//use the plan created taking actions in order. after each action update, print, check if youre dead, score
	}
	
	public void climbOut(){
		System.out.println("Agent has climbed out from start position.");
	}
	
	public void grabGold(){
		System.out.println("Agent has grabbed the gold at location ("+position.getX()+","+position.getY()+").");
	}
	
	public void moveForward(){
		BoardCoordinate newPosition = new BoardCoordinate(position.getX(),position.getY());
		switch (this.facing){
			case NORTH: newPosition.setX(position.getX()-1);
						break;
			case SOUTH: newPosition.setX(position.getX()+1);
						break;
			case EAST:  newPosition.setY(position.getY()+1);
					    break;
			case WEST:  newPosition.setY(position.getY()-1);
					    break;
		}
		if (newPosition.getX()<numRows&&newPosition.getY()<numColumns){
			System.out.println("Agent moved forward from ("+position.getX()+","+position.getY()+") to ("+newPosition.getX()+","+newPosition.getY()+").");
			this.setPosition(newPosition);
		}
		else
			System.out.println("Ooops! Agent has bumped into the wall at ("+position.getX()+","+position.getY()+").");
			
	}
	
	public int shootArrow(){
		if (!this.arrow){
			System.out.println("Agent attempted to shoot with no arrows.");
			return -1;
		} 
		BoardCoordinate targetPosition = new BoardCoordinate(position.getX(),position.getY());
		String direction ="invalid";
		switch (this.facing){
			case NORTH: targetPosition.setX(position.getX()-1);
						direction=" north ";
						break;
			case SOUTH: targetPosition.setX(position.getX()+1);
						direction=" south ";
						break;
			case EAST:  targetPosition.setY(position.getY()+1);
						direction=" east ";
					    break;
			case WEST:  targetPosition.setY(position.getY()-1);
						direction=" west ";
					    break;
		}
		if (targetPosition.getX()<numRows&&targetPosition.getY()<numColumns){// if valid target position (facing the towards the board)
			if(world.tiles[targetPosition.getX()][targetPosition.getY()].isWumpus()){// if wumpus is at target position 
				System.out.println("Agent shot arrow from ("+position.getX()+","+position.getY()+") facing"+direction+"killing the wumpus at ("+targetPosition.getX()+","+targetPosition.getY()+").");
				kb.setWumpusKilled();
				world.tiles[targetPosition.getX()][targetPosition.getY()].setWumpus(false); //remove real world wumpus so agent allowed to enter
				this.arrow=false; // no remaining arrows
			}
			else{// no wumpus in target location 
				System.out.println("Agent shot arrow from ("+position.getX()+","+position.getY()+") facing"+direction+"but no wumpus at ("+targetPosition.getX()+","+targetPosition.getY()+").");
				kb.kbTiles[targetPosition.getX()][targetPosition.getY()].setWumpus(wpState.NO);
				this.arrow=false; // no remaining arrows
			}
		}
		else{// invalid target position 
			System.out.println("Agent has wasted its arrow shooting at invalid position ("+targetPosition.getX()+","+targetPosition.getY()+").");
			this.arrow=false; // no remaining arrows
		}
		return 0;
	}
	
 	public void turn(Turn value){
		if (value==Turn.LEFT){
			switch(this.facing){
				case NORTH: setFacing(Direction.WEST); // if facing north and agent turns left new direction is west
							System.out.println("Agent has turned left, now facing west.");
						 	break;
				case SOUTH: setFacing(Direction.EAST); // if facing south and agent turns left new direction is east
							System.out.println("Agent has turned left, now facing east.");
							break;
				case EAST:	setFacing(Direction.NORTH); // if facing east and agent turns left new direction is north 
							System.out.println("Agent has turned left, now facing north.");
							break;
				case WEST:  setFacing(Direction.SOUTH); // if facing west and agent turns left new direction is south 
							System.out.println("Agent has turned left, now facing south.");
							break;
			}
		} 
		if (value==Turn.RIGHT){ 
			switch(this.getFacing()){
				case NORTH: setFacing(Direction.EAST); // if facing north and agent turns right new direction is east
							System.out.println("Agent has turned right, now facing east.");
						 	break;
				case SOUTH: setFacing(Direction.WEST); // if facing south and agent turns right new direction is west
							System.out.println("Agent has turned right, now facing west.");
							break;
				case EAST:	setFacing(Direction.SOUTH); // if facing east and agent turns right new direction is south 
							System.out.println("Agent has turned right, now facing south.");
							break;
				case WEST:  setFacing(Direction.NORTH); // if facing west and agent turns right new direction is north 
							System.out.println("Agent has turned right, now facing north.");
							break;
			}
		}
		if (value==Turn.DOUBLE){
			switch(this.getFacing()){
			case NORTH: setFacing(Direction.SOUTH); // if facing north and agent turns left twice new direction is south
						System.out.println("Agent has turned left twice, now facing south.");
					 	break;
			case SOUTH: setFacing(Direction.NORTH); // if facing south and agent turns left twice new direction is north 
						System.out.println("Agent has turned left twice, now facing north.");
						break;
			case EAST:	setFacing(Direction.WEST); // if facing east and agent turns left twice new direction is west
						System.out.println("Agent has turned left twice, now facing west.");
						break;
			case WEST:  setFacing(Direction.EAST); // if facing west and agent turns left twice new direction is east
						System.out.println("Agent has turned left twice, now facing east.");
						break;
			}
		}
	}
	
	public BoardCoordinate getPosition() {
		return position;
	}
	
	public void setPosition(BoardCoordinate position) {
		this.position = position;
	}
	
	private Direction addTurnsToFaceNeighToPlan(BoardCoordinate from, BoardCoordinate to, Direction currentFacing){
		//if locations are the same
		if(from==to){
			this.plan.add(Turn.NONE);
			return  currentFacing;
		}
			
		Direction toDirection=null;
		Direction newFacing=null;
		int vertDiff = from.getX()-to.getX(); //vertical difference
		int horzDiff = from.getY()-to.getY(); //horizontal difference
		
		//find out what direction the 'to' location is relative to the 'from' location
		if(vertDiff>0) // to is above from 
			toDirection=Direction.NORTH;
		else if(vertDiff<0) // to is below from 
			toDirection=Direction.SOUTH;
		else{ // when vertDiff==0, to is on same row
			if (horzDiff<0)// to is 
				toDirection=Direction.EAST;
			else
				toDirection=Direction.WEST;
		}
		
		//find turn(s) necessary to face in the direction of 'to' location, add needed turn(s) to plan
		switch (currentFacing){
			case NORTH: if (toDirection==Direction.NORTH){ // already facing north 
							this.plan.add(Turn.NONE);
							newFacing=Direction.NORTH;
						}
						else if(toDirection==Direction.SOUTH){ // if facing north turn twice to face south 
							this.plan.add(Turn.DOUBLE);
							newFacing=Direction.SOUTH;
						}
						else if(toDirection==Direction.EAST){ // if facing north turn right to face east 
							this.plan.add(Turn.RIGHT);
							newFacing=Direction.EAST;
						}
						else{ // if facing north turn left to face west 
							this.plan.add(Turn.LEFT);
							newFacing=Direction.WEST;
						}
						break;
			
			case SOUTH: if (toDirection==Direction.NORTH){ // if facing south turn twice to face north  
							this.plan.add(Turn.DOUBLE);
							newFacing=Direction.NORTH;
						}
						else if(toDirection==Direction.SOUTH){ // already facing south
							this.plan.add(Turn.NONE);
							newFacing=Direction.SOUTH;
						}
						else if(toDirection==Direction.EAST){ // if facing south turn left to face east 
							this.plan.add(Turn.LEFT);
							newFacing=Direction.EAST;
						}
						else{ // if facing south turn right to face west 
							this.plan.add(Turn.RIGHT);
							newFacing=Direction.WEST;
						}
						break;
			
			case EAST:  if (toDirection==Direction.NORTH){ // if facing east turn left to face north 
						this.plan.add(Turn.LEFT);
						newFacing=Direction.NORTH;
						}
						else if(toDirection==Direction.SOUTH){ // if facing east turn right to face south 
							this.plan.add(Turn.RIGHT);
							newFacing=Direction.SOUTH;
						}
						else if(toDirection==Direction.EAST){ // already facing east 
							this.plan.add(Turn.NONE);
							newFacing=Direction.EAST;
						}
						else{ // if facing east turn left twice to face west 
							this.plan.add(Turn.DOUBLE);
							newFacing=Direction.WEST;
						}
						break;
				
			case WEST:  if (toDirection==Direction.NORTH){ // if facing west turn right to face north
						this.plan.add(Turn.RIGHT);
						newFacing=Direction.NORTH;
						}
						else if(toDirection==Direction.SOUTH){ // if facing west turn left to face south 
							this.plan.add(Turn.LEFT);
							newFacing=Direction.SOUTH;
						}
						else if(toDirection==Direction.EAST){ // if facing west turn left twice to face east 
							this.plan.add(Turn.DOUBLE);
							newFacing=Direction.EAST;
						}
						else{ // already facing west 
							this.plan.add(Turn.NONE);
							newFacing=Direction.WEST;
						}
						break;
		}
		
		return newFacing;	 
	}
	
	private Direction getFacing() {
		return facing;
	}
	
	private void setFacing(Direction facing) {
		this.facing = facing;
	}	
}
