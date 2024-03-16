package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@ToString
@Subselect(
    """
    select d.day_id,
           d.travel_date,
           p.title,
           p.time,
           p.memo,
           p.expense,
           p.latitude,
           p.longitude
    from day d
             join places p
                  on p.day_id = d.day_id
    """
)
@Immutable // 읽기전용
@Synchronize({"places", "day"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayWithPlaceDetailsView {
    @Id
    private Long dayId;
    private LocalDate travelDate;
    private String title;
    private LocalTime time;
    private String memo;
    private long expense;
    private String latitude;
    private String longitude;

    public DayWithPlaceDetailsView(
            Long dayId, LocalDate travelDate, String title, LocalTime time,
            String memo, long expense, String latitude, String longitude
    ) {
        this.dayId = dayId;
        this.travelDate = travelDate;
        this.title = title;
        this.time = time;
        this.memo = memo;
        this.expense = expense;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
