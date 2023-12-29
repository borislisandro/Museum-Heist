package masterThief;

import collectionSite.CollectionSite;
import collectionSite.ICollectionSite;
import concentrationSite.ConcentrationSite;
import concentrationSite.IConcentrationSite;
import Register.RMIUtils;

/**
 * Entry point for the master thief's client
 */
public class Main {
	/**
	 * starts the master thief client
	 * @param args the rmi registry ip and rmi registry port
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				System.err.println("MasterThief- Please provide rmi registry host and port");
				return;
			}

			final String rmiRegHostName = args[0];
			final int rmiRegPortNumb;

			try {
				rmiRegPortNumb = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("MasterThief- Please provide rmi registry valid port");
				return;
			}

			/* RMI registry service */

			final RMIUtils rmiUtils = new RMIUtils(rmiRegHostName, rmiRegPortNumb);

			/* get remote reference to CollectionSite*/

			final ICollectionSite collectionSite = (ICollectionSite) rmiUtils.find(CollectionSite.class);
			/* get remote reference to ConcentrationSite*/

			final IConcentrationSite concentrationSite = (IConcentrationSite) rmiUtils.find(ConcentrationSite.class);

			final Thread masterThief = new MasterThief(collectionSite, concentrationSite);
			masterThief.start();
			try {
				masterThief.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			System.err.println("Server exception: " + e);
			e.printStackTrace();
		}
	}
}
