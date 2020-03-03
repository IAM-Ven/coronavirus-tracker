package io.javabrains.coronavirustracker.controller;

import java.util.List;

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

        System.out.println(allStats);
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum());
        model.addAttribute("totalNewCases", allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum());

        model.addAttribute("totalRecoveredCases", allStats.stream().mapToInt(stat -> stat.getCasesRecovered()).sum());
        model.addAttribute("totalNewRecoveredCases", allStats.stream().mapToInt(stat -> stat.getRecoveredFromPreviousDay()).sum());

        model.addAttribute("totalDeathCases", allStats.stream().mapToInt(stat -> stat.getTotalDeaths()).sum());
        model.addAttribute("totalNewDeathCases", allStats.stream().mapToInt(stat -> stat.getDeathCasesFromPreviousDay()).sum());
        return "home";
    }

}
