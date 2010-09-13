/**
 * MyInvasionUI.java
 * edu.jhu.cs.aheinz2.oose.invasion
 * @author alex_heinz
 * Created on Sep 10, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion;

import javax.swing.JFrame;

import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionUI extends JFrame
{
	private Class<? extends InvasionModel> modelClass = null;
	private InvasionModel model = null;
	
	public MyInvasionUI(Class<? extends InvasionModel> modelClass)
	{
		// Hold onto the class we're using for our model, so we can generate more if necessary
		this.modelClass = modelClass;
		
		// Create the first instance of our model
		try
		{
			this.model = this.modelClass.newInstance();
		}
		catch (Exception e)
		{
			// Invalid model class
			throw new RuntimeException("Invalid model class:", e);
		}
	}
}
