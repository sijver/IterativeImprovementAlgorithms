package main.core;

/**
 * Created with IntelliJ IDEA.
 */
public enum Neighbourhood {
    TRANSPOSE("T"),
    INSERT("I"),
    EXCHANGE("E");

    private Neighbourhood(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    private String abbreviation;
}
