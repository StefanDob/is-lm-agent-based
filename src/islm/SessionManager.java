package islm;

import java.util.ArrayList;
import java.util.List;

import islm.agenten.Haushalt;
import islm.agenten.Unternehmen;
import repast.simphony.random.RandomHelper;



public class SessionManager {
	public static List<Unternehmen> unternehmenListe = new ArrayList<>();
	public static List<Haushalt> hausHaltListe = new ArrayList<>();
	
	
	
	
    
	public static Unternehmen getRandomUnternehmen() {
		 int index = RandomHelper.nextIntFromTo(0, unternehmenListe.size() - 1);
		 return unternehmenListe.get(index);
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

	public static void registriereHaushalt(Haushalt h) {
		hausHaltListe.add(h);
		
	}

	public static void registriereUnternehmen(Unternehmen u) {
		unternehmenListe.add(u);
		
	}
	
	
    
  
   
    
}