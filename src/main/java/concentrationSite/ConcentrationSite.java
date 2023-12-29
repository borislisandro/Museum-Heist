package concentrationSite;

import logger.IConcurrentLogger;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class representing the concentration site
 */
public class ConcentrationSite implements IConcentrationSite {
	/**
	 * The logger
	 */
	private final IConcurrentLogger logger;
	/**
	 * The mutex's lock
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * The condition signaled when all thieves are ready to start crawl inward movement
	 */
	private final Condition prepareExcursion = lock.newCondition();
	/**
	 * The condition signaled when the master thief allows the crawling movement to start.
	 * Only the first thief must react to the signal
	 */
	private final Condition holdFirstThief = lock.newCondition();
	/**
	 * The condition signaled when master has reached the sendAssaultParty method and is awaiting
	 */
	private final Condition thiefWait = lock.newCondition();
	/**
	 * Number of thieves in each assault party.
	 * The index of the array is the id of the assault party
	 */
	private final int nThievesPerParty;
	/**
	 * Number of thieves ready to start the crawling inwards movement
	 */
	private int nReadyThieves;
	/**
	 * Counter used to give new room IDs to assault parties in need
	 */
	private int availableRoom = 0;
	/**
	 * Thieves must wait until the master thief is in sendAssaultParty
	 */
	private boolean isMasterReadyToSendParty;
	/**
	 * Thieves must wait while their excursion is not prepared
	 */
	private boolean prepareExcursionReady;
	/**
	 * Whether the thief entering is the first
	 */
	private boolean firstThief;

	/**
	 * Creates the concentration site
	 *
	 * @param logger           The concurrent logger
	 * @param nThievesPerParty The number of thieves in each party
	 * @throws RemoteException when the call to this remote method fails
	 */
	public ConcentrationSite(IConcurrentLogger logger, int nThievesPerParty) throws RemoteException {
		this.logger = logger;
		this.nReadyThieves = 0;
		this.nThievesPerParty = nThievesPerParty;
		this.isMasterReadyToSendParty = true;
		this.prepareExcursionReady = false;
		this.firstThief = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int prepareExcursion(boolean getRoom) throws RemoteException {
		try {
			lock.lock();
			while (isMasterReadyToSendParty)
				thiefWait.await();
			nReadyThieves++;
			if (nReadyThieves == nThievesPerParty) {
				nReadyThieves = 0;
				prepareExcursion.signal();
				prepareExcursionReady = true;
				if (getRoom) {
					return availableRoom++;
				}
			}
			if (getRoom) {
				return availableRoom;
			}
			if (nReadyThieves == 1) {
				while (firstThief)
					holdFirstThief.await();
			}
			return -1;
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
	public void sendAssaultParty() throws RemoteException {
		try {
			lock.lock();
			isMasterReadyToSendParty = false;
			firstThief = true;
			thiefWait.signalAll();

			while (!prepareExcursionReady)
				prepareExcursion.await();
			this.prepareExcursionReady = false;

			isMasterReadyToSendParty = true;
			firstThief = false;
			holdFirstThief.signal();
			logger.masterDecidingWhatToDo();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
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
