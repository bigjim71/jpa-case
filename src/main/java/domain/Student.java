package domain;


import org.joda.time.LocalDate;
import org.joda.time.Months;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * todo
 */
@Entity
@SecondaryTable(name = "Password")
public class Student {
  @Id
  @GeneratedValue
  private Long id;
  private String username;
  private int tokens;
  private String title;

  @Column(table = "Password")
  private String password;

  @Embedded
  private CreditCard card;

  @ElementCollection
  @Column(name = "email")
  @OrderColumn(name = "idx")
  private final List<String> emailAddresses = new LinkedList<>();

  public Student(String username, String password, String initialEmailAddress) {

    assert username != null;
    assert password != null;
    assert initialEmailAddress != null;
    this.username = username;
    this.password = password;
    this.tokens = 10;
    this.emailAddresses.add(initialEmailAddress);
  }

  protected  Student() {
  }

  public Long getId() {
    return id;
  }

  public void registerForEvent(String title) throws BookingException {
    if (tokens ==0) throw new BookingException("Insufficient tokens");
    Months months = Months.monthsBetween(LocalDate.now(), this.card.getExpDate());
    if (months.getMonths() < 1) throw new BookingException("Your card expires in 1 month");


    this.title = title;
    tokens = tokens-1;
  }

  public void useCard(String creditCardNumber, LocalDate expDate) {
    this.card = new CreditCard(creditCardNumber,expDate);
  }

  public List<String> getEmailAddresses() {
    return Collections.unmodifiableList(emailAddresses);
  }

  public void registerAdditionalAddress(String email){
    emailAddresses.add(email);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Student)) return false;

    Student student = (Student) o;

    return username.equals(student.username);

  }

  @Override
  public int hashCode() {
    return username.hashCode();
  }
}
