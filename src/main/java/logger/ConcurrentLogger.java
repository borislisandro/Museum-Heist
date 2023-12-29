package logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static logger.StringUtils.center;
import static logger.StringUtils.nChars;

// https://stackoverflow.com/questions/8154366/how-to-center-a-string-using-string-format

/**
 * A thread safe logger.
 * It exposes a safe interface to mutate the simulation state and log it to standard output and a log file
 * <p>
 * The state is scattered among classes separated from the ones to which they concern.
 * This choice was made so that there would be no circular dependency
 * from the thread, to the monitor, to the logger to the thread back again.
 * <p>
 * The logging process only starts when all random state variables (paintings per room and thief agilities) have been set.
 * Shutdown can only happen when the sumUpResults method has been called. If called prior, the method will hang until either
 * a timeout happens or the method has been called
 */
public class ConcurrentLogger implements IConcurrentLogger {

	/**
	 * Whether to break each entry in multiple lines
	 */
	private final boolean breakLines;
	/**
	 * Number of spaces between values in the same entry
	 */
	private final int entitySeparationSpace;
	/**
	 * The helper that writes the entries to the file
	 */
	private final FileWriter fw;
	/**
	 * The simulation status.
	 * IT IS NOT THREAD SAFE
	 */
	private final LoggerStatus status;
	/**
	 * The mutex lock
	 */
	private final ReentrantLock lock = new ReentrantLock();

	private final Condition setup = lock.newCondition();
	/**
	 * Number of thieves
	 */
	private final int nThieves;
	private boolean hasPresentedResults = false;
	/**
	 * Flag true when rooms info is coherent
	 */
	private boolean roomsFlag;
	/**
	 * Flag true when thieves info is coherent
	 */
	private boolean thievesFlag;
	/**
	 * Used to count thieves that set its agility
	 */
	private int thievesCounter;

	/**
	 * Constructor for the concurrent logger
	 *
	 * @param breakLines            Whether to break each entry in multiple lines
	 * @param entitySeparationSpace Number of spaces between values in the same entry
	 * @param logFilePath           The path to the log file
	 * @param status                The initial simulation status
	 * @param nThieves              The number of thieves in the simulation
	 * @throws IOException @see java.io.FileWriter
	 * @throws RemoteException when the call to this remote method fails
	 */
	public ConcurrentLogger(boolean breakLines, int entitySeparationSpace, String logFilePath, LoggerStatus status, int nThieves) throws IOException, RemoteException {
//		super();
		this.breakLines = breakLines;
		this.entitySeparationSpace = entitySeparationSpace;
		Files.deleteIfExists(Paths.get(logFilePath));
		this.fw = new FileWriter(logFilePath);
		this.status = status;
		this.nThieves = nThieves;
		this.roomsFlag = false;
		this.thievesFlag = false;
		this.thievesCounter = 0;
	}


