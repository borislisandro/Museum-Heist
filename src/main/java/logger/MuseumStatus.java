package logger;

import static logger.StringUtils.nChars;

/**
 * Class representing every property of the museum to be logged
 */
public class MuseumStatus implements Loggable {
    /**
     * The status of each room.
     * It's identifier is the array index
     */
    private final RoomStatus[] rooms;

    /**
     * Constructor for the RoomStatus class
     *
     * @param rooms the status of every room
     */
    public MuseumStatus(RoomStatus[] rooms) {
        this.rooms = rooms;
    }

    @Override
    public String getMessage(int nEmptyChars, boolean breakLines) {
        StringBuilder s = new StringBuilder();
        for (RoomStatus r : rooms)
            s.append(r.getMessage(nEmptyChars, breakLines)).append(nChars(' ', nEmptyChars));
        return s.toString();
    }

    /**
     * Get room's status
     *
     * @return The room's status
     */
    public RoomStatus[] getRooms() {
        return rooms;
    }

    /**
     * Used to initially set up the all rooms
     *
     * @param roomDistance The room's distances
     * @param roomCanvas   The room's number of canvas
     */
    public void setupRooms(int[] roomDistance, int[] roomCanvas) {
        int i = 0;
        for (RoomStatus x : rooms) {
            x.setupRoom(roomDistance[i], roomCanvas[i]);
            i++;
        }

    }

}
