package io.github.agomezlucena.libtory.books.domain;

import java.util.regex.Pattern;

/**
 * An ISBN is a string of digits or digits and hyphens than identifies a book.
 *
 * @author Alejandro Gómez Lucena.
 */
record Isbn (String isbnLiteral) {
    private static final Pattern PATTERN_FOR_CHECKING_INVALID_CHARACTERS = Pattern.compile(
            ".*[a-zA-Z!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?].*"
    );

    Isbn {
        if (!isValidISBN(isbnLiteral)) {
            throw new InvalidIsbn(isbnLiteral);
        }
    }

    /**
     * check if the given string matches with a valid ISBN 13 checking both the shape of the string
     * and also its checksum.
     *
     * @param isbn a string that identifies a book.
     * @return true if is a not null string with the shape of an ISBN 13 and has a valid checksum digit.
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.isBlank()) return false;
        if (PATTERN_FOR_CHECKING_INVALID_CHARACTERS.matcher(isbn).matches()) return false;
        return hasValidChecksumDigit(isbn);
    }

    /**
     * Create a ISBN 13 from a valid isbn string
     *
     * @param isbn a string with the shape of an ISBN 13 and with a valid checksum digit.
     * @return an ISBN 13.
     * @throws InvalidIsbn if the passed isbn is invalid
     */
    public static Isbn fromString(String isbn) {
        return new Isbn(isbn);
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
        return ((acc == 0) ? 0L : 10 - acc) == isbnCheckSumNumber;
    }


}
