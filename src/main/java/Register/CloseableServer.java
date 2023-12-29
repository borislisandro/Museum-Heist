package Register;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface to hold methods that close a RMI server
 */
public interface CloseableServer extends Remote {
	/**
	 * Waits for the server to shut down.
	 * Should be called in the main class that registers the server to make it wait until the internal server shuts down
	 *
	 * @throws RemoteException When the call to the remote method fails
	 */
	void waitUntilCompletion() throws RemoteException;

	/**
	 * Sends a request for the server to shut down.
	 * The server is not equired to shut down right away, refer to the class specific documentation
	 *
	 * @throws RemoteException When the call to the remote method fails
	 */
	void shutdown() throws RemoteException;
}
