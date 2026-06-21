package com.github.helderdiniz.ledger.util;

import static java.util.Objects.nonNull;

public final class PaginationUtil {
    static final Integer DEFAULT_LIMIT = 10;
    static final Integer MAX_LIMIT = 500;
    static final Integer DEFAULT_PAGE = 0;

    private PaginationUtil() {
    }

    public static Integer sanitizeLimit(final Integer limit) {
        return nonNull(limit) && limit >= 1 && limit <= MAX_LIMIT ? limit : DEFAULT_LIMIT;
    }

    public static Integer sanitizePage(final Integer page) {
        return nonNull(page) && page >= 0 ? page : DEFAULT_PAGE;
    }

    public static Long calculateSkipAmount(final Integer limit,
                                           final Integer page) {
        return nonNull(limit) && nonNull(page) ? (long) limit * page : DEFAULT_PAGE;
    }
}
