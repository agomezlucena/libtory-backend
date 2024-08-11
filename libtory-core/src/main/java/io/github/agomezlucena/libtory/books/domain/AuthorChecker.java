package io.github.agomezlucena.libtory.books.domain;

import java.util.UUID;

public interface AuthorChecker {
    boolean authorsExists(UUID... authorIds);
}
