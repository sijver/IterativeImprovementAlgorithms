package main.core;

/**
 * Created with IntelliJ IDEA.
 */
public enum PivotingRule {
    FIRST_IMPROVEMENT("First"),
    BEST_IMPROVEMENT("Best");

    private PivotingRule(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    String abbreviation;
}
