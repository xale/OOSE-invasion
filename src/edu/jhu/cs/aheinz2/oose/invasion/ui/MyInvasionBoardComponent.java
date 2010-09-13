/**
 * MyInvasionBoardComponent.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 13, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import javax.swing.JComponent;

import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionBoardComponent extends JComponent
{
	private InvasionModel model = null;
	private InvasionModelListener modelListener = null;
	
	/**
	 * Creates a new invasion board component, initially with no represented model.
	 */
	public MyInvasionBoardComponent()
	{
		// Create a listener that updates the component, which will eventually be attached to a model object
		this.modelListener = new InvasionModelListener()
		{
			@Override
			public void receiveEvent(InvasionModelEvent event)
			{
				MyInvasionBoardComponent.this.repaint();
			}
		};
	}
	
	/**
	 * Changes the model object that his component represents, and repaints the component accordingly.
	 * @param newModel The new represented invasion model.
	 */
	public void setModel(InvasionModel newModel)
	{
		// Remove our listener from the old model (if necessary)
		if (this.model != null)
			this.model.removeListener(this.modelListener);
		
		// Swap in the new model
		this.model = newModel;
		
		// Add the listener to the new model
		if (this.model != null)
			this.model.addListener(this.modelListener);
		
		// Repaint the component
		this.repaint();
	}
}
