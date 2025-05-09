package islm.agenten;

import java.util.ArrayList;
import java.util.*;

import islm.SessionManager;
import islm.agenten.Kredite.KreditAnfrage;
import islm.agenten.Kredite.Kreditvertrag;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Bank {
	
	private List<KreditAnfrage> kreditAnfragen = new ArrayList<>();
    private List<Kreditvertrag> aktiveKredite = new ArrayList<>();
    private double gesamtSparbetrag = 0; // gesamtes Geld das momentan bespart wird
    private double offenerSparBetrag = 0; //geld das noch nicht in kreditverträgen gebunden ist
    Map<Haushalt,Double> sparenProHaushalt = new HashMap<>();
    
    /**
     * es gibt keine garantie das dieser Marktzins gleich bleibt, in jedem schritt kann sich dieser marktzins ändern
     */
    private double marktZins = 0.0;
    
    
    public Bank(double marktZins) {
    	this.marktZins = marktZins;
    }
    
    
    //=====================================Kredite/Sparen===============================================================================================
    
    /**
     * wird von den Haushalten aufgerufen um ihre Geld auf ihr Konto in der Bank zu laden
     * @param betrag Betrag der zu besparen ist
     * @param haushalt von dem der Betrag kommt
     */
    public void empfangeSparbetrag(double betrag, Haushalt haushalt) {
        gesamtSparbetrag += betrag;
        offenerSparBetrag += betrag;
        
        sparenProHaushalt.put(haushalt, betrag);
    }
    
    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
    	//checke ob kredite geschlossen werden können
    	for (Kreditvertrag kreditVertrag : new ArrayList<>(aktiveKredite)) {
    	    if (kreditVertrag.istAbbezahlt()) {
    	        aktiveKredite.remove(kreditVertrag); 
    	    }
    	}
    	
    	verteileOffeneKredite();
    	
    	passeZinsAn();
    	
    	/*
    	try {
            Thread.sleep(5); // sleeps for 1000 milliseconds = 1 second
        } catch (InterruptedException e) {
            e.printStackTrace(); // or handle it more gracefully
        }
        */
    }
    
    public void passeZinsAn() {
    	for(Kreditvertrag kreditVertrag: aktiveKredite) {
			
			
		}
    }

    public void stelleKreditanfrage(KreditAnfrage anfrage) {
        kreditAnfragen.add(anfrage);
    }

    public void verteileOffeneKredite() {
        for (KreditAnfrage anfrage : new ArrayList<>(kreditAnfragen)) {
            if (offenerSparBetrag >= anfrage.betrag) {
                Kreditvertrag vertrag = new Kreditvertrag(anfrage.betrag, anfrage.unternehmen);
                aktiveKredite.add(vertrag);
                anfrage.unternehmen.empfangeKreditvertrag(vertrag);
                offenerSparBetrag -= anfrage.betrag;
                kreditAnfragen.remove(anfrage); // ✅ jetzt sicher
            }
        }
    }
    
    public double getAlleVerbindlichkeiten(Unternehmen unternehmen) {
		double alleVerbindlichkeiten = 0;
		for(Kreditvertrag kreditVertrag: aktiveKredite) {
			alleVerbindlichkeiten += kreditVertrag.getRestschuld();
		}
		
		return alleVerbindlichkeiten;
	}
    
    public double getAnzahlKredite(Unternehmen unternehmen) {
		double anzahlKredite = 0;
		for(Kreditvertrag kreditVertrag: aktiveKredite) {
			if(kreditVertrag.getKreditnehmer().equals(unternehmen)) {
				anzahlKredite ++;
			}
			
		}
		for(KreditAnfrage kreditAnfragen: kreditAnfragen) {
			if(kreditAnfragen.unternehmen.equals(unternehmen)) {
				anzahlKredite ++;
			}
			
		}
		
		return anzahlKredite;
	}
    
    /**
     * 
     * @param unternehmen
     * @param tilgungsRate nicht als prozent sondern als cash betraf
     */
    public void tilgeVerbindlichkeiten(Unternehmen unternehmen, double tilgungsRate) {
    	//Zinsen werden hier nicht getilgt sondern über die KreditVertrag klasse
    	List<Kreditvertrag> aktiveKrediteUnternehmen = getAktiveKrediteUnternehmen(unternehmen);
    	
    	double tilgungsRateAnteil = tilgungsRate / aktiveKrediteUnternehmen.size();
    	for (Kreditvertrag kredit : aktiveKrediteUnternehmen) {
    		//TODO what happens if some of the credits get negative - tilgungsRateAnteil größer als restkredit
            kredit.tilge(tilgungsRateAnteil);
        }
    }
    
    public List<Kreditvertrag> getAktiveKrediteUnternehmen(Unternehmen unternehmen){
    	List<Kreditvertrag> aktiveKrediteUnternehmen = new ArrayList<>();
    	for(Kreditvertrag kreditVertrag: aktiveKredite) {
			if(kreditVertrag.getKreditnehmer().equals(unternehmen)) {
				aktiveKrediteUnternehmen.add(kreditVertrag);
			}
		}
    	return aktiveKrediteUnternehmen;
    }
    
    
    //====================================================Gehalt Verteilen=======================================================================================================================
    /**
     * unternehmen nutzen diese Methode, um Löhne an mitarbeiter auszuzahlen, es wird noch nicht genau modeliert welcher Mitarbeiter in welchem unternehmen arbeitet.
     * stattdessen wird zufällig ein Haushalt gewählt und der entsprechende betrag überwiesen
     * @param lohn
     */
    public void zahleLohnAus(double lohn){
    	SessionManager.zufaelligerHaushalt().erhalteLohn(lohn);
    }

    
    
    //=====================================================Materialen/Produkte Kaufen und Konsumieren================================================================================================
    
    //diese methode wird von unternehmen aufgerufen um ihre materialkosten zu bezahlen
    public void bezahleMaterialien(double materialKosten) {
    	//in diesem modell wird erstmal angenommen, das die Nutzer und Unternehmen gleich konsumieren, indem sie einfach das Geld an zufällige unternehmen verteilen
    	empfangeKonsumZahlung(materialKosten);
    }

    public void empfangeKonsumZahlung(double konsum) {
        if (konsum <= 0) return;

        // Wähle eine zufällige Anzahl von Unternehmen (z. B. zwischen 1 und 5)
        int anzahl = RandomHelper.nextIntFromTo(1, 5);

        // Berechne den Teilbetrag für jedes Unternehmen
        double anteil = konsum / anzahl;

        for (int i = 0; i < anzahl; i++) {
            Unternehmen u = SessionManager.zufaelligesUnternehmen();
            u.erhalteZahlung(anteil);
        }
    }
    
    //===================================================================GETTER/SETTER=============================================================================================================
    
    public double getGesamtSparbetrag() {
    	return gesamtSparbetrag;
    }
    
    public double getMarktZins() {
    	return marktZins;
    }
    
    public List<Kreditvertrag> getAktiveKredite(){
    	return aktiveKredite;
    }
    
    public List<KreditAnfrage> getKreditAnfragen(){
    	return kreditAnfragen;
    }
    
    

	

}
