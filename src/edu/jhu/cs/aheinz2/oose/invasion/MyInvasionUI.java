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
	private InvasionModel model = null;
	
	public MyInvasionUI(Class<? extends InvasionModel> modelClass)
	{
		try
		{
			model = modelClass.newInstance();
		}
		catch (Exception e)
		{
			
		}
	}
}
