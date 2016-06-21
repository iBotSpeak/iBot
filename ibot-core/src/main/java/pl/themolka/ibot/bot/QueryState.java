package pl.themolka.ibot.bot;

public enum QueryState {
    LOADING("loading"),
    RUNNING("running"),
    GHOST("ghost"),
    ;

    private final String name;

    QueryState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
