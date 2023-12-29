package collectionSite;


import logger.IConcurrentLogger;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@inheritDoc}
 */
public class CollectionSite implements ICollectionSite {

	/**
	 * The logger
	 */
	private final IConcurrentLogger logger;
	/**
	 * The mutex's lock
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * The condition signaled when a thief wants to deliver a canvas to the master
	 */
	private final Condition handACanvas = lock.newCondition();
	/**
	 * The condition signaled when the master wants to collect a canvas from the thieves
	 */
	private final Condition collectACanvas = lock.newCondition();
	/**
	 * Master thief waits for thief arrival
	 */
	private final Condition thiefArrival = lock.newCondition();
	/**
	 * Thieves wait for the master to request their departure
	 */
	private final Condition[] prepareAssaultParty;
	/**
	 * Master waits for thieves in the first cycle
	 */
	private final Condition masterWait = lock.newCondition();
	/**
	 * /**
	 * Painting delivery queue.
	 * The array is structured as follows: [thiefId, roomId, numberOFCanvasStolen]
	 */
	private final Queue<int[]> paintings = new LinkedList<>();
	/**
	 * Total number of assault parties
	 */
	private final int numberOfAssaultParties;
	/**
	 * Total number of thieves
	 */
	private final int numberOfThieves;
	/**
	 * Rooms with canvas. The index of the array is the room identifier
	 */
	private final boolean[] roomHasCanvas;
	/**
	 * Whether there are elements of the assault party crawling or not
	 * The index in the array is the identifier of the assault party
	 */
	private final boolean[] prepareAssaultPartyHold;
	/**
	 * Number of thieves in each assault party
	 * The index in the array is the identifier of the assault party
	 */
	private final int[] nThievesPerParty;
	/**
	 * Number of thieves ready for departure in each assault party
	 * The index in the array is the identifier of the assault party
	 */
	private final int[] awaitingThieves;
	/**
	 * Whether the room was left empty by the last expedition
	 * The index in the array is the identifier of the room
	 */
	private final boolean[] getNewRoom;
	/**
	 * Last thief to reach collection site signals master in the first cycle
	 */
	private final boolean signalMaster = true;
	/**
	 * Number of rooms with paintings
	 */
	private int totalRoomsLeft;
	/**
	 * Whether the master thief can receive a canvas
	 */
	private boolean handACanvasHold;
	/**
	 * Whether the master thief can collect a canvas
	 */
	private boolean collectACanvasHold;
	/**
	 * Total number of canvas that were collected
	 */
	private int totalCanvasCollected;
	/**
	 * Whether the master thief is in the TakeARest state
	 */
	private boolean masterTakingARest;
	/**
	 * Whether there are thieves with canvas to deliver
	 */
	private boolean thiefWithCanvasToDeliver;
	/**
	 * Number of thieves in the collection site
	 */
	private int totalThievesOnSite;
	/**
	 * Whether the simulation has reached its end
	 */
	private boolean end = false;
	/**
	 * TODO:ASD
	 */
	private boolean makeMasterWait = true;


