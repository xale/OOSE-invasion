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
		super();
		
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
		
		// Add a listener to the model
		model.addListener(new InvasionModelListener()
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
}
