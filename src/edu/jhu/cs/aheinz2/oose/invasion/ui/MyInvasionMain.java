/**
 * MyInvasionMain.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 10, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import edu.jhu.cs.aheinz2.oose.invasion.model.MyInvasionModel;
import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionMain
{
	/**
	 * Main executable method. Starts an invasion UI using a specified model class, or the default model if none is specified. 
	 * @param args Arguments to program; first argument will be treated as the name of a model class, if it is present. Others ignored.
	 */
	public static void main(String[] args) throws ClassNotFoundException
	{
		// Assume the default model class (mine!)
		Class<? extends InvasionModel> modelClass = MyInvasionModel.class;
		
		// Check for arguments
		if (args.length > 0)
		{
			// Attempt to load a different model class (exceptions here will not be caught)
			modelClass = Class.forName(args[0]).asSubclass(InvasionModel.class);
		}
		
		// Create the UI using the specified class
		MyInvasionUI ui = new MyInvasionUI(modelClass);
		ui.setLocationRelativeTo(null);
		ui.setVisible(true);
	}
}
