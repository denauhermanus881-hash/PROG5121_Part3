import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        Message.loadStoredMessagesFromJson("messages.json");

        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

        // 1. REGISTRATION PHASE
        String regUsername = JOptionPane.showInputDialog("Register Enter username (must contain '_' and <=5 chars):");
        String regPassword = JOptionPane.showInputDialog("Register Enter password (>=8 chars, 1 capital, 1 number, 1 special):");
        String regFirst = JOptionPane.showInputDialog("Register Enter your First Name:");
        String regLast = JOptionPane.showInputDialog("Register Enter your Last Name:");
        String regCell = JOptionPane.showInputDialog("Register Enter cell phone number (+ code, max 12 chars):");

        Login login = new Login(regUsername, regPassword, regFirst, regLast, regCell);
        String regStatus = login.registerUser();
        JOptionPane.showMessageDialog(null, regStatus);
        if (!regStatus.equals("User successfully registered")) {
            return;
        }

        // 2. LOGIN PHASE
        String loginUser = JOptionPane.showInputDialog("Login Enter your username:");
        String loginPass = JOptionPane.showInputDialog("Login Enter your password:");
        boolean isLoggedIn = login.loginUser(loginUser, loginPass);
        JOptionPane.showMessageDialog(null, login.returnLoginStatus(isLoggedIn));
        if (!isLoggedIn) {
            return;
        }

        // 3. MAIN MENU LOOP
        boolean running = true;
        while (running) {
            String act = JOptionPane.showInputDialog(
                    "Please choose an option:\n" +
                            "1) Send Messages\n" +
                            "2) Show recently sent messages\n" +
                            "3) Stored Messages Menu\n" +
                            "4) Quit"
            );

            if (act == null || act.equals("4")) {
                JOptionPane.showMessageDialog(null, "Goodbye!");
                running = false;
                break;
            }

            if (act.equals("1")) {
                String numInput = JOptionPane.showInputDialog("Enter how many messages you wish to send:");
                if (numInput == null) continue;
                int totalMessages;
                try {
                    totalMessages = Integer.parseInt(numInput);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number entered.");
                    continue;
                }

                for (int i = 0; i < totalMessages; i++) {
                    String cell = JOptionPane.showInputDialog("Message " + (i + 1) + "\nEnter Recipient cell (+ code, max 12 chars): ");
                    String content = JOptionPane.showInputDialog("Message " + (i + 1) + "\nEnter message content (max 250 characters):");
                    Message msg = new Message(i, cell, content);

                    String cellValidation = msg.checkRecipientCell();
                    if (!cellValidation.startsWith("Cell phone number successfully")) {
                        JOptionPane.showMessageDialog(null, cellValidation);
                        i--; 
                        continue;
                    }

                    String lengthValidation = msg.checkMessageLength();
                    if (!lengthValidation.equals("Message ready to send.")) {
                        JOptionPane.showMessageDialog(null, lengthValidation);
                        i--; 
                        continue;
                    }

                    String choiceStr = JOptionPane.showInputDialog(
                            "Message validated successfully!\n" +
                                    "Choose an option:\n" +
                                    "1. Send Message\n" +
                                    "2. Disregard Message\n" +
                                    "3. Store Message to send later"
                    );
                    if (choiceStr != null) {
                        int choice = Integer.parseInt(choiceStr);
                        String result = msg.SentMessage(choice);
                        if (choice == 2) {
                            String del = JOptionPane.showInputDialog(result);
                            if (del != null && del.equals("0")) {
                                JOptionPane.showMessageDialog(null, "Message successfully deleted.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Message status changed to Disregarded.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "--- Message Details ---\n" +
                                            "Message ID: " + msg.getMessageID() + "\n" +
                                            "Message Hash: " + msg.getMessageHash() + "\n" +
                                            "Recipient: " + msg.getRecipientCell() + "\n" +
                                            "Message: " + msg.getMessageContent() + "\n\n" +
                                            result
                            );
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "Total messages processed: " + Message.returnTotalMessagess() + "\n\n" + Message.printMessages());

            } else if (act.equals("2")) {
                JOptionPane.showMessageDialog(null, Message.printMessages());

            } else if (act.equals("3")) {
                // PART 3 STORED MESSAGES SUB-MENU
                boolean subRunning = true;
                while (subRunning) {
                    String subChoice = JOptionPane.showInputDialog(
                            "--- STORED MESSAGES MENU ---\n" +
                                    "a. Display sender and recipient of all stored messages\n" +
                                    "b. Display the longest stored message\n" +
                                    "c. Search for a message ID and display recipient/message\n" +
                                    "d. Search for all messages stored for a particular recipient\n" +
                                    "e. Delete a message using the message hash\n" +
                                    "f. Display a report of all stored messages\n" +
                                    "g. Return to Main Menu"
                    );

                    if (subChoice == null || subChoice.equalsIgnoreCase("g")) {
                        subRunning = false;
                        break;
                    }

                    switch (subChoice.toLowerCase()) {
                        case "a":
                            JOptionPane.showMessageDialog(null, Message.displaySenderAndRecipient());
                            break;
                        case "b":
                            JOptionPane.showMessageDialog(null, Message.getLongestStoredMessage());
                            break;
                        case "c":
                            String searchId = JOptionPane.showInputDialog("Enter Message ID to search:");
                            if (searchId != null) {
                                JOptionPane.showMessageDialog(null, Message.searchMessageByID(searchId.trim()));
                            }
                            break;
                        case "d":
                            String recipient = JOptionPane.showInputDialog("Enter Recipient number to search:");
                            if (recipient != null) {
                                JOptionPane.showMessageDialog(null, Message.searchMessagesByRecipient(recipient.trim()));
                            }
                            break;
                        case "e":
                            String hash = JOptionPane.showInputDialog("Enter Message Hash to delete:");
                            if (hash != null) {
                                boolean deleted = Message.deleteMessageByHash(hash.trim());
                                if (deleted) {
                                    JOptionPane.showMessageDialog(null, "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Message hash not found.");
                                }
                            }
                            break;
                        case "f":
                            JOptionPane.showMessageDialog(null, Message.displayStoredReport());
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option. Please choose from a to g.");
                    }
                }
            }
        }
    }
}