	/**
	 * Creates the collection site
	 *
	 * @param logger                 The concurrent logger
	 * @param nRooms                 The number of rooms
	 * @param nThievesPerParty       The number of thieves in each assault party
	 * @param numberOfAssaultParties The number of assault parties
	 * @param numberOfThieves        The total number of thieves
	 * @throws RemoteException when the call to this remote method fails
	 */
	public CollectionSite(IConcurrentLogger logger, int nRooms, int[] nThievesPerParty, int numberOfAssaultParties, int numberOfThieves) throws RemoteException {
		this.logger = logger;

		this.numberOfAssaultParties = numberOfAssaultParties;
		this.numberOfThieves = numberOfThieves;
		this.prepareAssaultParty = new Condition[numberOfAssaultParties];
		for (int i = 0; i < this.prepareAssaultParty.length; i++) {
			this.prepareAssaultParty[i] = lock.newCondition();
		}

		this.roomHasCanvas = new boolean[nRooms];
		Arrays.fill(roomHasCanvas, true);

		this.awaitingThieves = new int[numberOfAssaultParties];
		Arrays.fill(awaitingThieves, 0);

		this.getNewRoom = new boolean[numberOfAssaultParties];
		Arrays.fill(getNewRoom, false);

		this.prepareAssaultPartyHold = new boolean[numberOfAssaultParties];
		Arrays.fill(prepareAssaultPartyHold, true);


		this.totalCanvasCollected = 0;
		this.nThievesPerParty = nThievesPerParty;
		this.masterTakingARest = false;
		this.thiefWithCanvasToDeliver = false;
		this.totalThievesOnSite = 0;
		this.totalRoomsLeft = nRooms;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startOperations() throws RemoteException {
		try {
			logger.masterDecidingWhatToDo();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean[] amINeeded(int partyNumber, int roomID) throws RemoteException {
		try {
			lock.lock();
			if (awaitingThieves[partyNumber] == 0) {
				getNewRoom[partyNumber] = false;
				prepareAssaultPartyHold[partyNumber] = true;
			}

			awaitingThieves[partyNumber]++;
			totalThievesOnSite++;
			if (signalMaster && (totalThievesOnSite == numberOfThieves)) {
				makeMasterWait = false;
				masterWait.signal();
			}

			thiefWithCanvasToDeliver = false;
			thiefArrival.signal();
			if (roomID != -1) {
				if (!roomHasCanvas[roomID])
					getNewRoom[partyNumber] = true;
			} else {
				getNewRoom[partyNumber] = true;
			}
			while (prepareAssaultPartyHold[partyNumber])
				prepareAssaultParty[partyNumber].await();

			totalThievesOnSite--;

			return new boolean[]{!end, getNewRoom[partyNumber]};

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean prepareAssaultParty() throws RemoteException {
		try {
			lock.lock();
			int partyToLeave = -1;
			for (int party = 0; party < numberOfAssaultParties; party++) {
				if ((awaitingThieves[party] == nThievesPerParty[party])) {
					awaitingThieves[party] = 0;
					partyToLeave = party;
					if (getNewRoom[partyToLeave]) {
						if (totalRoomsLeft != 0) {
							totalRoomsLeft--;
							prepareAssaultPartyHold[partyToLeave] = false;
							prepareAssaultParty[partyToLeave].signalAll();
							break;
						} else {
							partyToLeave = -1;
						}
					} else {
						prepareAssaultPartyHold[partyToLeave] = false;
						prepareAssaultParty[partyToLeave].signalAll();
						break;
					}
				}
			}

			boolean isOnePartyReady = partyToLeave != -1;
			if (isOnePartyReady) {
				try {
					logger.preparingAssaultParty();
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
			}
			return isOnePartyReady;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeARest() throws RemoteException {
		try {
			try {
				logger.takeRest();
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			lock.lock();
			handACanvasHold = true;
			masterTakingARest = true;
			while (handACanvasHold)
				handACanvas.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handACanvas(int thief, int room, int nCanvas, int thiefIdInAssaultParty, int partyId) throws RemoteException {
		try {
			lock.lock();
			try {
				logger.handCanvas(thiefIdInAssaultParty, partyId);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			collectACanvasHold = true;
			this.paintings.add(new int[]{thief, room, nCanvas});
			thiefWithCanvasToDeliver = true;
			handACanvasHold = false;
			this.handACanvas.signal();
			while (collectACanvasHold)
				this.collectACanvas.await();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collectACanvas() throws RemoteException {
		try {
			lock.lock();

			final int[] canvas = paintings.remove();
			if (canvas == null)
				System.exit(2);
			if (canvas[2] > 0) {
				totalCanvasCollected += canvas[2];
			} else {
				roomHasCanvas[canvas[1]] = false;
			}
			collectACanvasHold = false;
			this.collectACanvas.signal();

			while (thiefWithCanvasToDeliver)
				thiefArrival.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sumUpResults() throws RemoteException {
		try {
			lock.lock();
			try {
				logger.sumUpResults(totalCanvasCollected);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMasterResting() throws RemoteException {
		try {
			lock.lock();
			if (masterTakingARest) {
				masterTakingARest = false;
				return true;
			} else
				return false;

		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnd() throws RemoteException {
		try {
			lock.lock();
			for (boolean hasCanvas : roomHasCanvas)
				if (hasCanvas) {
					return false;
				}
			if ((totalThievesOnSite == numberOfThieves)) {
				end = true;
				int i = 0;
				for (Condition x : prepareAssaultParty) {
					prepareAssaultPartyHold[i++] = false;
					x.signalAll();
				}
				return true;
			} else {
				return false;
			}
		} finally {
			lock.unlock();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForThieves() throws RemoteException {
		try {
			lock.lock();
			if (totalThievesOnSite != numberOfThieves) {
				while (makeMasterWait)
					masterWait.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
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