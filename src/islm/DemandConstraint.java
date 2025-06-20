package islm;

import islm.agenten.Unternehmen;

public class DemandConstraint {
	Unternehmen unternehmen; //Unternehmen, dass die Restriktion hatte TODO set this private
	
	double restriktion; //Umfang der nachfrage REstriktion //TODO set this private
	
	
	public DemandConstraint(Unternehmen unternehmen, double restriktion) {
		this.unternehmen = unternehmen;
		this.restriktion = restriktion;
		if(restriktion <= 0) {
			throw new RuntimeException("Bad restriktion: " + restriktion);
		}
	}
	
	
	
	public double getRestriktion() {
		return restriktion;
	}
	
	public Unternehmen getUnternehmen() {
		return unternehmen;
	}
	
	
	
	//TODO getter setter, make sure that this restriktions are being set
}
