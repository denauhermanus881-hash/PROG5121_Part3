import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testCheckMessageLengthValid() {
        Message msg = new Message(0, "+27821234567", "Hello world");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testCheckMessageLengthInvalid() {
        String longText = "a".repeat(260);
        Message msg = new Message(0, "+27821234567", longText);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters"));
    }

    @Test
    public void testCheckRecipientCellValid() {
        Message msg = new Message(0, "+27821234567", "Test message");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testCheckRecipientCellInvalid() {
        Message msg = new Message(0, "082123456789", "Test message");
        assertTrue(msg.checkRecipientCell().contains("incorrectly formatted"));
    }

    @Test
    public void testCreateMessageHash() {
        Message msg = new Message(1, "+27821234567", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.contains("HIMICHAEL") || hash.contains("HITONIGHT"));
    }

    @Test
    public void testStoreMessageArray() {
        Message msg = new Message(0, "+27821234567", "Storage test");
        boolean stored = Message.storeMessage(msg);
        assertTrue(stored);
    }

    // --- PART 3 REQUIRED UNIT TESTS ---

    @Test
    public void testGetLongestStoredMessage() {
        Message.loadStoredMessagesFromJson("messages.json");
        String longest = Message.getLongestStoredMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    @Test
    public void testSearchMessageByID() {
        Message.loadStoredMessagesFromJson("messages.json");
        String result = Message.searchMessageByID("MSG004");
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchMessagesByRecipient() {
        Message.loadStoredMessagesFromJson("messages.json");
        String result = Message.searchMessagesByRecipient("+2783884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageByHash() {
        Message.loadStoredMessagesFromJson("messages.json");
        boolean isDeleted = Message.deleteMessageByHash("Test Message 2");
        assertTrue(isDeleted);
    }

    @Test
    public void testDisplayStoredReport() {
        Message.loadStoredMessagesFromJson("messages.json");
        String report = Message.displayStoredReport();
        assertNotNull(report);
        assertTrue(report.contains("STORED MESSAGES REPORT"));
    }
}