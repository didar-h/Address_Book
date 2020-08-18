import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.*;  // Import the IOException class to handle errors
import java.util.Scanner;


/**
 * @author     Shir
 * June 2020
 */
public class AddressBook extends JFrame implements ActionListener {
/** Configuration: custom screen colours, layout constants and custom fonts. */
private final Color
        veryLightGrey = new Color(227, 235, 240),
        darkBlue = new Color(5, 5, 150),
        backGroundColour = veryLightGrey,
        navigationBarColour = Color.lightGray,
        textColour = darkBlue;
private static final int
        windowWidth = 450, windowHeight = 600,               // Overall frame dimensions
        windowLocationX = 200, windowLocationY = 100;        //     and position
private final int
        panelWidth = 450, panelHeight = 250,                 // The drawing panel dimensions
        leftMargin = 50,                                     // All text and images start here
        mainHeadingY = 30,                                   // Main heading this far down the panel
        detailsY = mainHeadingY+40,                          // Details display starts this far down the panel
        detailsLineSep = 30;                                 // Separation of details text lines
private final Font
        mainHeadingFont = new Font("SansSerif", Font.BOLD, 20),
        detailsFont = new Font("SansSerif", Font.PLAIN, 14);
/** The navigation buttons. */
private JButton
        first = new JButton("First"),
        previous = new JButton("Previous"),
        next = new JButton("Next"),
        last = new JButton("Last");

    /** Text fields for data entry , the max number of characters can be changed */
    private JTextField
            nameField = new JTextField(20),                // For entering a new name, or a name to find
            addressField = new JTextField(30),             // For entering a new address
            mobileField = new JTextField(12),              // For entering a new mobile number
            emailField = new JTextField(30);               // For entering a new email address
    // /** The action buttons */
    private JButton
        addContact = new JButton("Add new contact"),   // To request adding a new contact
        deleteContact = new JButton("Delete contact"),
        deleteAll = new JButton("Delete all"),
        findContact = new JButton("Find exact name"),  // To find contact by exact match of name
        findPartial = new JButton("Find partial name"),// To find contact by partial, case insensitive match of name
        CloseWriter = new JButton("Close Writer");         // Closing Writer
        //sortZtoA = new JButton("Sort Z to A");

/** The contact details drawing panel. */
private JPanel contactDetails = new JPanel()
{
    // paintComponent is called automatically when a screen refresh is needed
    public void paintComponent(Graphics g)
    {
        // g is a cleared panel area
        super.paintComponent(g); // Paint the panel's background
        paintScreen(g);          // Then the required graphics
    }
};
    String allInfo =new String();   //for storing all info of one contact
    /**
     *  The main program launcher for the AddressBook class.
     */
    public static void main(String[] args) {
        createFille(); //Creating a file
        AddressBook contacts = new AddressBook();
        contacts.setSize(windowWidth, windowHeight);
        contacts.setLocation(windowLocationX, windowLocationY);
        contacts.setTitle("Shirmuhammet`s address book: 2019992111005");//author
        contacts.setUpAddressBook();//taking info from file
        contacts.setUpGUI();//Graphical User Interface
        contacts.setVisible(true);
    } // End of main

