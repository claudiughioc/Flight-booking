package airService.web;

/**
 * Implementation of the web services
 * 
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class AirService implements WebAirService {

	/**
	 * Returns the best route (by duration) between two locations
	 */
	public String[] getOptimalRoute(String source, String dest, int maxFlights,
			int departureDay) {
		return new String [] {"getOptimalRoute"};
	}

	/**
	 * Makes a reservation for a route between two locations
	 */
	public String bookTicket(String[] flightIds) {
		return "bookTicket";
	}

	/**
	 * Books a ticket for a route containing one or several flights
	 * A reservation must be done beforehand
	 */
	public String buyTicket(String reservationId, String creditCardInfo) {
		return "buyTicket";
	}
}
