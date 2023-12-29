package logger;

import Register.CloseableServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the methods the concurrent logger monitor implements
 */
public interface IConcurrentLogger extends Remote, CloseableServer {

	/**
	 * logs the log header.
	 * @throws RemoteException when the call to this remote method fails
	 */
	void header() throws RemoteException;

	/**
	 * Set up the distance to each room.
	 * The index in the arrays is the unique identifier of each room
	 *
	 * @param roomDistance The distance to each room
	 * @param roomCanvas   The number of canvas in each room
	 * @throws RemoteException when the call to this remote method fails
	 */
	void setupRooms(int[] roomDistance, int[] roomCanvas) throws RemoteException;

	/**
	 * Setup thief's agility
	 *
	 * @param id      The thief's id
	 * @param agility The thief's agility
	 * @throws RemoteException when the call to this remote method fails
	 */
	void setAgility(int id, int agility) throws RemoteException;

	/**
	 * The master enters the DecidingWhatToDo state
	 * @throws RemoteException when the call to this remote method fails
	 */
	void masterDecidingWhatToDo() throws RemoteException;

	/**
	 * For the transition of the master thief to the waiting for arrival state
	 * @throws RemoteException when the call to this remote method fails
	 */
	void takeRest() throws RemoteException;

	/**
	 * Logs the results of the simulation.
	 * Also closes the file writer, no more main.logging can be done after this point.
	 *
	 * @param paintings Number of stolen paintings
	 * @throws RemoteException when the call to this remote method fails
	 */
	void sumUpResults(int paintings) throws RemoteException;

	/**
	 * Logs that the thief is either at the concentration site or the collection site
	 *
	 * @param thief The thief that has arrived at a control site
	 * @throws RemoteException when the call to this remote method fails
	 */
	void atControl(int thief) throws RemoteException;

	/**
	 * Logs the robbery act.
	 *
	 * @param thief                 the thief robbing
	 * @param roomID                the room he is robbing from
	 * @param thiefIdInAssaultParty the thief's identifier in the assault party
	 * @param partyId               the thief's party identifier
	 * @throws RemoteException when the call to this remote method fails
	 */
	void rob(int thief, int roomID, int thiefIdInAssaultParty, int partyId) throws RemoteException;

	/**
	 * Logs the act of a thief handing a canvas to the master thief
	 *
	 * @param thiefIdInAssaultParty the thief's identifier in the assault party
	 * @param partyId               the thief's party identifier
	 * @throws RemoteException when the call to this remote method fails
	 */
	void handCanvas(int thiefIdInAssaultParty, int partyId) throws RemoteException;

	/**
	 * Logs the thief entering the crawl in state
	 *
	 * @param thief the thief that started to crawl in
	 * @throws RemoteException when the call to this remote method fails
	 */
	void crawlIn(int thief) throws RemoteException;

	/**
	 * Logs the thief entering the crawl out state
	 *
	 * @param thief the thief that started to crawl out
	 * @throws RemoteException when the call to this remote method fails
	 */
	void crawlOut(int thief) throws RemoteException;

	/**
	 * Logs the master thief entering the prepare assault party state
	 * @throws RemoteException when the call to this remote method fails
	 */
	void preparingAssaultParty() throws RemoteException;

	/**
	 * Logs the change of room of an assault party
	 *
	 * @param partyId the identifier of the assault party
	 * @param roomId  the identifier of the new room
	 * @throws RemoteException when the call to this remote method fails
	 */
	void setRoom(int partyId, int roomId) throws RemoteException;

	/**
	 * Logs a change of distance from the thief to the room or collection site
	 *
	 * @param partyId               Assault party's identifier
	 * @param thiefIdInAssaultParty Identifier of the thief in the assault party
	 * @param distance              The new distance the thief is from the destination
	 * @throws RemoteException when the call to this remote method fails
	 */
	void setDistance(int partyId, int thiefIdInAssaultParty, int distance) throws RemoteException;

	/**
	 * Logs the arrival of the thief at the destination room
	 *
	 * @param thief The thief taht arrived
	 * @throws RemoteException when the call to this remote method fails
	 */
	void atRoom(int thief) throws RemoteException;

}