	/**
	 * Logs a sequence of strings
	 * <p>
	 * WARNING: This method is to be called behind locks.
	 * This is just a wrapper around the IO operations and not a safe writing method.
	 * <p>
	 * Adding locks here would require a more cumbersome management of locks in the public facing methods,
	 * since the simulation state is not thread safe and needs to be manipulated in these methods.
	 *
	 * @param messages the messages to print
	 */
	private void log(String... messages) {
		try {
			for (String m : messages) {
				System.out.println(m);
				fw.write(m);
				fw.write('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs the current simulation status
	 * <p>
	 * WARNING: This method is to be called behind locks.
	 * This is just a wrapper around the IO operations and not a safe writing method.
	 * <p>
	 * Adding locks here would require a more cumbersome management of locks in the public facing methods,
	 * since the simulation state is not thread safe and needs to be manipulated in these methods.
	 */
	private void log() {
		try {
			final String m = status.getMessage(entitySeparationSpace, breakLines);
			System.out.println(m);
			fw.write(m);
			fw.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void header() throws RemoteException {
		try {
			lock.lock();
			final int nRooms = status.getMuseum().getRooms().length;
//			while (!(thievesFlag && roomsFlag))
//				setup.await();
			final StringBuilder h1 = new StringBuilder();
			final StringBuilder h2 = new StringBuilder();
			final StringBuilder h3 = new StringBuilder();


			// master
			h1.append("MstT").append(nChars(' ', entitySeparationSpace));
			h2.append("Stat").append(nChars(' ', entitySeparationSpace));

			//thieves
			final String thiefH2 = "Stat" + nChars(' ', entitySeparationSpace) + "S" + nChars(' ', entitySeparationSpace) + "MD" // + nChars(' ', entitySeparationSpace)
					;
			for (int i = 0; i < nThieves; i++) {
				h1.append(center("Thief" + (i + 1), thiefH2.length())).append(nChars(' ', entitySeparationSpace));
				h2.append(thiefH2).append(nChars(' ', entitySeparationSpace));
			}
			h3.append(nChars(' ', h1.length()));

			if (breakLines) {
				log(h1.toString(), h2.toString());
				h1.setLength(0);
				h2.setLength(0);
				h3.setLength(0);
				h1.append("\t");
				h2.append("\t");
				h3.append("\t");
			}

			//assault parties
			final String elemH3 = "Id" + nChars(' ', entitySeparationSpace) + "Pos" + nChars(' ', entitySeparationSpace) + "Cv";
			for (int i = 0; i < status.getAssaultParties().length; i++) {
				final int nElems = status.getAssaultParties()[i].getElements().length;
				h1.append(center("Assault Party " + (i + 1), 3 + entitySeparationSpace + nElems * (entitySeparationSpace + elemH3.length())));
				h2.append(nChars(' ', 3 + entitySeparationSpace));
				h3.append("RId").append(nChars(' ', entitySeparationSpace));

				for (int i1 = 0; i1 < nElems; i1++) {
					h2.append(center("Element " + (i1 + 1), elemH3.length())).append(nChars(' ', entitySeparationSpace));
					h3.append(elemH3).append(nChars(' ', entitySeparationSpace));
				}
			}

			//museum
			final String museumH3 = "NP" + nChars(' ', entitySeparationSpace) + "DT";
			h1.append(center("Museum", nRooms * (museumH3.length() + entitySeparationSpace)));
			for (int i = 0; i < nRooms; i++) {
				h2.append(center("Room " + (i + 1), museumH3.length())).append(nChars(' ', entitySeparationSpace));
				h3.append(museumH3).append(nChars(' ', entitySeparationSpace));
			}

			log(h1.toString(), h2.toString(), h3.toString());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Setup room's distances.
	 * The array index is the identifier of the room
	 * The logging process hangs while this has not been called
	 *
	 * @param roomDistance the distance to each room
	 * @param roomCanvas   the number of canvas in each room
	 */
	@Override
	public void setupRooms(int[] roomDistance, int[] roomCanvas) throws RemoteException {
		try {
			lock.lock();
			status.setupRooms(roomDistance, roomCanvas);
			roomsFlag = true;
			if (thievesFlag)
				setup.signal();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Setup thief's agility.
	 * The logging process hangs until this has been called for all thieves
	 *
	 * @param id      the identifier of the thief
	 * @param agility the thief's agility
	 */
	public void setAgility(int id, int agility) throws RemoteException {
		try {
			lock.lock();
			status.setAgility(id, agility);
			thievesCounter++;
			if (thievesCounter == nThieves) {
				thievesFlag = true;
				if (roomsFlag)
					setup.signal();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * The master enters the DecidingWhatToDo state
	 */
	@Override
	public void masterDecidingWhatToDo() throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.setMasterStatus('D');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * For the transition of the master thief to the waiting for arrival state
	 */
	@Override
	public void takeRest() throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.setMasterStatus('W');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {

			lock.unlock();
		}
	}

	/**
	 * Logs the results of the simulation.
	 * Also closes the file writer, no more main.logging can be done after this point.
	 *
	 * @param paintings Number of stolen paintings
	 */
	@Override
	public void sumUpResults(int paintings) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.setMasterStatus('P');
			log();
			log(String.format("My friends, tonight's effort produced %s priceless paintings!", paintings));
			fw.flush();
			fw.close();
			hasPresentedResults = true;
			completed.signal();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs that the thief is either at the concentration site or the collection site
	 *
	 * @param thief The thief that has arrived at a control site
	 */
	@Override
	public void atControl(int thief) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.getThieves()[thief].setStat('C');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the robbery act.
	 *
	 * @param thief                 the thief robbing
	 * @param roomID                the room he is robbing from
	 * @param thiefIdInAssaultParty the thief's identifier in the assault party
	 * @param partyId               the thief's party identifier
	 */
	@Override
	public void rob(int thief, int roomID, int thiefIdInAssaultParty, int partyId) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.getThieves()[thief].setStat('A');
			final RoomStatus room = status.getMuseum().getRooms()[roomID];
			room.setnPaintings(room.getnPaintings() - 1);
			final AssaultPartyStatus ap = status.getAssaultParties()[partyId];
			ap.setHasCanvas(thiefIdInAssaultParty, true);
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the act of a thief handing a canvas to the master thief
	 *
	 * @param thiefIdInAssaultParty the thief's identifier in the assault party
	 * @param partyId               the thief's party identifier
	 */
	@Override
	public void handCanvas(int thiefIdInAssaultParty, int partyId) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			final AssaultPartyStatus ap = status.getAssaultParties()[partyId];
			ap.setHasCanvas(thiefIdInAssaultParty, false);
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the thief entering the crawl in state
	 *
	 * @param thief the thief that started to crawl in
	 */
	@Override
	public void crawlIn(int thief) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.getThieves()[thief].setStat('I');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the thief entering the crawl out state
	 *
	 * @param thief the thief that started to crawl out
	 */
	@Override
	public void crawlOut(int thief) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.getThieves()[thief].setStat('O');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the master thief entering the prepare assault party state
	 */
	@Override
	public void preparingAssaultParty() throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.setMasterStatus('P');
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the change of room of an assault party
	 *
	 * @param partyId the identifier of the assault party
	 * @param roomId  the identifier of the new room
	 */
	@Override
	public void setRoom(int partyId, int roomId) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			final AssaultPartyStatus ap = status.getAssaultParties()[partyId];
			ap.setrId(roomId);
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs a change of distance from the thief to the room or collection site
	 *
	 * @param partyId               Assault party's identifier
	 * @param thiefIdInAssaultParty Identifier of the thief in the assault party
	 * @param distance              The new distance the thief is from the destination
	 */
	@Override
	public void setDistance(int partyId, int thiefIdInAssaultParty, int distance) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			final AssaultPartyStatus ap = status.getAssaultParties()[partyId];
			ap.setDistance(thiefIdInAssaultParty, distance);
			log();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Logs the arrival of the thief at the destination room
	 *
	 * @param thief The thief taht arrived
	 */
	@Override
	public void atRoom(int thief) throws RemoteException {
		try {
			lock.lock();
			while (!(thievesFlag && roomsFlag))
				setup.await();
			status.getThieves()[thief].setStat('A');
			log();
		} catch (InterruptedException e) {
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
