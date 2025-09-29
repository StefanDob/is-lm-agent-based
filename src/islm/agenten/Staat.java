package islm.agenten;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * {@code Staat} represents the government actor in the simulation economy.
 *
 * <p>The government performs two primary fiscal functions:</p>
 * <ul>
 *   <li>Collecting taxes from firms and households at a fixed rate 
 *       ({@link #steuerRate}).</li>
 *   <li>Redistributing collected revenue equally across all households 
 *       once per month.</li>
 * </ul>
 *
 * <p>Government funds are tracked in the state budget balance 
 * ({@link #staatsHaushaltSaldo}). At the end of each simulated month,
 * the balance is distributed to households as transfers, and reset to zero.</p>
 */
public class Staat {
		/** Fixed tax rate applied to taxable income (20%). */
		private static final double steuerRate = 0.2;
		
		/** The government’s budget balance, representing accumulated tax revenue. */
		private double staatsHaushaltSaldo = 0.0;
		
		
		public void empfangeSteuern(double steuern) {
			this.staatsHaushaltSaldo += steuern;
		}
		
		/**
	     * Finalizes the monthly budget cycle.
	     *
	     * <p>This scheduled method is executed once every 21 ticks
	     * (corresponding to one simulated month). The government distributes
	     * the accumulated budget equally across all households, then reduces
	     * its balance accordingly.</p>
	     *
	     * <p>Each household receives:</p>
	     * <pre>
	     *   transfer = staatsHaushaltSaldo / numberOfHouseholds
	     * </pre>
	     *
	     * <p>After transfers, the state budget is reset to zero.</p>
	     *
	     * <p>Scheduling parameters:</p>
	     * <ul>
	     *   <li>{@code start = 1} (begins at the first tick)</li>
	     *   <li>{@code interval = 21} (every 21 ticks ≈ monthly cycle)</li>
	     *   <li>{@code priority = 2} (executed after household actions)</li>
	     * </ul>
	     */
		@ScheduledMethod(start=1, interval=21, priority= 2)
	    public void finalizeMonth() {
			double transferPerHousehold = staatsHaushaltSaldo / SessionManager.getHausHaltListe().size();
			for(Haushalt h : SessionManager.getHausHaltListe()) {
				h.empfangeTransfer(transferPerHousehold);
				staatsHaushaltSaldo -= transferPerHousehold;
			}
			
		}
		
		public static double getSteuerRate() {
			return steuerRate;
		}
		
		
		
		
}
