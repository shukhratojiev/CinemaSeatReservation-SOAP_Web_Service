package webService_PTZ5NI;

import seatreservation.Lock;
import seatreservation.Seat;

public class Locked extends Lock{

    private String id;
    
    public Locked(Seat seat, int count) {
    	super();
		setSeat(seat);
		setCount(count);
		this.id = seat.getRow() + ':' + seat.getColumn() + ':' + getCount();
    }
    
    public String getId() {
		return id;
	}
    
}
