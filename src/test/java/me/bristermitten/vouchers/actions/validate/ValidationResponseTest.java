package me.bristermitten.vouchers.actions.validate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResponseTest {

    @Test
    void ok() {
        ValidationResponse<String> response = ValidationResponse.ok("value");
        assertTrue(response.isOk());
        assertEquals("value", response.getOrThrow());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    void error() {
        ValidationResponse<String> response = ValidationResponse.error("error");
        assertFalse(response.isOk());
        assertThrows(ValidationFailedException.class, response::getOrThrow);
        assertEquals("error", response.getErrors().get(0));
    }

    @Test
    void thenWithOkResponses() {
        ValidationResponse<String> first = ValidationResponse.ok("first");
        ValidationResponse<String> second = ValidationResponse.ok("second");
        ValidationResponse<String> combined = first.then(second);
        assertTrue(combined.isOk());
        assertEquals("second", combined.getOrThrow());
        assertTrue(combined.getErrors().isEmpty());
    }

    @Test
    void thenWithErrorResponses() {
        ValidationResponse<String> first = ValidationResponse.ok("first");
        ValidationResponse<String> second = ValidationResponse.error("second error");
        ValidationResponse<String> combined = first.then(second);
        assertFalse(combined.isOk());
        assertThrows(ValidationFailedException.class, combined::getOrThrow);
        assertEquals("second error", combined.getErrors().get(0));
    }

    @Test
    void thenWithFunction() {
        ValidationResponse<String> first = ValidationResponse.ok("first");
        ValidationResponse<Integer> second = first.then(value -> {
            if ("first".equals(value)) {
                return ValidationResponse.ok(42);
            } else {
                return ValidationResponse.error("Unexpected value");
            }
        });
        assertTrue(second.isOk());
        assertEquals(42, second.getOrThrow());
        assertTrue(second.getErrors().isEmpty());
    }

    @Test
    void thenWithFunctionError() {
        ValidationResponse<String> first = ValidationResponse.ok("first");
        ValidationResponse<Integer> second = first.then(value -> {
            if ("first".equals(value)) {
                return ValidationResponse.error("Error in function");
            } else {
                return ValidationResponse.ok(42);
            }
        });
        assertFalse(second.isOk());
        assertThrows(ValidationFailedException.class, second::getOrThrow);
        assertEquals("Error in function", second.getErrors().get(0));
    }

    @Test
    void getOrThrowWithError() {
        ValidationResponse<String> response = ValidationResponse.error("error");
        assertFalse(response.isOk());
        assertThrows(ValidationFailedException.class, response::getOrThrow);
    }

    @Test
    void getOrThrowWithOk() {
        ValidationResponse<String> response = ValidationResponse.ok("value");
        assertTrue(response.isOk());
        assertEquals("value", response.getOrThrow());
    }

    @Test
    void thenWithMixedResponses() {
        ValidationResponse<String> first = ValidationResponse.ok("first");
        ValidationResponse<String> second = ValidationResponse.error("second error");
        ValidationResponse<String> third = ValidationResponse.ok("third");

        ValidationResponse<String> combined = first.then(second).then(third);

        assertFalse(combined.isOk());
        assertThrows(ValidationFailedException.class, combined::getOrThrow);
        assertEquals("second error", combined.getErrors().get(0));
    }

}