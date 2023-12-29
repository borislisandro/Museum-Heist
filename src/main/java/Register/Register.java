package Register;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Operational interface of a remote object of type RegisterRemoteObject.
 * <p>
 * It provides the functionality to register remote objects in the local RMI registry service.
 */

public interface Register extends Remote {
    /**
     * Binds a remote reference to the specified name in this registry.
     *
     * @param name the name to associate with the reference to the remote object
     * @param ref  reference to the remote object
     * @throws RemoteException       if either the invocation of the remote method, or the communication with the registry
     *                               service fails
     * @throws AlreadyBoundException if the name is already registered
     */

    void bind(String name, Remote ref) throws RemoteException, AlreadyBoundException;

    /**
     * Removes the binding for the specified name in this registry.
     *
     * @param name the name associated with the reference to the remote object
     * @throws RemoteException   if either the invocation of the remote method, or the communication with the registry
     *                           service fails
     * @throws NotBoundException if the name is not in registered
     */

    void unbind(String name) throws RemoteException, NotBoundException;

    /**
     * Replaces the binding for the specified name in this registry with the supplied remote reference.
     * <p>
     * If a previous binding for the specified name exists, it is discarded.
     *
     * @param name the name to associate with the reference to the remote object
     * @param ref  reference to the remote object
     * @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                         service fails
     */

    void rebind(String name, Remote ref) throws RemoteException;
}
