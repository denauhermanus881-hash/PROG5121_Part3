import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Message {
    private String messageID;
    private int messageNumber;
    private String recipientCell;
    private String messageContent;
    private String messageHash;
    private String messageStatus;

    private static Message[] storageArray = new Message[100];
    private static int messageCount = 0;

    // Parallel arrays for Part 3 requirements
    private static String[] storedMessages = new String[0];
    private static String[] storedRecipients = new String[0];
    private static String[] messageIDs = new String[0];
    private static String[] messageHashes = new String[0];
    private static String[] sentMessages = new String[0];
    private static String[] disregardedMessages = new String[0];

    public Message() {}

    public Message(int messageNumber, String recipientCell, String messageContent) {
        this.messageID = generateID();
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageContent = messageContent;
        this.messageHash = createMessageHash();
        this.messageStatus = "Pending";
    }

    private String generateID() {
        Random r = new Random();
        long num = 1000000000L + (long) (r.nextDouble() * 9000000000L);
        return String.valueOf(num);
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipientCell != null && recipientCell.startsWith("+") && recipientCell.length() <= 12) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String checkMessageLength() {
        if (messageContent == null || messageContent.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = messageContent.length() - 250;
            return "Message exceeds 250 characters by " + excess + " characters; please reduce the size.";
        }
    }

    public String createMessageHash() {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return messageID.substring(0, 2).toUpperCase() + ":" + messageNumber + ":NONAME";
        }
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        String prefix = messageID.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    public static boolean storeMessage(Message message) {
        if (messageCount < storageArray.length) {
            storageArray[messageCount] = message;
            messageCount++;
            return true;
        } else {
            return false;
        }
    }

    public void saveToFile() {
        String json = "{\n" +
                "  \"messageID\": \"" + messageID + "\",\n" +
                "  \"messageNumber\": " + messageNumber + ",\n" +
                "  \"recipientCell\": \"" + recipientCell + "\",\n" +
                "  \"messageContent\": \"" + messageContent + "\",\n" +
                "  \"messageHash\": \"" + messageHash + "\",\n" +
                "  \"status\": \"" + messageStatus + "\"\n" +
                "},\n";
        try {
            FileWriter writer = new FileWriter("messages.json", true);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file");
        }
    }

    // --- JSON FILE READER METHOD (PART 3) ---
    public static void loadStoredMessagesFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                storedMessages = new String[]{
                    "Did you get the cake?",
                    "Where are you? You are late! I have asked you to be on time.",
                    "Yohoooo, I am at your gate.",
                    "It is dinner time !",
                    "Ok, I am leaving without you."
                };
                storedRecipients = new String[]{
                    "+27834557896",
                    "+2783884567",
                    "+27834484567",
                    "0838884567",
                    "+2783884567"
                };
                messageIDs = new String[]{"MSG001", "MSG002", "MSG003", "MSG004", "MSG005"};
                messageHashes = new String[]{"Test Message 1", "Test Message 2", "Test Message 3", "Test Message 4", "Test Message 5"};
                sentMessages = new String[]{"Did you get the cake?", "It is dinner time !"};
                disregardedMessages = new String[]{"Yohoooo, I am at your gate."};
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] entries = content.split("\\},\\s*\\{");
            int count = entries.length;

            storedMessages = new String[count];
            storedRecipients = new String[count];
            messageIDs = new String[count];
            messageHashes = new String[count];

            for (int i = 0; i < count; i++) {
                messageIDs[i] = extractJsonField(entries[i], "messageID");
                messageHashes[i] = extractJsonField(entries[i], "messageHash");
                storedRecipients[i] = extractJsonField(entries[i], "recipientCell");
                storedMessages[i] = extractJsonField(entries[i], "messageContent");
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
    }

    private static String extractJsonField(String jsonChunk, String field) {
        String searchKey = "\"" + field + "\": \"";
        int startIndex = jsonChunk.indexOf(searchKey);
        if (startIndex == -1) {
            searchKey = "\"" + field + "\": ";
            startIndex = jsonChunk.indexOf(searchKey);
            if (startIndex == -1) return "";
            startIndex += searchKey.length();
            int endIndex = jsonChunk.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = jsonChunk.indexOf("}", startIndex);
            return jsonChunk.substring(startIndex, endIndex).trim();
        }
        startIndex += searchKey.length();
        int endIndex = jsonChunk.indexOf("\"", startIndex);
        return (endIndex == -1) ? "" : jsonChunk.substring(startIndex, endIndex);
    }

    // --- PART 3 METHODS (a - f) ---

    public static String displaySenderAndRecipient() {
        StringBuilder sb = new StringBuilder("--- Stored Messages Senders & Recipients ---\n");
        if (storedMessages.length == 0) return "No stored messages.";
        for (int i = 0; i < storedMessages.length; i++) {
            if (storedMessages[i] != null && !storedMessages[i].isEmpty()) {
                sb.append("Recipient: ").append(storedRecipients[i]).append(" | Message: ").append(storedMessages[i]).append("\n");
            }
        }
        return sb.toString();
    }

    public static String getLongestStoredMessage() {
        if (storedMessages.length == 0) return "No stored messages.";
        String longest = "";
        for (String msg : storedMessages) {
            if (msg != null && msg.length() > longest.length()) {
                longest = msg;
            }
        }
        return longest;
    }

    public static String searchMessageByID(String targetID) {
        for (int i = 0; i < messageIDs.length; i++) {
            if (messageIDs[i] != null && messageIDs[i].equalsIgnoreCase(targetID)) {
                return "Recipient: " + storedRecipients[i] + "\nMessage: " + storedMessages[i];
            }
        }
        return "Message ID not found.";
    }

    public static String searchMessagesByRecipient(String targetRecipient) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (int i = 0; i < storedRecipients.length; i++) {
            if (storedRecipients[i] != null && storedRecipients[i].equals(targetRecipient)) {
                sb.append(storedMessages[i]).append("\n");
                found = true;
            }
        }
        return found ? sb.toString().trim() : "No messages found for this recipient.";
    }

    public static boolean deleteMessageByHash(String targetHash) {
        for (int i = 0; i < messageHashes.length; i++) {
            if (messageHashes[i] != null && messageHashes[i].equalsIgnoreCase(targetHash)) {
                storedMessages[i] = "";
                messageHashes[i] = "";
                return true;
            }
        }
        return false;
    }

    public static String displayStoredReport() {
        StringBuilder sb = new StringBuilder("========================================\n");
        sb.append("          STORED MESSAGES REPORT        \n");
        sb.append("========================================\n");
        for (int i = 0; i < storedMessages.length; i++) {
            if (storedMessages[i] != null && !storedMessages[i].isEmpty()) {
                sb.append("Message Hash : ").append(messageHashes[i]).append("\n");
                sb.append("Recipient    : ").append(storedRecipients[i]).append("\n");
                sb.append("Message      : ").append(storedMessages[i]).append("\n");
                sb.append("----------------------------------------\n");
            }
        }
        return sb.toString();
    }

    public String SentMessage(int choice) {
        if (choice == 1) {
            storeMessage(this);
            saveToFile();
            this.messageStatus = "Sent";
            return "Message successfully sent.";
        } else if (choice == 2) {
            this.messageStatus = "Disregarded";
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage(this);
            saveToFile();
            this.messageStatus = "Stored";
            return "Message successfully stored.";
        }
        return "Invalid selection.";
    }

    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageCount; i++) {
            Message m = storageArray[i];
            sb.append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipientCell()).append("\n")
              .append("Message: ").append(m.getMessageContent()).append("\n")
              .append("Status: ").append(m.messageStatus).append("\n\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessagess() {
        return messageCount;
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipientCell() { return recipientCell; }
    public String getMessageContent() { return messageContent; }
    public void setMessageStatus(String status) { this.messageStatus = status; }
}