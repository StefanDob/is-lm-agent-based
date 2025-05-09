package islm.agenten;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import islm.SessionManager;
import islm.agenten.Kredite.KreditAnfrage;
import islm.agenten.Kredite.Kreditvertrag;

public class Unternehmen {
	
	private double liquiditat;
	
	private double ausstehendeZahlungenGehalt = 0;
	
	private double ausstehendeZahlungenMaterialien = 0;

    

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
    	
    	
    	//ausstehende Zahlungen Gehalt
    	SessionManager.getBank().zahleLohnAus(ausstehendeZahlungenGehalt);
    	ausstehendeZahlungenGehalt = 0;
    	
    	//ausstehende Zahlungen Materialien
    	SessionManager.getBank().bezahleMaterialien(ausstehendeZahlungenMaterialien);
    	ausstehendeZahlungenMaterialien = 0;
    	
        

        // Investitionswunsch äußern
    	double investitionsBedarfVorhanden = RandomHelper.nextDouble() < 0.25 ? 0 : 1; // 0 in 25% der fälle sonst 1
        double investitionsbedarf = RandomHelper.nextDoubleFromTo(0, 0.5 * liquiditat) * investitionsBedarfVorhanden;
        //only ask for kredit if you do not have more than 3 kredits in order to not got too deep into debt
        if(SessionManager.getBank().getAnzahlKredite(this) <= 1) {
        	SessionManager.getBank().stelleKreditanfrage(new KreditAnfrage(this, investitionsbedarf));
        }
        

        // Zinsen und Tilgung zahlen
        double alleVerbindlichkeiten = SessionManager.getBank().getAlleVerbindlichkeiten(this);
        double tilgungsRate = alleVerbindlichkeiten * RandomHelper.nextDoubleFromTo(0.1, 0.2); //etwa 10 bis 20 des Kredites werden getilgt

        SessionManager.getBank().tilgeVerbindlichkeiten(this, tilgungsRate);
        liquiditat -= tilgungsRate;
        if (liquiditat < -1000) {
            System.out.println("Unternehmen insolvent.");
            SessionManager.getUnternehmenListe().remove(this);
        }
    }

    public void erhalteZahlung(double betrag) {
    	//TODO die Raten für lohnKosten, Materialkosten und Gewinn können dynamisch generiert werden
    	//ziehe zuerst die Kosten für das erstellen des Produktes ab - hier nur lohn und materialkosten
    	double lohnKosten = 0.2 * betrag;
    	ausstehendeZahlungenGehalt += lohnKosten;
    	
    	double materialKosten = 0.3 * betrag;
    	ausstehendeZahlungenMaterialien += materialKosten;
    	
    	//10% sind gewinn und erhöhen das Kapital
        liquiditat += betrag * 0.5;
    }

    public void empfangeKreditvertrag(Kreditvertrag vertrag) {
        liquiditat += vertrag.ursprungsbetrag;
    }

	public void zahleZinsen(double zinsen) {
		System.out.println("Gets here");
		liquiditat -= zinsen;
	}
	
	public boolean istInsolvent() {
		return liquiditat < -1000;
	}
}



