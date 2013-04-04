package main.core;

/**
 * Created with IntelliJ IDEA.
 */
public enum Neighbourhood {
    TRANSPOSE("tr"),
    INSERT("in"),
    EXCHANGE("ex");

    private Neighbourhood(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    private String abbreviation;
}
