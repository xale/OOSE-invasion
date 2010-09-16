/**
 * MyInvasionClickListener.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 15, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import edu.jhu.cs.oose.fall2010.invasion.iface.Location;

/**
 * A listener for click events on a board component, which include the location clicked.
 * @author alex_heinz
 */
public interface InvasionClickListener
{
	/**
	 * Called when the component to which this listener is listening receives a click.
	 * @param loc The location on the component's board of the click.
	 */
	public void locationClicked(Location loc);
}
