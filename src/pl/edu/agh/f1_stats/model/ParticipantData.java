package pl.edu.agh.f1_stats.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ParticipantData {
    private String position;
    private String driverNumber;
    private String driverFirstNameLastName;
    private String team;
    private String raceTime;
    private Integer pointsCollected;

    public boolean checkDriverData() {
        return this.getDriverNumber() != null && this.getDriverFirstNameLastName() != null && this.getPosition() != null
                && this.getPointsCollected() != null && this.getTeam() != null && this.getRaceTime() != null;
    }
}
