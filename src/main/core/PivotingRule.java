package main.core;

/**
 * Created with IntelliJ IDEA.
 */
public enum PivotingRule {
    FIRST_IMPROVEMENT("first"),
    BEST_IMPROVEMENT("best");

    private PivotingRule(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    String abbreviation;
}
