package pl.edu.agh.f1_stats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
@Getter
public class Race {
    private String country;
    private List<ParticipantData> driversData;
}
