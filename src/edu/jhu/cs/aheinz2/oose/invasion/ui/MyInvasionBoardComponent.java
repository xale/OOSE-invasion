/**
 * MyInvasionBoardComponent.java
 * edu.jhu.cs.aheinz2.oose.invasion.ui
 * @author alex_heinz
 * Created on Sep 13, 2010
 *
 * 
 */
package edu.jhu.cs.aheinz2.oose.invasion.ui;

import java.awt.*;
import java.awt.geom.*;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.ImageIcon;

import edu.jhu.cs.aheinz2.oose.invasion.iface.*;
import edu.jhu.cs.oose.fall2010.invasion.iface.*;

/**
 * @author alex_heinz
 *
 */
public class MyInvasionBoardComponent extends JComponent
{
	private InvasionModel model = null;
	private InvasionModelListener modelListener = null;
	
	private Location selectedPieceLocation = null;
	
	private ImageIcon boardImage = null;
	private Dimension defaultBoardDimensions = new Dimension(600, 600);
	
	private ImageIcon invaderImage = null;
	private ImageIcon defenderImage = null;
	
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
		
		// Load image resources
		// Board
		this.boardImage = this.loadImageResource("Board.png");
		
		// Invader (pirate) piece
		this.invaderImage = this.loadImageResource("Invader.png");
		
		// Defender (bulgar) piece
		this.defenderImage = this.loadImageResource("Defender.png");
		
		// Set a default size for the component
		this.setPreferredSize(this.defaultBoardDimensions);
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
		// Copy the graphics context and cast to Graphics2D
		Graphics2D g2d = (Graphics2D)g.create();
		
		// Scale the graphics context
		g2d.scale((this.getWidth() / this.defaultBoardDimensions.getWidth()), (this.getHeight() / this.defaultBoardDimensions.getHeight()));
		
		// Draw the background
		this.boardImage.paintIcon(this, g2d, 0, 0);
		
		// Draw the pieces
		for (int col = 0; col < InvasionConstants.INVASION_BOARD_WIDTH; col++)
		{
			for (int row = 0; row < InvasionConstants.INVASION_BOARD_HEIGHT; row++)
			{
				// Check that these coordinates represent a valid board location
				if (!InvasionConstants.coordinatesAreOnBoard(col, row))
					continue;
				
				// Check if the location is occupied
				Player pieceOwner = model.getPieceOwner(new Location(col, row));
				if (pieceOwner == null)
					continue;
				
				// Get the image corresponding to this player's piece
				// TODO: check if piece is selected
				ImageIcon pieceImage = this.pieceImageForPlayer(pieceOwner);
				
				// Find the point on the component corresponding to these board coordinates
				int x = (int)((col + 1) * (this.defaultBoardDimensions.getWidth() / (InvasionConstants.INVASION_BOARD_WIDTH + 1)));
				int y = (int)((row + 1) * (this.defaultBoardDimensions.getHeight() / (InvasionConstants.INVASION_BOARD_HEIGHT + 1)));
				
				// Center the image on the point
				int drawX = x - (pieceImage.getIconWidth() / 2);
				int drawY = y - (pieceImage.getIconHeight() / 2);
				
				// Draw the image
				pieceImage.paintIcon(this, g2d, drawX, drawY);
			}
		}
	}
	
	/**
	 * Gets the piece image for a piece owned by the specified player.
	 * @param pieceOwner The player who controls the piece.
	 * @return the "invader" image if the player controls the pirates, or the "defender" image if the player controls the bulgars.
	 */
	private ImageIcon pieceImageForPlayer(Player pieceOwner)
	{
		switch (pieceOwner)
		{
			case PIRATE:
				return this.invaderImage;
			case BULGAR:
				return this.defenderImage;
			default:
				break;
		}
		
		throw new RuntimeException("Piece image requested for invalid player: " + pieceOwner);
	}
	
	/**
	 * Attempts to load the specified image file from the .../invasion/ui/resources/ directory.
	 * @param imageName The name (with extension) of the image to load.
	 * @return The image loaded from the file with the specified name.
	 */
	private ImageIcon loadImageResource(String imageName)
	{
		URL imageURL = this.getClass().getResource("resources/" + imageName);
		if (imageURL == null)
			throw new RuntimeException("Could not load resource files. Please ensure that " + imageName + " is in the edu/jhu/cs/aheinz2/oose/invasion/ui/resources/ directory.");
		
		return new ImageIcon(this.getToolkit().getImage(imageURL));
	}
}
