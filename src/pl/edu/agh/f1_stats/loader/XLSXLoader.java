package pl.edu.agh.f1_stats.loader;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import pl.edu.agh.f1_stats.model.ParticipantData;
import pl.edu.agh.f1_stats.model.Race;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XLSXLoader {

    public List<Race> loadRacesFromFile(String path) {
        List<Race> races = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(path));
            workbook.close();
        } catch (EncryptedDocumentException | IOException e) {
            System.out.println("Wystąpił błąd podczas łądowania pliku xlxs: " + e.getMessage());
        }
        if (workbook != null) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                List<ParticipantData> driversData = new ArrayList<>();
                Sheet activeSheet = workbook.getSheetAt(i);
                for (int j = 0; j < activeSheet.getLastRowNum(); j++) {
                    Row row = activeSheet.getRow(j);
                    if (row != null) {
                        ParticipantData participantData = ParticipantData.builder()
                                .position(getCellValue(row.getCell(0)))
                                .driverNumber(getCellValue(row.getCell(1)))
                                .driverFirstNameLastName(convertDriverName(getCellValue(row.getCell(2))))
                                .team(getCellValue(row.getCell(3)))
                                .raceTime(getCellValue(row.getCell(4)))
                                .pointsCollected(Integer.parseInt(getCellValue(row.getCell(5))))
                                .build();
                        if (participantData.checkDriverData()) {
                            driversData.add(participantData);
                        }
                    }
                }
                races.add(new Race(activeSheet.getSheetName(), driversData));
            }
        }
        return races;
    }

    private String getCellValue(Cell cell) {
            return cell != null ? switch (cell.getCellType()) {
                case NUMERIC: yield String.valueOf(cell.getNumericCellValue()).split("\\.")[0];
                case STRING: yield cell.getStringCellValue();
                default: yield null;
            } : null;
    }

    private String convertDriverName(String driverName) {
        return driverName.substring(0, driverName.length() - 4);
    }
}
