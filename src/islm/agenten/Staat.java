package islm.agenten;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Staat {
	
	private double steuerRate = 0;
	
	private double staatsKasse = 0;
	
	private double ausstehendeZahlungenGehalt = 0;
	
	private double ausstehendeZahlungenMaterialien = 0;
	
	public Staat(double steuerRate) {
		this.steuerRate = steuerRate;
	}
	
	
	@ScheduledMethod(start = 1, interval = 1)
    public void step() {
		
		
		//ausstehende Zahlungen Gehalt
		SessionManager.getBank().zahleLohnAus(ausstehendeZahlungenGehalt);
    	ausstehendeZahlungenGehalt = 0;
    	
    	//ausstehende Zahlungen Materialien
    	SessionManager.getBank().bezahleMaterialien(ausstehendeZahlungenMaterialien);
    	ausstehendeZahlungenMaterialien = 0;
		
	}
	
	
	public void erhalteSteuern(double betrag) {
		if (betrag > 0) {
        	//TODO die Raten für lohnKosten, Materialkosten und Gewinn können dynamisch generiert werden
        	//das ist basically der gleiche code wie bei den unternehmens ausgaben. Nur das der Staat seine einnahmen direkt verteilt an arbeiter und für materialien
        	//staats kasse wird für erste nicht modelliert
        	//ziehe zuerst die Kosten für das erstellen des Produktes ab - hier nur lohn und materialkosten
        	double lohnKosten = 0.5 * betrag;
        	ausstehendeZahlungenGehalt += lohnKosten;
        
        	double materialKosten = 0.5 * betrag;
        	ausstehendeZahlungenMaterialien += materialKosten;
        }
        
    }
	
	//=========================================================================GETTER/SETTER================================================================================================
	
	public double getSteuerRate() {
		return steuerRate;
	}
	
	public void setSteuerRate(double steuerRate) {
		this.steuerRate = steuerRate;
	}
	
	

}
