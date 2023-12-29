package assaultParty;

import Register.CloseableServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the methods the assault party monitor implements
 */
public interface IAssaultParty extends Remote, CloseableServer {

	/**
	 * Thief crawls inwards the room
	 * <p>
	 * While crawling, certain rules must be followed:
	 * - 2 thieves cannot be on the same position
	 * - 2 consecutive thieves cannot be more than maxDistance apart from each other
	 *
	 * @param agility          the maximum distance this thief can walk
	 * @param idInAssaultParty the unique identifier of the thief in the party
	 * @param thief            thief's unique identifier
	 * @return true if the room has not yet been reached
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean crawlIn(int agility, int idInAssaultParty, int thief) throws RemoteException;

	/**
	 * When the last thief to arrive has robbed (or has seen no more paintings to rob),
	 * the party must reverse direction and go to the collection site
	 *
	 * @param thief the thief ID
	 * @throws RemoteException when the call to this remote method fails
	 */
	void reverseDirection(int thief) throws RemoteException;

	/**
	 * Thief crawls outwards to the collection site
	 * <p>
	 * While crawling, certain rules must be followed:
	 * - 2 thieves cannot be on the same position
	 * - 2 consecutive thieves cannot be more than maxDistance apart from each other
	 *
	 * @param agility          the maximum distance this thief can walk
	 * @param idInAssaultParty the unique identifier of the thief in the party
	 * @return true if the room has not yet been reached
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean crawlOut(int agility, int idInAssaultParty) throws RemoteException;

	/**
	 * Gets the assault party size
	 *
	 * @return AssaultParty party size
	 * @throws RemoteException when the call to this remote method fails
	 */
	int getPartySize() throws RemoteException;

	/**
	 * Sets the target room of the assault party
	 *
	 * @param roomID       target room ID
	 * @param roomDistance target room's distance
	 * @throws RemoteException when the call to this remote method fails
	 */
	void setRoom(int roomID, int roomDistance) throws RemoteException;

	/**
	 * Gets the assault party target room ID
	 *
	 * @return assault party's target room's unique identifier
	 * @throws RemoteException when the call to this remote method fails
	 */
	int getRoomID() throws RemoteException;

	/**
	 * Gets the assault part's unique identifier
	 *
	 * @return assault party's unique identifier
	 * @throws RemoteException when the call to this remote method fails
	 */
	int getPartyId() throws RemoteException;

	/**
	 * Logs that the thief is either at the concentration site or the collection site
	 *
	 * @param thief The thief that has arrived at a control site
	 * @throws RemoteException when the call to this remote method fails
	 */
	void atControl(int thief) throws RemoteException;

}
