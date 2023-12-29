package concentrationSite;


import logger.ConcurrentLogger;
import logger.IConcurrentLogger;
import Register.RMIUtils;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Emtry point for the concentration site server
 */
public class Main {

	/**
	 * Starts the concentration site server and waits until it is requested to shut down.
	 * @param args the port where the server will listen to requests, the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException {
		try {
			if (args.length < 1) {
				System.err.println("ConcentrationSite- Please provide service request port");
				return;
			}

			final int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("ConcentrationSite- Please provide a valid service request port");
				return;
			}

			if (args.length < 3) {
				System.err.println("ConcentrationSite- Please provide rmi registry host and port");
				return;
			}

			final String rmiRegHostName = args[1];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("ConcentrationSite- Please provide rmi registry valid port");
				return;
			}

			/* create and install the security manager */

			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());
			System.out.println("Security manager was installed!");


			/* RMI registry service */

			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);
			final IConcurrentLogger logger = (IConcurrentLogger) rmiUtils.find(ConcurrentLogger.class);

			final int thievesPerParty = 3;
			final ConcentrationSite concentrationSite = new ConcentrationSite(
					logger,
					thievesPerParty
			);

			rmiUtils.register(concentrationSite, port);

			System.out.println("CON waiting...");
			concentrationSite.waitUntilCompletion();
			rmiUtils.unbind(concentrationSite);
			System.out.println("CON COMPLETED");

		} catch (RemoteException e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}
	}
}
