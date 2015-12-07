package com.suhan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Suhan on 12/4/15.
 */

/**
 * Please update DB_FILE_PATH constant variable for the db you're working with.
 */
public class Main {
    public static void main(String[] args) {
        boolean loop = false;
        SQLiteCaller sqLiteCaller = new SQLiteCaller();
        do {
            loop = sqLiteCaller.selectMode();
        } while (loop);
    }
}

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
class SQLiteCaller {

    private static final String DB_FILE_PATH = "/Users/Suhan/Documents/csc675_hw4/chinook.db";
    private static final String QUIT = "quit mode";
    private static final int ALBUM_TITLE_ALBUM_NAME = 1;
    private static final int TRACKS = 2;
    private static final int PURCHASE_HISTORY = 3;
    private static final int UPDATE_TRACK_PRICE = 4;
    private static final int UPDATE_TRACK_PRICE_BATCH = 5;
    private QueriesLibrary queriesLibrary;

    private static Statement statement;
    private SQLiteJDBC jdbc;

    private Scanner scanner;
    private Scanner reader;


    /**
     * Constructor method for setting up the DB connections.
     */
    public SQLiteCaller() {
        scanner = new Scanner(System.in);
        queriesLibrary = new QueriesLibrary();
        jdbc = SQLiteJDBC.getInstacne();
        jdbc.initDBConnection(DB_FILE_PATH);

        try {
            statement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean selectMode() {

        reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("==========================================================");
        System.out.print(
                "1: Search for Album title and Album Id.\n"
                        + "2: Search for Tracks in album.\n"
                        + "3: Search for Purchase history of a customer.\n"
                        + "4: Update a single track unit price.\n"
                        + "5: Update price for all tracks.\n"
                        + "\n\nChoose a mode:\t"
        );

        int mode = 0;
        try {
            mode = reader.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Enter only 1--5 please..");
            return false;
        }


        switch (mode) {
            case ALBUM_TITLE_ALBUM_NAME:
                albumTitleAndId();
                break;
            case TRACKS:
                tracks();
                break;
            case PURCHASE_HISTORY:
                purchaseHistory();
                break;
            case UPDATE_TRACK_PRICE:
                updateTrackPrice();
                break;
            case UPDATE_TRACK_PRICE_BATCH:
                updateTrackPriceBatch();
                break;
        }

        if (mode > 0 && mode < 6) {
            return true;
        } else {
            return false;
        }

    }

    /*  MODE 1  */
    /**
     * This method is case one, which search the DB with Album Table by using the artist name.
     * Then print out the Title and the Album Id.
     */
    private void albumTitleAndId() {

        String artistName;
        try {
            System.out.print("Enter 'quit mode' to choose another mode.\n\nEnter artist name: ");
            artistName = scanner.nextLine();

            if (artistName.equalsIgnoreCase(QUIT)) {
                System.out.println();
                return;
            }

            ResultSet resultSet = queriesLibrary.albumFromArtistName(artistName);

            if (resultSet.next()) {
                do {
                    System.out.print("Title = " + resultSet.getString("title"));
                    System.out.println("AlbumId = " + resultSet.getString("AlbumId"));
                } while (resultSet.next());

            } else {

                System.out.println("Empty Result.");

            }

            System.out.println();

        } catch (SQLException e) {
        }
    }

    /*  MODE 2  */
    /**
     * This method is used for second mode, which searches for album that contain the albumTitle,
     * then proceed to print the tracks with in the album.
     */
    private void tracks() {
        Scanner purchase = new Scanner(System.in);
        String albumTitle;
        ResultSet tracksResultSet = null;
        ArrayList<String> validTrackIds;
        int count = 0;
        try {
            validTrackIds = new ArrayList<String>();
            System.out.print("Enter 'quit mode' to choose another mode.\n\nEnter track album title: ");
            albumTitle = scanner.nextLine();

            ResultSet albumResultSet = queriesLibrary.album(albumTitle);
            if (albumTitle.equalsIgnoreCase(QUIT)) {
                System.out.println();
                return;
            }

            //Searching for album result, if there is any.
            if (albumResultSet.next()) {
                count++;
                do {
                    System.out.println("########\tAlbum Id = " + albumResultSet.getString(1) + "\t########\n");

                    tracksResultSet = queriesLibrary.tracksQuery(albumTitle, albumResultSet.getString(1));

                    //Searching for track in the album set, if there is any.
                    if (tracksResultSet.next()) {
                        do {
                            validTrackIds.add(tracksResultSet.getString(2));
                            System.out.println("\tTrack name: " + tracksResultSet.getString(1));
                            System.out.println("\tTrack id: " + tracksResultSet.getString(2));
                            System.out.println("\tGenre name: " + tracksResultSet.getString(3));
                            System.out.println("\tUnit price: " + tracksResultSet.getString(4));
                            System.out.println();
                        } while (tracksResultSet.next());
                    }

                } while (albumResultSet.next());


            } else {
                System.out.println("Empty result for album: " + albumTitle);
            }
            //checking if it actually have any result, if there is, ask for purchasing.
            if (count > 0) {
                System.out.print("Do you wish to purchase? ");
                String response = purchase.nextLine();

                if (response.equalsIgnoreCase("yes")) {
                    System.out.println("Which one do you want to purchase? Enter Track ID.");
                    String trackId = scanner.nextLine();
                    System.out.print("Enter amount: ");
                    int amount = scanner.nextInt();

                    //Check for valid input for track id that were appeared in the search.
                    for (int i = 0; i < validTrackIds.size(); i++) {
                        if (validTrackIds.get(i).equalsIgnoreCase(trackId) && amount > 0) {
                            //check for valid input
                            queriesLibrary.purchaseTrack(trackId, amount);
                            System.out.println("Purchased: " + trackId + " Quantity: " + amount + "\n");
                            break;
                        }
                    }

                } else if (response.equalsIgnoreCase("no")) {

                }
            }

        } catch (SQLException | InputMismatchException e) {
            System.out.println("Error input..\n");
        }
    }

    /*Third mode*/

    /**
     * This is mode 3, which is printing out whole purchase history of a customer,
     * which is according to the entered customer ID.
     */
    private void purchaseHistory() {
        Scanner sc = new Scanner(System.in);
        String customerId;
        ResultSet customer_Invoice = null;
        ResultSet customerInvoiceLine = null;
        ResultSet customerTrack = null;
        ResultSet customerAlbum = null;

        String invoiceId;

        String invoiceDate;
        String quantity;
        String albumName;
        String trackName;

        try {
            System.out.print("Enter customer ID: ");
            customerId = sc.nextLine();
            customer_Invoice = queriesLibrary.customerInvoice(customerId);

            //Looping customer's invoice.
            if (customer_Invoice.next()) {

                do {
                    invoiceDate = customer_Invoice.getString("InvoiceDate");
                    invoiceId = customer_Invoice.getString("InvoiceId");
                    customerInvoiceLine = queriesLibrary.customerInvoiceLine(invoiceId);

                    //Looping customer's invoice line.
                    if (customerInvoiceLine.next()) {
                        do {
                            quantity = customerInvoiceLine.getString("Quantity");
                            customerTrack = queriesLibrary.customerTrack(customerInvoiceLine.getString("TrackId"));

                            //Looping customer tracks.
                            if (customerTrack.next()) {
                                trackName = customerTrack.getString("Name");
                                customerAlbum = queriesLibrary.customerAlbum(customerTrack.getString("AlbumId"));

                                //Looping purchased album.
                                if (customerAlbum.next()) {
                                    albumName = customerAlbum.getString("Title");
                                    do {
                                        System.out.println("Track name: " + trackName +
                                                "\tAlbum name: " + albumName +
                                                "\tQuantity: " + quantity +
                                                "\tInvoice Date: " + invoiceDate
                                        );
                                    } while (customerAlbum.next());
                                }
                            }

                        } while (customerInvoiceLine.next());
                    }

                } while (customer_Invoice.next());
            }
            System.out.println("\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Fourth mode*/
    /**
     * This is mode 4, which is selecting a track, based on track id, and update it's unit price.
     */
    private void updateTrackPrice() {
        String userInputTrackId;
        float userInputPrice;
        ResultSet track;

        try {
            System.out.print("Enter 'quit mode' to choose another mode.\n\nEnter track id: ");
            userInputTrackId = scanner.nextLine();

            if (userInputTrackId.equalsIgnoreCase(QUIT)) {
                System.out.println();
                return;
            }
            track = queriesLibrary.customerTrack(userInputTrackId);
            System.out.println("Current price is: " + track.getString("UnitPrice"));
            System.out.print("Enter the price you want to change it to: ");
            userInputPrice = scanner.nextFloat();

            queriesLibrary.updateSingleTrack(userInputTrackId, userInputPrice);

            //"Refresh"
            track = queriesLibrary.customerTrack(userInputTrackId);

            System.out.println("Updated price for track: " + track.getString("Name") + " is " + track.getString("UnitPrice"));

        } catch (SQLException | InputMismatchException e) {
            System.out.println("Error input..\n");
        }
    }

    /**
     * This is mode 5, which is updating all track's unit price, user supposed to enter 100% to -100%,
     * then it would update all the unit prices for the track based on the percentage.
     */
    private void updateTrackPriceBatch() {
        float percent;
        String userInputPercent;
        ResultSet track;
        HashMap<String, String> previousPrices = new HashMap<>();
        try {
            System.out.print("Enter 'quit mode' to choose another mode.\n\nEnter percentage you want to change (100% to -100%): ");
            userInputPercent = scanner.nextLine();

            if (userInputPercent.equalsIgnoreCase(QUIT)) {
                System.out.println();
                return;
            } else {
                userInputPercent = userInputPercent.replace("%", "");
            }

            percent = Float.parseFloat(userInputPercent);
            percent = (1 + (percent / 100));
            track = queriesLibrary.getAllTrack();

            //Looping through the track table.
            if (track.next()) {
                do {
                    previousPrices.put(track.getString("TrackId"), track.getString("UnitPrice"));
                } while (track.next());
            }

            queriesLibrary.updateTracksPrice(percent);

            track = queriesLibrary.getAllTrack();

            if (track.next()) {
                do {
                    System.out.println("Track Id: " + track.getString("TrackId") + "\nTrack Name: " + track.getString("Name") +
                            "\nPrevious Unit price: " + previousPrices.get(track.getString("TrackId")) + "\nUpdated Unit price: " + track.getString("UnitPrice") + "\n");
                } while (track.next());
            }

        } catch (SQLException | InputMismatchException e) {
            System.out.println("Error input..\n");
        }

    }
}