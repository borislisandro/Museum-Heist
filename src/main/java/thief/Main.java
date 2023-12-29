package thief;

import SimulationDefaults.SimulationDefaults;
import assaultParty.AssaultParty;
import assaultParty.IAssaultParty;
import collectionSite.CollectionSite;
import collectionSite.ICollectionSite;
import concentrationSite.ConcentrationSite;
import concentrationSite.IConcentrationSite;
import logger.ConcurrentLogger;
import logger.IConcurrentLogger;
import museum.IMuseum;
import museum.Museum;
import Register.RMIUtils;

import java.rmi.RemoteException;

/**
 * Entry point for the thieves' client.
 * All the thieves must be in the same process
 */
public class Main {

	/**
	 * Starts the thieves and at the end of the simulation sends shutdown signals to every server (except the RegisterRemoteObject).
	 * @param args the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				System.err.println("MasterThief- Please provide registry host and port");
				return;
			}

			final String rmiRegHostName = args[0];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("MasterThief- Please provide registry valid port");
				return;
			}

			/* RMI registry service */

			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);
			final IConcurrentLogger logger = (IConcurrentLogger) rmiUtils.find(ConcurrentLogger.class);
			/* get remote reference to CollectionSite */

			final ICollectionSite collectionSite = (ICollectionSite) rmiUtils.find(CollectionSite.class);

			/* get remote reference to ConcentrationSite*/
			final IConcentrationSite concentrationSite = (IConcentrationSite) rmiUtils.find(ConcentrationSite.class);

			/* get remote reference to Museum */

			final IMuseum museum = (IMuseum) rmiUtils.find(Museum.class);

			/* get remote reference to AssaultParty1 */

			final IAssaultParty assaultParty1 = (IAssaultParty) rmiUtils.find(AssaultParty.class, 0);

			/* get remote reference to AssaultParty2 */

			final IAssaultParty assaultParty2 = (IAssaultParty) rmiUtils.find(AssaultParty.class, 1);


			final Thread[] thieves = new Thief[SimulationDefaults.DEFAULT.getNumberOfThieves()];

			final int maxAgility = SimulationDefaults.DEFAULT.getMaxDisplacement();
			final int minAgility = SimulationDefaults.DEFAULT.getMinDisplacement();

			for (int i = 0; i < thieves.length; i++) {
				if (i < 3) {
					thieves[i] = new Thief(
							i,
							i % 3,
							(int) ((Math.random() * (maxAgility - minAgility)) + minAgility),
							assaultParty1,
							collectionSite,
							concentrationSite,
							museum,
							logger
					);
				} else {
					thieves[i] = new Thief(
							i,
							i % 3,
							(int) ((Math.random() * (maxAgility - minAgility)) + minAgility),
							assaultParty2,
							collectionSite,
							concentrationSite,
							museum,
							logger
					);
				}
				System.out.printf("%d started%n", i);
				thieves[i].start();
			}

			for (Thread thief : thieves) {
				try {
					thief.join();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			logger.shutdown();
			collectionSite.shutdown();
			concentrationSite.shutdown();
			museum.shutdown();
			assaultParty1.shutdown();
			assaultParty2.shutdown();
		} catch (RemoteException e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}
	}
}
