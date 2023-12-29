package collectionSite;

import Register.CloseableServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the methods the collection site monitor implements
 */
public interface ICollectionSite extends Remote, CloseableServer {

	/**
	 * Master thief starts the heist
	 * @throws RemoteException when the call to this remote method fails
	 */
	void startOperations() throws RemoteException;

	/**
	 * Thief waits until master signals to send its assault party or until master signals the end ot the heist
	 *
	 * @param partyNumber the identifier of the thief's assault party
	 * @param roomID      the id of the room he last went to
	 * @return {true,don't care} (second parameter has no meaning) if heist is finished, {false, true} if assault party needs a new room and {false, false} if assault party doesn´t need a new room
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean[] amINeeded(int partyNumber, int roomID) throws RemoteException;

	/**
	 * Master thief signals the awaiting thieves of the party he wants to prepare
	 *
	 * @return true if he wants to send an assault party or false if there is no party to send
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean prepareAssaultParty() throws RemoteException;

	/**
	 * Master thief takes a rest until the arrival of a thief to the collection site.
	 * @throws RemoteException when the call to this remote method fails
	 */
	void takeARest() throws RemoteException;

	/**
	 * Thief hands a canvas if he has one
	 *
	 * @param thief                 the thief that hands a canvas
	 * @param room                  the room it came from
	 * @param nCanvas               the number of canvas delivered
	 * @param partyId               the identifier of the assault party
	 * @param thiefIdInAssaultParty the thief's identifier in the assault party
	 * @throws RemoteException when the call to this remote method fails
	 */
	void handACanvas(int thief, int room, int nCanvas, int thiefIdInAssaultParty, int partyId) throws RemoteException;

	/**
	 * Master thief collects what thief had
	 * @throws RemoteException when the call to this remote method fails
	 */
	void collectACanvas() throws RemoteException;

	/**
	 * Master thief presents the final report of the heist
	 * @throws RemoteException when the call to this remote method fails
	 */
	void sumUpResults() throws RemoteException;

	/**
	 * Thief verifies if he can handACanvas()
	 *
	 * @return true if master is resting or false if master is not resting
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean isMasterResting() throws RemoteException;

	/**
	 * Master verifies if heist is finished or not
	 *
	 * @return true if heist is completed or false if heist is still running
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean isEnd() throws RemoteException;

	/**
	 * Makes master wait until all thieves are initialized
	 * @throws RemoteException when the call to this remote method fails
	 */
	void waitForThieves() throws RemoteException;

}
