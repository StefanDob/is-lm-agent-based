package islm.export.datasources;

import repast.simphony.data2.AggregateDataSource;
import islm.agenten.Haushalt;

//deprecated - not used anymore
public class UnemploymentData implements AggregateDataSource {

    @Override
    public String getId() {
        return "UnemploymentRate";
    }

    @Override
    public Class<?> getDataType() {
        return Double.class;
    }

    // wird bei jedem Tick aufgerufen, hier Wert berechnen
    @Override
    public Object get(Iterable<?> objs, int size) {
        int unemployed = 0;
        int total = 0;

        for (Object o : objs) {
            if (o instanceof Haushalt) {
                Haushalt h = (Haushalt) o;
                total++;
                if (h.getArbeitGeber() != null) {
                    unemployed++;
                }
            }
        }
        return total > 0 ? (double) unemployed / total : 0.0;
    }

    @Override
    public void reset() {}

	@Override
	public Class<?> getSourceType() {
		// TODO Auto-generated method stub
		return null;
	}
}

