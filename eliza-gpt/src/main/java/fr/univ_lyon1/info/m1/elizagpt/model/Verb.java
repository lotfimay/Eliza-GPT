package fr.univ_lyon1.info.m1.elizagpt.model;

public class Verb {
    private final String firstSingular;
    private final String secondPlural;


    Verb(final String firstSingular, final String secondPlural) {
        this.firstSingular = firstSingular;
        this.secondPlural = secondPlural;
    }

    public String getFirstSingular() {
        return firstSingular;
    }

    public String getSecondPlural() {
        return secondPlural;
    }

}