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
	 * Creates a new invasion board component, representing the specified model.
	 * @param model The represented invasion model drawn by this component.
	 */
	public MyInvasionBoardComponent(InvasionModel model)
	{
		// Hold onto the model we've been given
		this.model = model;
		
		// Create a listener that updates the component
		this.modelListener = new InvasionModelListener()
		{
			@Override
			public void receiveEvent(InvasionModelEvent event)
			{
				MyInvasionBoardComponent.this.repaint();
			}
		};
		
		// Attach the listener to the model
		this.model.addListener(this.modelListener);
	}
	
	public void setModel(InvasionModel newModel)
	{
		// Remove our listener from the old model
		this.model.removeListener(this.modelListener);
		
		// Swap in the new model
		this.model = newModel;
		
		// Add the listener to the new model
		this.model.addListener(this.modelListener);
		
		// Repaint the component
		this.repaint();
	}
}
