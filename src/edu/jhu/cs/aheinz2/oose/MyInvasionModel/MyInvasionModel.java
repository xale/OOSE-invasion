/**
 * MyInvasionModel.java
 * edu.jhu.cs.aheinz2.oose.MyInvasionModel
 * @author alex_heinz
 * Created on Sep 1, 2010
 *
 * InvasionModel-conforming model object implementation
 */

package edu.jhu.cs.aheinz2.oose.MyInvasionModel;

import java.util.*;

import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionModel implements InvasionModel
{
	private MyInvasionBoard board = new MyInvasionBoard();
	private Player currentPlayer = Player.PIRATE;	// Pirates play first
	private boolean currentPlayerHasMoved = false;
	private Player winningPlayer = null;
	
	private Set<InvasionModelListener> listeners = new HashSet<InvasionModelListener>();
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#move(edu.jhu.cs.oose.fall2010.invasion.iface.Location, edu.jhu.cs.oose.fall2010.invasion.iface.Location)
	 */
	@Override
	public void move(Location fromLocation, Location toLocation) throws IllegalMoveException
	{
		// Check that the piece being moved is owned by the current player
		Player pieceOwner = this.getPieceOwner(fromLocation);
		if (pieceOwner == null)
			throw new IllegalMoveException("No piece selected");
		if (!pieceOwner.equals(this.currentPlayer))
			throw new IllegalMoveException("You must move your own pieces");
		
		// Attempt to move the piece (invalid moves will throw an exception)
		this.board.movePiece(fromLocation, toLocation);
		
		// Make note that the current player has made a move
		this.currentPlayerHasMoved = true;
		
		// Check if the game is over
		this.winningPlayer = this.board.getGameWinner();
		if (this.winningPlayer != null)
		{
			// Notify listeners of the end of the game
			this.sendEvent(new InvasionModelEvent(true, false, true));
		}
		else
		{
			// Notify listeners of moved piece
			this.sendEvent(new InvasionModelEvent(true, false, false));
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#endTurn()
	 */
	@Override
	public void endTurn() throws IllegalMoveException
	{
		// Check that the player can legally end his or her turn
		if (this.board.playerHasLegalMoves(this.currentPlayer) && !this.currentPlayerHasMoved)
			throw new IllegalMoveException("You must make a move");
		
		// Change the current player
		this.currentPlayer = this.getNextPlayer();
		this.currentPlayerHasMoved = false;
		
		// Notify listeners of turn change
		this.sendEvent(new InvasionModelEvent(false, true, false));
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#getCurrentPlayer()
	 */
	@Override
	public Player getCurrentPlayer()
	{
		return this.currentPlayer;
	}
	
	/**
	 * Returns the next player to play; if the current player is pirates, returns bulgars, and vice-versa.
	 * @return The next player to play.
	 */
	private Player getNextPlayer()
	{
		switch (this.currentPlayer)
		{
			case PIRATE:
				return Player.BULGAR;
			case BULGAR:
				return Player.PIRATE;
			default:
					break;
		}
		
		throw new RuntimeException("Invalid currentPlayer in MyInvasionModel.getNextPlayer(): " + this.currentPlayer);
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#getPieceOwner(edu.jhu.cs.oose.fall2010.invasion.iface.Location)
	 */
	@Override
	public Player getPieceOwner(Location location)
	{
		MyInvasionPiece piece = null;
		
		// Check if there is a piece at the specified location
		piece = this.board.getPieceAtLocation(location);
		
		// If there is a piece at the location, return the piece's owner
		if (piece != null)
			return piece.getOwner();
		
		// Otherwise, return null
		return null;
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#getWinner()
	 */
	@Override
	public Player getWinner()
	{
		return this.winningPlayer;
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#addListener(edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModelListener)
	 */
	@Override
	public void addListener(InvasionModelListener listener)
	{
		// Add to the model's set of listeners
		this.listeners.add(listener);
	}
	
	/**
	 * Sends the specified event to this model's listeners.
	 * @param event The event to send to the listeners.
	 */
	private void sendEvent(InvasionModelEvent event)
	{
		// Iterate over the model's current listeners
		for (InvasionModelListener listener : this.listeners)
		{
			// Send the event to each listener
			listener.receiveEvent(event);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModel#removeListener(edu.jhu.cs.oose.fall2010.invasion.iface.InvasionModelListener)
	 */
	@Override
	public void removeListener(InvasionModelListener listener)
	{
		// Remove from the model's set of listeners
		this.listeners.remove(listener);
	}
	
	/**
	 * Object used by the model to represent an Invasion game board.
	 * @author alex_heinz
	 */
	static class MyInvasionBoard
	{
		// Note: the board is indexed left-to-right, top-to-bottom
		private MyInvasionPiece[][] contents = new MyInvasionPiece[InvasionModelConstants.INVASION_BOARD_WIDTH][InvasionModelConstants.INVASION_BOARD_HEIGHT];
		private Map<Location,Set<Location>> diagonals = new HashMap<Location,Set<Location>>();
		
		/**
		 * Initializes a new board for the game, with all pieces in place.
		 */
		public MyInvasionBoard()
		{
			// Set up the pirates pieces
			for (int x = 0; x < InvasionModelConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; y < InvasionModelConstants.INVASION_BOARD_NUM_PIRATE_OCCUPIED_ROWS; y++)
				{
					if (this.coordinatesAreOnBoard(x, y))
						this.contents[x][y] = new MyInvasionPiece(Player.PIRATE);
				}
			}
			
			// Set up the bulgars pieces
			for (Location location : InvasionModelConstants.INVASION_BOARD_INITIAL_BULGAR_LOCATIONS)
			{
				this.contents[location.getX()][location.getY()] = new MyInvasionPiece(Player.BULGAR);
			}
			
			// Map each location on the board to a set of locations that are adjacent across a diagonal
			for (int x = 0; x < InvasionModelConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; y < InvasionModelConstants.INVASION_BOARD_HEIGHT; y++)
				{
					// Shortcut: diagonally join locations with "even" coordinates; i.e., the subset of (valid) locations for which (x + y) is even
					if (this.coordinatesAreOnBoard(x, y) && (((x + y) % 2) == 0))
					{
						// Add each valid, diagonally-adjacent neighbor of the location to a set
						Set<Location> adjacents = new HashSet<Location>(4);
						if (this.coordinatesAreOnBoard((x - 1), (y - 1)))
							adjacents.add(new Location((x - 1), (y - 1)));
						if (this.coordinatesAreOnBoard((x + 1), (y - 1)))
							adjacents.add(new Location((x + 1), (y - 1)));
						if (this.coordinatesAreOnBoard((x - 1), (y + 1)))
							adjacents.add(new Location((x - 1), (y + 1)));
						if (this.coordinatesAreOnBoard((x + 1), (y + 1)))
							adjacents.add(new Location((x + 1), (y + 1)));
						
						// Map the location to the set
						this.diagonals.put(new Location(x, y), adjacents);
					}
					else
					{
						// If the location has no diagonals, map it to an empty set
						this.diagonals.put(new Location(x, y), new HashSet<Location>(0));
					}
				}
			}
		}

		/**
		 * Attempts to move a piece from the specified location to the specified destination
		 * @param fromLocation The current location of the piece to move.
		 * @param toLocation The destination location for the moved piece.
		 */
		// TODO: cleanup
		public void movePiece(Location fromLocation, Location toLocation) throws IllegalMoveException
		{
			// Check for a piece at the specified location
			MyInvasionPiece piece = this.getPieceAtLocation(fromLocation);
			if (piece == null)
				throw new IllegalMoveException("You have not selected a piece to move");
			
			// Check that the destination location exists, and is empty
			if (!this.locationIsOnBoard(toLocation))
				throw new IllegalMoveException("You cannot move off of the board");
			if (this.contents[toLocation.getX()][toLocation.getY()] != null)
				throw new IllegalMoveException("You cannot move to an occupied location");
			
			// If the player controls the pirates, check that the move does not place the piece further away
			if (piece.getOwner().equals(Player.PIRATE) && this.locationIsFurtherFromFortress(toLocation, fromLocation))
				throw new IllegalMoveException("You cannot move your pirates away from the fortress");
			
			// Check that the destination is reachable
			boolean destinationReachable = false;
			String errorReason = null;
			Location jumpedLocation = null;
			
			// Horizontal/vertical moves
			if ((fromLocation.getX() == toLocation.getX()) || (fromLocation.getY() == fromLocation.getY()))
			{
				// Determine the number of coordinates the piece is moving
				int moveDistance = Math.max(Math.abs(fromLocation.getX() - toLocation.getX()), Math.abs(fromLocation.getY() - toLocation.getY()));
				
				if (moveDistance == 1)
				{
					// Piece can move
					destinationReachable = true;
				}
				else if (moveDistance == 2)
				{
					// Check if the piece is a bulgar
					if (piece.getOwner().equals(Player.BULGAR))
					{
						// Find the location between the start and destination
						if (fromLocation.getX() == toLocation.getX())
						{
							if (toLocation.getY() > fromLocation.getY())
								jumpedLocation = new Location(fromLocation.getX(), (fromLocation.getY() + 1));
							else
								jumpedLocation = new Location(fromLocation.getX(), (fromLocation.getY() - 1));
						}
						else
						{
							if (toLocation.getX() > fromLocation.getX())
								jumpedLocation = new Location((fromLocation.getX() + 1), fromLocation.getY());
							else
								jumpedLocation = new Location((fromLocation.getX() - 1), fromLocation.getY());	
						}
						
						// Check if the location contains a pirate piece
						MyInvasionPiece opponentPiece = this.contents[jumpedLocation.getX()][jumpedLocation.getY()];
						if (opponentPiece != null)
						{
							if (opponentPiece.getOwner().equals(Player.PIRATE))
							{
								// Piece can jump
								destinationReachable = true;
							}
							else
							{
								// Cannot jump own pieces
								errorReason = "You may not jump your own pieces";
							}
						}
						else
						{
							// No piece to jump
							errorReason = "Pieces may not move more than one step";
						}
					}
					else
					{
						// Pirates may not attempt to jump
						errorReason = "Pieces may not move more than one step";
					}
				}
				else
				{
					// Piece cannot move more than one step at a time
					errorReason = "Pieces may not move more than one step";
				}
			}
			// Diagonal moves
			else if (Math.abs(fromLocation.getX() - toLocation.getX()) == Math.abs(fromLocation.getY() - toLocation.getY()))
			{
				// TODO: WRITEME
			}
			// Indirect move (not horizontal, vertical or diagonal)
			else
			{
				// Cannot move in any other directions
				errorReason = "Pieces may not move more than one step";
			}
			
			// Check if the destination is unreachable
			if (!destinationReachable)
				throw new IllegalMoveException(errorReason);
			
			// Otherwise, perform the move
			this.contents[fromLocation.getX()][fromLocation.getY()] = null;
			this.contents[toLocation.getX()][toLocation.getY()] = piece;
			
			// If the move was a jump, removed the jumped piece
			if (jumpedLocation != null)
				this.contents[jumpedLocation.getX()][jumpedLocation.getY()] = null;
		}

		/**
		 * Determines if the specified player can legally move a piece on the field.
		 * @param player The player attempting to move.
		 * @return True if the player has at least one legal move, false otherwise.
		 */
		public boolean playerHasLegalMoves(Player player)
		{
			// Iterate over the board, looking for the player's pieces
			for (int x = 0; x < InvasionModelConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; x < InvasionModelConstants.INVASION_BOARD_HEIGHT; y++)
				{
					// Check that these coordinates are on the board
					if (this.coordinatesAreOnBoard(x, y))
					{
						// Determine if there is a piece owned by the player at these coordinates
						MyInvasionPiece piece = this.contents[x][y];
						if ((piece != null) && piece.getOwner().equals(player))
						{
							// Determine if the piece can be moved
							if (this.playerCanMovePieceAtCoordinates(piece.getOwner(), x, y))
								return true;
						}
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Determines if a piece at the specified coordinates, owned by the specified player, can be moved.
		 * @param pieceOwner The piece's owner.
		 * @param x The x-coordinate of the piece.
		 * @param y The y-coordinate of the piece.
		 * @return True if the piece can be moved (or can jump) in at least one direction, false otherwise.
		 */
		// TODO: cleanup
		private boolean playerCanMovePieceAtCoordinates(Player pieceOwner, int x, int y)
		{
			// Determine if the player controls the pirates
			boolean isPirate = pieceOwner.equals(Player.PIRATE);
			
			Location oldLocation = new Location(x, y);
			
			// Check each possible direction of horizontal or vertical movement
			// Left
			if (this.coordinatesAreOnBoard((x - 1), y) && (this.contents[(x - 1)][y] == null) && !(isPirate && this.locationIsFurtherFromFortress(new Location((x - 1), y), oldLocation)))
				return true;
			// Right
			if (this.coordinatesAreOnBoard((x + 1), y) && (this.contents[(x + 1)][y] == null) && !(isPirate && this.locationIsFurtherFromFortress(new Location((x + 1), y), oldLocation)))
				return true;
			// Down
			if (this.coordinatesAreOnBoard(x, (y + 1)) && (this.contents[x][(y + 1)] == null) && !(isPirate && this.locationIsFurtherFromFortress(new Location(x, (y + 1)), oldLocation)))
				return true;
			// Up
			if (this.coordinatesAreOnBoard(x, (y - 1)) && (this.contents[x][(y - 1)] == null) && !(isPirate && this.locationIsFurtherFromFortress(new Location(x, (y - 1)), oldLocation)))
				return true;
			
			// Check for diagonal moves
			for (Location newLocation : this.diagonals.get(oldLocation))
			{
				if ((this.contents[newLocation.getX()][newLocation.getY()] == null) && !(isPirate && this.locationIsFurtherFromFortress(newLocation, oldLocation)))
					return true;
			}
			
			// If the player controls the bulgars, check for legal jumps
			if (pieceOwner.equals(Player.BULGAR))
			{
				// Left
				if (this.coordinatesAreOnBoard((x - 2), y) && (this.contents[(x - 2)][y] == null) && (this.contents[(x - 1)][y] != null) && (this.contents[(x - 1)][y].getOwner().equals(Player.PIRATE)))
					return true;
				// Right
				if (this.coordinatesAreOnBoard((x + 2), y) && (this.contents[(x + 2)][y] == null) && (this.contents[(x + 1)][y] != null) && (this.contents[(x + 1)][y].getOwner().equals(Player.PIRATE)))
					return true;
				// Up
				if (this.coordinatesAreOnBoard(x, (y - 2)) && (this.contents[x][(y - 2)] == null) && (this.contents[x][(y - 1)] != null) && (this.contents[x][(y - 1)].getOwner().equals(Player.PIRATE)))
					return true;
				// Down
				if (this.coordinatesAreOnBoard(x, (y + 2)) && (this.contents[x][(y + 2)] == null) && (this.contents[x][(y + 1)] != null) && (this.contents[x][(y + 1)].getOwner().equals(Player.PIRATE)))
					return true;
				
				// Check for diagonal jumps
				for (Location newLocation : this.diagonals.get(oldLocation))
				{
					// Check if there are any diagonally-adjacent pirates
					MyInvasionPiece piece = contents[newLocation.getX()][newLocation.getY()];
					if ((piece != null) && (piece.getOwner().equals(Player.PIRATE)))
					{
						// Check that the location on the other side of the pirate exists and is unoccupied
						Location jumpDestination = new Location((x + (newLocation.getX() - x)), (y + (newLocation.getX() - y)));
						if (this.diagonals.get(newLocation).contains(jumpDestination) && (this.contents[jumpDestination.getX()][jumpDestination.getY()] == null))
							return true;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Determines if a new location is further from the fortress than a reference location.
		 * @param newLocation The new location.
		 * @param referenceLocation The reference location.
		 * @return True if newLocation is close to the fortress than fromLocation, false otherwise. 
		 */
		private boolean locationIsFurtherFromFortress(Location newLocation, Location referenceLocation)
		{
			return (this.distanceOfLocationFromFortress(newLocation) > this.distanceOfLocationFromFortress(referenceLocation));
		}
		
		/**
		 * Determines the minimum number of moves necessary to reach the fortress from the given location.
		 * @param location The location for which to determine the distance.
		 * @return The number of moves separating the location and the closest location inside the fortress.
		 */
		public int distanceOfLocationFromFortress(Location location)
		{
			int distance = InvasionModelConstants.INVASION_BOARD_FORTRESS_DISTANCES[location.getX()][location.getY()];
			
			if (distance < 0)
				throw new RuntimeException("Fortress distance requested for coordinates not on board: (" + location.getX() + ", " + location.getY() + ")");
			
			return distance;
		}
		
		/**
		 * Returns the piece at the specified Location.
		 * @return The piece at the specified location on the board, if present, or null.
		 */
		public MyInvasionPiece getPieceAtLocation(Location location)
		{
			return this.getPieceAtCoordinates(location.getX(), location.getY());
		}
		
		/**
		 * Returns the piece at the specified coordinates, or null if no piece exists.
		 * @param x The x-coordinate of the piece.
		 * @param y The y-coordinate of the piece.
		 * @return The piece at (x, y) on the board, or null.
		 */
		private MyInvasionPiece getPieceAtCoordinates(int x, int y)
		{
			// Check if the coordinates are valid (i.e., on the board)
			if (!this.coordinatesAreOnBoard(x, y))
				throw new RuntimeException("Piece requested at coordinates not on board: (" + x + ", " + y + ")");
			
			// Return the contents of the board at the specified coordinates
			return this.contents[x][y];
		}
		
		/**
		 * Determines whether a given location is valid in the board's coordinate system; i.e., if the location is on the board.
		 * @param location The location to test.
		 * @return True if the location is a valid location on the board, false otherwise.
		 */
		public boolean locationIsOnBoard(Location location)
		{
			return this.coordinatesAreOnBoard(location.getX(), location.getY());
		}
		
		/**
		 * Determines if the specified x- and y-coordinates specify a valid location on the board.
		 * @param x The x-coordinate of the location to test.
		 * @param y The y-coordinate of the location to test.
		 * @return True of the board contains (x, y), false otherwise.
		 */
		private boolean coordinatesAreOnBoard(int x, int y)
		{
			// Check that the location is within the bounds of the board
			if ((x < 0) || (y < 0) || (x >= InvasionModelConstants.INVASION_BOARD_WIDTH) || (y >= InvasionModelConstants.INVASION_BOARD_HEIGHT))
				return false;
			
			// Check that the location does not lie in one of the corners
			// Top left corner
			if ((x < InvasionModelConstants.INVASION_BOARD_CORNER_WIDTH) && (y < InvasionModelConstants.INVASION_BOARD_CORNER_HEIGHT))
				return false;
			// Top right corner
			if ((x >= (InvasionModelConstants.INVASION_BOARD_WIDTH - InvasionModelConstants.INVASION_BOARD_CORNER_WIDTH)) && (y < InvasionModelConstants.INVASION_BOARD_CORNER_HEIGHT))
				return false;
			// Bottom left corner
			if ((x < InvasionModelConstants.INVASION_BOARD_CORNER_WIDTH) && (y >= (InvasionModelConstants.INVASION_BOARD_HEIGHT - InvasionModelConstants.INVASION_BOARD_CORNER_HEIGHT)))
				return false;
			// Bottom right corner
			if ((x >= (InvasionModelConstants.INVASION_BOARD_WIDTH - InvasionModelConstants.INVASION_BOARD_CORNER_WIDTH)) && (y >= (InvasionModelConstants.INVASION_BOARD_HEIGHT - InvasionModelConstants.INVASION_BOARD_CORNER_HEIGHT)))
				return false;
			
			// Valid location
			return true;
		}
		
		/**
		 * Checks the board to see if one of the players has reached a victory condition.
		 * @return Player.BULGAR if there are fewer than nine pirates on the board, Player.PIRATE if the fortress is occupied by pirates, or null otherwise.
		 */
		public Player getGameWinner()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Object used by the model to represent a piece on an Invasion game board.
	 * @author alex_heinz
	 */
	static class MyInvasionPiece
	{
		private Player owner = null;
		
		/**
		 * Initializes a piece owned by the specified player.
		 * @param owner The player owning the new piece.
		 */
		public MyInvasionPiece(Player owner)
		{
			this.owner = owner;
		}
		
		/**
		 * Accesses the piece's owner.
		 * @return The piece's owner.
		 */
		public Player getOwner()
		{
			return this.owner;
		}
	}
}
