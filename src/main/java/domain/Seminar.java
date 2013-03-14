package domain;

import org.joda.time.LocalDate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * todo
 */
@Entity
@DiscriminatorValue(value = "S")
public class Seminar extends Event {
  private String code;
  private String title;
  private int costInTokens;

  protected Seminar() {
  }

  @Override
  public int getPriceInTokens() {
    return costInTokens;
  }


  public Seminar(String code, LocalDate startDate, String title, int costInTokens) {
    super(startDate);
    assert code != null;
    this.code = code;
    this.title = title;
    this.costInTokens = costInTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Seminar)) return false;

    Seminar seminar = (Seminar) o;

    return code.equals(seminar.code);

  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }
}
