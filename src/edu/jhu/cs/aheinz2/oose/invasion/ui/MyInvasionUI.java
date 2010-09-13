/**
 * MyInvasionUI.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 10, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import java.awt.BorderLayout;

import javax.swing.*;

import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionUI extends JFrame
{
	private Class<? extends InvasionModel> modelClass = null;
	private InvasionModel model = null;
	
	private JLabel statusLabel = new JLabel();
	private JButton endButton = new JButton("End Turn");
	
	public MyInvasionUI(Class<? extends InvasionModel> modelClass)
	{
		super();
		
		// Hold onto the class we're using for our model, so we can generate more if necessary
		this.modelClass = modelClass;
		
		// Create the first instance of our model
		this.model = this.newModelInstance();
		
		// Add a listener to the model
		this.model.addListener(new InvasionModelListener()
		{
			@Override
			public void receiveEvent(InvasionModelEvent event)
			{
				if (event.isBoardChanged())
					MyInvasionUI.this.observeBoardChanged();
				if (event.isTurnChanged())
					MyInvasionUI.this.observeTurnChanged();
				if (event.isGameOver())
					MyInvasionUI.this.observeGameEnded();
			}
		});
		
		// Set up the status label
		this.statusLabel.setText("Pirates play first.");
		
		// Add the status label and button to a panel to be placed at the bottom of the window
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(this.statusLabel, BorderLayout.CENTER);
		bottomPanel.add(this.endButton, BorderLayout.EAST);
		
		// Configure the layout of the bottom panel
		// TODO: WRITEME
		
		// Add all GUI elements to this window
		this.setContentPane(bottomPanel); // FIXME: temporary, for testing
		// TODO: WRITEME
	}

	/**
	 * Called whenever an InvasionModelEvent informing us of a change in board state is received.  
	 */
	protected void observeBoardChanged()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Called whenever an InvasionModelEvent informing us of a turn change is received.
	 */
	protected void observeTurnChanged()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Called when an InvasionModelEvent indicating the end of a game is received.
	 */
	protected void observeGameEnded()
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * Creates a new instance of the current class used for the model.
	 * @return A new model object.
	 */
	private InvasionModel newModelInstance()
	{
		InvasionModel newModel = null;
		try
		{
			newModel = this.modelClass.newInstance();
		}
		catch (Exception e)
		{
			// Invalid model class
			throw new RuntimeException("Invalid model class:", e);
		}
		
		return newModel;
	}
}
