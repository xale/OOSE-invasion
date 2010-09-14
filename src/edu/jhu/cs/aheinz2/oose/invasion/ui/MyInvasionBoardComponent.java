/**
 * MyInvasionBoardComponent.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 13, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import edu.jhu.cs.aheinz2.oose.invasion.iface.InvasionConstants;
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
		
		// Set a default size for the component
		this.setPreferredSize(new Dimension(500, 500));
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
	
	@Override
	protected void paintComponent(Graphics g)
	{
		// Paint the background black
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Paint the grid
		for (int x = 0; x < InvasionConstants.INVASION_BOARD_WIDTH; x++)
		{
			for (int y = 0; y < InvasionConstants.INVASION_BOARD_HEIGHT; y++)
			{
				if (!InvasionConstants.coordinatesAreOnBoard(x, y))
					continue;
				
				// TODO: WRITEME
			}
		}
		
		// Draw the pieces
		for (int x = 0; x < InvasionConstants.INVASION_BOARD_WIDTH; x++)
		{
			for (int y = 0; y < InvasionConstants.INVASION_BOARD_HEIGHT; y++)
			{
				if (!InvasionConstants.coordinatesAreOnBoard(x, y))
					continue;
				
				// TODO: WRITEME
				// g.drawImage(img, x, y, width, height, null);
			}
		}
		
	}
}
