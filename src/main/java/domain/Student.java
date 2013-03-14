package domain;

import org.hibernate.annotations.IndexColumn;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import javax.persistence.*;
import java.util.*;

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

  @Column(table = "Password")
  private String password;

  @Embedded
  private CreditCard card;

  @ElementCollection
  @Column(name = "email")
  @IndexColumn(name = "idx")
  private final List<String> emailAddresses = new LinkedList<>();

  @ManyToMany
  private Set<Event> registeredEvents = new HashSet<>();

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


  public void registerForEvent(final Event event) throws BookingException {

    if (this.registeredEvents.contains(event))
      throw new BookingException("Student already registered on this event");


    int tokensThisCourseCosts = event.getPriceInTokens();

    if (tokens < tokensThisCourseCosts)
      throw new BookingException("Not enough tokens");

    Months months = Months.monthsBetween(LocalDate.now(), this.card.getExpDate());
    if (months.getMonths() < 1) throw new BookingException("Your card expires in 1 month");


    this.registeredEvents.add(event);
    event._registerStudent(this);

    tokens = tokens - tokensThisCourseCosts;

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
