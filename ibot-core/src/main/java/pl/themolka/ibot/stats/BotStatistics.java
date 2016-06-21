package pl.themolka.ibot.stats;

import pl.themolka.ibot.bot.BotQuery;

public class BotStatistics extends Statistics {
    private final BotQuery botQuery;

    public BotStatistics(BotQuery botQuery) {
        this.botQuery = botQuery;

        this.registerDefaults();
    }

    @Override
    public void release() {
        /*File directory = new File(this.botQuery.getDirectory(), "stats");
        directory.mkdirs();

        try {
            File target = new File(directory, System.currentTimeMillis() + ".json");
            target.createNewFile();

            Gson gson = new Gson();
            System.out.println(gson.toJson(this.serialize()));
            gson.toJson(this.serialize(), new JsonWriter(new FileWriter(target)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    private void registerDefaults() {
//        this.addPart(new FullTicksPart());
//        this.addPart(new TickTasksPart());
    }
}
