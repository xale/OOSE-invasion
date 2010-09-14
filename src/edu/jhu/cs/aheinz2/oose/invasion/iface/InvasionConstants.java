/**
 * InvasionConstants.java
 * edu.jhu.cs.aheinz2.oose.invasion.iface
 * @author alex_heinz
 * Created on Sep 13, 2010
 *
 * Invasion game/board constants useful to both the model and UI.
 */

package edu.jhu.cs.aheinz2.oose.invasion.iface;

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