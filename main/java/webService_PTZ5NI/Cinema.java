package webService_PTZ5NI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import seatreservation.ArrayOfSeat;
import seatreservation.CinemaException;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaGetAllSeatsCinemaException;
import seatreservation.ICinemaGetSeatStatusCinemaException;
import seatreservation.ICinemaInitCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.ICinemaUnlockCinemaException;
import seatreservation.Seat;
import seatreservation.SeatStatus;

@WebService(
		name="CinemaService",
        portName="ICinema_HttpSoap11_Port",
        targetNamespace="http://www.iit.bme.hu/soi/hw/SeatReservation",
        endpointInterface="seatreservation.ICinema",
        wsdlLocation="WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema {

    
	private static List<Seated> seatoperation;
	private static List<Locked> lockoperation;
	
	private boolean seatinitial = false;
	public int rowCounter;
	public int columnCounter;
	

	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		lockoperation = new ArrayList<>();
		seatoperation = new ArrayList<>();
		
		if(rows < 1 || rows > 26) {
			String message = "Invalid number of rows";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaInitCinemaException(message, exception);
		}
		
		if(columns < 1 || columns > 100) {
			String message = "Invalid number of columns";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaInitCinemaException(message, exception);
		}
		
		char rowLetter = 'A';		
		for(int i=0; i<rows; i++) {
			for(int j=0; j<columns; j++) {
				Seated seat = new Seated(rowLetter, j+1);
				seatoperation.add(seat);
			}
			rowLetter++;
		}
		
		rowCounter=rows;
		columnCounter=columns;
		seatinitial = true;
				
	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		
		if(!seatinitial) {
			return new ArrayOfSeat();
		}
		
		ArrayOfSeat array = new ArrayOfSeat();
		for(Seated seat : seatoperation) {
			array.getSeat().add(seat);
		}
	
		return array;
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {

		if(!seatinitial) {
			throw new ICinemaGetSeatStatusCinemaException("Not initialized",null);
		}
		
		Seated xseat = getExSeat(seat);
		
		if (xseat == null) {
			String message = "Seat not found.";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaGetSeatStatusCinemaException(message, exception);
		}
		
		return xseat.getStatus();
	}
	


	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
        
		if(!seatinitial) {
			throw new ICinemaLockCinemaException("Not initialized", null);
		}
		
		if (count < 1) {
			throw new ICinemaLockCinemaException("Seats number should be greater than 0.", null);
		}
		
		Seated xseat = getExSeat(seat);
		
		if (xseat == null) {
			String message = "Seat not found";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaLockCinemaException(message, exception);
		}
		
		if (Integer.parseInt(xseat.getColumn()) + count - 1 > columnCounter) {
			String message = "Not enough seats";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaLockCinemaException(message, exception);
		}
		
		if(xseat.getStatus() != SeatStatus.FREE) {
			String message = "There are not enough seats or seats are alredy locked";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaLockCinemaException(message, exception);
		}
		
		xseat.setStatus(SeatStatus.LOCKED);
		
		Locked lock = new Locked(seat, count);
		lockoperation.add(lock);
		String lockid = lock.getId();
		System.out.println("Seats locked, ID: " + lockid);
		
		return lockid;
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
	
		if (!seatinitial) {
			throw new ICinemaUnlockCinemaException("Cinema initialition invalid", null);
		}
		
	    Locked lock = findLock(lockId);
	    
	    
		if (lock == null) {
			String message = "Seat is free: " + lockId;
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaUnlockCinemaException(message, exception);
		}
		
		Seated xseat = getExSeat(lock.getSeat());
		
		
		if(xseat.getStatus() != SeatStatus.LOCKED) {
			String message = "Not Locked";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaUnlockCinemaException(message, exception);
		}
		
		xseat.setStatus(SeatStatus.FREE);
		System.out.println("Seat unlocked.");
		
	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
	
		if(!seatinitial) {
			throw new ICinemaReserveCinemaException("Not initialized", null);
		}
		
		Locked lock = findLock(lockId);
		
		if(lock == null) {
			String message = "";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaReserveCinemaException(message, exception);
		}
		
		Seated xseat = getExSeat(lock.getSeat());
		
		if(xseat.getStatus() != SeatStatus.LOCKED) {
			String message = "Could not find the locked seat";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaReserveCinemaException(message, exception);
		}
		
		xseat.setStatus(SeatStatus.RESERVED);
		System.out.println("Seats reserved.");
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {
		
		if(!seatinitial) {
			throw new ICinemaBuyCinemaException("Not initialized", null);
		}
		
		Locked lock = findLock(lockId);
		
		if(lock == null) {
			String message = "";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaBuyCinemaException(message, exception);
		}
		
		Seated xseat = getExSeat(lock.getSeat());
		
		if(xseat.getStatus() != SeatStatus.LOCKED) {
			String message = "Could not find the locked seat";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaBuyCinemaException(message, exception);
		}
		
		xseat.setStatus(SeatStatus.SOLD);
		System.out.println("Seats sold.");
			
	}
	
	private Seated getExSeat(Seat seat) {
		
		for(Seated xseat : seatoperation) {
			if(xseat.equals(seat)) {
				return xseat;
			}
		}
		return null;
	}
	
	private Locked findLock(String lockId) {
		for (Locked lock: lockoperation) {
			if (lock.getId().equals(lockId)) {
				return lock;
			}
		}
		return null;
	}

}
