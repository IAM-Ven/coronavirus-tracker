package io.javabrains.coronavirustracker.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.utils.Constants;

@Service
@Data
public class CoronaVirusDataService {

    private List<LocationStats> allStats = new ArrayList<>();
    private int counter = 0;

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws FileNotFoundException, IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest confirmedRequest = HttpRequest.newBuilder().uri(URI.create(Constants.CONFIRMED_VIRUS_DATA_URL)).build();
        HttpResponse<String> confirmedHttpResponse = client.send(confirmedRequest, HttpResponse.BodyHandlers.ofString());
        StringReader confirmedStringReader = new StringReader(confirmedHttpResponse.body());
        Iterable<CSVRecord> confirmedRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(confirmedStringReader);

        HttpRequest recoveredRequest = HttpRequest.newBuilder().uri(URI.create(Constants.RECOVERED_VIRUS_DATA_URL)).build();
        HttpResponse<String> recoveredHttpResponse = client.send(recoveredRequest, HttpResponse.BodyHandlers.ofString());
        StringReader recoveredStringReader = new StringReader(recoveredHttpResponse.body());
        Iterable<CSVRecord> recoveredRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(recoveredStringReader);

        HttpRequest deathRequest = HttpRequest.newBuilder().uri(URI.create(Constants.DEATHS_VIRUS_DATA_URL)).build();
        HttpResponse<String> deathHttpResponse = client.send(deathRequest, HttpResponse.BodyHandlers.ofString());
        StringReader deathStringReader = new StringReader(deathHttpResponse.body());
        Iterable<CSVRecord> deathRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(deathStringReader);

        for (CSVRecord record : confirmedRecords) {
            LocationStats locationStats = new LocationStats();
            locationStats.setId(counter++);
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));

            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int previousCases = Integer.parseInt(record.get(record.size() - 2));

            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPreviousDay(latestCases - previousCases);
            newStats.add(locationStats);
        }

        counter = 0;
        for (CSVRecord record : recoveredRecords) {
            int latestRecoveredCases = Integer.parseInt(record.get(record.size() - 1));
            int previousCases = Integer.parseInt(record.get(record.size() - 2));

            int current_ptr = counter++;

            newStats.get(current_ptr).setCasesRecovered(latestRecoveredCases);
            newStats.get(current_ptr).setRecoveredFromPreviousDay(latestRecoveredCases-previousCases);
        }

        counter = 0;
        for (CSVRecord record : deathRecords) {
            int deathCases = Integer.parseInt(record.get(record.size() - 1));
            int previousCases = Integer.parseInt(record.get(record.size() - 2));

            int current_ptr = counter++;

            newStats.get(current_ptr).setTotalDeaths(deathCases);
            newStats.get(current_ptr).setDeathCasesFromPreviousDay(deathCases-previousCases);
        }

        this.allStats = newStats;
    }
}
