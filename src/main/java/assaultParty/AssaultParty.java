package assaultParty;

import logger.IConcurrentLogger;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class representing an assault party.
 * Assault parties are groups of thieves that go to an assigned room,
 * rob paintings and take them back to the collection site
 */
public class AssaultParty implements IAssaultParty {
	/**
	 * The assault party's unique identifier
	 */
	private final int id;
	/**
	 * The mutex lock
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * Thieves must wait for the master thief's order at the concentration site to depart
	 * and the order of the last thief to enter the room to turn back
	 */
	private final Condition holdCond = lock.newCondition();
	/**
	 * The distance each thief is from the destination
	 */
	private final int[] distances;
	/**
	 * The amount of thieves in the assault party
	 */
	private final int partySize;
	/**
	 * The maximum distance 2 consecutive thieves can be from each other while crawling
	 */
	private final int maxSeparation;
	/**
	 * The logger to write to standard output and log file
	 */
	private final IConcurrentLogger logger;
	/**
	 * The identifier of the room to be robbed
	 */
	private int roomID;
	/**
	 * The distance to the room
	 */
	private int roomDistance;
	/**
	 * Whether the master thief has allowed this party to depart
	 */
	private boolean hasMasterAllowedDeparture;
	/**
	 * Whether the last thief to reach the room has robbed and requested the return to the collection site
	 */
	private boolean hasLastThiefReversedDirection;
	/**
	 * Number of thieves ready to walk
	 */
	private int thiefCounter;

