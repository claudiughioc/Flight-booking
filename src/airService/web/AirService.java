package airService.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	public static final int MAXIMUM_COST	= 99999999;
	public Connection connection;
	public static Object lock = new Object();

	/**
	 * Returns the best route (by duration) between two locations
	 */
	public String[] getOptimalRoute(String source, String dest, int maxFlights,
			int departureDay) {
		System.out.println("Getting optimal route from " + source + " to " + dest +
				" maxFlights " + maxFlights + " day " + departureDay);

		// Connect to the database
		createDBConnection();

		String [] finalFlightIds = null;
		try {
			Statement st = connection.createStatement();
			int minCost = MAXIMUM_COST;

			for (int i = 1; i <= maxFlights; i++) {
				String sql = createSQLQuerry(i, source, dest) + " \n";
				ResultSet rs = st.executeQuery(sql);
				String [] fligthIds = new String[i + 1];

				while (rs.next()) {
					int cost = rs.getInt(i + 1);
					if (cost < minCost) {
						for (int j = 1; j <= i; j++)
							fligthIds[j] = rs.getString(j);
						minCost = cost;
						finalFlightIds = fligthIds;
					}
				}
			}
			// Close the DB connection
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error on connecting to the db");
			e.printStackTrace();
			return null;
		}

		if (finalFlightIds != null) {
			finalFlightIds[0] = getRouteDetails(finalFlightIds);
			return finalFlightIds;
		} else return new String[] {"No route found"};
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

		String sql = "";
		boolean ok = true;
		System.out.println("Len is " + flightIds.length);
		for (int i = 0; i < flightIds.length; i++) {
			System.out.println("Processing flight " + flightIds[i]);
			int total = 0, booked = 0, state = Flight.STATE_CANCELED;
			sql = "SELECT total_seats, booked_seats, state from Flight where " +
					"flight_id_official = " + flightIds[i];
			try {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
				while(rs.next()) {
					total = rs.getInt(1);
					booked = rs.getInt(2);
					state = rs.getInt(3);
				}
				// Overbooking and canceled test
				if (booked + 1 > (total * 1.1) || state == Flight.STATE_CANCELED)
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

		// Closing connection to database
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error on closing connection");
			e.printStackTrace();
		}

		if (ok)
			return createReservation(flightIds);
		else
			return "";
	}

	/**
	 * Books a ticket for a route containing one or several flights
	 * A reservation must be done beforehand
	 */
	public String buyTicket(String reservationId, String creditCardInfo) {
		String result = "";
		System.out.println("Buying ticket reservation " + reservationId + " creditCard " + creditCardInfo);
		// Connect to the database
		createDBConnection();

		try {
			int ticketId = 0;
			// Get the last ticket Id
			Statement st = connection.createStatement();
			String sql = "SELECT max(id) from Ticket;";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next())
				ticketId = rs.getInt(1);

			// Create a new Ticket
			ticketId++;
			sql = "INSERT INTO Ticket (id, reservation_id, creditCardInfo) " +
					"value (" + ticketId + ", " + reservationId + ", " + creditCardInfo + ")";
			st.executeUpdate(sql);
			result += ticketId;
			st.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error on creating connection to DB " + e);
			e.printStackTrace();
			return "";
		}
		return result;
	}


	/**
	 * Creates a reservation to a series of flights
	 * @return the reservation Id
	 */
	private String createReservation(String [] flightIds) {
		String reservationId = "";
		// Connect to the DB
		createDBConnection();

		String sql = "SELECT max(id) from Reservation;";
		String insert = "INSERT into Reservation (id) value (";
		try {
			// Get the last reservation ID
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			int lastReservation = 0;
			while (rs.next())
				lastReservation = rs.getInt(1);
			rs.close();

			// Create a new Reservation
			lastReservation++;
			insert += lastReservation + ")";
			st.executeUpdate(insert);

			// Create the Flight Reservations
			for (int i = 0; i < flightIds.length; i++) {
				insert = "INSERT INTO FlightReservation " +
						"(flight_id_official, reservation_id) " +
						"value (" + flightIds[i] + ", " + lastReservation + ")";
				st.executeUpdate(insert);

				// Update the current flight's number of seats
				String update = "UPDATE Flight set booked_seats = booked_seats + 1 " +
						" where flight_id_official = " + flightIds[i];
				st.executeUpdate(update);
				System.out.println("Reserved flight" + flightIds[i]);
			}
			reservationId += lastReservation;
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error on creating reservation");
			e.printStackTrace();
		}
		return reservationId;
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
	 * Creates a part of the sql querry to extract shortest path
	 * for a maximum number of Flights
	 * @param maxFlights
	 * @param source
	 * @param dest
	 * @return
	 */
	private String createSQLQuerry(int maxFlights, String source, String dest) {
		String sql = "SELECT ";
		String []flights = new String[maxFlights];
		for (int i = 0; i < maxFlights; i++) {
			flights[i] = "f" + i;
			sql += flights[i] + ".flight_id_official, ";
		}
		sql += "if(" + flights[maxFlights - 1] + ".day = " + flights[0] +
				".day, " + flights[maxFlights - 1] + ".hour - " + flights[0] +
				".hour, (" + flights[maxFlights - 1] + ".day - " + flights[0] +
				".day - 1) * 24 + " + flights[maxFlights - 1] +
				".hour + (24 - " + flights[0] + ".hour)) + "  +
				flights[maxFlights - 1] + ".duration cost";
		sql += " from Flight " + flights[0] + " ";
		for (int i = 1; i < maxFlights; i++)
			sql += " join Flight " + flights[i] + " on " + flights[i - 1] + 
			".destination = " + flights[i] + ".source and ((" +
			flights[i - 1] + ".day < " + flights[i] +
			".day) or (" + flights[i - 1] + ".day = " +
			flights[i] + ".day and " + flights[i - 1] +
			".hour + " + flights[i - 1] + ".duration < " + flights[i] + ".hour))";
		sql += " where f" + 0 + ".source = \"" + source +
				"\" and f" + (maxFlights - 1) + ".destination = \"" + dest + "\"" +
				" order by cost ASC limit 1;";
		return sql;
	}


	/**
	 * Builds details about all the Flights in a Route
	 * @param finalFlightIds
	 * @return
	 */
	private String getRouteDetails(String [] finalFlightIds) {
		String details = "";

		// Create the DB connection
		createDBConnection();

		try {
			Statement st = connection.createStatement();
			ResultSet res;
			for (int i = 1; i < finalFlightIds.length; i++) {
				String sql = "SELECT source, destination, day, hour, duration," +
						" total_seats, booked_seats from Flight where " +
						" flight_id_official = " + finalFlightIds[i];
				res = st.executeQuery(sql);
				while (res.next())
					details += "Flight " + finalFlightIds[i] + " (" + res.getString(1) +
						" - " + res.getString(2) +", day " + res.getInt(3) +
						", hour " + res.getInt(4) + ", duration " + res.getInt(5) +
						", total_seats " + res.getInt(6) + ", booked_seats " +
						res.getInt(7) + ") ";
			}
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error on retrieving route details");
			e.printStackTrace();
			return "";
		}
		return details;
	}
}
