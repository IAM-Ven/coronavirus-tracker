package io.javabrains.coronavirustracker.models;

import lombok.Data;

@Data
public class LocationStats {
    private int id;
    private String state;
    private String country;
    private int latestTotalCases;
    private int diffFromPreviousDay;
    private int casesRecovered;
    private int recoveredFromPreviousDay;
    private int totalDeaths;
    private int deathCasesFromPreviousDay;
}
