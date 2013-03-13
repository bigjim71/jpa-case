package domain;

import org.joda.time.LocalDate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Date;

/**
 * todo
 */
@Embeddable
@Access(AccessType.FIELD)
public class CreditCard {
  private String cardNumber;
  private Date expDate;

  public CreditCard(String cardNumber, LocalDate expDate) {
    assert cardNumber !=null;
    assert expDate !=null;
    this.cardNumber = cardNumber;
    this.expDate = expDate.toDate();
  }

  protected CreditCard() {
  }

  public LocalDate getExpDate() {
    return LocalDate.fromDateFields(expDate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CreditCard)) return false;

    CreditCard that = (CreditCard) o;

    if (!cardNumber.equals(that.cardNumber)) return false;
    if (!expDate.equals(that.expDate)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = cardNumber.hashCode();
    result = 31 * result + expDate.hashCode();
    return result;
  }
}
