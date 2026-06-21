package com.github.helderdiniz.ledger.util;

import org.junit.jupiter.api.Test;

import static com.github.helderdiniz.ledger.util.PaginationUtil.DEFAULT_LIMIT;
import static com.github.helderdiniz.ledger.util.PaginationUtil.DEFAULT_PAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationUtilTest {

    @Test
    void sanitizeNegativeLimit() {
        final var result = PaginationUtil.sanitizeLimit(-5);
        assertEquals(DEFAULT_LIMIT, result);
    }

    @Test
    void sanitizeNullLimit() {
        final var result = PaginationUtil.sanitizeLimit(null);
        assertEquals(DEFAULT_LIMIT, result);
    }

    @Test
    void sanitizeOverflowLimit() {
        final var result = PaginationUtil.sanitizeLimit(150000);
        assertEquals(DEFAULT_LIMIT, result);
    }

    @Test
    void sanitizeNegativePage() {
        final var result = PaginationUtil.sanitizePage(-12);
        assertEquals(DEFAULT_PAGE, result);
    }

    @Test
    void sanitizeNullPage() {
        final var result = PaginationUtil.sanitizePage(null);
        assertEquals(DEFAULT_PAGE, result);
    }

    @Test
    void calculateSkip() {
        final var expected = 150;
        final var result = PaginationUtil.calculateSkipAmount(3, 50);
        assertEquals(expected, result);
    }

    @Test
    void calculateSkipOnNull() {
        final var result = PaginationUtil.calculateSkipAmount(null, null);
        assertEquals(0L, result);
    }
}
