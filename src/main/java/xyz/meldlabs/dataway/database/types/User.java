package xyz.meldlabs.dataway.database.types;

import lombok.Getter;

public class User {
  
  @Getter
  private final String id;

  @Getter
  private final String friendCode;

  @Getter
  private boolean online;

  /**
   * Represents a user in the database.
   * @param id The id of the user.
   * @param friendCode The friend code of the user.
   * @param online Whether the user is online or not.
   */
  public User(String id, String friendCode, boolean online) {
    this.id = id;
    this.friendCode = friendCode;
    this.online = online;
  }
}
