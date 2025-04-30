package islm;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.continuous.*;
import repast.simphony.space.grid.*;
import repast.simphony.random.RandomHelper;

public class ISLMBuilder extends DefaultContext<Object> {

    public ISLMBuilder() {
        super("ISLMContext");
        build();
    }

    private void build() {
        Zentralbank zentralbank = new Zentralbank();
        add(zentralbank);

        for (int i = 0; i < 50; i++) {
            Haushalt h = new Haushalt(zentralbank);
            add(h);
        }

        for (int i = 0; i < 10; i++) {
            Unternehmen u = new Unternehmen(zentralbank);
            add(u);
        }
    }
}