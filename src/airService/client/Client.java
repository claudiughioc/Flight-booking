package airService.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import org.apache.axis.client.Call;

/**
 * The client implementation which calls the webservice
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class Client {

	// Commands information
	public static final String GET_ROUTE_COMMAND 		= "getOptimalRoute";
	public static final String GET_ROUTE_FORMAT		= GET_ROUTE_COMMAND +
			" source, destination, maxFlights, departureDay";
	public static final String BOOK_TICKET_COMMAND 	= "bookTicket";
	public static final String BOOK_TICKET_FORMAT		= BOOK_TICKET_COMMAND +
			" flightID1, flightId2, ... , flightIdn";
	public static final String BUY_TICKET_COMMAND		= "buyTicket";
	public static final String BUY_TICKET_FORMAT		= BUY_TICKET_COMMAND +
			" reservationId, creditCardInfo";
	public static final String SEPARATOR = "--------------------------------------------------------------";
	public static String MENU;

	public static String AIRSERVICE_URL;
	public static final String AIRSERVICE_NAMESPACE 	= "http://localhost:8080/axis/services/AirService";
	public static final String AIRSERVICE_NAME		= "AirServiceService";
	public static QName serviceQN;
	public static Service service;

	/**
	 * Parses a command and calls the specific webservices
	 * @param command
	 * @return
	 */
	public boolean parseCommand(String command) {
		if (command.equals("exit") || command.equals("quit"))
			return false;
		if (command.length() == 0)
			return true;

		StringTokenizer st = new StringTokenizer(command, ", ");
		String comm = st.nextToken();
		if (comm.equals(GET_ROUTE_COMMAND)) {
			callGetRoute(st);
			return true;
		}
		if (comm.equals(BOOK_TICKET_COMMAND)) {
			callBookTicket(st);
			return true;
		}
		if (comm.equals(BUY_TICKET_COMMAND)) {
			callBuyTicket(st);
			return true;
		}
		System.out.println("Unknown command! Try one of these:");
		System.out.println(Client.MENU);
		return true;
	}

	/**
	 * Calls the getOptimalRoute method from AirService
	 * @param st
	 */
	public void callGetRoute(StringTokenizer st) {
		System.out.println("Calling get optimal route");
		String source = st.nextToken();
		String dest = st.nextToken();
		int maxFlights = Integer.parseInt(st.nextToken());
		int departureDay = Integer.parseInt(st.nextToken());

		try{
			Call call = (Call)service.createCall();
			call.setPortTypeName(Client.serviceQN);
			call.setOperationName(new QName(Client.AIRSERVICE_NAMESPACE, "getOptimalRoute"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");

			call.addParameter("source", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("dest", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("maxFlights", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("departureDay", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.setTargetEndpointAddress(Client.AIRSERVICE_URL);
			call.setReturnClass(String[].class);
			Object[] inParams = new Object[]{source, dest, maxFlights, departureDay };

			String[] ret = (String[]) call.invoke(inParams);
			for (int i = 0; i < ret.length; i++)
				System.out.println(ret[i]);
		} catch(Exception ex) {
			System.out.println("Error on calling getOptimalRoute webservice");
			ex.printStackTrace();
		}
	}

	/**
	 * Calls bookTicket methods from AirService
	 * @param st
	 */
	public void callBookTicket(StringTokenizer st) {
		ArrayList<String> flights = new ArrayList<String>();
		while(st.hasMoreTokens())
			flights.add(st.nextToken());
		String[] flightsArray = new String[flights.size()];
		flightsArray = flights.toArray(flightsArray);

		try{
			Call call = (Call)service.createCall();
			call.setPortTypeName(Client.serviceQN);
			call.setOperationName(new QName(Client.AIRSERVICE_NAMESPACE, "bookTicket"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
			call.addParameter("flightIds", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.setTargetEndpointAddress(Client.AIRSERVICE_URL);
			call.setReturnClass(String.class);
			Object[] inParams = new Object[]{flightsArray};

			String ret = (String) call.invoke(inParams);
			System.out.println("Created reservation " + ret);
		} catch(Exception ex) {
			System.out.println("Error on calling bookTicket webservice");
			ex.printStackTrace();
		}
	}

	/**
	 * Calls buyTicket method from AirService
	 * @param st
	 */
	public void callBuyTicket(StringTokenizer st) {
		String reservationId = st.nextToken();
		String creditCardInfo = st.nextToken();
		try{
			Call call = (Call)service.createCall();
			call.setPortTypeName(Client.serviceQN);
			call.setOperationName(new QName(Client.AIRSERVICE_NAMESPACE, "buyTicket"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
			call.addParameter("reservationId", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("creditCardInfo", Client.serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.setTargetEndpointAddress(Client.AIRSERVICE_URL);
			call.setReturnClass(String.class);
			Object[] inParams = new Object[]{reservationId, creditCardInfo};

			String ret = (String) call.invoke(inParams);
			System.out.println("Bought ticket " + ret);
		} catch(Exception ex) {
			System.out.println("Error on calling bookTicket webservice");
			ex.printStackTrace();
		}
	}

	/**
	 * Initiates the web service to be called by the client
	 * This will create a service when the client starts,
	 * used by all the client commands
	 */
	private static void initWebService() {
		System.out.print("Initiating the WebService... ");
		try{
			String namespace = "http://localhost:8080/axis/services/AirService";
			String serviceName = "AirServiceService";
			Client.serviceQN = new QName(namespace, serviceName);

			ServiceFactory serviceFactory = ServiceFactory.newInstance();

			// Create the static common service
			Client.service = serviceFactory.createService(new
					URL(Client.AIRSERVICE_URL), serviceQN);
		} catch(Exception ex) {
			System.out.println("Error on initiating webservice");
			ex.printStackTrace();
		}
		System.out.println("WebService created");
	}

	public static void main (String [] args) {
		System.out.println("Starting the client");
		if (args.length < 1)
			System.out.println("The webservice URL must be the first argument");

		// Initiate the webservice
		Client.AIRSERVICE_URL = args[0];
		Client.initWebService();

		// Build the command menu
		Client.MENU = Client.SEPARATOR + "\n";
		Client.MENU += Client.GET_ROUTE_FORMAT + "\n";
		Client.MENU += Client.BOOK_TICKET_FORMAT + "\n";
		Client.MENU += Client.BUY_TICKET_FORMAT + "\n";
		Client.MENU += Client.SEPARATOR +"\n";
		System.out.println(Client.MENU);

		Client client = new Client();
		// Parse commands
		Scanner scanner = new Scanner(System.in);
		try {
			while(true) {
				System.out.flush();
				String command = scanner.nextLine();
				if (!client.parseCommand(command))
					break;
			}
			System.out.println("While broken");
		} catch (Exception e) {
			System.out.println("Unable to interpret command");
			e.printStackTrace();
		}
	}
}
