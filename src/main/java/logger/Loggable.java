package logger;

/**
 * Each class on the logger must have a method to extract its message and concatenate in each line.
 */
public interface Loggable {
    /**
     * The method that extracts the information from each class correctly formatted to fit the logger format.
     *
     * @param nEmptyChars Number of spaces between each value
     * @param breakLines  Whether to break the current entry in multiple lines.
     *                    It is not mandatory to use in every class, refer to the logger specification for instructions.
     * @return The string corresponding to this class that is to be added to the logger.
     */
    String getMessage(int nEmptyChars, boolean breakLines);
}
