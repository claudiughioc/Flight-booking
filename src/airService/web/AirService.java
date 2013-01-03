package airService.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Implementation of the web services
 * 
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class AirService implements WebAirService {
	public static final String DATASOURCE_JNDI_NAME = "java:comp/env/jdbc/airservice";
	public Connection connection;
	public static Object lock = new Object();

	/**
	 * Returns the best route (by duration) between two locations
	 */
	public String[] getOptimalRoute(String source, String dest, int maxFlights,
			int departureDay) {
		System.out.println("Getting optimal route from " + source + " to " + dest +
				" maxFlights " + maxFlights + " day " + departureDay);
		int noFlights = 0;

		// Connect to the database
		createDBConnection();

		// Initiate all available flights
		PriorityQueue<Flight> availableFlights = initAllFlights(departureDay);
		ArrayList<Flight> auxList = new ArrayList<Flight>();
		// Create a root and an end dummy Fligths 
		Flight rootDummyFlight = initRootInfo(source, availableFlights);
		Flight endDummyFlight = new Flight(dest, dest);
		endDummyFlight.day = 366;
		endDummyFlight.hour = 25;
		availableFlights.add(rootDummyFlight);
		availableFlights.add(endDummyFlight);
		
		// Apply the Dijkstra algorithm
		while (!availableFlights.isEmpty()) {
			Flight current = availableFlights.remove();
			auxList.add(current);
			System.out.println("Removed from queue " + current);
			if (current.cost == Flight.MAXIMUM_COST)
				break;

			PriorityQueue<Flight> copy = new PriorityQueue<Flight>(availableFlights);
			for (Flight connection : availableFlights) {
				// Select only the connected flights later than the current
				if (!connection.source.equals(current.destination) ||
						(connection.day < current.day) || (connection.day == current.day && connection.hour < current.hour))
					continue;

				// Add the current cost for all the flights except the root
				int alt;
				if (!current.source.equals(current.destination))
					alt = current.cost - current.duration + distanceBetweenFlights(current, connection);
				else
					// Add cost for dummy flights
					if (current.source.equals(source))
						alt = connection.duration;
					else
						alt = current.cost;

				if (alt < connection.cost && (current.noFlights <= maxFlights)) {
					connection.cost = alt;
					connection.previous = current;
					connection.noFlights = current.noFlights + 1;

					// Re-add the changed objects in a copied queue
					copy.remove(connection);
					copy.add(connection);
					System.out.println("Connection: " + current + " and " + connection + " total cost " + connection.cost);
				}
			}

			// Force the reordering of the queue
			availableFlights = new PriorityQueue<Flight>(copy);
		}
		
		for (Flight flight : auxList)
			if (flight.source.equals(flight.destination) && flight.destination.equals(dest))
				endDummyFlight = flight;
		if (endDummyFlight.previous == null)
			return new String[]{"No route from " + source + " to " + dest};

		System.out.println("Arrival with flight " + endDummyFlight);

		// Build the correctly ordered route
		LinkedList<Flight> finalRoute = new LinkedList<Flight>();
		while (true) {
			Flight previous = endDummyFlight.previous;
			if (previous.source.equals(source) && previous.source.equals(previous.destination))
				break;
			finalRoute.addFirst(previous);
			endDummyFlight = previous;
		}

		// Built the formatted String array
		String [] response = new String[finalRoute.size() + 1];
		response[0] = "";
		int i = 1;
		for (Flight flight : finalRoute) {
			response[0] += flight;
			if (i != finalRoute.size())
				response[0] += "\n";
			response[i] = flight.flightIdOfficial;
			i++;
		}
		return response;
	}


	/**
	 * Makes a reservation for a route between two locations
	 */
	public String bookTicket(String[] flightIds) {
		System.out.println("Booking ticket");
		if (flightIds == null)
			return "No arguments";

		// Connect to the database
		createDBConnection();
		System.out.println("After creating db connection");

		String sql = "";
		boolean ok = true;
		System.out.println("Len is " + flightIds.length);
		for (int i = 0; i < flightIds.length; i++) {
			int total = 0, booked = 0;
			sql = "SELECT total_seats, booked_seats from Flight where " +
					"flight_id_official = " + flightIds[i];
			try {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
				while(rs.next()) {
					total = rs.getInt(1);
					booked = rs.getInt(2);
				}
				if (booked + 1 > total)
					ok = false;
				rs.close();
				statement.close();
			} catch (SQLException e) {
				System.out.println("Error on retrieving flight information: " + e);
				e.printStackTrace();
			}
			if (!ok)
				break;
		}
		if (ok)
			System.out.println("The reservation can be made");
		else
			System.out.println("There is a full plane");
		//}
		return "bookTicket";
	}

	/**
	 * Books a ticket for a route containing one or several flights
	 * A reservation must be done beforehand
	 */
	public String buyTicket(String reservationId, String creditCardInfo) {
		System.out.println("Buying ticket reservation " + reservationId + " creditCard " + creditCardInfo);
		// Connect to the database
		createDBConnection();

		try {
			Statement st = connection.createStatement();
			String sql = "SELECT source, destination from Flight";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
			}
			st.close();
		} catch (SQLException e) {
			System.out.println("Error on creating connection to DB " + e);
			e.printStackTrace();
			return null;
		}
		return "buyTicketOut";
	}


	/**
	 * Attempts to connect to the "airservice" DB
	 */
	private void createDBConnection() {
		System.out.println("Creating DB connection");
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_JNDI_NAME);
			connection = ds.getConnection();
		} catch (NamingException e) {
			System.out.println("Error on creating context" + e);
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			System.out.println("Error on creating connection");
			e.printStackTrace();
			return;
		}
		System.out.println("Connection ok");
	}

	
	/**
	 * Initiates the list with all the flights
	 */
	private PriorityQueue<Flight> initAllFlights(int departureDay) {
		PriorityQueue<Flight> availableFlights = new PriorityQueue<Flight>();

		// Get all available flights
		String sql = "SELECT id, flight_id_official, source, destination, hour, day, " +
				"duration, state, total_seats, booked_seats from Flight where day >= " +
				departureDay + " and state = " + Flight.STATE_AVAILABLE;
		
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);

			// Create the list with available flights
			while (rs.next())
				availableFlights.add(new Flight(rs.getInt(1), rs.getInt(5),
						rs.getInt(6), rs.getInt(7), rs.getInt(8),
						rs.getInt(9), rs.getInt(10), rs.getString(2),
						rs.getString(3), rs.getString(4)));
			statement.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("Exception on creating query");
			e.printStackTrace();
			return null;
		}
		return availableFlights;
	}


	/**
	 * Calculate the cost between two Flights
	 * @param first
	 * @param second
	 * @return - duration in hours
	 */
	private int distanceBetweenFlights(Flight first, Flight second) {
		int distance = 0;
		if (second.day + 1 >= first.day) {
			distance = (second.day - 1 - first.day) * 24;
			distance += (24 - first.hour) + second.hour;
		} else
			distance = second.hour - first.hour;
		distance += second.duration;
		return distance;
	}


	/**
	 * Initialize information for a dummy flight, the root of the graph
	 * @param source
	 * @param availableFlights
	 * @return the Flight object corresponding to the root
	 */
	private Flight initRootInfo(String source, PriorityQueue<Flight> availableFlights) {
		Flight root = new Flight(source, source);
		root.cost = 0;
		root.noFlights = 0;
		return root;
	}
}
