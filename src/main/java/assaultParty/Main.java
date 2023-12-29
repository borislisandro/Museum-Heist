package assaultParty;

import SimulationDefaults.SimulationDefaults;
import logger.ConcurrentLogger;
import logger.IConcurrentLogger;
import Register.RMIUtils;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Entry point for the assault party server.
 */
public class Main {
	/**
	 * Starts the assault party server and waits until it is requested to shut down.
	 * @param args the port where the server will listen to requests, the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.err.println("AssaultParty- Please provide service request port");
				return;
			}

			final int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("AssaultParty- Please provide a valid service request port");
				return;
			}

			if (args.length < 2) {
				System.err.println("AssaultParty- Please provide a party ID");
				return;
			}

			final int assaultId;
			try {
				assaultId = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("AssaultParty- Please provide a valid assault party ID");
				return;
			}

			if (args.length < 4) {
				System.err.println("AssaultParty- Please provide rmi registry host and port");
				return;
			}

			final String rmiRegHostName = args[2];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.err.println("AssaultParty- Please provide registry valid port");
				return;
			}

			/* create and install the security manager */

			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());
			System.out.println("Security manager was installed!");

			/* RMI registry service */
			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);
			final IConcurrentLogger logger = (IConcurrentLogger) rmiUtils.find(ConcurrentLogger.class);


			final AssaultParty assaultParty = new AssaultParty(
					assaultId,
					SimulationDefaults.DEFAULT.getAssaultPartySize(),
					SimulationDefaults.DEFAULT.getMaxSeparation(),
					logger
			);

			rmiUtils.register(assaultParty, assaultId, port);

			System.out.println("AP waiting...");
			assaultParty.waitUntilCompletion();
			rmiUtils.unbind(assaultParty, assaultId);
			System.out.println("AP COMPLETED");

		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
}
