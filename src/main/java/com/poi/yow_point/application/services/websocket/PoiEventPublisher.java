package com.poi.yow_point.application.services.websocket;

//import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.websocketDTO.PoiEvent;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class PoiEventPublisher {

    // Le "Sink" est la source de notre Flux d'événements.
    private final Sinks.Many<PoiEvent> sink;

    public PoiEventPublisher() {
        // Sinks.many() : Pour diffuser des éléments à plusieurs abonnés.
        // .replay() : Permet de "rejouer" des éléments pour les nouveaux abonnés.
        // .latest() : Spécifie que seuls les derniers éléments sont rejoués.
        // Dans notre cas, il rediffusera le dernier événement à tout client qui se
        // connecte.
        this.sink = Sinks.many().replay().latest();
    }

    /**
     * Méthode appelée par le service pour publier un nouvel événement.
     * 
     * @param event L'événement à diffuser.
     */
    public void publishEvent(PoiEvent event) {
        // tryEmitNext envoie l'événement dans le "tuyau".
        // Les abonnés seront notifiés.
        sink.tryEmitNext(event);
    }

    /**
     * Méthode utilisée par le WebSocketHandler pour s'abonner au flux d'événements.
     * 
     * @return Un Flux<PoiEvent> que les clients peuvent écouter.
     */
    public Flux<PoiEvent> getPublisher() {
        return sink.asFlux();
    }
}