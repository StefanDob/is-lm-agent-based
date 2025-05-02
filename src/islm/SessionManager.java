package islm;

import java.util.ArrayList;
import java.util.List;

import islm.agenten.Haushalt;
import islm.agenten.Staat;
import islm.agenten.Unternehmen;
import islm.agenten.Bank;


public class SessionManager {
	public static List<Unternehmen> unternehmenListe = new ArrayList<>();
	public static List<Haushalt> hausHaltListe = new ArrayList<>();
	
	public static Staat staat = new Staat(0.2);
	
	public static Bank bank = new Bank();
	
	
	
    public static Unternehmen zufaelligesUnternehmen() {
        
        if (unternehmenListe.isEmpty()) return null;
        return unternehmenListe.get((int)(Math.random() * unternehmenListe.size()));
    }
    
    public static Haushalt zufaelligerHaushalt() {
        
        if (hausHaltListe.isEmpty()) return null;
        return hausHaltListe.get((int)(Math.random() * hausHaltListe.size()));
    }
    
    public static void registriereUnternehmen(Unternehmen unternehmen) {
    	unternehmenListe.add(unternehmen);
    }
    
    public static void registriereHaushalt(Haushalt haushalt) {
    	hausHaltListe.add(haushalt);
    }
    
    
    //==================================================================GETTER/SETTER=================================================================================================================
    
    // Getter für unternehmenListe
    public static List<Unternehmen> getUnternehmenListe() {
        return unternehmenListe;
    }

    // Setter für unternehmenListe
    public static void setUnternehmenListe(List<Unternehmen> neueUnternehmenListe) {
        unternehmenListe = neueUnternehmenListe;
    }

    // Getter für hausHaltListe
    public static List<Haushalt> getHausHaltListe() {
        return hausHaltListe;
    }

    // Setter für hausHaltListe
    public static void setHausHaltListe(List<Haushalt> neueHausHaltListe) {
        hausHaltListe = neueHausHaltListe;
    }
    
    public static Staat getStaat() {
        return staat;
    }

    public static void setStaat(Staat neuerStaat) {
    	SessionManager.staat = neuerStaat;
    }
    
    public static Bank getBank() {
        return bank;
    }

    public static void setBank(Bank neueBank) {
    	SessionManager.bank = neueBank;
    }
    
   
    
}