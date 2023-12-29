package logger;

import static logger.StringUtils.nChars;

/**
 * Class representing every property of an assault party element that is to b elogged
 */
public class ElementStatus implements Loggable {
    /**
     * The identifier in the assault party
     */
    private final int id;
    /**
     * The position during the crawl movement, -1 if not crawling
     */
    private int pos;
    /**
     * Whether he is carrying a canvas or not
     */
    private boolean isCarryingCanvas;

    /**
     * Constructor for the ElementStatus class
     *
     * @param id               the identifier in the assault party
     * @param pos              the position during the crawl movement (-1 if not crawling)
     * @param isCarryingCanvas whether he is carrying a canvas
     */
    public ElementStatus(int id, int pos, boolean isCarryingCanvas) {
        this.id = id;
        this.pos = pos;
        this.isCarryingCanvas = isCarryingCanvas;
    }

    @Override
    public String getMessage(int nEmptyChars, boolean breakLines) {
        return String.format("%2d", id) + nChars(' ', nEmptyChars)
                + String.format("%3d", pos) + nChars(' ', nEmptyChars)
                + (isCarryingCanvas ? "Y " : "N ")
                ;
    }

    /**
     * Set element's current distance
     *
     * @param distance The distance
     */
    public void setDistance(int distance) {
        this.pos = distance;
    }

    /**
     * Set element's carrying canvas status
     *
     * @param hasCanvas The carrying canvas status
     */
    public void setHasCanvas(boolean hasCanvas) {
        this.isCarryingCanvas = hasCanvas;
    }
}
