package museum;

import Register.CloseableServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the methods the museum monitor implements
 */
public interface IMuseum extends Remote, CloseableServer {

	/**
	 * Get the distance to a certain room
	 *
	 * @param roomID the room ID
	 * @return room distance
	 * @throws RemoteException when the call to this remote method fails
	 */
	int getRoomDistance(int roomID) throws RemoteException;

	/**
	 * Thief tries to steal a canvas
	 *
	 * @param thief          the thief ID
	 * @param roomID         the target room ID
	 * @param partyID        the thief's assault party ID
	 * @param assaultPartyID the assault party ID
	 * @return true if a canvas can be stolen, false otherwise
	 * @throws RemoteException when the call to this remote method fails
	 */
	boolean rollCanvas(int thief, int roomID, int partyID, int assaultPartyID) throws RemoteException;

	/**
	 * Gets number of canvas in each room
	 *
	 * @return the number of canvas in each room
	 * @throws RemoteException when the call to this remote method fails
	 */
	int[] getRoomPaintings() throws RemoteException;
}
