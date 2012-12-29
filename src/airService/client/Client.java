package airService.client;

import java.net.URL;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

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
	public static final String SEPARATOR = "--------------------------------";
	public static String MENU;
	public static String AIRSERVICE_URL;

	/**
	 * Parses a command and call the specific webservices
	 * @param command
	 * @return
	 */
	public boolean parseCommand(String command) {
		System.out.println("Command " + command);
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
		System.out.println("Unknown command!");
		System.out.println(Client.MENU);
		return true;
	}

	public void callGetRoute(StringTokenizer st) {
		System.out.println("Calling get optimal route");
		try{
			String wsdlURL = Client.AIRSERVICE_URL;
			String namespace = "http://localhost:8080/axis/services/AirService";
			String serviceName = "AirService";
			QName serviceQN = new QName(namespace, serviceName);

			ServiceFactory serviceFactory = ServiceFactory.newInstance();
			Service service = serviceFactory.createService(new
					URL(wsdlURL), serviceQN);
			Call call = service.createCall();
			call.setPortTypeName(serviceQN);
			call.setOperationName(new QName(namespace, "getOptimalRoute"));
			call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");

			call.addParameter("source", serviceQN, javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(serviceQN);
			Object[] inParams = new Object[]{"Bucuresti"};
			String ret = (String) call.invoke(inParams);
			System.out.println("ret:" + ret);

		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void callBookTicket(StringTokenizer st) {

	}

	public void callBuyTicket(StringTokenizer st) {

	}

	public static void main (String [] args) {
		System.out.println("Starting the client");
		if (args.length < 1)
			System.out.println("The webservice URL must be the first argument");
		Client.AIRSERVICE_URL = args[0];

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
				if (client.parseCommand(command))
					break;
			}
		} catch (Exception e) {
			System.out.println("Unable to interpret command");
			e.printStackTrace();
		}
	}
}
