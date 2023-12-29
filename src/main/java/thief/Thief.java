package thief;

import assaultParty.IAssaultParty;
import collectionSite.ICollectionSite;
import concentrationSite.IConcentrationSite;
import logger.IConcurrentLogger;
import museum.IMuseum;

import java.rmi.RemoteException;

/**
 * Class representing the thieves
 */
public class Thief extends Thread {

	/**
	 * Thief's unique identifier
	 */
	private final int id;
	/**
	 * The thief's unique identifier in the assault party
	 * Parties are assigned at the beginning of the simulation
	 */
	private final int idInAssaultParty;
	/**
	 * Distance the thief can move to.
	 * In transit, it can move less given the movement constraints
	 */
	private final int agility;
	/**
	 * The party he belongs to.
	 * Parties are assigned at the beginning of the simulation
	 */
	private final IAssaultParty assaultParty;
	/**
	 * The concentration site
	 */
	private final IConcentrationSite concentrationSite;
	/**
	 * The collection site
	 */
	private final ICollectionSite collectionSite;
	/**
	 * The museum
	 */
	private final IMuseum museum;
	/**
	 * The logger
	 */
	private final IConcurrentLogger logger; // TODO: nada contra no guiao por ter o logger no thief right?
	/**
	 * Whether this thief is carrying a canvas
	 */
	private boolean hasCanvas;
	/**
	 * The thief's internal state
	 */
	private State state;

	/**
	 * Creates a new thief
	 *
	 * @param id                the thief's unique identifier
	 * @param idInAssaultParty  the thief's identifier in the assault party
	 * @param agility           the maximum distance he can move
	 * @param assaultParty      the assault party heS belongs to
	 * @param collectionSite    the collection site
	 * @param concentrationSite the concentration site
	 * @param museum            the museum
	 * @param logger            the logger
	 */
	public Thief(int id, int idInAssaultParty, int agility, IAssaultParty assaultParty, ICollectionSite collectionSite, IConcentrationSite concentrationSite, IMuseum museum, IConcurrentLogger logger) {
		this.logger = logger;
		this.id = id;
		this.idInAssaultParty = idInAssaultParty;
		this.collectionSite = collectionSite;
		this.concentrationSite = concentrationSite;
		this.museum = museum;
		this.state = State.CONCENTRATION_SITE;
		this.agility = agility;
		this.assaultParty = assaultParty;
		this.hasCanvas = false;
		try {
			logger.setAgility(id, agility);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The thief's lifecycle, according to the exercise specification
	 */
	@Override
	public void run() {

		lifecycle:
		while (true) {
			switch (this.state) {
				case COLLECTION_SITE:
					try {
						this.assaultParty.atControl(id);
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute atControl" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					try {
						if (collectionSite.isMasterResting()) {
							final int partyId;
							try {
							partyId = this.assaultParty.getPartyId();
							} catch (RemoteException e) {
								System.err.println("Thief could could not execute getPartyId" + e.getMessage());
								e.printStackTrace();
								System.exit(0);
								return;
							}
							try {
								this.collectionSite.handACanvas(id, assaultParty.getRoomID(), hasCanvas ? 1 : 0, idInAssaultParty, partyId);
							} catch (RemoteException e) {
								System.err.println("Thief could could not execute handACanvas" + e.getMessage());
								e.printStackTrace();
								System.exit(0);
							}
							this.state = State.CONCENTRATION_SITE;
						}
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute isMasterResting" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					break;
				case CONCENTRATION_SITE:
					boolean[] ret;
					try {
						ret = this.collectionSite.amINeeded(assaultParty.getPartyId(), assaultParty.getRoomID());
						if (ret[0]) {
							final int roomId;
							try {
								roomId = this.concentrationSite.prepareExcursion(ret[1]);
							} catch (RemoteException e) {
								System.err.println("Thief could could not execute prepareExcursion" + e.getMessage());
								e.printStackTrace();
								System.exit(0);
								return;
							}
							final int roomDistance;
							try {
								roomDistance = this.museum.getRoomDistance(roomId);
							} catch (RemoteException e) {
								System.err.println("Thief could could not execute getRoomDistance" + e.getMessage());
								e.printStackTrace();
								System.exit(0);
								return;
							}
							try {
								this.assaultParty.setRoom(roomId, roomDistance);
							} catch (RemoteException e) {
								System.err.println("Thief could could not execute setRoom" + e.getMessage());
								e.printStackTrace();
								System.exit(0);
							}
							this.state = State.CRAWLING_INWARDS;
							break;
						}
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute amINeeded" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					break lifecycle;
				case CRAWLING_INWARDS:

					while (true) {
						try {
							if (!this.assaultParty.crawlIn(agility, idInAssaultParty, id)) break;
						} catch (RemoteException e) {
							System.err.println("Thief could could not execute crawlIn" + e.getMessage());
							e.printStackTrace();
							System.exit(0);
						}
					}
					this.state = State.AT_ROOM;
					break;
				case AT_ROOM:
					final int roomID;
					try {
						roomID = this.assaultParty.getRoomID();
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute getRoomID" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
						return;
					}
					final int partyId;
					try {
						partyId = this.assaultParty.getPartyId();
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute getPartyId" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
						return;
					}
					try {
						this.hasCanvas = this.museum.rollCanvas(id, roomID, idInAssaultParty, partyId);
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute rollCanvas" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					try {
						this.assaultParty.reverseDirection(id);
					} catch (RemoteException e) {
						System.err.println("Thief could could not execute reverseDirection" + e.getMessage());
						e.printStackTrace();
						System.exit(0);
					}
					this.state = State.CRAWLING_OUTWARDS;
					break;
				case CRAWLING_OUTWARDS:
					while (true) {
						try {
							if (!this.assaultParty.crawlOut(agility, idInAssaultParty)) break;
						} catch (RemoteException e) {
							System.err.println("Thief could could not execute crawlOut" + e.getMessage());
							e.printStackTrace();
							System.exit(0);
						}
					}
					this.state = State.COLLECTION_SITE;
					break;
			}
		}
	}

	/**
	 * Set of states the thief can be in
	 */
	private enum State {
		/**
		 * Id for the COLLECTION_SITE state
		 */
		COLLECTION_SITE,
		/**
		 * Id for the CONCENTRATION_SITE state
		 */
		CONCENTRATION_SITE,
		/**
		 * Id for the CRAWLING_INWARDS state
		 */
		CRAWLING_INWARDS,
		/**
		 * Id for the AT_ROOM state
		 */
		AT_ROOM,
		/**
		 * Id for the CRAWLING_OUTWARDS state
		 */
		CRAWLING_OUTWARDS,
	}
}
