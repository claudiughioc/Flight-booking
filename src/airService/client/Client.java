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
		String source = st.nextToken();
		String dest = st.nextToken();
		int maxFlights = Integer.parseInt(st.nextToken());
		int departureDay = Integer.parseInt(st.nextToken());

		try {
			org.apache.axis.client.Service service = new org.apache.axis.client.Service();
			Call call = (Call)service.createCall();
			call.setTargetEndpointAddress(new URL(Client.AIRSERVICE_URL));
			call.setOperationName(new QName("getOptimalRoute"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
			QName string = new QName("http://echo.demo.oracle/", "string");
			QName intType = new QName("http://echo.demo.oracle/", "int");
			call.addParameter("source", string, String.class, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("dest", string, String.class, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("maxFlights", intType, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("departureDay", intType, javax.xml.rpc.ParameterMode.IN);
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

		// Call the bookTicket method
		try {
			org.apache.axis.client.Service service = new org.apache.axis.client.Service();
			Call call = (Call)service.createCall();
			call.setTargetEndpointAddress(new URL(Client.AIRSERVICE_URL));
			call.setOperationName(new QName("bookTicket"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
			call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
			call.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "http://schemas.xmlsoap.org/soap/encoding/");
			call.setProperty(Call.OPERATION_STYLE_PROPERTY, "rpc");
			QName stringArray = new QName("http://echo.demo.oracle/", "stringArray");
			call.addParameter("flightIds", stringArray, String[].class, javax.xml.rpc.ParameterMode.IN);
			call.setTargetEndpointAddress(Client.AIRSERVICE_URL);
			call.setReturnClass(String.class);
			Object[] inParams = new Object[]{flightsArray};

			String ret = (String) call.invoke(inParams);
			if (ret.equals(""))
				System.out.println("The reservation could not be made. There is a canceled or a full flight.");
			else
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
			org.apache.axis.client.Service service = new org.apache.axis.client.Service();
			Call call = (Call)service.createCall();
			call.setTargetEndpointAddress(new URL(Client.AIRSERVICE_URL));
			call.setOperationName(new QName("buyTicket"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
			QName string = new QName("http://echo.demo.oracle/", "string");
			call.addParameter("reservationId", string, String.class, javax.xml.rpc.ParameterMode.IN);
			call.addParameter("creditCardInfo", string, String.class, javax.xml.rpc.ParameterMode.IN);
			call.setTargetEndpointAddress(Client.AIRSERVICE_URL);
			call.setReturnClass(String.class);

			Object[] inParams = new Object[]{reservationId, creditCardInfo};

			String ret = (String) call.invoke(inParams);
			if (ret.equals(""))
				System.out.println("Unable to buy ticket");
			else
				System.out.println("Bought ticket " + ret);
		} catch(Exception ex) {
			System.out.println("Error on calling bookTicket webservice");
			ex.printStackTrace();
		}
	}


	public static void main (String [] args) {
		System.out.println("Starting the client");
		if (args.length < 1)
			System.out.println("The webservice URL must be the first argument");

		// Initiate the webservice
		Client.AIRSERVICE_URL = args[0];

		// Build the command menu
		Client.MENU = Client.SEPARATOR + "\n";
		Client.MENU += Client.GET_ROUTE_FORMAT + "\n";
		Client.MENU += Client.BOOK_TICKET_FORMAT + "\n";
		Client.MENU += Client.BUY_TICKET_FORMAT + "\n";
		Client.MENU += Client.SEPARATOR;
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
		} catch (Exception e) {
			System.out.println("Unable to interpret command");
			e.printStackTrace();
		}
	}
}
