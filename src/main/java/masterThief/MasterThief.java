package masterThief;

import collectionSite.ICollectionSite;
import concentrationSite.IConcentrationSite;

import java.rmi.RemoteException;

/**
 * Class representing the master thief
 */
public class MasterThief extends Thread {

	/**
	 * The concentration site
	 */
	private final IConcentrationSite concentrationSite;
	/**
	 * The collection site
	 */
	private final ICollectionSite collectionSite;
	/**
	 * The master thief's current state
	 */
	private State state;

	/**
	 * Constructor for the Master Thief
	 *
	 * @param collectionSite    The collection site
	 * @param concentrationSite The concentration siter
	 */
	public MasterThief(ICollectionSite collectionSite, IConcentrationSite concentrationSite) {
		this.state = State.PLANNING_THE_HEIST;
		this.collectionSite = collectionSite;
		this.concentrationSite = concentrationSite;
	}

	/**
	 * Master Thief's lifecycle, according to the exercise's specification
	 */
	@Override
	public void run() {
		try {
			collectionSite.waitForThieves();
		} catch (RemoteException e) {
			System.err.println("MasterThief could could not execute waitForThieves" + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		lifecycle:
		while (true) {
			switch (this.state) {
				case PLANNING_THE_HEIST:
					try {
						this.collectionSite.startOperations();
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute startOperations" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					this.state = State.DECIDING_WHAT_TO_DO;
					break;
				case ASSEMBLING_GROUP:
					try {
						this.concentrationSite.sendAssaultParty();
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute sendAssaultParty" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					this.state = State.DECIDING_WHAT_TO_DO;
					break;
				case DECIDING_WHAT_TO_DO:
					try {
						if (this.collectionSite.isEnd()) {
							this.state = State.PRESENTING_THE_REPORT;
						} else if (this.collectionSite.prepareAssaultParty()) {
							this.state = State.ASSEMBLING_GROUP;
						} else {
							this.state = State.WAITING_FOR_ARRIVAL;
						}
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute isEnd" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					break;
				case PRESENTING_THE_REPORT:
					try {
						this.collectionSite.sumUpResults();
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute sumUpResults" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					break lifecycle;
				case WAITING_FOR_ARRIVAL:
					try {
						this.collectionSite.takeARest();
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute takeARest" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					try {
						this.collectionSite.collectACanvas();
					} catch (RemoteException e) {
						System.err.println("MasterThief could could not execute collectACanvas" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					this.state = State.DECIDING_WHAT_TO_DO;
					break;
			}
		}
	}

	/**
	 * Set of states the master thief can be in
	 */
	private enum State {
		/**
		 * Id for the PLANNING_THE_HEIST state
		 */
		PLANNING_THE_HEIST,
		/**
		 * Id for the ASSEMBLING_GROUP state
		 */
		ASSEMBLING_GROUP,
		/**
		 * Id for the DECIDING_WHAT_TO_DO state
		 */
		DECIDING_WHAT_TO_DO,
		/**
		 * Id for the PRESENTING_THE_REPORT state
		 */
		PRESENTING_THE_REPORT,
		/**
		 * Id for the WAITING_FOR_ARRIVAL state
		 */
		WAITING_FOR_ARRIVAL,
	}
}
