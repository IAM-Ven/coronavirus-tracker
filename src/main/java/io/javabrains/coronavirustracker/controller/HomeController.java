package io.javabrains.coronavirustracker.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum());
        model.addAttribute("totalNewCases", allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum());

        model.addAttribute("totalRecoveredCases", allStats.stream().mapToInt(stat -> stat.getCasesRecovered()).sum());
        model.addAttribute("totalNewRecoveredCases", allStats.stream().mapToInt(stat -> stat.getRecoveredFromPreviousDay()).sum());

        model.addAttribute("totalDeathCases", allStats.stream().mapToInt(stat -> stat.getTotalDeaths()).sum());
        model.addAttribute("totalNewDeathCases", allStats.stream().mapToInt(stat -> stat.getDeathCasesFromPreviousDay()).sum());

        // top 5 countries with most reported cases
        List<LocationStats> sorted = allStats.stream().filter(line -> !line.getCountry().contains("China") && !line.getCountry().contains("Others")).sorted(Comparator.comparing(LocationStats::getLatestTotalCases).reversed()).collect(Collectors.toList());
        Map<String, Integer> barChartData = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            barChartData.put(sorted.get(i).getState() + " (" + sorted.get(i).getCountry() + ")", sorted.get(i).getLatestTotalCases());
        }

        model.addAttribute("barChartData", barChartData);

        return "home";
    }

}
