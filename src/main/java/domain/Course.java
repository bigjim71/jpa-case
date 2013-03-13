package domain;


import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * todo
 */
@Entity

public class Course  {


  @Id
  @GeneratedValue
  private Long id;


  @Temporal(TemporalType.DATE)
  private Date startDate;
  @ManyToOne
  private CourseTitle courseTitle;

  @ManyToMany(mappedBy = "registeredEvents")
  private Set<Student> students = new HashSet<>();

  //friend function for CourseTitle
  Course(LocalDate date, CourseTitle courseTitle) {
    assert date != null;
    assert courseTitle != null;
    this.startDate = date.toDate();
    this.courseTitle = courseTitle;
  }

  protected Course() {
  }

  private boolean hasSeatsLeft() {
    return this.getStudents().size() < 10;
  }

  public int getPriceInTokens() {
    int costPerDay = 1;
    return courseTitle.getDuration().getDays() * costPerDay;
  }

  void _registerStudent(Student student) throws BookingException {
    if ((getStartDate().isBefore(LocalDate.now().plusDays(3))))
      throw new BookingException("Can't book on a course that starts in less than two days");

    if (!hasSeatsLeft()) throw new BookingException("No more seats available");
    boolean studentWasAdded = students.add(student);
    assert studentWasAdded;

  }

  public LocalDate getStartDate() {
    return new LocalDate(this.startDate);
  }

  public Set<Student> getStudents() {
    return Collections.unmodifiableSet(students);
  }

  public boolean hasRegistrations() {
    return !this.students.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Course)) return false;

    Course course = (Course) o;
    return courseTitle.equals(course.courseTitle) && getStartDate().equals(course.getStartDate());
  }

  @Override
  public int hashCode() {
    int result = courseTitle.hashCode();
    result = 31 * result + getStartDate().hashCode();
    return result;
  }
}
