package pl.edu.agh.f1_stats.utils;

import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.edu.agh.f1_stats.model.DriverSeasonSummary;
import pl.edu.agh.f1_stats.model.ParticipantData;
import pl.edu.agh.f1_stats.model.Race;
import pl.edu.agh.f1_stats.model.Scorers;

import java.io.*;
import java.util.*;

import static pl.edu.agh.f1_stats.model.Scorers.*;

@NoArgsConstructor
public class F1Statistics {

    public void printDriversWhoWon(List<Race> races) {
        Set<String> winnerDrivers = new HashSet<>();
        for (Race race : races) {
            for (ParticipantData driverData : race.getDriversData()) {
                if (driverData.getPosition().equals("1")) {
                    winnerDrivers.add(driverData.getDriverFirstNameLastName());
                }
            }
        }
        System.out.println("Kierowcy którzy przynajmniej raz wygrali wyścig: ");
        winnerDrivers.forEach(System.out::println);
    }

    public String getSeasonStatistics(Map<String, Integer> drivers) {
        List<DriverSeasonSummary> seasonSummaries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : drivers.entrySet()) {
            seasonSummaries.add(new DriverSeasonSummary(entry.getKey(), entry.getValue()));
        }
        seasonSummaries.sort(DriverSeasonSummary.getComparator());
        Collections.reverse(seasonSummaries);
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append("=".repeat(52)).append("\n");
        builder.append("| Miejsce | ")
                .append("Kierowca")
                .append(" ".repeat(30 - "Kierowca".length()))
                .append("| Punkty |\n");
        for (int i = 0; i < seasonSummaries.size(); i++) {
            builder.append("|    ").append(i + 1).append(" ".repeat(5 - String.valueOf(i + 1).length()));
            builder.append("| ").append(seasonSummaries.get(i).getDriverName()).append(" ".repeat(30 -
                    seasonSummaries.get(i).getDriverName().length()));
            builder.append("|  ").append(seasonSummaries.get(i).getSeasonPoints()).append(" ".repeat(6 -
                    seasonSummaries.get(i).getSeasonPoints().toString().length())).append("|");
            builder.append("\n");
        }
        builder.append("=".repeat(52));
        return builder.toString();
    }

    public Map<Scorers, Map<String, Integer>> getPointsSummaryMap(List<Race> races) {
        Map<Scorers, Map<String, Integer>> summaryMap = new EnumMap<>(Scorers.class);
        Map<String, Integer> driverSummary = new HashMap<>();
        Map<String, Integer> teamSummary = new HashMap<>();
        for (Race race : races) {
            for (ParticipantData driverData : race.getDriversData()) {
                Integer driverPoints = driverSummary.putIfAbsent(driverData.getDriverFirstNameLastName(), 0);
                Integer teamPoints = teamSummary.putIfAbsent(driverData.getTeam(), 0);
                if (driverPoints != null) {
                    driverSummary.put(driverData.getDriverFirstNameLastName(), driverPoints + driverData.getPointsCollected());
                }
                if (teamPoints != null) {
                    teamSummary.put(driverData.getTeam(), teamPoints + driverData.getPointsCollected());
                }
            }
        }
        summaryMap.put(DRIVERS, driverSummary);
        summaryMap.put(TEAMS, teamSummary);
        return summaryMap;
    }

    public boolean saveSeasonSummary(List<Race> races) {
        Workbook seasonSummary = new XSSFWorkbook();
        Sheet sheet = seasonSummary.createSheet("Season Summary");
        Row row = sheet.createRow(0);
        row.createCell(0, CellType.STRING).setCellValue("Kierowcy");
        for (int i = 0; i < races.size(); i++) {
            row.createCell(i +1, CellType.STRING).setCellValue(races.get(i).getCountry());
        }
        Map<String, Map<String, Integer>> driversPointsSummary = getDriversPointsSummary(races);
        int rowCounter = 1;
        for (Map.Entry<String, Map<String, Integer>> summary : driversPointsSummary.entrySet()) {
            int pointsCounter = 0;
            Row driverRow = sheet.createRow(rowCounter);
            driverRow.createCell(0, CellType.STRING).setCellValue(summary.getKey());
            Map<String, Integer> selectedRaceDriverPoints = summary.getValue();
            for (int j = 0; j < races.size(); j++) {
                if (selectedRaceDriverPoints.containsKey(races.get(j).getCountry())) {
                    pointsCounter += selectedRaceDriverPoints.get(races.get(j).getCountry());
                }
                driverRow.createCell(j + 1, CellType.NUMERIC).setCellValue(pointsCounter);
            }
            rowCounter++;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        return saveWorkbook(seasonSummary);
    }

    private Map<String, Map<String, Integer>> getDriversPointsSummary(List<Race> races) {
        Map<String, Map<String, Integer>> pointsSummary = new HashMap<>();
        for (Race race : races) {
            for (ParticipantData driverData : race.getDriversData()) {
                Map<String, Integer> driverPoints = pointsSummary.get(driverData.getDriverFirstNameLastName());
                if (driverPoints != null) {
                    driverPoints.put(race.getCountry(), driverData.getPointsCollected());
                    pointsSummary.put(driverData.getDriverFirstNameLastName(), driverPoints);
                } else {
                    Map<String, Integer> newMap = new HashMap<>();
                    newMap.put(race.getCountry(), driverData.getPointsCollected());
                    pointsSummary.put(driverData.getDriverFirstNameLastName(), newMap);
                }
            }
        }
        return pointsSummary;
    }

    private boolean saveWorkbook(Workbook workbook) {
        try(BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(getDesktopPath() + "/Standings.xlsx"))) {
            workbook.write(os);
            return true;
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas zapisu pliku na dysku: " + e.getMessage());
            return false;
        }
    }

    private String getDesktopPath() {
        String desktopPath = null;
        try {
            desktopPath = System.getProperty("user.home") + "/Desktop";
            return desktopPath.replace("\\", "/");
        } catch (Exception e) {
            System.out.println("Wystąpił błąd podczas pobierania ścieżki pulpitu użytkownika: " + e.getMessage());
            return desktopPath;
        }
    }
}
