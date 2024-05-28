package io.github.agomezlucena.libtory.books.domain;

import java.util.regex.Pattern;

/**
 * An ISBN is a string of digits or digits and hyphens than identifies a book.
 * must have one of both shapes:
 * <ul>
 *     <li>
 *         <strong>hyphen shape:</strong>
 *         <code>^97[89]-\d-(\d{2}-\d{6}|\d{3}-\d{5})-\d$</code>
 *     </li>
 *     <li>
 *         <strong>13 digits shape:</strong>
 *         <code>^(978|979)\d{10}$</code>
 *     </li>
 * </ul>
 *
 * @author Alejandro Gómez Lucena.
 */
public class Isbn  {
    private static final String ISBN_13_ATOM = "^97[89]-\\d-(\\d{2}-\\d{6}|\\d{3}-\\d{5})-\\d$";
    private static final String ISBN_13_PATTERN_STRING = String.format("(%s|^(978|979)\\d{10}$)", ISBN_13_ATOM);
    private static final Pattern ISBN_PATTERN = Pattern.compile(ISBN_13_PATTERN_STRING);

    private final String isbnLiteral;

    private Isbn(String id) {
        this.isbnLiteral = id;
    }

    /**
     * check if the given string matches with a valid ISBN 13 checking both the shape of the string
     * and also its checksum.
     * @param isbn a string that identifies a book.
     * @return true if is a not null string with the shape of an ISBN 13 and has a valid checksum digit.
     */
    public static boolean isValidISBN(String isbn){
        return isbn != null && ISBN_PATTERN.matcher(isbn).matches() && hasValidChecksumDigit(isbn);
    }

    /**
     * Create a ISBN 13 from a valid isbn string
     * @param isbn a string with the shape of an ISBN 13 and with a valid checksum digit.
     * @return an ISBN 13.
     * @throws InvalidIsbn if the passed isbn is invalid
     */
    public static Isbn fromString(String isbn) {
        if (!isValidISBN(isbn)) {
            throw new InvalidIsbn(isbn);
        }
        return new Isbn(isbn);
    }

    public String getValue(){
        return isbnLiteral;
    }

    private static boolean hasValidChecksumDigit(String isbn) {
        long isbnCheckSumNumber = Integer.parseInt(isbn.substring(isbn.length() - 1));
        //obtain all digits from the isbn except the last
        long isbnAsLong = Long.parseLong(String.join("", isbn.split("-"))) / 10;

        boolean mustMultiplyByThree = true;
        long acc = 0;

        //going backwards through the ISBN to apply the weighted sum
        while (isbnAsLong != 0) {
            acc += (isbnAsLong % 10) * ((mustMultiplyByThree) ? 3 : 1);
            isbnAsLong /= 10;
            mustMultiplyByThree = !mustMultiplyByThree;
        }

        acc %= 10;
        return ((acc == 0) ? 0L : 10  - acc) == isbnCheckSumNumber;
    }


}
