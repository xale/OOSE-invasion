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
	private InvasionModelListener modelListener = null;
	
	private MyInvasionBoardComponent boardView = new MyInvasionBoardComponent();
	private JLabel statusLabel = new JLabel();
	private JButton endButton = new JButton("End Turn");
	
	public MyInvasionUI(Class<? extends InvasionModel> modelClass)
	{
		super();
		
		// Set the window title
		this.setTitle("Invasion");
		
		// Hold onto the class we're using for our model, so we can generate more if necessary
		this.modelClass = modelClass;
		
		// Create a listener for the model
		this.modelListener = new InvasionModelListener()
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
		};
		
		// Create the first model instance
		this.setModel(this.newModelInstance());
		
		// Set up the status label
		this.statusLabel.setText("Pirates play first.");
		
		// Add the status label and button to a panel to be placed at the bottom of the window
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(this.statusLabel, BorderLayout.CENTER);
		bottomPanel.add(this.endButton, BorderLayout.EAST);
		
		// Add the bottom panel and the board to a main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.boardView, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		// Add all GUI elements to this window, and ask them to arrange themselves
		this.setContentPane(mainPanel);
		this.pack();
		
		// Set up the application to close with the window
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	 * Sets the model object representing the current game in progress. Called at the beginning of a game.
	 * @param newModel The model object storing information about a new game to be played.
	 */
	private void setModel(InvasionModel newModel)
	{
		// Remove our listener from the old model, if necessary
		if (this.model != null)
			this.model.removeListener(this.modelListener);
		
		// Swap out the models
		this.model = newModel;
		
		// Add the listener to the model
		if (this.model != null)
			this.model.addListener(this.modelListener);
		
		// Update the board view's model
		this.boardView.setModel(this.model);
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
