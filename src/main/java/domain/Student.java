package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * todo
 */
@Entity
public class Student {
  @Id
  @GeneratedValue
  private Long id;
  private String username;
  private int tokens;
  private String title;

  public Student(String username) {
    this.username = username;
    this.tokens = 10;
  }

  protected  Student() {
  }

  public Long getId() {
    return id;
  }

  public void registerForEvent(String title) throws BookingException {
    if (tokens ==0) throw new BookingException("Insufficient tokens");
    this.title = title;
    tokens = tokens-1;
  }
}
