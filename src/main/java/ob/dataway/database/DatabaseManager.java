package ob.dataway.database;

import java.sql.Statement;
import java.util.Random;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ob.dataway.database.types.User;

@Slf4j
public class DatabaseManager {
  
  private Connection connection;
  private Statement statement;
  private int friendCodeLength = 4; //TODO: store friendCodeLength in config file
  private String defaultFilePath = "src/main/java/ob/dataway/database/database.db";
  private Random rnd = new Random();

  /**
   * The singleton instance of the database manager.
   */
  @Getter
  private static DatabaseManager instance = new DatabaseManager();


  /**
   * Simplifies managing the database.
   * This class is a singleton and gets instantiated at the start of the program.
   */
  private DatabaseManager() {
    try {
      // create database connection
      this.connection = DriverManager.getConnection("jdbc:sqlite:" + defaultFilePath);
      this.statement = this.connection.createStatement();
      this.statement.setQueryTimeout(30);

      // create tables if they don't exist
      this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id TEXT UNIQUE PRIMARY KEY, friendCode TEXT UNIQUE, online BOOLEAN)");

      log.info("Loaded database {}", defaultFilePath);
    }
    catch (SQLException e) {
      log.error("Failed to connect to the database.", e);
      System.exit(1);
    }
  }


  /**
   * This generates a 4 character long friend code.
   * The friend code contains both numeric and lower case alphabetic characters.
   * @return A friend code.
   */
  private String generateFriendCode() {
    StringBuilder friendCode = new StringBuilder();

    for (int i = 0; i < this.friendCodeLength; i++) {
      int rndInt = rnd.nextInt(2);
      if (rndInt == 0) {
        friendCode.append((char) (rnd.nextInt(26) + 97));
      } else {
        friendCode.append(rnd.nextInt(10));
      }
    }
    return friendCode.toString();
  }

  /**
   * Authenticates a user with the given id.
   * @param userID The id of the user to login.
   * @return True if the user is logged in, false otherwise.
   * @throws SQLException If the database cannot be accessed.
   */
  public boolean loginUser(String userID) {
    try {
      User user = this.getUser(userID);

      // update user online status
      this.statement.executeUpdate("UPDATE users SET online = TRUE WHERE id = '" + userID + "'");
  
      return user != null;
    }
    catch (SQLException e) {
      log.error("Failed to login user {}.", userID);
      System.exit(1);
      return false;
    }
  }

  /**
   * 
   * @param userID
   * @throws SQLException
   */
  public void logoutUser(String userID) {
    try {
      this.statement.executeUpdate("UPDATE users SET online = FALSE WHERE id = '" + userID + "'");
    }
    catch (SQLException e) {
      log.error("Failed to logout user {}.", userID);
      System.exit(1);
    }
  }

  /**
   * Retrieves a user from the database.
   * @param userID The id of the user to retrieve.
   * @return The user if it exists, null otherwise.
   */
  public User getUser(String userID) {
    try{
      ResultSet resultSet = this.statement.executeQuery("SELECT * FROM users WHERE id = \"" + userID + "\"");

      if (resultSet.isClosed()) {
        return null;
      }
  
      resultSet.next();
  
      return new User(
        resultSet.getString("id"), 
        resultSet.getString("friendCode"),
        resultSet.getBoolean("online")
      );
    }
    catch (SQLException e) {
      log.error("Failed to retrieve user from database.", e);
      System.exit(1);
      return null;
    }
  }

  /**
   * Retrieves a user from the database using the friendcode.
   * @param userFriendCode The id of the user to retrieve.
   * @return The user if it exists, null otherwise.
   */
  public User getUserByFriendCode(String userFriendCode) {
    try{
      ResultSet resultSet = this.statement.executeQuery("SELECT * FROM users WHERE friendCode = \"" + userFriendCode + "\"");

      if (resultSet.isClosed()) {
        return null;
      }
  
      resultSet.next();
  
      return new User(
        resultSet.getString("id"), 
        resultSet.getString("friendCode"),
        resultSet.getBoolean("online")
      );
    }
    catch (SQLException e) {
      log.error("Failed to retrieve user from database.", e);
      System.exit(1);
      return null;
    }
  }

  /**
   * Creates a new user in the database.
   * See {@link User} for more information about the user.
   * @return The new user.
   */
  public User addUser() {
    String id = UUID.randomUUID().toString();
    String friendCode = this.generateFriendCode();

    try {
      this.statement.executeUpdate("INSERT INTO users (id, friendCode, online) VALUES (\"" + id + "\", \"" + friendCode + "\", TRUE)");
    } catch (SQLException e) {
      return this.addUser();
    }

    return new User(id, friendCode, true);
  }

}
