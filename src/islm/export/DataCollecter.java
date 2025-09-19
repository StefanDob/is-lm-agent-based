package islm.export;

import java.util.List;
import java.util.stream.Collectors;

import islm.SessionManager;
import islm.agenten.Haushalt;
import islm.agenten.Unternehmen;

public class DataCollecter {
	private static double preis0Monat = 0.0;
	private static double preis0Semianual = 0.0;
	//this method returns all currently employed households
		public static int getOpenPositions() {
			int openPositions = 0;
			for (Unternehmen u  : SessionManager.getUnternehmenListe()) {
				openPositions += u.getOpenPositions();
			}
			
			return openPositions;
		}
	
	//this method returns all currently employed households
	public static int getEmployedCount() {
		int employedCount = 0;
		for (Haushalt h : SessionManager.getHausHaltListe()) {
		    if (h.getArbeitGeber() != null) employedCount++;
		}
		
		return employedCount;
	}
	
	public static int beschäftigteLeute() {
		List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    if (unternehmenListe.isEmpty()) return 0;

	    int summe = 0;
	    for (Unternehmen u : unternehmenListe) {
	    	summe += u.getNumberOfWorkers();
	    }
	    
	    return summe;
	}
	
	
	
	public static double getDurchschnittsgehalt() {
	    List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    if (unternehmenListe.isEmpty()) return 0;

	    double summe = 0;
	    for (Unternehmen u : unternehmenListe) {
	        summe += u.getGehalt();  
	    }

	    return summe / unternehmenListe.size();
	}
	
	public static double getDurchSchnittspreis() {
		List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    if (unternehmenListe.isEmpty()) return 0;

	    double summe = 0;
	    for (Unternehmen u : unternehmenListe) {
	        summe += u.getPreis();  
	    }

	    return summe / unternehmenListe.size();
	}
	
	public static double getDurchSchnittsinventar() {
		List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    if (unternehmenListe.isEmpty()) return 0;

	    double summe = 0;
	    for (Unternehmen u : unternehmenListe) {
	        summe += u.getInventar();  
	    }

	    return summe / unternehmenListe.size();
	}
	
	
	
	
	public static double getGesamtNachfrage() {
		List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    if (unternehmenListe.isEmpty()) return 0;

	    double summe = 0;
	    for (Unternehmen u : unternehmenListe) {
	    	summe += u.getNachfrageLetzterMonat();
	    }
	    
	    return summe;
	}
	
	public static double getGeplanterMonatlicherKonsum() {
		List<Haushalt> haushalte = SessionManager.getHausHaltListe();
		double summe = 0;
	    for (Haushalt h : haushalte) {
	    	summe += h.getPerMonthKonsumption();
	    }
	    
	    return summe;
	}
	
	public static double getAllUnternehmenMoney() {
		double summe = 0;
	    List<Unternehmen> unternehmenListe = SessionManager.getUnternehmenListe();
	    for (Unternehmen u : unternehmenListe) {
	    	summe += u.getLiquiditaet();
	    }
	    
	    return summe;
	}
	
	public static double getAllHouseholdMoney(){
			double summe = 0;
			List<Haushalt> haushalte = SessionManager.getHausHaltListe();
			for (Haushalt h : haushalte) {
		    	summe += h.getLiquiditaet();
		    }
		    
		    return summe;
	}
	
	public static double getAllMoney() {
		return getAllUnternehmenMoney() + getAllHouseholdMoney();
	}
	
	public static double getUnmetDemandRatioDurchschnitt() {
		List<Haushalt> haushalte = SessionManager.getHausHaltListe();
		double summe = 0;
	    for (Haushalt h : haushalte) {
	    	summe += h.getUnmetDemandRatio();
	    }
	    
	    return summe / haushalte.size();
	}
	
	public static double getDurchschnittlicherReservationsGehalt() {
		List<Haushalt> haushalte = SessionManager.getHausHaltListe();
		double summe = 0;
	    for (Haushalt h : haushalte) {
	    	summe += h.getReservationsGehalt();
	    }
	    
	    return summe / haushalte.size();
	}
	
	public static double getVermögensGini(){
		List<Double> liquidList = SessionManager.getHausHaltListe().stream().map(Haushalt::getLiquiditaet).collect(Collectors.toList());
		
		return InequalityUtils.gini(liquidList);
	}
	
	public static double getGehaltsGini(){
		List<Double> liquidList = SessionManager.getHausHaltListe().stream().map(Haushalt::getAktuellesGehalt).collect(Collectors.toList());
		
		return InequalityUtils.gini(liquidList);
	}

	public static double getEinkommenGini() {
		List<Double> liquidList = SessionManager.getHausHaltListe().stream().map(Haushalt::getAktuellesEinkommen).collect(Collectors.toList());
		
		return InequalityUtils.gini(liquidList);
		
	}
	
	//this method is only allowed to be called once per period
	public static double getDeltaPriceMonat() {
		double preis1 = getDurchSchnittspreis();
		double deltaPreis = (preis1 - preis0Monat) / preis0Monat;
		preis0Monat = preis1;
		return deltaPreis;
		
	}
	
	//this method is only allowed to be called once per period
		public static double getDeltaPriceSemianual() {
			double preis1 = getDurchSchnittspreis();
			double deltaPreis = (preis1 - preis0Semianual) / preis0Semianual;
			preis0Semianual = preis1;
			return deltaPreis;
			
		}

	public static double getDurchschnittsArbeitslosenMonat() {
		return SessionManager.getUnemploymentTracker().getUnemploymentMonat().stream().mapToInt(Integer::intValue).average().orElse(0.0);
	}
	
	public static double getDurchschnittsArbeitslosenSemianual() {
		return SessionManager.getUnemploymentTracker().getUnemploymentQuarter().stream().mapToInt(Integer::intValue).average().orElse(0.0);
	}
	
	
	
}
