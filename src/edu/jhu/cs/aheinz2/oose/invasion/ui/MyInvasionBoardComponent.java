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
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URL;
import java.util.*;

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
	
	private Set<InvasionClickListener> clickListeners = new HashSet<InvasionClickListener>();
	
	private ImageIcon boardImage = null;
	private static final Dimension DEFAULT_BOARD_DIMENSIONS = new Dimension(600, 600);
	private static final double HORIZ_GRID_SPACING = (DEFAULT_BOARD_DIMENSIONS.getWidth() / (InvasionConstants.INVASION_BOARD_WIDTH + 1));
	private static final double VERT_GRID_SPACING = (DEFAULT_BOARD_DIMENSIONS.getHeight() / (InvasionConstants.INVASION_BOARD_HEIGHT + 1));
	
	private ImageIcon invaderImage = null;
	private ImageIcon selectedInvaderImage = null;
	private ImageIcon defenderImage = null;
	private ImageIcon selectedDefenderImage = null;
	private static final double TURN_PIECE_IMAGE_X = (1.5 * HORIZ_GRID_SPACING);
	private static final double TURN_PIECE_IMAGE_Y = (6.5 * VERT_GRID_SPACING);
	
	private AffineTransform currentTransform = new AffineTransform();
	
	private Location selectedPieceLocation = null;
	private static final double EMPTY_LOCATION_CLICK_RADIUS = 25.0;
	private static final double OCCUPIED_LOCATION_CLICK_RADIUS = 35.0;
	
	/**
	 * Creates a new invasion board component, initially with no represented model.
	 */
	public MyInvasionBoardComponent()
	{
		// Create a listener for model events
		this.modelListener = new InvasionModelListener()
		{
			@Override
			public void receiveEvent(InvasionModelEvent event)
			{
				if (event.isTurnChanged() || event.isGameOver())
				{
					// If the turn or game ended, deselect the current piece and repaint the component
					MyInvasionBoardComponent.this.setSelectedPieceLocation(null);
				}
				else
				{
					// Otherwise, just repaint the component
					MyInvasionBoardComponent.this.repaint();
				}
			}
		};
		
		// Create a listener for resize events
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				MyInvasionBoardComponent.this.recalculateTransform();
			}
		});
		
		// Create a listener for mouse events
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				MyInvasionBoardComponent.this.mouseClicked(e);
			}
		});
		
		// Load image resources
		// Board
		this.boardImage = this.loadImageResource("Board.png");
		
		// Invader (pirate) piece
		this.invaderImage = this.loadImageResource("Invader.png");
		
		// Selected invader piece
		this.selectedInvaderImage = this.loadImageResource("InvaderSelected.png");
		
		// Defender (bulgar) piece
		this.defenderImage = this.loadImageResource("Defender.png");
		
		// Selected defender piece
		this.selectedDefenderImage = this.loadImageResource("DefenderSelected.png");
		
		// Set a default size for the component
		this.setPreferredSize(DEFAULT_BOARD_DIMENSIONS);
	}

	/**
	 * Called when the user clicks on the component; attempts to select/deselect at piece, or move the currently selected piece.
	 * @param event The mouse event corresponding to the click. Should be a MOUSE_CLICKED event.
	 */
	protected void mouseClicked(MouseEvent event)
	{
		// Check that this is a left-mouse (or button 1, for ambidexterity) event
		if (event.getButton() != MouseEvent.BUTTON1)
			return;
		
		// Get the coordinates of the click
		Point2D clickPoint = null;
		try
		{
			// Convert to the transformed coordinate system
			clickPoint = currentTransform.inverseTransform(event.getPoint(), null);
		}
		catch (NoninvertibleTransformException e)
		{
			throw new RuntimeException("Non-invertible affine transform in mouseClicked()");
		}
		
		// Map the clicked point to a location on the board, and inform listeners
		this.sendClickEvent(this.locationAtPoint(clickPoint));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// Copy the graphics context and cast to Graphics2D
		Graphics2D g2d = (Graphics2D)g.create();
		
		// Apply the current graphics transformation
		g2d.setTransform(this.currentTransform);
		
		// Draw the background
		this.boardImage.paintIcon(this, g2d, 0, 0);
		
		// Draw the pieces
		for (int col = 0; col < InvasionConstants.INVASION_BOARD_WIDTH; col++)
		{
			for (int row = 0; row < InvasionConstants.INVASION_BOARD_HEIGHT; row++)
			{
				// Check that these coordinates represent a valid board location
				Location loc = new Location(col, row);
				if (!InvasionConstants.locationIsOnBoard(loc))
					continue;
				
				// Check if the location is occupied
				Player pieceOwner = model.getPieceOwner(loc);
				if (pieceOwner == null)
					continue;
				
				// Get the image corresponding to this player's piece
				ImageIcon pieceImage = this.pieceImageForPlayer(pieceOwner, loc.equals(this.selectedPieceLocation));
				
				// Find the point on the component corresponding to these board coordinates
				int x = (int)((col + 1) * HORIZ_GRID_SPACING);
				int y = (int)((row + 1) * VERT_GRID_SPACING);
				
				// Center the image on the point
				int drawX = x - (pieceImage.getIconWidth() / 2);
				int drawY = y - (pieceImage.getIconHeight() / 2);
				
				// Draw the image
				pieceImage.paintIcon(this, g2d, drawX, drawY);
			}
		}
		
		// Draw the piece indicating whose turn it is
		ImageIcon turnPieceImage = this.pieceImageForPlayer(this.model.getCurrentPlayer(), false);
		int turnPieceX = (int)(TURN_PIECE_IMAGE_X - (turnPieceImage.getIconWidth() / 2));
		int turnPieceY = (int)(TURN_PIECE_IMAGE_Y - (turnPieceImage.getIconHeight() / 2));
		turnPieceImage.paintIcon(this, g2d, turnPieceX, turnPieceY);
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
	
	/**
	 * Retrieves the location on the board of the current selected piece.
	 * @return The location on the board of the current selected piece.
	 */
	public Location getSelectedPieceLocation()
	{
		return selectedPieceLocation;
	}

	/**
	 * Sets the selected piece on the board to the piece at the specified location.
	 * @param newLocation The new location on the board of the new selected piece.
	 */
	public void setSelectedPieceLocation(Location newLocation)
	{
		// Check that there is a piece at the specified location
		if ((newLocation != null) && (this.model.getPieceOwner(newLocation) == null))
			throw new RuntimeException("Attempt to select location with no piece present: " + newLocation);
		
		this.selectedPieceLocation = newLocation;
		
		// Repaint the component
		this.repaint();
	}

	/**
	 * Adds a listener for clicks on this component.
	 * @param listener The listener to add
	 */
	public void addClickListener(InvasionClickListener listener)
	{
		this.clickListeners.add(listener);
	}
	
	/**
	 * Sends a click event at the specified board location to all listeners.
	 * @param clickedLocation The clicked location on the board.
	 */
	private void sendClickEvent(Location clickedLocation)
	{
		for (InvasionClickListener listener : this.clickListeners)
			listener.locationClicked(clickedLocation);
	}
	
	/**
	 * Removes the specified listener from this component
	 * @param listener The listener to remove.
	 */
	public void removeClickListener(InvasionClickListener listener)
	{
		this.clickListeners.remove(listener);
	}
	
	/**
	 * Calculates the affine transform appropriate for the component size.
	 */
	protected void recalculateTransform()
	{
		// Create a transform that maps the board to the largest possible centered square
		if (this.getWidth() > this.getHeight())
		{
			// Wider than tall: shift horizontally to center
			this.currentTransform.setToTranslation(((this.getWidth() - this.getHeight()) / 2), 0);
			this.currentTransform.scale((this.getHeight() / DEFAULT_BOARD_DIMENSIONS.getWidth()), (this.getHeight() / DEFAULT_BOARD_DIMENSIONS.getHeight()));
		}
		else
		{
			// Taller than wide: shift vertically to center
			this.currentTransform.setToTranslation(0, ((this.getHeight() - this.getWidth()) / 2));
			this.currentTransform.scale((this.getWidth() / DEFAULT_BOARD_DIMENSIONS.getWidth()), (this.getWidth() / DEFAULT_BOARD_DIMENSIONS.getHeight()));
		}
	}
	
	/**
	 * Attempts to map a clicked point on the component to a location on the board. 
	 * @param clickPoint The clicked point, in the un-transformed coordinate system.
	 * @return A location on the board, or null if no location is appropriate.
	 */
	private Location locationAtPoint(Point2D clickPoint)
	{
		// Find the nearest row and column on the board grid (note that these indexes may be off the board)
		int nearestCol = ((int)Math.round(clickPoint.getX() / HORIZ_GRID_SPACING) - 1);
		int nearestRow = ((int)Math.round(clickPoint.getY() / VERT_GRID_SPACING) - 1);
		
		// Check if the coordinates are on the board
		if (!InvasionConstants.coordinatesAreOnBoard(nearestCol, nearestRow))
			return null;
		
		// Determine if there is a piece at the nearest location to the click
		Location nearestLocation = new Location(nearestCol, nearestRow);
		Player pieceOwner = model.getPieceOwner(nearestLocation);
		
		// Determine how far away the click was from the coordinates
		double distance = Math.hypot((((nearestCol + 1) * HORIZ_GRID_SPACING) - clickPoint.getX()), (((nearestRow + 1) * VERT_GRID_SPACING) - clickPoint.getY()));
		
		// If the location is unoccupied, use a small radius for click targets
		if ((pieceOwner == null) && (distance <= EMPTY_LOCATION_CLICK_RADIUS))
			return nearestLocation;
		// If the location is occupied, try a larger click radius
		else if ((pieceOwner != null) && (distance <= OCCUPIED_LOCATION_CLICK_RADIUS))
			return nearestLocation;
		
		return null;
	}
	
	/**
	 * Gets the piece image for a piece owned by the specified player.
	 * @param pieceOwner The player who controls the piece.
	 * @param selected Specifies if the piece is currently selected.
	 * @return the "invader" image if the player controls the pirates, or the "defender" image if the player controls the bulgars.
	 */
	private ImageIcon pieceImageForPlayer(Player pieceOwner, boolean selected)
	{
		switch (pieceOwner)
		{
			case PIRATE:
			{
				if (selected)
					return this.selectedInvaderImage;
				else
					return this.invaderImage;
			}	
			case BULGAR:
			{
				if (selected)
					return this.selectedDefenderImage;
				else
					return this.defenderImage;
			}	
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
;