/**
 * MyInvasionModel.java
 * edu.jhu.cs.aheinz2.oose.invasion.model
 * @author alex_heinz
 * Created on Sep 1, 2010
 *
 * InvasionModel-conforming model object implementation
 */

package edu.jhu.cs.aheinz2.oose.invasion.model;

import java.util.*;

import edu.jhu.cs.aheinz2.oose.invasion.iface.*;
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
	private boolean currentPlayerHasJumped = false;
	private Location lastJumpDestination = null;
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
			throw new IllegalMoveException("No piece selected!");
		if (!pieceOwner.equals(this.currentPlayer))
			throw new IllegalMoveException("You must move your own pieces!");
		
		// Check the distance being moved, to determine if this is a move or a jump
		int dx = (toLocation.getX() - fromLocation.getX()), dy = (toLocation.getY() - fromLocation.getY());
		Location jumpedLocation = null;
		
		switch (Math.max(Math.abs(dx), Math.abs(dy)))
		{
			case 0:
				// I doubt the view/controller could ever hand us this, but just in case
				throw new IllegalMoveException("Not a move!");
			
			case 1:
				// If the total distance moved is one location, evaluate this as a "move"
				// A move is valid if:
				// - the player has not already moved or jumped, and
				// - the destination is on the board, and
				// - the locations are connected on the board, and
				// - the destination is not occupied, and
				// - if the player controls the pirates, the piece is not being moved away from the fortress, and
				// - if the player controls the bulgars, no jumps are possible
				
				// Check if the player has already moved
				if (this.currentPlayerHasMoved)
					throw new IllegalMoveException("You may not move twice in one turn!");
				
				// Check if the player has jumped a piece
				if (this.currentPlayerHasJumped)
					throw new IllegalMoveException("You may not make a non-jump move after a jump!");
				
				// Check that the destination is on the board
				if (!InvasionConstants.locationIsOnBoard(toLocation))
					throw new IllegalMoveException("You cannot move off of the board!");
				
				// Check if the locations are connected
				if (!this.board.locationsAreConnected(fromLocation, toLocation))
					throw new IllegalMoveException("Piece cannot reach that location!");
				
				// Check if the destination is occupied
				if (this.board.getPieceAtLocation(toLocation) != null)
					throw new IllegalMoveException("That location is already occupied!");
				
				// Check if a pirate is being moved away from the fortress
				if (this.currentPlayer.equals(Player.PIRATE) && this.board.locationIsFurtherFromFortress(toLocation, fromLocation))
					throw new IllegalMoveException("You may not move pirates away from the fortress!");
				
				// Check if the player has valid jumps (always returns false if the player controls the pirates)
				if (this.board.playerHasLegalJumps(this.currentPlayer))
					throw new IllegalMoveException("You cannot move when a jump is possible!");
				
				// Valid move
				break;
				
			case 2:
				// If the total distance is two, evaluate this as a "jump"
				// A jump is valid if:
				// - the player controls the bulgars, and
				// - the player has not already moved, and
				// - if the player has already jumped, the same piece is being used for this jump, and
				// - the destination is on the board, and
				// - the origin, and destination lie in a straight horizontal, vertical, or 45¡ diagonal line, and
				// - the origin is connected to the jumped location, and
				// - the jumped location is connected to the destination, and
				// - the jumped location contains a pirate, and
				// - the destination location is not occupied
				
				// Check if the player controls the bulgars
				if (!this.currentPlayer.equals(Player.BULGAR))
					throw new IllegalMoveException("Piece cannot reach that location!");
				
				// Check if the player has already moved
				if (this.currentPlayerHasMoved)
					throw new IllegalMoveException("You may not move twice in one turn!");
				
				// Check that if the player has already jumped, he or she is using the same piece for this jump
				if (this.currentPlayerHasJumped && !this.lastJumpDestination.equals(fromLocation))
					throw new IllegalMoveException("You may not move two different pieces in one turn!");
				
				// Check that the destination is on the board
				if (!InvasionConstants.locationIsOnBoard(toLocation))
					throw new IllegalMoveException("You cannot move off of the board!");
				
				// Determine the direction in which the piece is attempting to jump
				int dHoriz = 0, dVert = 0;
				
				// Horizontal
				if (dy == 0)
				{
					// Determine left/right direction
					dHoriz = ((dx > 0) ? 1 : -1);
				}
				// Vertical
				else if (dx == 0)
				{
					// Determine up/down direction (note inverted coordinate system)
					dVert = ((dy > 0) ? 1 : -1);
				}
				// Diagonal
				else if (Math.abs(dx) == Math.abs(dy))
				{
					// Determine horizontal and vertical directions
					dHoriz = ((dx > 0) ? 1 : -1);
					dVert = ((dy > 0) ? 1 : -1);
				}
				else
				{
					// (Anything else is not a straight line)
					throw new IllegalMoveException("Piece cannot reach that location!");
				}
				
				// Determine the location being jumped over
				jumpedLocation = new Location((fromLocation.getX() + dHoriz), (fromLocation.getY() + dVert));
				
				// Check that the origin is connected to the jumped location
				if (!this.board.locationsAreConnected(fromLocation, jumpedLocation))
					throw new IllegalMoveException("Piece cannot reach that location!");
				
				// Check that the jumped location is connected to the destination
				// NOTE: I'm not sure its actually possible for the previous check to succeed and this to fail; this is just for safety
				if (!this.board.locationsAreConnected(jumpedLocation, toLocation))
					throw new IllegalMoveException("Piece cannot reach that location!");
				
				// Check if the jumped location contains a piece
				if (this.board.getPieceAtLocation(jumpedLocation) == null)
					throw new IllegalMoveException("Piece cannot reach that location!");
				
				// Check that the jumped piece is a pirate
				if (!this.getPieceOwner(jumpedLocation).equals(Player.PIRATE))
					throw new IllegalMoveException("You cannot jump your own pieces!");
				
				// Check if the destination is occupied
				if (this.board.getPieceAtLocation(toLocation) != null)
					throw new IllegalMoveException("That location is already occupied!");
				
				// Valid jump
				break;
				
			default:
				throw new IllegalMoveException("Piece cannot reach that location!");
		}
		
		// Move the piece
		this.board.movePiece(fromLocation, toLocation);
		
		// If this was a jump, remove the jumped piece and make note
		if (jumpedLocation != null)
		{
			this.board.removePiece(jumpedLocation);
			this.lastJumpDestination = toLocation;
			this.currentPlayerHasJumped = true;
		}
		else
		{
			// Otherwise, make note that the current player has made a move
			this.currentPlayerHasMoved = true;
		}
		
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
		if (!this.currentPlayerHasMoved && !this.currentPlayerHasJumped && this.board.playerHasLegalMoves(this.currentPlayer))
			throw new IllegalMoveException("You must make a move!");
		
		// Change the current player
		this.currentPlayer = this.getNextPlayer();
		this.currentPlayerHasMoved = false;
		this.currentPlayerHasJumped = false;
		this.lastJumpDestination = null;
		
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
		private MyInvasionPiece[][] contents = new MyInvasionPiece[InvasionConstants.INVASION_BOARD_WIDTH][InvasionConstants.INVASION_BOARD_HEIGHT];
		private int piratesLeft = 0;
		
		/**
		 * Initializes a new board for the game, with all pieces in place.
		 */
		public MyInvasionBoard()
		{
			// Set up the pirates pieces
			for (int x = 0; x < InvasionConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; y < InvasionModelConstants.INVASION_BOARD_NUM_PIRATE_OCCUPIED_ROWS; y++)
				{
					if (InvasionConstants.coordinatesAreOnBoard(x, y))
					{
						this.contents[x][y] = new MyInvasionPiece(Player.PIRATE);
						this.piratesLeft++;
					}
				}
			}
			
			// Set up the bulgars pieces
			for (Location location : InvasionModelConstants.INVASION_BOARD_INITIAL_BULGAR_LOCATIONS)
			{
				this.contents[location.getX()][location.getY()] = new MyInvasionPiece(Player.BULGAR);
			}
		}

		/**
		 * Moves a piece from the specified starting location to the specified destination.
		 * @param fromLocation The current location of the piece to move.
		 * @param toLocation The destination location for the moved piece.
		 */
		public void movePiece(Location fromLocation, Location toLocation)
		{
			// Get the piece at the origin
			MyInvasionPiece piece = this.getPieceAtLocation(fromLocation);
			
			// Remove the piece from the origin
			this.contents[fromLocation.getX()][fromLocation.getY()] = null;
			
			// Place it at the destination
			this.contents[toLocation.getX()][toLocation.getY()] = piece;
		}

		/**
		 * Removes a (jumped/captured) piece (assumed to be a pirate) from the board.
		 * @param pieceLocation The location of the piece to remove.
		 */
		public void removePiece(Location pieceLocation)
		{
			this.contents[pieceLocation.getX()][pieceLocation.getY()] = null;
			this.piratesLeft--;
		}
		
		/**
		 * Determines if the specified player can legally move a piece on the field.
		 * @param player The player attempting to move.
		 * @return True if the player has at least one legal move (or jump), false otherwise.
		 */
		public boolean playerHasLegalMoves(Player player)
		{
			// Iterate over the board, looking for the player's pieces
			for (int x = 0; x < InvasionConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; y < InvasionConstants.INVASION_BOARD_HEIGHT; y++)
				{
					// Check that these coordinates are on the board
					if (InvasionConstants.coordinatesAreOnBoard(x, y))
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
		 * Determines if the specified player owns any pieces that can perform at least valid jump.
		 * @param player The player attempting to jump.
		 * @return True if the player controls the bulgars, and at least one bulgar piece can make at least one jump/capture; false otherwise.
		 */
		public boolean playerHasLegalJumps(Player player)
		{
			// If the player controls the pirates, he or she cannot possibly make any jumps
			if (player.equals(Player.PIRATE))
				return false;
			
			// Search the board for bulgar pieces
			for (int x = 0; x < InvasionConstants.INVASION_BOARD_WIDTH; x++)
			{
				for (int y = 0; y < InvasionConstants.INVASION_BOARD_HEIGHT; y++)
				{
					// Check that these coordinates are on the board
					if (InvasionConstants.coordinatesAreOnBoard(x, y))
					{
						// Determine if these coordinates contain a bulgar
						MyInvasionPiece piece = this.getPieceAtCoordinates(x, y);
						if ((piece != null) && piece.getOwner().equals(player))
						{
							// Check if the piece can perform a jump
							if (this.pieceAtCoordinatesHasLegalJumps(x, y))
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
		private boolean playerCanMovePieceAtCoordinates(Player pieceOwner, int x, int y)
		{
			Location oldLocation = new Location(x, y);
			Location[] adjacentLocations =
			{
				new Location((x - 1), y),	// Left
				new Location((x + 1), y),	// Right
				new Location(x, (y + 1)),	// Down
				new Location(x, (y - 1))	// Up
			};
			
			// Test each direction of horizontal or vertical movement
			for (Location newLocation : adjacentLocations)
			{
				// Check that the location exists
				if (!InvasionConstants.locationIsOnBoard(newLocation))
					continue;
				
				// Check that the location is unoccupied
				if (this.getPieceAtLocation(newLocation) != null)
					continue;
					
				// Check if the move would require a pirate to move away from the fortress
				if ((pieceOwner.equals(Player.PIRATE) && this.locationIsFurtherFromFortress(newLocation, oldLocation)))
					continue;
				
				// Valid location for move
				return true;
			}
			
			// Check for diagonal moves
			for (Location newLocation : InvasionConstants.INVASION_BOARD_DIAGONALS.get(oldLocation))
			{
				// Check that the location is unoccupied
				if (this.getPieceAtLocation(newLocation) != null)
					continue;
						
				// Check if the move would require a pirate to move away from the fortress
				if ((pieceOwner.equals(Player.PIRATE) && this.locationIsFurtherFromFortress(newLocation, oldLocation)))
					continue;
				
				// Valid location for move
				return true;
			}
			
			// If the piece is a bulgar, check for legal jumps
			if (pieceOwner.equals(Player.BULGAR))
			{
				if (this.pieceAtCoordinatesHasLegalJumps(x, y))
					return true;
			}
			
			return false;
		}
		
		/**
		 * Determines if the piece at the specified coordinates (assumed to be a bulgar) can make at least one valid jump.
		 * @param x The x-coordinate of the piece.
		 * @param y The y-coordinate of the piece.
		 * @return True if the piece can jump over at least one pirate piece, false otherwise.
		 */
		private boolean pieceAtCoordinatesHasLegalJumps(int x, int y)
		{
			Location oldLocation = new Location(x, y);
			Location[][] jumps =
			{
				{new Location((x - 1), y), new Location((x - 2), y)},	// Left
				{new Location((x + 1), y), new Location((x + 2), y)},	// Right
				{new Location(x, (y - 1)), new Location(x, (y - 2))},	// Up
				{new Location(x, (y + 1)), new Location(x, (y + 2))}	// Down
			};
			
			// Check for possible horizontal or vertical jumps
			for (Location[] jump : jumps)
			{
				// Check that the location to jump over exists
				if (!InvasionConstants.locationIsOnBoard(jump[0]))
					continue;
				
				// Check that the location to jump to exists, and is unoccupied
				if (!InvasionConstants.locationIsOnBoard(jump[1]) || (this.getPieceAtLocation(jump[1]) != null))
					continue;
				
				// Check that the location to jump over contains a pirate piece
				MyInvasionPiece jumpedPiece = this.getPieceAtLocation(jump[0]);
				if ((jumpedPiece == null) || !jumpedPiece.getOwner().equals(Player.PIRATE))
					continue;
				
				// Valid direction for a jump
				return true;
			}
			
			// Check for diagonal jumps
			for (Location jumpedLocation : InvasionConstants.INVASION_BOARD_DIAGONALS.get(oldLocation))
			{
				// Check if there are any diagonally-adjacent pirates
				MyInvasionPiece piece = this.getPieceAtLocation(jumpedLocation);
				if ((piece != null) && (piece.getOwner().equals(Player.PIRATE)))
				{
					// Extrapolate in the direction of the jump to determine the destination (landing point)
					int dx = (jumpedLocation.getX() - x), dy = (jumpedLocation.getY() - y);
					Location jumpDestination = new Location((jumpedLocation.getX() + dx), (jumpedLocation.getY() + dy));
					
					// Check that the landing point exists and is unoccupied
					if (!InvasionConstants.locationIsOnBoard(jumpDestination) || (this.getPieceAtLocation(jumpDestination) != null))
						continue;
					
					// Check that the location to jump is reachable from the jumped location 
					if (!this.locationsAreConnected(jumpedLocation, jumpDestination))
						continue;
					
					// Valid direction for a jump
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * Checks the board to see if one of the players has reached a victory condition.
		 * @return Player.BULGAR if there are fewer than nine pirates on the board, Player.PIRATE if the fortress is occupied by pirates, or null otherwise.
		 */
		public Player getGameWinner()
		{
			// Check if the pirates control the fortress
			boolean occupied = true;
			for (int x = InvasionModelConstants.INVASION_BOARD_FORTRESS_MIN_X; occupied && (x <= InvasionModelConstants.INVASION_BOARD_FORTRESS_MAX_X); x++)
			{
				for (int y = InvasionModelConstants.INVASION_BOARD_FORTRESS_MIN_Y; occupied && (y <= InvasionModelConstants.INVASION_BOARD_FORTRESS_MAX_Y); y++)
				{
					MyInvasionPiece piece = this.contents[x][y];
					if ((piece == null) || !piece.getOwner().equals(Player.PIRATE))
						occupied = false;
				}
			}
			if (occupied)
				return Player.PIRATE;
			
			// Check if there are enough pirates left to win
			if (this.piratesLeft < InvasionModelConstants.INVASION_BOARD_MINIMUM_PIRATES_TO_WIN)
				return Player.BULGAR;
			
			return null;
		}
		
		/**
		 * Determines if two locations are adjacent and connected by a line on the board.
		 * NOTE: This method is technically direction-sensitive; it tests if a is connected to b, and not vice-versa, but this should be invertible for all locations on the board.
		 * @param a	The first of the two locations to test.
		 * @param b The second of the two locations to test.
		 * @return True if a is connected to b, false otherwise.
		 */
		public boolean locationsAreConnected(Location a, Location b)
		{
			// Test if either of the locations is not on the board
			if (!InvasionConstants.locationIsOnBoard(a) || !InvasionConstants.locationIsOnBoard(b))
				return false;
			
			// Test if the locations are adjacent horizontally
			if ((a.getY() == b.getY()) && (Math.abs(a.getX() - b.getX()) == 1))
				return true;
			
			// Test if the locations are adjacent vertically
			if ((a.getX() == b.getX()) && (Math.abs(a.getY() - b.getY()) == 1))
				return true;
			
			// Test if the locations are connected diagonally
			if (InvasionConstants.INVASION_BOARD_DIAGONALS.get(a).contains(b))
				return true;
			
			return false;
		}
		
		/**
		 * Determines if a new location is further from the fortress than a reference location.
		 * @param newLocation The new location.
		 * @param referenceLocation The reference location.
		 * @return True if newLocation is closer to the fortress than fromLocation, false otherwise.
		 */
		public boolean locationIsFurtherFromFortress(Location newLocation, Location referenceLocation)
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
			// Return the contents of the board at the specified coordinates
			return this.contents[x][y];
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
