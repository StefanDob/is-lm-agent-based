package islm;

import repast.simphony.context.Context;

import repast.simphony.dataLoader.ContextBuilder;

public class ISLMBuilder implements ContextBuilder<Object> {

    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("ISLMContext");

        Zentralbank zentralbank = new Zentralbank();
        context.add(zentralbank);

        for (int i = 0; i < 50; i++) {
            Haushalt h = new Haushalt(zentralbank);
            context.add(h);
        }

        for (int i = 0; i < 10; i++) {
            Unternehmen u = new Unternehmen(zentralbank);
            context.add(u);
        }

        return context;
    }
}
