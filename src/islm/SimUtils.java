package islm;

import java.util.ArrayList;
import java.util.List;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;

public class SimUtils {

    public static Unternehmen zufaelligesUnternehmen() {
        Context<Object> context = RunState.getInstance().getMasterContext();
        List<Unternehmen> firmen = new ArrayList<>();
        for (Object obj : context.getObjects(Unternehmen.class)) {
            firmen.add((Unternehmen) obj);
        }
        if (firmen.isEmpty()) return null;
        return firmen.get((int)(Math.random() * firmen.size()));
    }
}