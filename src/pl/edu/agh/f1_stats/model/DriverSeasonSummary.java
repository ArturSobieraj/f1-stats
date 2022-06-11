package pl.edu.agh.f1_stats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

@Getter
@AllArgsConstructor
public class DriverSeasonSummary {
    private String driverName;
    private Integer seasonPoints;

    public static DriverSeasonSummaryComparator getComparator() {
        return new DriverSeasonSummaryComparator();
    }

    private static class DriverSeasonSummaryComparator implements Comparator<DriverSeasonSummary> {
        @Override
        public int compare(DriverSeasonSummary o1, DriverSeasonSummary o2) {
            return Integer.compare(o1.seasonPoints, o2.getSeasonPoints());
        }
    }
}