	/**
	 * @param id            The party's unique identifier
	 * @param partySize     The size of the party
	 * @param maxSeparation The maximum distance 2 consecutive thieves can be from each other
	 * @param logger        The logger
	 * @throws RemoteException when the call to this remote method fails
	 */
	public AssaultParty(int id, int partySize, int maxSeparation, IConcurrentLogger logger) throws RemoteException {
		this.logger = logger;
		this.id = id;
		this.maxSeparation = maxSeparation;
		this.roomID = -1;
		this.distances = new int[partySize];
		this.partySize = partySize;
		this.hasMasterAllowedDeparture = true;
		this.hasLastThiefReversedDirection = true;
		this.thiefCounter = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean crawlIn(int agility, int idInAssaultParty, int thief) throws RemoteException {
		try {
			lock.lock();

			if (hasMasterAllowedDeparture) {
				thiefCounter++;
				logger.crawlIn(thief);
				if (thiefCounter < partySize) {
					while (hasMasterAllowedDeparture) {
						holdCond.await();
					}
				} else {
					hasMasterAllowedDeparture = false;
					hasLastThiefReversedDirection = true;
					thiefCounter = 0;
					holdCond.signalAll();
				}
			}

			if (distances[idInAssaultParty] == roomDistance) { //if all thieves arrived change thieves state
				return false;
			} else {
				int situation;
				int index_inf = -1;
				int index_sup = -1;

				if (distances[idInAssaultParty] != 0) {
					int maxAt = 0;
					for (int i = 1; i < distances.length; i++) { // finds the index of the thief with the most distance travelled
						if (distances[i] > distances[maxAt]) maxAt = i;
					}

					int minAt = 0;
					for (int i = 1; i < distances.length; i++) { // finds the index of the thief with the least distance travelled
						if (distances[i] < distances[minAt]) minAt = i;
					}

					if (minAt == idInAssaultParty) { // if current thief is the one with the least distance travelled situation = 0
						situation = 0;
					} else if (maxAt == idInAssaultParty) { // if current thief is the one with the most distance travelled situation = 2

						situation = 2;
						int temp = Integer.MIN_VALUE;
						for (int i = 0; i < partySize; i++) {
							if ((temp < distances[i]) && (distances[i] < distances[idInAssaultParty])) { // find thief behind
								index_inf = i;
								temp = distances[i];
							}
						}
					} else { // if thief is between two thieves = 1
						situation = 1;
						int temp = Integer.MIN_VALUE;
						int temp2 = Integer.MAX_VALUE;

						for (int i = 0; i < partySize; i++) {
							if ((temp < distances[i]) && (distances[i] < distances[idInAssaultParty])) { // find thief behind
								index_inf = i;
								temp = distances[i];
							}
							if ((distances[i] > distances[idInAssaultParty]) && (temp2 > distances[i])) { // find thief in front
								index_sup = i;
								temp2 = distances[i];
							}
						}
					}
				} else {
					situation = 2;
					int temp = Integer.MAX_VALUE;
					for (int i = 0; i < partySize; i++) {
						if (distances[i] < temp) {
							index_inf = i;
							temp = distances[i];
						}
					}
				}

				if (distances[idInAssaultParty] < roomDistance) {
					for (int possibleMovement = agility; possibleMovement > 0; possibleMovement--) {
						boolean movementFlag = true;
						int futureDistance = distances[idInAssaultParty] + possibleMovement;
						if (futureDistance <= roomDistance) { // futureDistance needs to be equal or lower than destination distance
							switch (situation) {
								case 0: // when the thief with less distance travelled is trying to walk
									for (int distance : distances)
										if ((distance == futureDistance) && (futureDistance != roomDistance)) {// check for overlapping
											movementFlag = false;
											break;
										}
									break;
								case 1: // when the thief is between two other thieves
									for (int distance : distances)
										if ((distance == futureDistance) && (futureDistance != roomDistance)) { // check for overlapping
											movementFlag = false;
											break;
										}
									if (movementFlag) {
										if (distances[index_sup] < futureDistance) { // if thief overtakes
											if (distances[index_sup] - distances[index_inf] > maxSeparation) //check if maximum maxSeparation was fullfilled
												movementFlag = false;
										} else if (futureDistance - distances[index_inf] > maxSeparation) //check if maximum maxSeparation was fullfilled with no overtake
											movementFlag = false;
									}
									break;
								case 2:// when the thief with most distance travelled is trying to walk
									for (int distance : distances)
										if (distance == futureDistance) { // check for overlapping
											movementFlag = false;
											break;
										}
									if (futureDistance - distances[index_inf] > maxSeparation) //checks if maximum maxSeparation was fullfilled with no overtake
										movementFlag = false;
									break;
							}
						} else {
							movementFlag = false;
						}
						if (movementFlag) {

							distances[idInAssaultParty] = futureDistance;
							logger.setDistance(id, idInAssaultParty, futureDistance);
							break;
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reverseDirection(int thief) throws RemoteException {
		try {
			lock.lock();
			logger.crawlOut(thief);
			if (hasLastThiefReversedDirection) {
				thiefCounter++;
				if (thiefCounter < partySize) {
					while (hasLastThiefReversedDirection) {
						holdCond.await();
					}
				} else {
					hasLastThiefReversedDirection = false;
					hasMasterAllowedDeparture = true;
					thiefCounter = 0;
					holdCond.signalAll();
				}
			}
		} catch (InterruptedException | RemoteException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean crawlOut(int agility, int idInAssaultParty) throws RemoteException {
		try {
			lock.lock();

			int thiefTracking = 0;
			for (int distance : distances) {// count how many arrived
				if (distance == 0)
					thiefTracking++;
			}

			if (thiefTracking == partySize) { //if all thieves arrived change thieves state
				return false;
			} else {
				int situation;
				int index_inf = -1;
				int index_sup = -1;

				if (distances[idInAssaultParty] != roomDistance) {
					int maxAt = 0;
					for (int i = 1; i < distances.length; i++) { // finds the index of the thief with the most distance (last)
						if (distances[i] > distances[maxAt]) maxAt = i;
					}

					int minAt = 0;
					for (int i = 1; i < distances.length; i++) { // finds the index of the thief with the least distance (first)
						if (distances[i] < distances[minAt]) minAt = i;
					}

					if (maxAt == idInAssaultParty) { // if current thief is the one with the most distance situation = 0
						situation = 0;
					} else if (minAt == idInAssaultParty) { // if current thief is the one with the least distance situation = 2

						situation = 2;
						int temp = Integer.MAX_VALUE;
						for (int i = 0; i < partySize; i++) {
							if ((distances[i] > distances[idInAssaultParty]) && (temp > distances[i])) {
								index_sup = i;
								temp = distances[i];
							}
						}
					} else { // if thief is between two thieves = 1
						situation = 1;
						int temp = Integer.MIN_VALUE;
						int temp2 = Integer.MAX_VALUE;

						for (int i = 0; i < partySize; i++) {
							if ((distances[i] < distances[idInAssaultParty]) && (distances[i] > temp)) {
								index_inf = i;
								temp = distances[i];
							}
							if ((distances[i] > distances[idInAssaultParty]) && (temp2 > distances[i])) {
								index_sup = i;
								temp2 = distances[i];
							}
						}
					}
				} else {
					situation = 2;
					int temp = Integer.MIN_VALUE;
					for (int i = 0; i < partySize; i++) {
						if (distances[i] > temp) {
							index_sup = i;
							temp = distances[i];
						}
					}
				}

				if (0 < distances[idInAssaultParty]) {
					for (int possibleMovement = agility; possibleMovement > 0; possibleMovement--) {
						boolean movementFlag = true;
						int futureDistance = distances[idInAssaultParty] - possibleMovement;
						if (futureDistance >= 0) { // futureDistance needs to be equal or lower than destination distance
							switch (situation) {
								case 0: // when the thief with less distance travelled is trying to walk
									for (int distance : distances)
										if ((distance == futureDistance) && (futureDistance != 0)) { // check for overlapping
											movementFlag = false;
											break;
										}
									break;
								case 1: // when the thief is between two other thieves
									for (int distance : distances)
										if ((distance == futureDistance) && (futureDistance != 0)) { // check for overlapping
											movementFlag = false;
											break;
										}
									if (movementFlag) {
										if (distances[index_inf] > futureDistance) { // if thief overtakes
											if (distances[index_sup] - distances[index_inf] > maxSeparation) //checks if maximum maxSeparation was fullfilled
												movementFlag = false;
										} else if (distances[index_sup] - futureDistance > maxSeparation) //checks if maximum maxSeparation was fullfilled with no overtake
											movementFlag = false;
									}
									break;
								case 2:// when the thief with most distance travelled is trying to walk
									for (int distance : distances)
										if (distance == futureDistance) {// check for overlapping
											movementFlag = false;
											break;
										}
									if (distances[index_sup] - futureDistance > maxSeparation) //checks if maximum maxSeparation was fullfilled with no overtake
										movementFlag = false;
									break;
							}
						} else {
							movementFlag = false;
						}
						if (movementFlag) {
							distances[idInAssaultParty] = futureDistance;
							logger.setDistance(id, idInAssaultParty, futureDistance);
							break;
						}
					}
				}
			}
			return true;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPartySize() throws RemoteException {
		try {
			lock.lock();
			return partySize;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRoom(int roomID, int roomDistance) throws RemoteException {
		try {
			lock.lock();
			if (roomID != -1) {
				this.roomID = roomID;
				this.roomDistance = roomDistance;
				logger.setRoom(id, roomID);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRoomID() throws RemoteException {
		try {
			lock.lock();
			return roomID;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPartyId() throws RemoteException {
		try {
			lock.lock();
			return this.id;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void atControl(int thief) throws RemoteException {
		try {
			logger.atControl(thief);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private final Condition completed = lock.newCondition();

	@Override
	public void waitUntilCompletion() throws RemoteException {
		try {
			lock.lock();
			while (!shutdown)
				completed.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	private boolean shutdown = false;

	@Override
	public void shutdown() throws RemoteException {
		try {
			lock.lock();
			shutdown = true;
			completed.signal();
		} finally {
			lock.unlock();
		}
	}
}
