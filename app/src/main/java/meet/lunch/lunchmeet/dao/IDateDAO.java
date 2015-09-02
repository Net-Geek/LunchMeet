package meet.lunch.lunchmeet.dao;

import meet.lunch.lunchmeet.dto.Date;

/**
 * Created by tripleao on 8/4/2015.
 */
public interface IDateDAO {
    /**
     * Creates a date in the database.
     * @param date the date to create
     * @throws Exception new date could not be created.
     */
    public String createDate(Date date) throws Exception;

    /**
     * Fetches a date from the database.
     * @param dateID the dateID of the date to retrieve
     * @throws Exception date could not be retrieved from the database.
     */
    public Date fetchDate(String dateID) throws Exception;

    /**
     * Updates a date in the database.
     * @param dateID the dateID of the date to update
     * @param date the updated date object to overwrite the current date on database
     * @throws Exception date could not be updated in the database.
     */
    public String updateDate(String dateID, Date date) throws Exception;

    /**
     * Deletes a date from the database.
     * @param dateID the dateID of the date to delete
     * @throws Exception date could not be deleted from the database.
     */
    public String deleteDate(String dateID) throws Exception;
}
