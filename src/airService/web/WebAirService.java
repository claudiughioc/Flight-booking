package airService.web;

public interface WebAirService {
	public String [] getOptimalRoute(String source, String dest, int maxFlights, int departureDay);
	public String bookTicket(String []flightIds);
	public String buyTicket(String reservationId, String creditCardInfo);
}
