/**
 * MyInvasionMain.java
 * edu.jhu.cs.aheinz2.oose.invasion
 * @author alex_heinz
 * Created on Sep 10, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion;

import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionMain
{
	/**
	 * @param args
	 */
	public static void main(String[] args) throws ClassNotFoundException
	{
		// Assume the default model class (mine!)
		Class<? extends InvasionModel> modelClass = MyInvasionModel.class;
		
		// Check for arguments
		if (args.length > 0)
		{
			// Attempt to load a different model class
			modelClass = Class.forName(args[0]).asSubclass(InvasionModel.class);
		}
		
		// 
	}
}
