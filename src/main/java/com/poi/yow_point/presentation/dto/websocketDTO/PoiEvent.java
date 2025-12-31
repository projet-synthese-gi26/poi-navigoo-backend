package com.poi.yow_point.presentation.dto.websocketDTO;

import com.poi.yow_point.presentation.dto.PointOfInterestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoiEvent {
    private EventType type;
    private PointOfInterestDTO payload;

    public enum EventType {
        POI_CREATED,
        POI_UPDATED,
        POI_DELETED,
        POI_ACTIVATED,
        POI_DESACTIVATED,
        POI_REVIEWED,
        POI_LIKED,
        POI_UNLIKED,
        POI_VIEWED
    }
}