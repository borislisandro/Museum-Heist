package logger;

import static logger.StringUtils.nChars;

/**
 * Class representing every property in a log entry
 */
public class LoggerStatus implements Loggable {
    /**
     * The status of each thief.
     * Their identifier is the array index
     */
    private final ThiefLogStatus[] thieves;
    /**
     * The status of each assault party.
     * The assault party identifier is the array index
     */
    private final AssaultPartyStatus[] assaultParties;
    /**
     * The status of the museum
     */
    private final MuseumStatus museum;
    /**
     * The status of the master thief
     */
    private char masterStatus;


    /**
     * Constructor for the LoggerStatus class
     *
     * @param masterStatus   The status of the master thief
     * @param thieves        The status of each thief
     * @param assaultParties The status of each assault party
     * @param museum         The status of the museum
     */
    public LoggerStatus(char masterStatus, ThiefLogStatus[] thieves, AssaultPartyStatus[] assaultParties, MuseumStatus museum) {
        this.masterStatus = masterStatus;
        this.thieves = thieves;
        this.assaultParties = assaultParties;
        this.museum = museum;
    }

    @Override
    public String getMessage(int nEmptyChars, boolean breakLines) {
        StringBuilder s = new StringBuilder(String.format(" %C  ", masterStatus) + nChars(' ', nEmptyChars));

        for (ThiefLogStatus thief : thieves)
            s.append(thief.getMessage(nEmptyChars, breakLines)).append(nChars(' ', nEmptyChars));

        if (breakLines)
            s.append("\n\t");

        for (AssaultPartyStatus assaultParty : assaultParties)
            s.append(assaultParty.getMessage(nEmptyChars, breakLines));//.append(nChars(' ', nEmptyChars));

        s.append(museum.getMessage(nEmptyChars, breakLines));

        return s.toString();
    }

    /**
     * Set master thief logger status
     *
     * @param masterStatus The master thief status
     */
    public void setMasterStatus(char masterStatus) {
        this.masterStatus = masterStatus;
    }

    /**
     * Get thief's log status
     *
     * @return The thief's log status
     */
    public ThiefLogStatus[] getThieves() {
        return thieves;
    }

    /**
     * Get assault party's log status
     *
     * @return The assault party's log status
     */
    public AssaultPartyStatus[] getAssaultParties() {
        return assaultParties;
    }

    /**
     * Get museum's log status
     *
     * @return The museum's log status
     */
    public MuseumStatus getMuseum() {
        return museum;
    }

    /**
     * Used to initially set up the all rooms
     *
     * @param roomDistance The room's distances
     * @param roomCanvas   The room's number of canvas
     */
    public void setupRooms(int[] roomDistance, int[] roomCanvas) {
        this.museum.setupRooms(roomDistance, roomCanvas);
    }

    /**
     * Used to initialy set up thief's agility
     *
     * @param id      The thief's id
     * @param agility The thief's agility
     */
    public void setAgility(int id, int agility) {
        this.thieves[id].setMaxDisplacement(agility);
    }

}
