/**
 * InvasionModelConstants.java
 * edu.jhu.cs.aheinz2.oose.MyInvasionModel
 * @author alex_heinz
 * Created on Sep 2, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.MyInvasionModel;

import edu.jhu.cs.oose.fall2010.invasion.iface.Location;

/**
 * @author alex_heinz
 *
 */
interface InvasionModelConstants
{
	static final int INVASION_BOARD_WIDTH = 7;
	static final int INVASION_BOARD_HEIGHT = INVASION_BOARD_WIDTH;
	static final int INVASION_BOARD_CORNER_WIDTH = 2;
	static final int INVASION_BOARD_CORNER_HEIGHT = INVASION_BOARD_CORNER_WIDTH;
	
	static final int INVASION_BOARD_NUM_PIRATE_OCCUPIED_ROWS = 4;
	static final Location[] INVASION_BOARD_INITIAL_BULGAR_LOCATIONS =
	{
		new Location(2, 5),
		new Location(4, 5)
	};
}
