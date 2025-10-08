package islm;

import java.util.ArrayList;
import java.util.List;

import islm.agenten.Haushalt;
import islm.agenten.Unternehmen;
import repast.simphony.random.RandomHelper;


/**
 * {@code SessionManager} acts as a central registry and utility class
 * for managing global simulation state.
 * 
 * <p>This class holds references to all households and firms, as well as
 * shared entities.
 * It provides utility methods for random selection of firms, registration 
 * of new agents, and allocation of profits across households.</p>
 *
 * <p><b>Important:</b> Since all fields and methods are static, 
 * {@code SessionManager} functions as a global singleton without requiring instantiation.</p>
 */
public class SessionManager {
	/** Global list of all firms (Unternehmen) in the simulation. */
	public static List<Unternehmen> unternehmenListe = new ArrayList<>();
	
	/** Global list of all households (Haushalt) in the simulation. */
	public static List<Haushalt> hausHaltListe = new ArrayList<>();
	
	
	
	
	/**
     * Returns a randomly selected firm from the global firm list.
     *
     * @return a random {@link Unternehmen} from {@link #unternehmenListe}
     * @throws IllegalArgumentException if {@link #unternehmenListe} is empty
     */
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


	/**
	 * Distributes a given amount of profit among all households in {@code haushaltsListe},
	 * with each household receiving a share proportional to its current liquidity.
	 * <p>
	 * The total liquidity of all households is calculated, and each household receives:
	 * <pre>
	 *   share = (household.liquidity / totalLiquidity) * profit
	 * </pre>
	 * If the total liquidity is zero or the profit is non-positive, no distribution occurs.
	 * </p>
	 *
	 * @param profit the total amount of profit to be distributed (must be > 0)
	 */
	public static void allocateProfits(double profit) {
	    if (profit <= 0 || hausHaltListe.isEmpty()) return;

	    double totalLiquidity = hausHaltListe.stream()
	        .mapToDouble(Haushalt::getLiquiditaet)
	        .sum();

	    if (totalLiquidity == 0) return;

	    for (Haushalt h : hausHaltListe) {
	        double share = (h.getLiquiditaet() / totalLiquidity) * profit;
	        h.empfangeProfit(share);
	    }
	}

	
	
    
  
   
    
}