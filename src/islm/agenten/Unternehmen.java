package islm.agenten;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.*;

import islm.SessionManager;

public class Unternehmen {
	private double liquiditaet;
	private double preis = 0;
    private double gehalt = 0;; 
    private int inventar = 0;
    
    private double nachfrageLetzterMonat = 0; //TODO make sure this gets set
    private double marginaleKosten = 0;
    
    private int durchgehendeEinstellungsmonate = 0; //anzahl an monaten in denen durchgehend leute eingestellt wurden
    
    private static final int GAMMA = 3; //anzahl an aufeinanderfolgenden monaten in denen konsequent leute eingestellt wurden
    private static final double DELTA = 0.019; //boundries of distribution to increase wage
    private static final double UPPER_PHI_INVENTORIES = 1.0;
    private static final double LOWER_PHI_INVENTORIES = 0.25;
    
    private static final double UPPER_PHI_PRICE = 1.15;
    private static final double LOWER_PHI_PRICE = 0.025;
    
    private static final double THETA = 0.75; //propability of changing price if inventory is not in bounds
    private static final double theta = 0.02;
    
    private List<Unternehmen> typeAPartners = new ArrayList<>(); //buy consumption goods 
    private List<Haushalt> typeBPartners = new ArrayList<>(); // employment
    
    private boolean workerNeedsToBeFired = false;

    
    
    
    
    @ScheduledMethod(start = 1, interval = 21, priority = ScheduleParameters.FIRST_PRIORITY)
    public void beginningOfMonth() {
    	//fire people if you have to do so because of last periods
    	if(workerNeedsToBeFired) {
    		fireRandomWorker();
    	}
    	
    	//adjust wages based on months without hiring
    	if (durchgehendeEinstellungsmonate == 0) {
    		//im letzen monat wurde niemand eingestellt
            adjustWage(true); // increase wage
        } else if (durchgehendeEinstellungsmonate >= GAMMA ) {
            adjustWage(false); // decrease wage
        }
    	
    	//adjust number of employees and price
    	
    	double upperBarrierInventory = UPPER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	double lowerBarrierInventory = LOWER_PHI_INVENTORIES * nachfrageLetzterMonat;
    	
    	double upperBarrierPrice = UPPER_PHI_PRICE * marginaleKosten;
    	double lowerBarrierPrice = LOWER_PHI_PRICE * marginaleKosten;
    	
    	
    	if(inventar > upperBarrierInventory) {
    		//fire randomly chosen worker in next month
    		workerNeedsToBeFired = true;
    		if(preis > upperBarrierPrice) {
    			//decrease price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(false); //decrease prce
    			}
    		}
    	}else if(inventar < lowerBarrierInventory) {
    		//create new position to raise production
    		addTypeBPartner(new Haushalt()); //TODO fix this to not add a new household but to create opportunity for households to apply
    		if(preis > upperBarrierPrice) {
    			//increase price with probability Thita
    			if (RandomHelper.nextDouble() < THETA) {
    				adjustPrice(true); //increase price
    			}
    		}
    		
    	}
    	
    	
    }
    
    
    private Haushalt fireRandomWorker() {
    	if (typeBPartners == null || typeBPartners.isEmpty()) return null;

        int index = RandomHelper.nextInt(typeBPartners.size());
        return typeBPartners.remove(index);
		
	}


	/*
     * decrease wage if increase is false else increase it
     */
    public void adjustWage(boolean increase) {
        double mu = RandomHelper.nextDouble() * DELTA;  // μᵢ ∈ [0, δ)

        if (increase) {
            gehalt *= (1.0 + mu);
        } else {
        	gehalt *= (1.0 - mu);
        }
    }

    /*
     * decrease price if increase is false, else increase it
     */
    public void adjustPrice(boolean increase) {
        double vu = RandomHelper.nextDouble() * theta;  // vᵢ ∈ [0, ϑ)

        if (increase) {
            preis *= (1.0 + vu);
        } else {
            preis *= (1.0 - vu);
        }
    }
    
    //===============================Getter/Setter===========================================
    
    
    public void addTypeAPartner(Unternehmen f) {
        typeAPartners.add(f);
    }

    public void addTypeBPartner(Haushalt f) {
        typeBPartners.add(f);
    }

    public List<Unternehmen> getTypeAPartners() {
        return typeAPartners;
    }

    public List<Haushalt> getTypeBPartners() {
        return typeBPartners;
    }


	public int getNumberOfWorkers() {
		return typeBPartners.size();
	}
	
	public double getPreis() {
		return preis;
	}
	
	public boolean getOpenPosition() {
		//TODO figure out some logic for hiring people
		return true;
	}
	
	public double getGehalt() {
		return gehalt;
	}


	public void empfangeBewerbungAufArbeit(Haushalt haushalt) {
		// TODO Auto-generated method stub 
		
	}
	
    
    
    
	
}



