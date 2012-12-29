package airService.admin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import airService.web.Flight;

/**
 * The administration application
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class Admin {
	public static final String ADD_FLIGHT_COMMAND 	= "adaugare_Zbor";
	public static final String CANCEL_FLIGHT_COMMAND 	= "anulare_Zbor";
	public static final String ADD_FLIGHT_FORMAT		= ADD_FLIGHT_COMMAND +
			" source, destination, departureDay, departureHour, duration, numberOfSeats, flightId";
	public static final String CANCEL_FLIGHT_FORMAT	= CANCEL_FLIGHT_COMMAND +
			" flightId";

	private static Connection connection;

	/**
	 * Adds a flight into airservice database
	 * @param st
	 */
	public static void addFlight(StringTokenizer st) {
		String flightId, source, dest;
		int departureDay, departureHour, duration, numberOfSeats;
		try {
			source = st.nextToken();
			dest = st.nextToken();
			departureDay = Integer.parseInt(st.nextToken());
			departureHour = Integer.parseInt(st.nextToken());
			duration = Integer.parseInt(st.nextToken());
			numberOfSeats = Integer.parseInt(st.nextToken());
			flightId = st.nextToken();
		} catch (Exception e) {
			System.out.println("Incorrect command arguments!");
			System.out.println("Try like this: " + ADD_FLIGHT_FORMAT);
			return;
		}
		
		String sql = "INSERT INTO Flight " +
				" (flight_id_official, source, destination, hour, day, duration, " +
				"state, total_seats, booked_seats ) value " +
				"(\"" + flightId + "\", \"" + source + "\", \"" + dest +
				"\", " + departureHour + ", " + departureDay+ ", " + duration + ", "
				+ Flight.STATE_AVAILABLE + ", " + numberOfSeats + ", 0)";
		try {
			Statement statement = Admin.connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			System.out.println("Could not insert flight");
			e.printStackTrace();
			return;
		}
		System.out.println("Flight added successfuly");
	}

	/**
	 * Cancels a flight
	 * @param st
	 */
	public static void cancelFlight(StringTokenizer st) {
		String flightId = "";
		if (st.hasMoreTokens())
			flightId = st.nextToken();
		else {
			System.out.println("Incorrect command arguments! ");
			System.out.println("Try like this: " + CANCEL_FLIGHT_FORMAT);
		}

		String sql = "UPDATE Flight set state = " + Flight.STATE_CANCELED +
				" WHERE flight_id_official = " + flightId;
		try {
			Statement statement = Admin.connection.createStatement();
			statement.executeUpdate(sql);
			System.out.println("Flight " + flightId + " has been canceled.");
			statement.close();
		} catch (SQLException e) {
			System.out.println("Unable to cancel flight");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Parse the command from standard input
	 * @param command
	 * @return
	 */
	public static boolean parseCommand(String command) {
		if (command.equals("exit") || command.equals("quit"))
			return false;
		if (command.length() == 0)
			return true;
		StringTokenizer st = new StringTokenizer(command, ", ");
		String comm = st.nextToken();
		if (comm.equals(ADD_FLIGHT_COMMAND)) {
			addFlight(st);
			return true;
		}
		if (comm.equals(CANCEL_FLIGHT_COMMAND)) {
			cancelFlight(st);
			return true;
		}
		System.out.println("Unknown command. Try one of these two:");
		System.out.println(ADD_FLIGHT_FORMAT);
		System.out.println(CANCEL_FLIGHT_FORMAT);
		return true;
	}

	/**
	 * Creates a database connection using credentials from 
	 * properties file
	 * @return
	 */
	public Connection getSimpleConnection() {
		// Load admin login properties
		Properties properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("admin_login.properties");
        try {
        	properties.load(in);
        } catch (IOException e) {
        	System.out.println("Unable to load admin properties file");
            e.printStackTrace();
            return null;
        }

        // Create DB connection
	    Connection result = null;
	    try {
	       Class.forName(properties.getProperty("DRIVER_CLASS_NAME")).newInstance();
	    } catch (Exception ex){
	       System.out.println("Check classpath. Cannot load db driver.");
	       ex.printStackTrace();
	       return null;
	    }
	    try {
	      result = DriverManager.getConnection(
	    		  properties.getProperty("DB_CONN_STRING"),
	    		  properties.getProperty("USER_NAME"),
	    		  properties.getProperty("PASSWORD"));
	    } catch (SQLException e){
	       System.out.println("Driver loaded, but cannot connect to db");
	       e.printStackTrace();
	       return null;
	    }
	    System.out.println("Coonected to airservice DB");
	    return result;
	  }


	public static void main(String [] args) {
		System.out.println("Starting the admin application");
		
		// Create the DB connection
		Admin.connection = new Admin().getSimpleConnection();
		if (Admin.connection == null)
			return;

		// Parse commands
		Scanner scanner = new Scanner(System.in);
		try {
			while(true) {
				System.out.flush();
				String command = scanner.nextLine();
				if (!Admin.parseCommand(command))
					break;
			}

			// Close DB connection at the end
			Admin.connection.close();
		} catch (Exception e) {
			System.out.println("Unable to interpret command");
			e.printStackTrace();
		}
	}
}
