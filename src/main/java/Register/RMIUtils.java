package Register;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class of utility methods to abstract some logic of dealing with remote objects.
 * Can handle finding, registration and unbinding
 */
public class RMIUtils {

	/**
	 * Name to which the register object will be registered
	 */
	public static final String REGISTER_NAME = "REGISTER";

	private Registry registry;
	private Register register;

	/**
	 * Creates a RMIUtils instance.
	 * Connects to the remote registry and creates a {@link Register} object
	 *
	 * @param rmiRegHostName The host where the RMI registry is running.
	 * @param rmiRegPortNumb The port where the RMI registry is exposed in it's machine
	 */
	public RMIUtils(String rmiRegHostName, int rmiRegPortNumb) {
		try {
			registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
		} catch (RemoteException e) {
			System.err.println("RMI registry creation exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("RMI registry was created!");
		try {
			register = (Register) registry.lookup(REGISTER_NAME);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject lookup exception: " + e.getMessage());
			System.exit(0);
		} catch (NotBoundException e) {
			System.err.println("RegisterRemoteObject not bound exception: " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Exports and registers an object.
	 * In order to register multiple objects of the same class, please use {@link RMIUtils#register(Remote, int, int)}
	 *
	 * @param obj  The object to be exported
	 * @param port The port to listen to remote requests
	 * @param <T>  The object's type. Must implement Remote
	 */
	public <T extends Remote> void register(T obj, int port) {
		final T stub;                                                               // remote reference to it

		try {
			stub = (T) UnicastRemoteObject.exportObject(obj, port);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject stub generation exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
			return;
		}

		try {
			register.bind(obj.getClass().getName(), stub);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject stub bind exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (AlreadyBoundException e) {
			System.err.println("RegisterRemoteObject stub already bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Exports and registers an object.
	 * This method can be used to register multiple instances of the same class.
	 * Just pass different values to cnt in order to register multiple instances.
	 *
	 * @param obj  The object to be exported
	 * @param cnt  the identifier of this object's instance in the RMI registry
	 * @param port The port to listen to remote requests
	 * @param <T>  The object's type. Must implement Remote
	 */
	public <T extends Remote> void register(T obj, int cnt, int port) {
		final T stub;                                                               // remote reference to it

		try {
			stub = (T) UnicastRemoteObject.exportObject(obj, port);
		} catch (RemoteException e) {
			System.out.println("RegisterRemoteObject stub generation exception: " + e.getMessage());
			System.exit(0);
			return;
		}

		try {
			register.bind(obj.getClass().getName(), stub);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject stub bind exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (AlreadyBoundException e) {
			System.err.println("RegisterRemoteObject stub already bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Finds an object of a given type in the registry.
	 * In order to find a specific instance among many of the same class,
	 * please use {@link RMIUtils#find(Class, int)}
	 *
	 * @param type The object's class
	 * @param <T>  The object's type. Must implement Remote
	 * @return The remote object.
	 */
	public <T extends Remote> Remote find(Class<T> type) {
		try {
			return registry.lookup(type.getName());
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject object lookup exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			System.err.println("RegisterRemoteObject object not bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	/**
	 * Finds a specific instance of an object of a given type in the registry.
	 *
	 * @param type The object's class
	 * @param cnt  the identifier of this object's instance in the RMI registry
	 * @param <T>  The object's type. Must implement Remote
	 * @return The remote object.
	 */
	public <T extends Remote> Remote find(Class<T> type, int cnt) {
		try {
			return registry.lookup(type.getName() + cnt);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject object lookup exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			System.err.println("RegisterRemoteObject object not bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	/**
	 * Removes and unexports the object from the remote repository.
	 * It must have been registered prior.
	 * If the object is one of many from the same class, please use {@link RMIUtils#unbind(Remote, int)}
	 *
	 * @param obj the object to be removed and unexported
	 * @param <T> the object's type
	 */
	public <T extends Remote> void unbind(T obj) {

		try {
			register.unbind(obj.getClass().getName());
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject object lookup exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			System.err.println("RegisterRemoteObject object not bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		try {
			UnicastRemoteObject.unexportObject(obj, true);
		} catch (NoSuchObjectException e) {
			System.err.println("RegisterRemoteObject object does not exist exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Removes and unexports the object from the remote repository.
	 * It must have been registered prior.
	 * To be used when the object is one of many exported of the same class.
	 *
	 * @param obj the object to be removed and unexported
	 * @param cnt the identifier of this object's instance in the RMI registry
	 * @param <T> the object's type
	 */
	public <T extends Remote> void unbind(T obj, int cnt) {
		try {
			register.unbind(obj.getClass().getName() + cnt);
		} catch (RemoteException e) {
			System.err.println("RegisterRemoteObject object lookup exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (NotBoundException e) {
			System.err.println("RegisterRemoteObject object not bound exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		try {
			UnicastRemoteObject.unexportObject(obj, true);
		} catch (NoSuchObjectException e) {
			System.err.println("RegisterRemoteObject object does not exist exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}
}
