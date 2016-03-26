package pl.nkg.biblospk.events;

public class CanceledEvent {
    final private boolean success;
    final private int reserveId;

    public CanceledEvent(boolean success, int reserveId) {
        this.success = success;
        this.reserveId = reserveId;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getReserveId() {
        return reserveId;
    }
}
