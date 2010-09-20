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
import java.awt.Color;
import java.awt.event.*;

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
	private boolean gameOver = false;
	
	private MyInvasionBoardComponent boardView = new MyInvasionBoardComponent();
	private JLabel statusLabel = new JLabel();
	private JButton endButton = new JButton("End Turn");
	
	public MyInvasionUI(Class<? extends InvasionModel> modelClass)
	{
		super();
		
		// Set the window title
		this.setTitle("Invasion");
		
		// Hold onto the class we're using for our model, so we can generate more model objects
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
		
		// Create a click listener for the board component
		this.boardView.addClickListener(new InvasionClickListener()
		{
			@Override
			public void locationClicked(Location clickedLocation)
			{
				MyInvasionUI.this.observeLocationClicked(clickedLocation);
			}
		});
		
		// Create a listener for the "end turn" [/ "new game"] button
		this.endButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MyInvasionUI.this.endButtonPressed();
			}
		});
		
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
	 * Called whenever a player clicks the board component.
	 * @param clickedLocation The location clicked on the board (may be null.)
	 */
	protected void observeLocationClicked(Location clickedLocation)
	{
		// If the game is over, disregard clicks
		if (this.gameOver)
			return;
		
		// If the player has "clicked off," clear the selection
		if (clickedLocation == null)
		{
			this.boardView.setSelectedPieceLocation(null);
			return;
		}
		
		// If the player has clicked an opponent's piece, do nothing
		Player pieceOwner = this.model.getPieceOwner(clickedLocation);
		if ((pieceOwner != null) && !pieceOwner.equals(this.model.getCurrentPlayer()))
		{
			return;
		}
		
		// If the player has clicked one of his or her own piece, toggle selection
		if ((pieceOwner != null) && pieceOwner.equals(this.model.getCurrentPlayer()))
		{
			if (clickedLocation.equals(this.boardView.getSelectedPieceLocation()))
				this.boardView.setSelectedPieceLocation(null);
			else
				this.boardView.setSelectedPieceLocation(clickedLocation);
			
			return;
		}
		
		// If the player has clicked an empty location, and the player has a piece selected, attempt to move the piece
		Location selectedLocation = this.boardView.getSelectedPieceLocation();
		if ((pieceOwner == null) && (selectedLocation != null))
		{
			try
			{
				// Make note of whose turn it is before the move (only necessary for models that automatically end the turn)
				Player playerToMove = this.model.getCurrentPlayer();
				
				// Attempt to move the piece, catching the exception in the event of an illegal move
				this.model.move(selectedLocation, clickedLocation);
				
				// If the turn hasn't ended, and the game isn't over, select the moved piece
				if (playerToMove.equals(this.model.getCurrentPlayer()) && !this.gameOver)
					this.boardView.setSelectedPieceLocation(clickedLocation);
			}
			catch (IllegalMoveException illegalMove)
			{
				// Display the reason the move is illegal on the status label
				this.statusLabel.setForeground(Color.red);
				this.statusLabel.setText(illegalMove.getMessage());
			}
		}
	}
	
	/**
	 * Called whenever the user presses the "end turn" button (which also functions as the "new game" button.)
	 */
	protected void endButtonPressed()
	{
		// Check if we're pretending to be a "new game" button
		if (this.gameOver)
		{
			// Create a new model
			this.setModel(this.newModelInstance());
			
			// Reset the "end turn" button
			this.endButton.setText("End Turn");
			
			return;
		}
		
		// Otherwise, attempt to end the turn
		try
		{
			this.model.endTurn();
		}
		catch (IllegalMoveException illegalMove)
		{
			// Display the reason the move is illegal on the status label
			this.statusLabel.setForeground(Color.red);
			this.statusLabel.setText(illegalMove.getMessage());
		}
	}
	
	/**
	 * Called whenever an InvasionModelEvent informing us of a change in board state is received.  
	 */
	protected void observeBoardChanged()
	{
		// Change the status label
		this.statusLabel.setForeground(Color.black);
		this.statusLabel.setText(this.getCurrentPlayerName() + "' turn.");
	}
	
	/**
	 * Called whenever an InvasionModelEvent informing us of a turn change is received.
	 */
	protected void observeTurnChanged()
	{
		// Clear the board selection
		this.boardView.setSelectedPieceLocation(null);
		
		// Change the status label
		this.statusLabel.setForeground(Color.black);
		this.statusLabel.setText(this.getCurrentPlayerName() + "' turn.");
	}
	
	/**
	 * Called when an InvasionModelEvent indicating the end of a game is received.
	 */
	protected void observeGameEnded()
	{
		this.gameOver = true;
		
		// Clear the board selection
		this.boardView.setSelectedPieceLocation(null);
		
		// Update the status label with the winner
		if (this.model.getWinner() != null)
		{
			this.statusLabel.setForeground(Color.green);
			this.statusLabel.setText(this.getWinningPlayerName() + " win!");
		}
		else
		{
			// Don't think this can happen, but just in case
			this.statusLabel.setForeground(Color.black);
			this.statusLabel.setText("Draw!");
		}
		
		// Change the "end turn" button to a "new game" button
		this.endButton.setText("New Game");
	}
	
	/**
	 * Retrieves the name for the player whose turn it is.
	 * @return The name of the player whose turn it is.
	 */
	private String getCurrentPlayerName()
	{
		if (this.model == null)
			return "";
		
		return this.getNameForPlayer(this.model.getCurrentPlayer());
	}
	
	/**
	 * Returns the name for the winning player.
	 * @return The name of the player who won the last game.
	 */
	private String getWinningPlayerName()
	{
		if (this.model == null)
			return "";
		
		return this.getNameForPlayer(this.model.getWinner());
	}
	
	/**
	 * Returns a display name for a given player
	 * @param player The player to name.
	 * @return "Pirates" (if the player controls the pirates) or "Bulgars" (if the player controls the bulgars.)
	 */
	private String getNameForPlayer(Player player)
	{
		switch (player)
		{
			case PIRATE:
				return "Pirates";
			case BULGAR:
				return "Bulgars";
			default:
				break;
		}
		
		throw new RuntimeException("Invalid player in getCurrentPlayerName(): " + player);
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
		
		// Reset the status label
		this.statusLabel.setForeground(Color.black);
		this.statusLabel.setText(this.getCurrentPlayerName() + " play first.");
		
		// Clear the "game over" flag
		this.gameOver = false;
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
