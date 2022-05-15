package ob.dataway.database;

import java.sql.Statement;
import java.util.Random;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import ob.dataway.database.types.User;

@Slf4j
public class DatabaseManager {
  
  private Connection connection;
  private Statement statement;
  private int friendCodeLength = 4;
  private Random rnd = new Random();

  /**
   * Simplifies managing the database.
   * @param filePath The path to the database file.
   * @throws SQLException If the database cannot be accessed.
   */
  public DatabaseManager(String filePath, int friendCodeLength) throws SQLException {
    this.friendCodeLength = friendCodeLength;

    // create database connection
    this.connection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
    this.statement = this.connection.createStatement();
    this.statement.setQueryTimeout(30);

    // create tables if they don't exist
    this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id TEXT UNIQUE PRIMARY KEY, friendID TEXT UNIQUE, online BOOLEAN)");

    log.debug("Loaded database {}", filePath);
  }

  /**
   * Simplifies managing the database.
   * @param filePath The path to the database file.
   * @throws SQLException If the database cannot be accessed.
   */
  public DatabaseManager(String filePath) throws SQLException {
    this(filePath, 4);
  }

  /**
   * This generates a 4 character long friend code.
   * The friend code contains both numeric and lower case alphabetic characters.
   * @return A friend code.
   */
  private String generateFriendCode() {
    StringBuilder friendID = new StringBuilder();

    for (int i = 0; i < this.friendCodeLength; i++) {
      int rndInt = rnd.nextInt(2);
      if (rndInt == 0) {
        friendID.append((char) (rnd.nextInt(26) + 97));
      } else {
        friendID.append(rnd.nextInt(10));
      }
    }
    return friendID.toString();
  }

  /**
   * Authenticates a user with the given id.
   * @param userID The id of the user to login.
   * @return True if the user is logged in, false otherwise.
   * @throws SQLException If the database cannot be accessed.
   */
  public boolean loginUser(String userID) throws SQLException {
    User user = this.getUser(userID);

    // update user online status
    this.statement.executeUpdate("UPDATE users SET online = 1 WHERE id = '" + userID + "'");

    return user != null;
  }

  /**
   * 
   * @param userID
   * @throws SQLException
   */
  public void logoutUser(String userID) throws SQLException {
    this.statement.executeUpdate("UPDATE users SET online = FALSE WHERE id = '" + userID + "'");
  }

  /**
   * Retrieves a user from the database.
   * @param userID The id of the user to retrieve.
   * @return The user if it exists, null otherwise.
   * @throws SQLException If the database cannot be accessed.
   */
  public User getUser(String userID) throws SQLException {
    ResultSet resultSet = this.statement.executeQuery("SELECT * FROM users WHERE id = \"" + userID + "\"");

    if (resultSet.isClosed()) {
      return null;
    }

    resultSet.next();

    return new User(
      resultSet.getString("id"), 
      resultSet.getString("friendID"),
      resultSet.getBoolean("online")
    );
  }

  /**
   * Creates a new user in the database.
   * See {@link User} for more information about the user.
   * @return The new user.
   */
  public User addUser() {
    String id = UUID.randomUUID().toString();
    String friendID = this.generateFriendCode();

    try {
      this.statement.executeUpdate("INSERT INTO users (id, friendID, online) VALUES (\"" + id + "\", \"" + friendID + "\", TRUE)");
    } catch (SQLException e) {
      return this.addUser();
    }

    return new User(id, friendID, true);
  }

}
