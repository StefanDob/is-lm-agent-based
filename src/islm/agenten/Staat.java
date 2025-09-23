package islm.agenten;

import islm.SessionManager;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Staat {
		private static final double steuerRate = 0.2;
		
		private double staatsHaushaltSaldo = 0.0;
		
		public static double getSteuerRate() {
			return steuerRate;
		}
		
		public void empfangeSteuern(double steuern) {
			this.staatsHaushaltSaldo += steuern;
		}
		
		@ScheduledMethod(start=1, interval=21, priority= 2)
	    public void finalizeMonth() {
			double transferPerHousehold = staatsHaushaltSaldo / SessionManager.getHausHaltListe().size();
			for(Haushalt h : SessionManager.getHausHaltListe()) {
				h.empfangeTransfer(transferPerHousehold);
				staatsHaushaltSaldo -= transferPerHousehold;
			}
			
		}
		
		
		
}
