/**
 * InvasionConstants.java
 * edu.jhu.cs.aheinz2.oose.invasion.iface
 * @author alex_heinz
 * Created on Sep 13, 2010
 *
 * Invasion game/board constants useful to both the model and UI.
 */

package edu.jhu.cs.aheinz2.oose.invasion.iface;

import java.util.*;

import edu.jhu.cs.oose.fall2010.invasion.iface.Location;

/**
 * @author alex_heinz
 *
 */
public class InvasionConstants
{
	// Board size
	public static final int INVASION_BOARD_WIDTH = 7;
	public static final int INVASION_BOARD_HEIGHT = INVASION_BOARD_WIDTH;
	
	// Cut-out corner sizes
	public static final int INVASION_BOARD_CORNER_WIDTH = 2;
	public static final int INVASION_BOARD_CORNER_HEIGHT = INVASION_BOARD_CORNER_WIDTH;
	
	// Diagonal connections
	public static final Map<Location, Set<Location>> INVASION_BOARD_DIAGONALS;
	static
	{
		INVASION_BOARD_DIAGONALS = new HashMap<Location, Set<Location>>(); 
		for (int x = 0; x < INVASION_BOARD_WIDTH; x++)
		{
			for (int y = 0; y < INVASION_BOARD_HEIGHT; y++)
			{
				// Shortcut: diagonally join locations with "even" coordinates; i.e., the subset of (valid) locations for which (x + y) is even
				if (coordinatesAreOnBoard(x, y) && (((x + y) % 2) == 0))
				{
					// Add each valid, diagonally-adjacent neighbor of the location to a set
					Set<Location> adjacents = new HashSet<Location>(4);
					if (coordinatesAreOnBoard((x - 1), (y - 1)))
						adjacents.add(new Location((x - 1), (y - 1)));
					if (coordinatesAreOnBoard((x + 1), (y - 1)))
						adjacents.add(new Location((x + 1), (y - 1)));
					if (coordinatesAreOnBoard((x - 1), (y + 1)))
						adjacents.add(new Location((x - 1), (y + 1)));
					if (coordinatesAreOnBoard((x + 1), (y + 1)))
						adjacents.add(new Location((x + 1), (y + 1)));
					
					// Map the location to the set
					INVASION_BOARD_DIAGONALS.put(new Location(x, y), adjacents);
				}
				else
				{
					// If the location has no diagonals, map it to an empty set
					INVASION_BOARD_DIAGONALS.put(new Location(x, y), new HashSet<Location>(0));
				}
			}
		}
	}
	
	/**
	 * Determines if a coordinate pair represents a valid position on the board.
	 * @param x The x-coordinate of the position.
	 * @param y The y-coordinate of the position.
	 * @return true if the specified coordinates are a valid board position, false otherwise.
	 */
	public static boolean coordinatesAreOnBoard(int x, int y)
	{
		// Check that the location is within the bounds of the board
		if ((x < 0) || (y < 0) || (x >= INVASION_BOARD_WIDTH) || (y >= INVASION_BOARD_HEIGHT))
			return false;
		
		// Check that the location does not lie in one of the corners
		// Top left corner
		if ((x < INVASION_BOARD_CORNER_WIDTH) && (y < INVASION_BOARD_CORNER_HEIGHT))
			return false;
		// Top right corner
		if ((x >= (INVASION_BOARD_WIDTH - INVASION_BOARD_CORNER_WIDTH)) && (y < INVASION_BOARD_CORNER_HEIGHT))
			return false;
		// Bottom left corner
		if ((x < INVASION_BOARD_CORNER_WIDTH) && (y >= (INVASION_BOARD_HEIGHT - INVASION_BOARD_CORNER_HEIGHT)))
			return false;
		// Bottom right corner
		if ((x >= (INVASION_BOARD_WIDTH - INVASION_BOARD_CORNER_WIDTH)) && (y >= (INVASION_BOARD_HEIGHT - INVASION_BOARD_CORNER_HEIGHT)))
			return false;
		
		// Valid location
		return true;
	}
	
	/**
	 * Determines whether a given location is valid in the board's coordinate system; i.e., if the location is on the board.
	 * @param location The location to test.
	 * @return True if the location is a valid location on the board, false otherwise.
	 */
	public static boolean locationIsOnBoard(Location location)
	{
		return coordinatesAreOnBoard(location.getX(), location.getY());
	}
}