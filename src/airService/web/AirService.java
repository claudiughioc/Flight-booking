package airService.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

	/**
	 * Returns the best route (by duration) between two locations
	 */
	public String[] getOptimalRoute(String source, String dest, int maxFlights,
			int departureDay) {
		System.out.println("Getting optimal route from " + source + " to " + dest +
				" maxFlights " + maxFlights + " day " + departureDay);

		// Connect to the database
		createDBConnection();

		// Initiate all available flights
		ArrayList<Flight> availableFlights = initAllFlights(departureDay);
		PriorityQueue<Flight> pq = new PriorityQueue<Flight>();
		ArrayList<Flight> sourceFlights = new ArrayList<Flight>();
		for (Flight fromSource : availableFlights)
			if (fromSource.equals(source) && (fromSource.day >= departureDay))
				sourceFlights.add(fromSource);
		
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
		// Getting the JDBC DataSource
		System.out.println("Buying ticket reservation " + reservationId + " creditCard " + creditCardInfo);

		Connection conn;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_JNDI_NAME);
			conn = ds.getConnection();
			Statement st = conn.createStatement();
			String sql = "SELECT source, destination from Flight";
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
			}
			conn.close();
		} catch (NamingException e) {
			System.out.println("Error on creating context" + e);
			e.printStackTrace();
			return null;
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
	public void createDBConnection() {
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
	public ArrayList<Flight> initAllFlights(int departureDay) {
		ArrayList<Flight> availableFlights = new ArrayList<Flight>();

		// Get all available flights
		String sql = "SELECT id, flight_id_official, source, destination, hour, day, " +
				"duration, state, total_seats, booked_seats from Flight where day >= " +
				departureDay;
		
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
}
