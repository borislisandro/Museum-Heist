package logger;

import static logger.StringUtils.nChars;

/**
 * Represents the data of a room that is to be logged
 */
public class RoomStatus implements Loggable {
    /**
     * The current number of paintings
     */
    private int nPaintings;
    /**
     * The distance it is from the concentration site
     */
    private int distance;


    /**
     * Instantiates the RoomStatus
     */
    public RoomStatus() {
        this.nPaintings = 0;
        this.distance = 0;
    }

    @Override
    public String getMessage(int nEmptyChars, boolean breakLines) {
        return String.format("%2d", nPaintings) + nChars(' ', nEmptyChars) + String.format("%2d", distance);
    }

    /**
     * Getter for the number of paintings in the room
     *
     * @return the number of paintings in the room
     */
    public int getnPaintings() {
        return nPaintings;
    }

    /**
     * Setter for the number of paintings in the room
     *
     * @param nPaintings The number of paintings in the room
     */
    public void setnPaintings(int nPaintings) {
        this.nPaintings = nPaintings;
    }

    /**
     * Sets the distance and number of paintings in this room
     *
     * @param roomDistance The distance this room is from the enterance
     * @param nPaintings   The number of paintings it initially holds
     */
    public void setupRoom(int roomDistance, int nPaintings) {
        this.distance = roomDistance;
        this.nPaintings = nPaintings;
    }

}
