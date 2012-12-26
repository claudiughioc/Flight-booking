package airService.web;

/**
 * Flight class containing all the information
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class Flight implements Comparable<Flight> {
	public static final int STATE_AVAILABLE	= 0;
	public static final int STATE_CANCELED	= 1;

	public int id, hour, day, duration, state, totalSeats, bookedSeats, cost = 0;
	public String flightIdOfficial, source, destination;
	public Flight previous = null;

	public Flight(int id, int hour, int day, int duration, int state, int totalSeats,
			int bookedSeats, String flightIdOfficial, String source, String destination) {
		this.id = id;
		this.hour = hour;
		this.day = day;
		this.state = state;
		this.totalSeats = totalSeats;
		this.bookedSeats = bookedSeats;
		this.flightIdOfficial = flightIdOfficial;
		this.source = source;
		this.destination = destination;
	}

	public String toString() {
		return "Flight " + flightIdOfficial + " id " + id + " from " +
				source + " to " + destination + " day " + day + " hour " +
				hour + " duration " + duration + " state " + state + " booked " +
				bookedSeats + " seats from total " + totalSeats;
	}

	@Override
	public int compareTo(Flight o) {
		return this.cost - o.cost;
	}
}
