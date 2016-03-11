package pl.nkg.biblospk.events;

import pl.nkg.biblospk.services.ServiceStatus;

public class StatusUpdatedEvent {
    final private ServiceStatus mServiceStatus;

    public StatusUpdatedEvent(ServiceStatus serviceStatus) {
        mServiceStatus = serviceStatus;
    }

    public ServiceStatus getServiceStatus() {
        return mServiceStatus;
    }
}
