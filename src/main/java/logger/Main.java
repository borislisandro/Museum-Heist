package logger;


import SimulationDefaults.SimulationDefaults;
import Register.RMIUtils;

import java.io.IOException;
import java.rmi.*;

/**
 * Entry point for the logger server
 */
public class Main {
	/**
	 * Starts the assault party server and waits until it is requested to shut down.
	 * @param args the port where the server will listen to requests, the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.err.println("ConcurrentLogger- Please provide service request port");
				return;
			}

			final int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("ConcurrentLogger- Please provide a valid service request port");
				return;
			}
			if (args.length < 3) {
				System.err.println("ConcurrentLogger- Please provide registry host and port");
				return;
			}

			final String rmiRegHostName = args[1];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("ConcurrentLogger- Please provide registry valid port");
				return;
			}

			final SimulationDefaults params = SimulationDefaults.DEFAULT;

			final ThiefLogStatus[] thiefLogStatuses = new ThiefLogStatus[params.getNumberOfThieves()];
			for (int i = 0; i < thiefLogStatuses.length; i++) {
				thiefLogStatuses[i] = new ThiefLogStatus('C', 'P');
			}

			int[] nThievesPerParty = new int[params.getNumberOfAssaultParties()];
			int temp = params.getNumberOfThieves();
			for (int i = 0; i < params.getNumberOfAssaultParties(); i++) {
				if (temp >= params.getAssaultPartySize()) {
					nThievesPerParty[i] = params.getAssaultPartySize();
					temp = temp - params.getAssaultPartySize();
				} else if (temp == 0) {
					nThievesPerParty[i] = 0;
				} else {
					nThievesPerParty[i] = temp;
					temp = 0;
				}
			}

			final AssaultPartyStatus[] assaultPartyStatuses = new AssaultPartyStatus[params.getNumberOfAssaultParties()];
			for (int i = 0; i < assaultPartyStatuses.length; i++) {
				final ElementStatus[] elementStatuses = new ElementStatus[nThievesPerParty[i]];
				for (int i1 = 0; i1 < elementStatuses.length; i1++) {
					elementStatuses[i1] = new ElementStatus(i1, 0, false);
				}
				// TODO: 4/22/23 getAssaultParty room
				assaultPartyStatuses[i] = new AssaultPartyStatus(0, elementStatuses);
			}

			final RoomStatus[] roomStatuses = new RoomStatus[params.getNumberOfRooms()];
			for (int i = 0; i < roomStatuses.length; i++) {
				// TODO: 4/22/23 bind to configuration
				roomStatuses[i] = new RoomStatus();
			}

			final MuseumStatus museumStatus = new MuseumStatus(roomStatuses);

			final LoggerStatus status = new LoggerStatus('P', thiefLogStatuses, assaultPartyStatuses, museumStatus);

			/* create and install the security manager */

			if (System.getSecurityManager() == null) {
				System.err.println("IS NULL");
				System.setSecurityManager(new SecurityManager());
			}
			System.out.println("Security manager was installed!");

			/* RMI registry service */

			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);

			final ConcurrentLogger concurrentLogger = new ConcurrentLogger(params.isBreakLogger(), params.getEntitySeparationSpace(), params.getLogFilePath(), status, params.getNumberOfThieves());
			rmiUtils.register(concurrentLogger, port);

			System.out.println("LOG waiting...");
			concurrentLogger.waitUntilCompletion();
			rmiUtils.unbind(concurrentLogger);

			System.out.println("LOG COMPLETED");

		} catch (RemoteException e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
