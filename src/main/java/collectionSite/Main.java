package collectionSite;

import SimulationDefaults.SimulationDefaults;
import logger.ConcurrentLogger;
import logger.IConcurrentLogger;
import Register.RMIUtils;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * Entry point for the collection site server
 */
public class Main {

	/**
	 * Starts the collection site server and waits until it is requested to shut down.
	 * @param args the port where the server will listen to requests, the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.err.println("CollectionSite- Please provide service request port");
				return;
			}
			final int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("CollectionSite- Please provide a valid request port");
				return;
			}

			if (args.length < 3) {
				System.err.println("CollectionSite- Please provide rmi registry host and port");
				return;
			}

			final String rmiRegHostName = args[1];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("CollectionSite- Please provide rmi registry valid port");
				return;
			}

			/* create and install the security manager */

			if (System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager());
			System.out.println("Security manager was installed!");

			/* RMI registry service */

			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);
			final IConcurrentLogger logger = (IConcurrentLogger) rmiUtils.find(ConcurrentLogger.class);

			final int[] thievesPerParty = new int[SimulationDefaults.DEFAULT.getNumberOfAssaultParties()];
			Arrays.fill(thievesPerParty, SimulationDefaults.DEFAULT.getAssaultPartySize());

			final CollectionSite collectionSite = new CollectionSite(logger, SimulationDefaults.DEFAULT.getNumberOfRooms(), thievesPerParty, SimulationDefaults.DEFAULT.getNumberOfAssaultParties(), SimulationDefaults.DEFAULT.getNumberOfThieves());

			rmiUtils.register(collectionSite, port);

			System.out.println("COL waiting...");
			collectionSite.waitUntilCompletion();
			rmiUtils.unbind(collectionSite);
			System.out.println("COL COMPLETED");

		} catch (RemoteException e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}
	}
}
