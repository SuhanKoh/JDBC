package com.suhan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Suhan on 12/5/15.
 */
public class QueriesLibrary {

    /**
     * This function is searching album table based on artist name from Artist, then check his/her Album.
     *
     * @param artistName The name of the artist. (Have to be match case, ie: Aerosmith != aerosmith)
     * @return ResultSet of the Album, (searched via artist name)
     * @throws SQLException
     */
    public ResultSet albumFromArtistName(String artistName) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT AL.Title, AL.AlbumId " +
                        "FROM Artist A, Album AL " +
                        "WHERE AL.ArtistId = A.ArtistId " +
                        "AND A.Name = \"" + artistName + "\""
        );
    }

    /**
     * This function is searching Album table based on album title.
     *
     * @param albumTitle Title of the album.
     * @return Resultest of the Album, (searched via album id)
     * @throws SQLException
     */
    public ResultSet album(String albumTitle) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT AL.AlbumId " +
                        "FROM  Album AL " +
                        "WHERE AL.Title = \"" + albumTitle + "\""
        );
    }

    /**
     * This function is searching Album, Track, Genre, and InvoiceLine table.
     *
     * @param albumTitle Title of the album.
     * @param albumId    Id of the album.
     * @return ResultSet of Track name, Track TrackId, Genre Name, InvoiceLine UnitPrice, and Track AlbumId
     * @throws SQLException
     */
    public ResultSet tracksQuery(String albumTitle, String albumId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT DISTINCT T.Name, T.TrackId, G.Name, IL.UnitPrice, T.AlbumId " +
                        "FROM  Album AL, Track T, Genre G, InvoiceLine IL " +
                        "WHERE AL.Title = \"" + albumTitle + "\"" +
                        "AND T.AlbumId = \"" + albumId + "\" " +
                        "AND G.GenreId = T.GenreId " +
                        "AND IL.TrackId = T.TrackId");
    }

    /**
     * This function does not upsert, which only update if its exist.
     *
     * @param trackId
     * @param amount
     * @throws SQLException
     */
    public void purchaseTrack(String trackId, int amount) throws SQLException {
        ResultSet customerRS = customerInvoiceLineId("25");
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        queryStatement.execute(
                "UPDATE InvoiceLine " +
                        "set Quantity = \"" + amount + "\" " +
                        "WHERE InvoiceLineId = \"" + customerRS.getString("InvoiceLineId") + "\"" +
                        "AND TrackId = \"" + trackId + "\" "
        );
    }

    /**
     * This function is searching Invoice, and InvoiceLine table.
     *
     * @param customerId Customer Id.
     * @return Result set of InvoiceLineId, InvoiceId, TrackId, UnitPrice, and Quanity of InvoiceLine.
     * @throws SQLException
     */
    public ResultSet customerInvoiceLineId(String customerId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT IL.InvoiceLineId, IL.InvoiceId, IL.TrackId, IL.UnitPrice, IL.Quantity " +
                        "FROM Invoice I, InvoiceLine IL " +
                        "WHERE I.CustomerId = \"" + customerId + "\"" +
                        "AND IL.InvoiceId = I.InvoiceId"
        );
    }

    /**
     * This function is searching Invoice table.
     *
     * @param customerId Customer Id.
     * @return ResultSet of InvoiceId, InvoiceDate. From Invoice table.
     * @throws SQLException
     */
    public ResultSet customerInvoice(String customerId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT I.InvoiceId, I.InvoiceDate " +
                        "FROM Invoice I " +
                        "WHERE I.CustomerId = \"" + customerId + "\""
        );
    }

    /**
     * This function is searching for customer invoice line.
     *
     * @param invoiceId Invoice Id.
     * @return ResultSet of InvoiceId, Quanity, and TrackId.
     * @throws SQLException
     */
    public ResultSet customerInvoiceLine(String invoiceId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT IL.InvoiceId, IL.Quantity, IL.TrackId " +
                        "FROM InvoiceLine IL " +
                        "WHERE IL.InvoiceId = \"" + invoiceId + "\" "
        );
    }

    /**
     * This function is searching for customer Track table.
     *
     * @param trackId of Track table.
     * @return ResultSet of every column in Track table.
     * @throws SQLException
     */
    public ResultSet customerTrack(String trackId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT * " +
                        "FROM Track T " +
                        "WHERE T.TrackId = \"" + trackId + "\" "
        );
    }

    /**
     * This function is searching for Album table.
     *
     * @param albumId of Album table.
     * @return ResultSet of every column in Album table.
     * @throws SQLException
     */
    public ResultSet customerAlbum(String albumId) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT * " +
                        "FROM Album A " +
                        "WHERE A.AlbumId = \"" + albumId + "\" "
        );
    }

    /**
     * This function is updating single track's unit price in Track table.
     *
     * @param trackId of tracks
     * @param price   price.
     * @throws SQLException
     */
    public void updateSingleTrack(String trackId, float price) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        queryStatement.execute(
                "UPDATE Track " +
                        "set UnitPrice = \"" + price + "\" " +
                        "WHERE TrackId = \"" + trackId + "\"" +
                        "AND TrackId = \"" + trackId + "\" "
        );
    }

    /**
     * This function is updating all trakcs unit price in Track table.
     *
     * @param percent of changing.
     * @throws SQLException
     */
    public void updateTracksPrice(float percent) throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        queryStatement.execute(
                "UPDATE Track " +
                        "set UnitPrice = UnitPrice * \"" + percent + "\" "
        );
    }

    /**
     * This function is returning every column from track tables without condition.
     *
     * @return ResultSet of Track table with every column.
     * @throws SQLException
     */
    public ResultSet getAllTrack() throws SQLException {
        Statement queryStatement = SQLiteJDBC.getInstacne().getConnection().createStatement();
        return queryStatement.executeQuery(
                "SELECT *" +
                        "FROM TRACK "
        );
    }
}
