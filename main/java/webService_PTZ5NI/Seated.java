package webService_PTZ5NI;

import seatreservation.Seat;
import seatreservation.SeatStatus;

public class Seated extends Seat {

	private SeatStatus status;
	
	public Seated(char row, int column) {
		super();
		setRow("" + row);
		setColumn("" + column);
		this.status = SeatStatus.FREE;
	}
	
	public SeatStatus getStatus() {
		return status;
	}
	
	public void setStatus(SeatStatus status) {
		this.status = status;
	}
	
	public boolean equals(Seat seat) {
		return getRow().equals(seat.getRow()) && getColumn().equals(seat.getColumn());
	}
	
}
