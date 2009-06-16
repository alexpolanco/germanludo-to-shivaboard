package ludo.ui;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ludo.domainmodel.spieler.SpielerFarbe;
import ludo.domainmodel.spieler.Spielfigur;
import ludo.ui.SpielbrettGrafik;

import org.junit.Test;


public class TestSpielbrettGrafik {

	
	@Test
	public void moveSpielfigur()
	{	
		//Erzeuge Spielfigur
		Spielfigur figur = new Spielfigur(365, 15, 365, 15, null,
				SpielerFarbe.ROT, new ImageIcon(SpielbrettGrafik.getInstance()
						.getImageOrdnerPfad()
						+ "spielerRot.png"));
		
		//Spielbrett anzeigen
		SpielbrettGrafik.getInstance().spielbrettAnzeigen();
		
		//Rote Figur aufs Startfeld setzen
		SpielbrettGrafik.getInstance().zeichneSpielfigur(figur);
		
		//Bewege Figur

	}
}
