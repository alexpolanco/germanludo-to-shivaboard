package ludo.domainmodel.manager;

import java.awt.Color;
import java.awt.Point;

import ludo.domainmodel.MovementDirection;
import ludo.domainmodel.Collision;
import ludo.domainmodel.Counter;
import ludo.domainmodel.GameBoard;
import ludo.domainmodel.GameField;
import ludo.exceptions.CounterPositionNotFoundException;
import ludo.exceptions.GameBoardNotFoundException;
import ludo.exceptions.GameFieldIsOccupiedException;
import ludo.ui.GameBoardUI;

/**
 * Organizes available {@link Counter}s and offers further functionality in
 * regard to {@link Counter}s.
 */
public class CounterManager {

	private static CounterManager self = null;

	private CounterManager() {

	}

	public static CounterManager getInstance() {
		if (self == null)
			self = new CounterManager();
		return self;
	}


	/**
	 * Puts a given {@link Counter} into the starting zone location, which was
	 * originally defined for him. Furthermore it removes the {@link Counter}
	 * from his current {@link GameField} and updates the {@link Counter}
	 * location on the graphical {@link GameBoard}.
	 */
	public void moveCounterToStartingZone(Counter counter) {
		/*
		 * Check, whether the counter is already in game, if yes, we'll remove
		 * him from his current GameField and afterwards place him in the
		 * starting zone.
		 */
		try {
			// Remove the counter from his current GameField
			counter.getOwningPlayer().getGameBoard()
					.getCounterPosition(counter).setIsOccupiedBy(null);

		} catch (CounterPositionNotFoundException e) {
			// The counter is not present on his owners GameBoard - do nothing
		}
		counter.setCurrentLocation(counter.getStartingZoneLocation());
		counter.setActive(false);
		
		GameBoardUI.getInstance().drawCounters();
	}
	
	/**
	 * Places a given {@link Counter} on his {@link Color}s start field, hence:
	 * puts him into the actual game.
	 * 
	 * In case the start field is occupied by a {@link Counter} of another
	 * {@link Color}, that {@link Counter} is defeated and put back into his
	 * owner's starting zone.
	 * 
	 * If the starting field is occupied by a {@link Counter} of the same
	 * {@link Color} as the current {@link Counter}, a message is displayed and
	 * the move is not carried out.
	 * 
	 * @throws CounterPositionNotFoundException
	 * @throws GameFieldIsOccupiedException
	 * @throws GameBoardNotFoundException
	 */
	public void moveCounterToStartField(Counter counter)
			throws CounterPositionNotFoundException,
			GameFieldIsOccupiedException, GameBoardNotFoundException 
	{
		Collision collision = GameBoardManager.getInstance()
				.collisionDetection(counter, 0);

		if (collision != null) {
			// Did we collide with a counter of our own team?
			if (collision.hasSameOwner(counter)) {
				GameBoardUI.getInstance().displayStatusMessage(
						"Eigene Figuren können nicht geschlagen werden.");
				// TODO play should be allowed to chose a different counter for
				// moving
				return;
			} else {
				/*
				 * Remove colliding counter from his GameField and place him in
				 * his starting zone.
				 */
				moveCounterToStartingZone(collision.getCollidingCounter());
			}
		}
		// Update the location of our current counter
		counter.setCurrentLocation(counter.getOwningPlayer()
				.getStartFieldLocation());
		GameBoardManager.getInstance().placeCounterOnGameField(counter, 0);
	}

	/**
	 * Processes the {@link Counter} movement of a given {@link Counter} and
	 * moves him according to the diced number.
	 * 
	 * @param toMove
	 *            the {@link Counter} to be moved on the {@link GameBoard}
	 * @param diceValue
	 *            the diced number
	 * @throws GameFieldIsOccupiedException 
	 * @throws GameBoardNotFoundException 
	 * @throws CounterPositionNotFoundException 
	 */
	public void processCounterMovement(Counter counterToMove, int diceValue) throws CounterPositionNotFoundException, GameBoardNotFoundException, GameFieldIsOccupiedException
	{
		if(GameBoardManager.getInstance().getCounterPosition(counterToMove).getFieldNumber() + diceValue > 43)
		{
			//The Counter would surpass the last GameField
			GameBoardUI.getInstance().displayStatusMessage("Spielzug nicht durchführbar.");
			//TODO allow the player to move a different counter instead			
			return;
			
		} else {
			//TODO collision detected with current field instead of future field
			Collision collision = GameBoardManager.getInstance()
					.collisionDetection(
							counterToMove,
							GameBoardManager.getInstance().getCounterPosition(
									counterToMove).getFieldNumber()
									+ diceValue);

			if(collision != null)
			{
				// Did we collide with a counter of our own team?
				if(collision.hasSameOwner(counterToMove))
				{
					GameBoardUI.getInstance().displayStatusMessage(
							"Eigene Figuren können nicht geschlagen werden.");
					//TODO allow the player to move a different counter instead - maybe by throwing an exception			
					return;
				}
				else
				{
					/*
					 * Remove colliding counter from his GameField and place him in
					 * his starting zone.
					 */
					moveCounterToStartingZone(collision.getCollidingCounter());				
				}
			}
			//Calculate new Location
			Point newLocation = calculateNewLocation(diceValue, counterToMove
					.getOwningPlayer().getGameBoard(), GameBoardManager
					.getInstance().getCounterPosition(counterToMove)
					.getFieldNumber(), counterToMove.getCurrentLocation());
			
			// Update the location of our current counter
			counterToMove.setCurrentLocation(newLocation);
			GameBoardManager.getInstance().placeCounterOnGameField(
					counterToMove,
					GameBoardManager.getInstance().getCounterPosition(
							counterToMove).getFieldNumber()
							+ diceValue);							
			
			//Remove counter from his current GameField
			GameBoardManager.getInstance().getCounterPosition(counterToMove).setIsOccupiedBy(null);

			/*
			 * Check, whether the current player has navigated all his counters
			 * into his home zone.
			 */
			if(PlayerManager.getInstance().hasCompletedGame(counterToMove.getOwningPlayer()))
			{
				PlayerManager.getInstance().grantMedal(counterToMove.getOwningPlayer());
			}
		}	
	}

	/**
	 * Calculates the coordinates when moving a predefined number of fields,
	 * starting from the current {@link GameField} of a given {@link GameBoard}.
	 */
	public Point calculateNewLocation (int diceValue, GameBoard board, int fieldNumber, Point currentLocation)
	{

		while(diceValue > 0)
		{
			//Determine current GameField
			GameField currentField = board.getGameFieldList().get(fieldNumber);
			
			if(currentField.getDirectionToNextField() == MovementDirection.LEFT)
			{
						currentLocation = new Point((int) currentLocation.getX() - 60,
						(int) currentLocation.getY());
			}
			else if(currentField.getDirectionToNextField() == MovementDirection.RIGHT)
			{
				currentLocation = new Point((int) currentLocation.getX() + 60,
						(int) currentLocation.getY());
			}
			else if(currentField.getDirectionToNextField() == MovementDirection.ABOVE)
			{
				currentLocation = new Point((int) currentLocation.getX(),
						(int) currentLocation.getY() - 60);
			}
			else if(currentField.getDirectionToNextField() == MovementDirection.BELOW)
			{
				currentLocation = new Point((int) currentLocation.getX(),
						(int) currentLocation.getY() + 60);
			}			
			--diceValue;
			++fieldNumber;
		}
		return currentLocation;			
	}	
	
}
