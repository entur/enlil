package org.entur.enlil.siri.service;

import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.entur.enlil.siri.helpers.SiriObjectFactory;
import org.springframework.stereotype.Service;
import uk.org.siri.siri21.Siri;

import java.util.Collections;

@Service
public class EstimatedTimetableDeliveryService {
    private final FirestoreTemplate firestoreTemplate;
    private final SiriObjectFactory siriObjectFactory;

    public EstimatedTimetableDeliveryService(FirestoreTemplate firestoreTemplate, SiriObjectFactory siriObjectFactory) {
        this.firestoreTemplate = firestoreTemplate;
        this.siriObjectFactory = siriObjectFactory;
    }

    public Siri getEstimatedTimetableDelivery() {
        return siriObjectFactory.createETServiceDelivery(Collections.emptyList());
    }
}
