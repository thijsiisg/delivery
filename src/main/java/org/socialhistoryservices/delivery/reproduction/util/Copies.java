package org.socialhistoryservices.delivery.reproduction.util;

import org.socialhistoryservices.delivery.record.entity.Record;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Igor on 4/26/2017.
 */
public class Copies {

    private static final int NO_NUMBER_OF_COPIES = 0;
    private static final int DEFAULT_NUMBER_OF_COPIES = 1;

    private static final Pattern PATTERN_CONTAINS_COPIES = Pattern.compile("([0-9]+ p\\.)|([0-9]+ ex)");
    private static final Pattern PATTERN_BETWEEN_BRACKETS = Pattern.compile("\\(([^)]+)\\)");
    private static final Pattern PATTERN_LEFT_AND_COPIES = Pattern.compile("(.*?)(([0-9]+) p\\.|([0-9]+) ex)");
    private static final Pattern PATTERN_SEPERATORS = Pattern.compile("[\\p{Punct}\\s]+");
    private static final Pattern PATTERN_NUMBERS = Pattern.compile("[0-9]+");

    private Record record;
    private int numberOfCopies;

    public Copies(Record record){
        this.record = record;
        this.numberOfCopies = determineNumberOfCopies();
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public boolean containsNumberOfCopies(){
        return numberOfCopies > NO_NUMBER_OF_COPIES;
    }

    private int determineNumberOfCopies() {
        String left = "";
        String copies = "";

        // Do we have a physical description?
        String physicalDescription = record.getPhysicalDescription();
        if ((physicalDescription == null) || physicalDescription.isEmpty())
            return DEFAULT_NUMBER_OF_COPIES;

        // First count the number of copies found
        int count = NO_NUMBER_OF_COPIES;
        Matcher containsPagesMatcher = PATTERN_CONTAINS_COPIES.matcher(physicalDescription);
        while (containsPagesMatcher.find())
            count++;

        // If no copies were found, or more than one, then stop here
        if ((count == 0) || (count > 1))
            return DEFAULT_NUMBER_OF_COPIES;

        // Get the part between brackets containing the number of copies
        Matcher betweenBracketsMatcher = PATTERN_BETWEEN_BRACKETS.matcher(physicalDescription);
        while (betweenBracketsMatcher.find()) {
            String betweenBrackets = betweenBracketsMatcher.group(1);
            Matcher groupContainsPagesMatcher = PATTERN_CONTAINS_COPIES.matcher(betweenBrackets);
            if (groupContainsPagesMatcher.find()) {
                physicalDescription = betweenBrackets;
                break;
            }
        }

        // Then divide in two parts: the page numbering and whatever is on the left
        Matcher leftAndPagesMatcher = PATTERN_LEFT_AND_COPIES.matcher(physicalDescription);
        while (leftAndPagesMatcher.find()) {
            left = leftAndPagesMatcher.group(1);
            copies = (leftAndPagesMatcher.group(3) == null)
                ? leftAndPagesMatcher.group(4)
                : leftAndPagesMatcher.group(3);
        }

        // Attempt to divide whatever is on the left into seperate parts and get the last one
        String[] leftParts = PATTERN_SEPERATORS.split(left);
        String lastLeftPart = leftParts[leftParts.length - 1];

        // If the last part contains only numbers, this might be part of the number of pages, so stop here
        Matcher numbersMatcher = PATTERN_NUMBERS.matcher(lastLeftPart);
        if (numbersMatcher.matches())
            return DEFAULT_NUMBER_OF_COPIES;

        // Otherwise we have found the number of pages
        return Integer.parseInt(copies);
    }
}
