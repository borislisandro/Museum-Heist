package concentrationSite;

import Register.CloseableServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the methods the concentration site monitor implements
 */
public interface IConcentrationSite extends Remote, CloseableServer {

	/**
	 * The last thief to join/get ready in the assault party signals master thief that is waiting in prepareAssaultParty().
	 *
	 * @param getRoom flag used to know if new room attribution is needed
	 * @return -1 if thief isn't the last one or if assault party's target room still has canvas
	 * or a new roomID if the thief is the last one and the assault party's target room has no canvas left
	 * @throws RemoteException when the call to this remote method fails
	 */
	int prepareExcursion(boolean getRoom) throws RemoteException;

	/**
	 * Master thief wakes up the first assault party thief to start the crawl inward movement
	 * @throws RemoteException when the call to this remote method fails
	 */
	void sendAssaultParty() throws RemoteException;

}
