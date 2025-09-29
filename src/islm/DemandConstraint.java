package islm;

import islm.agenten.Unternehmen;

/**
 * {@code DemandConstraint} represents an unsatisfied demand that a household
 * experienced when attempting to purchase from a given {@link Unternehmen}.
 * 
 * <p>A demand constraint occurs when a household is willing to spend more on 
 * goods than the firm was able (or willing) to supply. This class stores both 
 * the firm responsible and the magnitude of the unsatisfied demand.</p>
 */
public class DemandConstraint {
	/** The firm (Unternehmen) where the demand constraint occurred. */
	private Unternehmen unternehmen; //Unternehmen, dass die Restriktion hatte TODO set this private
	
	/** 
     * The magnitude of the demand restriction (unsatisfied demand).
     * Must be strictly positive. 
     */
	private double restriktion; 
	
	/**
     * Creates a new demand constraint for the given firm and restriction size.
     *
     * @param unternehmen the firm where the constraint occurred
     * @param restriktion the magnitude of unsatisfied demand (must be > 0)
     * @throws RuntimeException if {@code restriktion <= 0}
     */
	public DemandConstraint(Unternehmen unternehmen, double restriktion) {
		this.unternehmen = unternehmen;
		this.restriktion = restriktion;
		if(restriktion <= 0) {
			throw new RuntimeException("Bad restriktion: " + restriktion);
		}
	}
	
	//--- getter ---
	
	public double getRestriktion() {
		return restriktion;
	}
	
	public Unternehmen getUnternehmen() {
		return unternehmen;
	}
}
