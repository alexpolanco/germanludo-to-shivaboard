package ludo.ui.controls;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import ludo.domainmodel.Counter;
import ludo.domainmodel.manager.CounterManager;
import ludo.domainmodel.manager.GameBoardManager;
import ludo.domainmodel.manager.GameManager;
import ludo.domainmodel.manager.PlayerManager;
import ludo.domainmodel.spielbrett.FeldTyp;
import ludo.domainmodel.spielbrett.GameBoard;
import ludo.exceptions.CounterPositionNotFoundException;
import ludo.exceptions.GameFieldIsOccupiedException;
import ludo.ui.SpielbrettGrafik;

public class FigurenListener implements MouseListener{

	public void mouseClicked(MouseEvent arg0) {
		JLabel label = (JLabel) arg0.getSource();
		System.out.println("Es wurde eine Spielfigur angeklickt");

		// Position of the label on which the player clicked
		Point pointOfClick = new Point(label.getX(), label.getY());
		
		try
		{
			for (Counter counter : PlayerManager.getInstance().getCurrentPlayer().getCounters())
			{
				/*
				 * Does any of the counter's location match the point on which the
				 * player clicked?
				 */
				if(counter.getCurrentLocation() == pointOfClick)
				{
					// Check whether a 6 was diced and whether the counter is in the
					// starting zone
					if (Integer.valueOf(SpielbrettGrafik.getInstance()
							.getDiceValue()) == 6
							&& GameBoardManager.getInstance().getCounterPosition(
									counter).getFieldType().equals(
									FeldTyp.WARTEFELD))
					{
						// Place the counter on the start field
						CounterManager.getInstance().placeCounterOnStartField(counter);
					}
					// Check whether the counter is elsewhere on the GameBoard
					else if (GameBoardManager.getInstance().getIsCounterOnGameBoard(counter))
					{
						CounterManager.getInstance().processCounterMovement(
								counter,
								Integer.valueOf(SpielbrettGrafik.getInstance()
										.getDiceValue()));						
					}
					
					if(Integer.valueOf(SpielbrettGrafik.getInstance().getDiceValue()) != 6)
					{						
						//Nächster Spieler ist an der Reihe
						PlayerManager.getInstance().switchActivePlayer();
					}
					else
					{
						SpielbrettGrafik.getInstance().displayStatusMessage("Der Spieler darf noch einmal würfeln");
						//TODO remove once status bar works
						SpielbrettGrafik.getInstance().setDiceValue(counter.getCounterColor().toString());
					}
					
				}
			}			
		} catch(Exception exc)
		{
			// TODO give error messages on status bar
			exc.printStackTrace();
		}		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}