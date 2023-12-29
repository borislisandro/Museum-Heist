package logger;

import static logger.StringUtils.nChars;

/**
 * The data about a thief that is to be logged
 */
public class ThiefLogStatus implements Loggable {
	/**
	 * Whether he is waiting for a room to rob (W) or robbing (R)
	 */
	private final char waitingOrRobbing;
	/**
	 * The status of the thief
	 */
	private char stat;
	/**
	 * The maximum distance he can walk at once
	 */
	private int maxDisplacement;

	/**
	 * Constructor for the ThiefLogStatus
	 *
	 * @param stat             the thief status
	 * @param waitingOrRobbing whether he is waiting for a room to rob (W) or robbing (R)
	 */
	public ThiefLogStatus(char stat, char waitingOrRobbing) {
		this.stat = stat;
		this.waitingOrRobbing = waitingOrRobbing;
		this.maxDisplacement = -1;
	}


	@Override
	public String getMessage(int nEmptyChars, boolean breakLines) {
		return String.format(" %C  ", stat) + nChars(' ', nEmptyChars) +
				String.format("%C", waitingOrRobbing) + nChars(' ', nEmptyChars) +
				String.format("%2d", maxDisplacement)
				;
	}

	/**
	 * Setter for the thief state
	 *
	 * @param stat the thief's state
	 */
	public void setStat(char stat) {
		this.stat = stat;
	}

	/**
	 * Setter for the maximum distance the thief can crawl
	 *
	 * @param agility The thief's agility
	 */
	public void setMaxDisplacement(int agility) {
		maxDisplacement = agility;
	}

}