    /** Organizes overall set up of the address book data at launch time. */
    Writer myWriter;{
        try {
             //myWriter= new FileWriter("filename.txt");
            myWriter = new BufferedWriter(new FileWriter("filename.txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setUpAddressBook()
    {
        // Set up the contacts' details in the database
        currentSize = 0;    // No contacts initially
        // pass the path to the file as a parameter, make sure to match with your location
        File file =new File("C:\\Users\\Administrator\\IdeaProjects\\address book\\filename.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String str ;
        while (sc.hasNextLine()) {
            str=sc.nextLine();
            String[] splitStr = str.split("\\s+");
            addContact(splitStr[0],splitStr[1],splitStr[2],splitStr[3]);
        }
        currentContact = 0;
    } // End of setUpAddressBook, Read from file by name filename.

    /** Sets up the graphical user interface.
     *
     * Some extra embedded JPanels are used to improve layout a little
     */
    private void setUpGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());
        window.setBackground(navigationBarColour);

        // Set up the GUI buttons
        // The widget order is:
        // first (||<<), previous (<<), next (>>), last (>>||)

        window.add(new JLabel("Basic Navigation"));
        window.add(first);
        first.addActionListener(this);
        window.add(previous);
        previous.addActionListener(this);
        window.add(next);
        next.addActionListener(this);
        window.add(last);
        last.addActionListener(this);
        // Set up the details graphics panel
        contactDetails.setPreferredSize(new Dimension(panelWidth, panelHeight));
        contactDetails.setBackground(backGroundColour);
        window.add(contactDetails);

        // Set up action buttons
        JPanel addDelPanel = new JPanel();
        addDelPanel.add(addContact);
        addContact.addActionListener(this);
        window.add(addDelPanel);
        JPanel findPanel = new JPanel();
        addDelPanel.add(deleteContact);
        deleteContact.addActionListener(this);
        addDelPanel.add(deleteAll);
        deleteAll.addActionListener(this);
        findPanel.add(findContact);
        findContact.addActionListener(this);
        findPanel.add(findPartial);
        findPartial.addActionListener(this);
        window.add(findPanel);
        JPanel sortPanel = new JPanel();
        sortPanel.add(CloseWriter);
        CloseWriter.addActionListener(this);
        window.add(sortPanel);
        // Set up text fields for data entry
        // (using extra JPanels to improve layout control)
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Create New or Find name:"));
        namePanel.add(nameField);
        window.add(namePanel);

        JPanel addressPanel = new JPanel();
        addressPanel.add(new JLabel("New address:"));
        addressPanel.add(addressField);
        window.add(addressPanel);

        JPanel mobilePanel = new JPanel();
        mobilePanel.add(new JLabel("New mobile:"));
        mobilePanel.add(mobileField);
        window.add(mobilePanel);

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("New email:"));
        emailPanel.add(emailField);
        window.add(emailPanel);
    } // End of setUpGUI

    /**
     *  Display non-background colour areas, heading and currently selected database contact.
     *
     * @param  g  The Graphics area to be drawn on, already cleared.
     */
    private void paintScreen(Graphics g)
    {
        // Main heading
        g.setColor(textColour);
        g.setFont(mainHeadingFont);
        g.drawString("Contact details", leftMargin, mainHeadingY);

        // Current details
        displayCurrentDetails(g);
    } // End of paintScreen

    /**
     *  Display the currently selected contact.
     *
     * @param  g  The Graphics area to be drawn on.
     */
    private void displayCurrentDetails(Graphics g)
    {
        g.setColor(textColour);
        g.setFont(detailsFont);
        if (currentContact == -1)           // Check if no contact is selected, that is there are no contacts
            g.drawString("There are no contacts", leftMargin, detailsY);
        else
        {   // Display selected contact
            g.drawString(name[currentContact], leftMargin, detailsY);
            g.drawString(address[currentContact], leftMargin, detailsY + detailsLineSep);
            g.drawString("Mobile: " + mobile[currentContact], leftMargin, detailsY + 2 * detailsLineSep);
            g.drawString("Email: " + email[currentContact], leftMargin, detailsY + 3 * detailsLineSep);
        }
    } // End of displayCurrentDetails

    /**
     *  Handle the various button clicks.
     *
     * @param  e  Information about the button click
     */
    public void actionPerformed(ActionEvent e)
    {
        // If first is clicked: Cause the 0th contact to become selected (or -1 if there are none)
        if (e.getSource() == first)
            if (currentContact >= 0)
                currentContact = 0;
            else
                currentContact = -1;

        // If previous is clicked: Cause the previous contact to become selected, if there is one
        if (e.getSource() == previous && currentContact > 0)
            currentContact--;

        // If next is clicked: Cause the next contact to become selected, if there is one
        if (e.getSource() == next && currentContact < currentSize - 1)
            currentContact++;

        // If last is clicked: Cause the final available contact to become selected (or -1 if there are none)
        if (e.getSource() == last)
            currentContact = currentSize - 1;

        // Add a new contact
        if (e.getSource() == addContact)
            doAddContact();

        // Delete the current contact
        if (e.getSource() == deleteContact)
            deleting();

        // Delete all contacts
        if (e.getSource() == deleteAll)
            delete_ALL();

        // Find a contact with exact name match
        if (e.getSource() == findContact)
            doFindContact();

        // Find a contact with partial, case insensitive name match
        if (e.getSource() == findPartial)
            doFindPartial();

        // Re-order the contacts by name A to Z
        if (e.getSource() == CloseWriter)
            CloseWriter();
        repaint();
    } // End of actionPerformed

    /**
     * Add a new contact using data from the entry text fields
     *
     * Only adds if the name field is not empty (other fields do not matter),
     * and if there is space in the arrays.
     * Pops up dialogue box giving reason if contact is not added.
     * The new contact is selected immediately.
     */
    private void doAddContact()
    {
        String newName = nameField.getText();       nameField.setText("");
        String newAddress = addressField.getText(); addressField.setText("");
        String newMobile = mobileField.getText();   mobileField.setText("");
        String newEmail = emailField.getText();     emailField.setText("");

        if (newName.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No name entered");
            return;
        }
        int index = 0; // index is where added, or -1
        index = addContact(newName, newAddress, newMobile, newEmail);
        writeContact(newName, newAddress, newMobile, newEmail);
        //stores information to the file
        if (index == -1)                   // Check for success
            JOptionPane.showMessageDialog(null, "No space for new name");
        else
            currentContact = index;        // Immediately select the new contact
    } // End of doAddContact

    private void deleting() {    //Delete temporary from our database, from inside a program, not from a file
        if (currentSize == 0) {// No contacts? If so do nothing
            JOptionPane.showMessageDialog(null, "No contacts to delete");
            return;
        }
        deleteContact(currentContact);
        // currentContact is OK as the selected contact index, unless:
        if (currentContact == currentSize)    // Just deleted the highest indexed contact?
            currentContact--;                 // Adjust down to previous (or -1 if all deleted)
    } // End of doDeleteContact

    private void delete_ALL() {
        clearContacts();
        currentContact = -1;    // No contact selected
    } // End of doDeleteAll
    /**
     * Search for the contact whose name is an exact match to the name given in the name text field.
     *
     * The search name must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindContact()
    {
        String searchName = nameField.getText();
        if (searchName.length() == 0)               // Check and exit if the search name is empty
        {
            JOptionPane.showMessageDialog(null, "Name must not be empty");
            return;
        }
        int location = findContact(searchName);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else
        {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindContact

    /**
     * Search for the contact whose name contains the text given in the name text field,
     * case insensitively.
     *
     * The search text must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindPartial()
    {
        String searchText = nameField.getText();
        if (searchText.length() == 0)               // Check and exit if the search text is empty
        {
            JOptionPane.showMessageDialog(null, "Search text must not be empty");
            return;
        }
        int location = findPartial(searchText);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else
        {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindPartial

    private void CloseWriter()
    {
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    } // End of CloseWriter

    /**
     * Re-order the contacts in the database so that the names are in descending alphabetic order
     *
     * The first contact becomes selected, provided that there is one.
     */



    //////////////////////////////////////////////////////////////////////////////////////////////
    /** To hold contacts' names, addresses, etc. */
    public static void createFille() {
        try {
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Maximum capacity of the database. */
    private final int databaseSize = 18;        //这个数字因为跟我来中国的年龄有关。
    private String[]
    name = new String[databaseSize],
    address = new String[databaseSize],
    mobile = new String[databaseSize],
    email = new String[databaseSize];

    /** The current number of entries - always a value in range 0 .. databaseSize.
     *
     * The entries are held in elements 0 .. currentSize-1 of the arrays.
     */
    private int currentSize = 0;
    /** To hold index of currently selected contact
     *
     * There is always one selected contact, unless there are no entries at all in the database.
     * If there are one or more entries, then currentContact has a value in range 0 .. currentSize-1.
     * If there are no entries, then currentContact is -1.
     */
    private int currentContact = -1;
    /**
     * Add a new contact to the database in the next available location, if there is space.
     *
     * Return the index where added if successful, or -1 if no space so not added.
     */
    //FileWriter myWriter;







    private void writeContact(String newName, String newAddress, String newMobile, String newEmail)
    {   //writeContact to the file
        allInfo = newName + " " + newAddress + " " + newMobile + " " + newEmail + "\n";
        try {
            myWriter.append(allInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // End of writeContact

    private int addContact(String newName, String newAddress, String newMobile, String newEmail)
    {
        // Need to check if there is space available, and return -1 if not
        if(currentSize>=databaseSize)
            return -1;
        name[currentSize] = newName;         // Add data at first free element in each array
        address[currentSize] = newAddress;
        mobile[currentSize] = newMobile;
        email[currentSize] = newEmail;
        currentSize++;                       // Count one more contact
        return currentSize-1;                // Success, return where added
    }

    private void deleteContact(int index) {
        if (index > -1)
        {
            for (int i = index; i < currentSize; i++)
            {
                name[i] = name[i+1];         // Add data at first free element in each array
                address[i] = address[i+1];
                mobile[i] = mobile[i+1];
                email[i] = email[i+1];
            }
            currentSize--;
        } else return;
    }
    private void clearContacts() {  //deleting all Contacts
        for (int i = 0; i < currentSize; i++) {
            name[i] = null;
            address[i] = null;
            mobile[i] = null;
            email[i] = null;
            currentSize--;
        }
    }

    // End of addContact
    /**
     * Search the database for an exact match for the given name.
     *
     * Return the index of the match found, or -1 if no match found.
     */
    private int findContact(String searchName)
    {
        // TO BE DONE: Implement this method body - see comments above
        for (int i = 0; i < currentSize; i++)
        {
            if (name[i].equals(searchName))
                return i;
        }

        return -1;                          // Return where found or -1
    } // End of findContact

    /**
     * Search the database for a contact whose name contains the given search text, case insensitively.
     *
     * Return the index of the match found, or -1 if no match found.
     */
    private int findPartial(String searchText)
    {
        // TO BE DONE: Implement this method body - see comments above
        for (int i = 0; i < currentSize; i++)
        {
            if (name[i].contains(searchText))
                return i;
        }
        return -1;                          // Return where found or -1
    } // End of findPartial

} // End of AddressBook