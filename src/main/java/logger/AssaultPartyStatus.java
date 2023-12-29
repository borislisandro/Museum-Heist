package logger;

import static logger.StringUtils.nChars;

/**
 * Represents all data about an assault party that is to be logged
 */
public class AssaultPartyStatus implements Loggable {
    /**
     * The status of each of the party's elements.
     * The index is their identifier in the assault party
     */
    private final ElementStatus[] elements;
    /**
     * The identifier of the room assigned to the assault party
     */
    private int rId;

    /**
     * Constructor for the AssaultPartyStatus
     *
     * @param rId      The identifier of the room assigned to the assault party
     * @param elements The status of each of the party's elements
     */
    public AssaultPartyStatus(int rId, ElementStatus[] elements) {
        this.rId = rId;
        this.elements = elements;
    }

    @Override
    public String getMessage(int nEmptyChars, boolean breakLines) {
        StringBuilder s = new StringBuilder();
        s
                .append(String.format("%2d", rId))
                .append(nChars(' ', nEmptyChars + 1))
        ;
        for (ElementStatus e : elements)
            s.append(e.getMessage(nEmptyChars, breakLines)).append(nChars(' ', nEmptyChars));
        return s.toString();
    }

    /**
     * Get assault party's elements
     *
     * @return The assault party's elements
     */
    public ElementStatus[] getElements() {
        return elements;
    }

    /**
     * Set assault party's target room
     *
     * @param rId The target room ID
     */
    public void setrId(int rId) {
        this.rId = rId;
    }

    /**
     * Set element's current distance
     *
     * @param partyId  The element's party ID
     * @param distance The element's current distance
     */
    public void setDistance(int partyId, int distance) {
        this.elements[partyId].setDistance(distance);
    }

    /**
     * Set element's carrying canvas state
     *
     * @param partyId   The element's party ID
     * @param hasCanvas The carrying canvas state
     */
    public void setHasCanvas(int partyId, boolean hasCanvas) {
        this.elements[partyId].setHasCanvas(hasCanvas);
    }
}
