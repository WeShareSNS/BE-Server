package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "places",
            joinColumns = @JoinColumn(name = "day_id")
    )
    private List<Place> places;
    private LocalDate travelDate;

    @Builder
    private Day(List<Place> places, LocalDate travelDate) {
        this.places = places;
        this.travelDate = travelDate;
    }
}
