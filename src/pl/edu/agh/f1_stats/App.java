package pl.edu.agh.f1_stats;

import pl.edu.agh.f1_stats.loader.XLSXLoader;
import pl.edu.agh.f1_stats.model.Race;
import pl.edu.agh.f1_stats.model.Scorers;
import pl.edu.agh.f1_stats.utils.F1Statistics;

import java.util.List;
import java.util.Map;

import static pl.edu.agh.f1_stats.model.Scorers.*;

public class App {

    public static void main(String[] args) throws InterruptedException {
        F1Statistics f1Statistics = new F1Statistics();
        XLSXLoader xlsxLoader = new XLSXLoader();
        List<Race> races = xlsxLoader.loadRacesFromFile("resources/f1-results.xlsx");
        Map<Scorers, Map<String, Integer>> summary = f1Statistics.getPointsSummaryMap(races);
        f1Statistics.printDriversWhoWon(races);
        Thread.sleep(1000);
        System.out.println("\nLewis Hamilton zdobył: " + summary.get(DRIVERS).get("Lewis Hamilton") + " punktów");
        Thread.sleep(1000);
        System.out.println("\nZespół Ferrari zdobył: " + summary.get(TEAMS).get("Ferrari") + " punktów");
        Thread.sleep(1000);
        System.out.println(f1Statistics.getSeasonStatistics(summary.get(DRIVERS)));
        Thread.sleep(1000);
        System.out.println("Zapisywanie arkusza kalkulacyjnego z podsumowaniem sezonu...");
        if (f1Statistics.saveSeasonSummary(races)) {
            System.out.println("Zapisano pomyślnie");
        } else {
            System.out.println("Zapisywanie nie powiodło się");
        }
    }
}
