package domain;


import org.joda.time.LocalDate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * todo
 */
@Entity
@DiscriminatorValue(value = "C")
public class Course extends Event {


  @ManyToOne
  private CourseTitle courseTitle;


  //friend function for CourseTitle
  Course(LocalDate date, CourseTitle courseTitle) {
    super(date);
    assert date != null;
    assert courseTitle != null;

    this.courseTitle = courseTitle;
  }

  protected Course() {
  }

  @Override
  protected void canRegister(Student student) throws BookingException {

    if ((getStartDate().isBefore(LocalDate.now().plusDays(3))))
      throw new BookingException("Can't book on a course that starts in less than two days");

    if (!hasSeatsLeft()) throw new BookingException("No more seats available");


  }

  private boolean hasSeatsLeft() {
    return this.getStudents().size() < 10;
  }


  @Override
  public int getPriceInTokens() {
    int costPerDay = 1;
    return courseTitle.getDuration().getDays() * costPerDay;
  }

  public CourseTitle getCourseTitle() {
    return courseTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Course)) return false;

    Course other = (Course) o;

    return courseTitle.equals(other.courseTitle) && getStartDate().equals(other.getStartDate());

  }

  @Override
  public int hashCode() {
    int result = courseTitle.hashCode();
    result = 31 * result + getStartDate().hashCode();
    return result;
  }


}
