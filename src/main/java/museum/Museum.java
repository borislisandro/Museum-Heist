package museum;

import logger.IConcurrentLogger;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class representing the museum with all it's rooms
 */
public class Museum implements IMuseum {
	/**
	 * The logger
	 */
	private final IConcurrentLogger logger;
	/**
	 * The mutex's lock
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * Number of canvas of each room.
	 * The index in the array is the room identifier.
	 */
	private final int[] roomPaintings;
	/**
	 * Distance from the concentration site to each room
	 */
	private final int[] roomDistance;

	/**
	 * Creates the concentration site
	 *
	 * @param numberOfRooms The number of rooms in the museum
	 * @param maxPaintings  The maximum number of paintings per room
	 * @param minPaintings  The minimum number of paintings per room
	 * @param maxDistance   The maximum distance a room can be from the concentration site
	 * @param minDistance   The minimum distance a room can be from the concentration site
	 * @param logger        The concurrent logger
	 * @throws RemoteException when the call to this remote method fails
	 */
	public Museum(int numberOfRooms, int maxPaintings, int minPaintings, int maxDistance, int minDistance, IConcurrentLogger logger) throws RemoteException {
		this.logger = logger;
		this.roomPaintings = new int[numberOfRooms];
		this.roomDistance = new int[numberOfRooms];

		for (int i = 0; i < numberOfRooms; i++) {
			this.roomPaintings[i] = (int) ((Math.random() * (maxPaintings - minPaintings)) + minPaintings);
			this.roomDistance[i] = (int) ((Math.random() * (maxDistance - minDistance)) + minDistance);
		}
		this.logger.setupRooms(roomDistance, roomPaintings);

//		logger.makeThreadWait();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRoomDistance(int roomID) throws RemoteException {
		try {
			lock.lock();
			if (roomID != -1)
				return roomDistance[roomID];
			else
				return -1;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean rollCanvas(int thief, int roomID, int partyID, int assaultPartyID) throws RemoteException {
		try {
			lock.lock();
			logger.atRoom(thief);
			if (roomPaintings[roomID] > 0) {
				roomPaintings[roomID]--;
				logger.rob(thief, roomID, partyID, assaultPartyID);
				return true;
			} else {
				return false;
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
	public int[] getRoomPaintings() throws RemoteException {
		return roomPaintings;
	}

	/**
	 * Getter for the distance to every room.
	 * The index in the array is the id of the room
	 *
	 * @return The distance to every room
	 * @throws RemoteException when the call to this remote method fails
	 */
	public int[] getRoomDistance() throws RemoteException {
		return roomDistance;
	}
}
