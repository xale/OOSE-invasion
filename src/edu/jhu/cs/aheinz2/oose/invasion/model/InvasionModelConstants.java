/**
 * InvasionModelConstants.java
 * edu.jhu.cs.aheinz2.oose.invasion.model
 * @author alex_heinz
 * Created on Sep 2, 2010
 *
 * 
 */

package edu.jhu.cs.aheinz2.oose.invasion.model;

import edu.jhu.cs.oose.fall2010.invasion.iface.Location;

/**
 * @author alex_heinz
 *
 */
interface InvasionModelConstants
{
	// Board size
	static final int INVASION_BOARD_WIDTH = 7;
	static final int INVASION_BOARD_HEIGHT = INVASION_BOARD_WIDTH;
	
	// Cut-out corner sizes
	static final int INVASION_BOARD_CORNER_WIDTH = 2;
	static final int INVASION_BOARD_CORNER_HEIGHT = INVASION_BOARD_CORNER_WIDTH;
	
	// Bounding box on the fortress
	static final int INVASION_BOARD_FORTRESS_MIN_X = 2;
	static final int INVASION_BOARD_FORTRESS_MIN_Y = 4;
	static final int INVASION_BOARD_FORTRESS_MAX_X = 4;
	static final int INVASION_BOARD_FORTRESS_MAX_Y = 6;
	
	// Initial setup data
	static final int INVASION_BOARD_NUM_PIRATE_OCCUPIED_ROWS = 4;
	static final Location[] INVASION_BOARD_INITIAL_BULGAR_LOCATIONS =
	{
		new Location(2, 5),
		new Location(4, 5)
	};
	
	// Victory condition for bulgars
	static final int INVASION_BOARD_MINIMUM_PIRATES_TO_WIN = 9;
	
	// Lookup table for fortress distances (-1 is off of board)
	static final int[][] INVASION_BOARD_FORTRESS_DISTANCES =
	{
		{-1, -1,  2,  2,  2, -1, -1},
		{-1, -1,  2,  1,  1, -1, -1},
		{ 4,  3,  2,  1,  0,  0,  0},
		{ 4,  3,  2,  1,  0,  0,  0},
		{ 4,  3,  2,  1,  0,  0,  0},
		{-1, -1,  2,  1,  1, -1, -1},
		{-1, -1,  2,  2,  2, -1, -1}
	};
}
