package museum;

import SimulationDefaults.SimulationDefaults;
import logger.ConcurrentLogger;
import logger.IConcurrentLogger;
import Register.RMIUtils;

/**
 * Entry point  for the museum server
 */
public class Main {

	/**
	 * Starts the assault party server and waits until it is requested to shut down.
	 * @param args the port where the server will listen to requests, the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.err.println("Museum- Please provide service request port");
				return;
			}

			final int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Museum- Please provide a valid service request port");
				return;
			}

			if (args.length < 3) {
				System.err.println("Museum- Please provide rmi registry host and port");
				return;
			}

			final String rmiRegHostName = args[1];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("Museum- Please provide rmi registry valid port");
				return;
			}

			/* create and install the security manager */

			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());
			System.out.println("Security manager was installed!");

			/* RMI registry service */
			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);
			final IConcurrentLogger logger = (IConcurrentLogger) rmiUtils.find(ConcurrentLogger.class);

			final Museum museum = new Museum(
					SimulationDefaults.DEFAULT.getNumberOfRooms(),
					SimulationDefaults.DEFAULT.getMaxPaintings(),
					SimulationDefaults.DEFAULT.getMinPaintings(),
					SimulationDefaults.DEFAULT.getMaxDistance(),
					SimulationDefaults.DEFAULT.getMinDistance(),
					logger
			);

			rmiUtils.register(museum, port);

			System.out.println("MU waiting...");
			museum.waitUntilCompletion();
			rmiUtils.unbind(museum);
			System.out.println("MU COMPLETED");

		} catch (Exception e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}
	}
}
