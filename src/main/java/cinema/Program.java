package cinema;

import javax.xml.ws.BindingProvider;

import seatreservation.CinemaService;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaInitCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.Seat;
import seatreservation.SeatStatus;

public class Program {

	public static void main(String[] args) {
		
		String url = args[0];
		String row = args[1];
		String column = args[2];
		String command = args[3];
		
		        // Create the proxy factory:
				CinemaService cinemaService = new CinemaService();
				// Create the hello proxy object:
				ICinema cinema = cinemaService.getICinemaHttpSoap11Port();
				// Cast it to a BindingProvider:
				BindingProvider bp = (BindingProvider)cinema;
				// Set the URL of the service:
				bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,url);
		        					
				            
    			try {
    				String lockId;
    				Seat seat = new Seat();
    				seat.setRow(row);
    				seat.setColumn(column);
    				
    				switch(command) {
    					case "Lock": 
    						lockId = cinema.lock(seat, 1);
    						System.out.println("Seat locked.");
    						break;
    						
    				    case "Reserve": 
    						lockId = cinema.lock(seat, 1);
    						cinema.reserve(lockId);
    						System.out.println("Seat reserved.");
    						break;
    							
    					case "Buy": 
    						lockId = cinema.lock(seat, 1);
    						cinema.buy(lockId);
    						System.out.println("Seat bought.");
    						break;
    				}
    			} catch (ICinemaLockCinemaException e) {
    				e.printStackTrace();
    			} catch (ICinemaReserveCinemaException e) {
    				e.printStackTrace();
    			} catch (ICinemaBuyCinemaException e) {
    				e.printStackTrace();
    			}    				
    				
              				
                
               			
	}

}
