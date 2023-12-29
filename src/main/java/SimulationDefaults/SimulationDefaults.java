package SimulationDefaults;

import java.rmi.RemoteException;

/**
 * Holds the configuration for the simulation
 */
public class SimulationDefaults {

    /**
     * The default simulation values as specified by the assignment
     */
    public static final SimulationDefaults DEFAULT = new SimulationDefaults(
            2,
            3,
            3,
            6,
            2,
            6,
            5,
            15,
            30,
            8,
            16,
            false,
            3,
            "./log.txt",
            10000
    );

    /* Thieves/Assault party config*/
    /**
     * Number of assault parties (default: 2)
     */
    private final int numberOfAssaultParties /*= 2*/;
    /**
     * Number of thieves per assault party (default: 3)
     */
    private final int assaultPartySize /*= 2*/;
    /**
     * Maximum distance between 2 consecutive thieves (default: 3)
     */
    private final int maxSeparation /*= 3*/;
    /**
     * Number of thieves in the party (excluding the Master) (default: 6)
     */
    private final int numberOfThieves /*= 3*/;
    /**
     * Minimum distance for a thief's agility (default: 2)
     */
    private final int minDisplacement /*= 3*/;
    /**
     * Maximum distance for a thief's agility (default: 6)
     */
    private final int maxDisplacement /*= 3*/;

    /* Room config */
    /**
     * Number of rooms in the museum (default: 5)
     */
    private final int numberOfRooms /*= 5*/;
    /**
     * Minimum distance a room can be from the concentration site (default: 15)
     */
    private final int minDistance /*= 30*/;
    /**
     * Maximum distance a room can be from the concentration site (default: 30)
     */
    private final int maxDistance /*= 30*/;
    /**
     * Minimum number of paintings a room can have (default: 8)
     */
    private final int minPaintings /*= 8*/;
    /**
     * Maximum number of paintings a room can have (default: 16)
     */
    private final int maxPaintings /*= 16*/;
    /**
     * Whether to break every log entry across lines (default: false)
     */
    private final boolean breakLogger /*=false*/;
    /**
     * Number of spaces between fields of the log (default: 3)
     */
    private final int entitySeparationSpace /*= 3*/;
    /**
     * path to where the log file should be written (default: ./log.txt)
     */
    private final String logFilePath; /*= "./log.txt"*/
    /**
     * Timeout for each connection (default: 1000ms)
     */
    private final int connectionTimeout; /*= 1000*/

    /**
     * Creates all the properties for the simulation
     *
     * @param numberOfAssaultParties Number of assault parties (default: 2)
     * @param assaultPartySize       Number of thieves per assault party (default: 3)
     * @param maxSeparation          Maximum distance between 2 consecutive thieves (default: 3)
     * @param numberOfThieves        Number of thieves in the party (excluding the Master) (default: 6)
     * @param minDisplacement        Minimum distance for a thief's agility (default: 2)
     * @param maxDisplacement        Maximum distance for a thief's agility (default: 6)
     * @param numberOfRooms          Number of rooms in the museum (default: 5)
     * @param minDistance            Minimum distance a room can be from the concentration site (default: 15)
     * @param maxDistance            Maximum distance a room can be from the concentration site (default: 30)
     * @param minPaintings           Minimum number of paintings a room can have (default: 8)
     * @param maxPaintings           Maximum number of paintings a room can have (default: 16)
     * @param breakLogger            Whether to break every log entry across lines (default: false)
     * @param entitySeparationSpace  Number of spaces between fields of the log (default: 3)
     * @param logFilePath            Path to where the log file should be written (default: ./log.txt)
     * @param connectionTimeout      Timeout for each connection (default: 1000ms)
     */
    public SimulationDefaults(int numberOfAssaultParties, int assaultPartySize, int maxSeparation, int numberOfThieves, int minDisplacement, int maxDisplacement, int numberOfRooms, int minDistance, int maxDistance, int minPaintings, int maxPaintings, boolean breakLogger, int entitySeparationSpace, String logFilePath, int connectionTimeout) {
        this.numberOfAssaultParties = numberOfAssaultParties;
        this.assaultPartySize = assaultPartySize;
        this.maxSeparation = maxSeparation;
        this.numberOfThieves = numberOfThieves;
        this.minDisplacement = minDisplacement;
        this.maxDisplacement = maxDisplacement;
        this.numberOfRooms = numberOfRooms;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.minPaintings = minPaintings;
        this.maxPaintings = maxPaintings;
        this.breakLogger = breakLogger;
        this.entitySeparationSpace = entitySeparationSpace;
        this.logFilePath = logFilePath;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * getter for the number of assault parties
     *
     * @return The number of assault parties
     */
    public int getNumberOfAssaultParties() {
        return numberOfAssaultParties;
    }

    /**
     * Getter for the number of thieves
     *
     * @return The number of thieves
     */
    public int getNumberOfThieves() {
        return numberOfThieves;
    }

    /**
     * Getter for the minimum thief displacement
     *
     * @return The minimum thief displacement
     */
    public int getMinDisplacement() {
        return minDisplacement;
    }

    /**
     * Getter for the maximum thief displacement
     *
     * @return The maximum thief displacement
     */
    public int getMaxDisplacement() {
        return maxDisplacement;
    }

    /**
     * Getter for the number of rooms
     *
     * @return The number of rooms
     */
    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    /**
     * Getter for the minimum distance to each room
     *
     * @return The minimum distance to each room
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * Getter for the maximum distance to each room
     *
     * @return The maximim distance to each room
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Getter for the minimum number of paintings in each room
     *
     * @return The minimum number of paintings in each room
     */
    public int getMinPaintings() {
        return minPaintings;
    }

    /**
     * Getter for the maximum number of paintings in each room
     *
     * @return The maximum number of paintings in each room
     */
    public int getMaxPaintings() {
        return maxPaintings;
    }

    /**
     * Getter for whether the logger will split each entry in 2 lines
     *
     * @return Whether the logger will split each entry in 2 lines
     */
    public boolean isBreakLogger() {
        return breakLogger;
    }

    /**
     * getter for the number of spaces separating each value in each logger entry
     *
     * @return The number of spaces separating each value in each logger entry
     */
    public int getEntitySeparationSpace() {
        return entitySeparationSpace;
    }

    /**
     * Getter for the path to the log file
     *
     * @return The path to the log file
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Getter for the size of an assault party
     *
     * @return the size of an assault party
     */
    public int getAssaultPartySize() {
        return assaultPartySize;
    }

    /**
     * Getter for the maximum distance 2 thieves can be from each other
     *
     * @return the maximum distance 2 thieves can be from each other
     */
    public int getMaxSeparation() {
        return maxSeparation;
    }

    /**
     * Getter for the time in ms that a communication can take
     *
     * @return the time in ms that a communication can take
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

}
