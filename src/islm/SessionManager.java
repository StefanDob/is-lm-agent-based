package islm;

import java.util.ArrayList;
import java.util.List;

import islm.agenten.Haushalt;
import islm.agenten.Unternehmen;



public class SessionManager {
	public static List<Unternehmen> unternehmenListe = new ArrayList<>();
	public static List<Haushalt> hausHaltListe = new ArrayList<>();
	
	
	
	
    
    
    
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

	public static void registriereHaushalt(Haushalt h) {
		hausHaltListe.add(h);
		
	}

	public static void registriereUnternehmen(Unternehmen u) {
		unternehmenListe.add(u);
		
	}
    
  
   
    
}