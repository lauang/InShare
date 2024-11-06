package no.bufferoverflow.inshare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vavr.collection.HashMap;

import java.time.Instant;
import java.util.UUID;


public class NoteTest {

    private Note originalNote;

    @BeforeEach
    public void setUp() {
        // Creating a sample note to use in tests
        originalNote = new Note(
                UUID.randomUUID(),
                new User(UUID.randomUUID(), "AuthorName", "AuthorPassword"),
                "Sample Note",
                Instant.now(),
                "Original Content",
                HashMap.empty()
        );
    }

    @Test
    public void testWithContent_sanitizesMaliciousContent() {
        String maliciousContent = "<script>alert('XSS');</script><b>Bold Text</b>";

        Note updatedNote = originalNote.withContent(maliciousContent);

        assertNotEquals(originalNote.content, updatedNote.content, 
                        "Content should be updated and sanitized.");

        assertFalse(updatedNote.content.contains("<script>"), 
                    "Sanitized content should not contain <script> tags.");

        assertTrue(updatedNote.content.contains("<b>"), 
                   "Sanitized content should retain allowed <b> tags.");
    }

    @Test
    public void testWithContent_preservesSafeHtml() {
        String safeContent = "<p>Paragraph</p><i>Italic Text</i>";
        
        Note updatedNote = originalNote.withContent(safeContent);
        
        // Normalize whitespace (e.g., remove newline differences) for comparison
        String expectedContent = "<p>Paragraph</p><i>Italic Text</i>".replaceAll("\\s+", "");
        String actualContent = updatedNote.content.replaceAll("\\s+", "");
        
        assertEquals(expectedContent, actualContent, "Safe HTML tags should remain intact in sanitized content.");
    }


    @Test
    public void testWithContent_handlesEmptyContent() {
        Note updatedNote = originalNote.withContent("");

        assertEquals("", updatedNote.content, 
                     "Empty content should remain empty after sanitization.");
    }
}

