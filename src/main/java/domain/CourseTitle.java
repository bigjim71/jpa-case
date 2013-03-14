package domain;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.joda.time.LocalDate.now;

/**
 * todo
 */
@Entity
public class CourseTitle {
  @Id
  @GeneratedValue
  private Long id;

  private int durationInDays;
  private String courseCode;
  private String title;

  @OneToMany(mappedBy = "courseTitle",
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
  )
  Set<Course> scheduledCourses = new HashSet<Course>();


  public CourseTitle(String courseCode, Days days, String title) {
    assert courseCode != null;
    assert title != null;
    scheduledCourses = new HashSet<Course>();

    this.durationInDays = days.getDays();
    this.courseCode = courseCode;
    this.title = title;
  }

  protected CourseTitle() {
  }


  public Days getDuration() {
    return Days.days(durationInDays);
  }

  public void scheduleCourses(LocalDate... dates) {
    for (LocalDate dateTime : dates) {
      Course course = new Course(dateTime, this);
      this.scheduledCourses.add(course);
    }
  }


  public void cancel(Course course) throws BookingException {
    if (course.getStartDate().isBefore(now()))
      throw new BookingException("Can't cancel courses in the past");

    if (course.hasRegistrations())
      throw new BookingException("Can't cancel a course with registered students");
    this.scheduledCourses.remove(course);
  }


  public long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CourseTitle)) return false;

    CourseTitle that = (CourseTitle) o;

    if (!courseCode.equals(that.courseCode)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return courseCode.hashCode();
  }

  @Override
  public String toString() {
    return "CourseTitle{" +
          "courseCode='" + courseCode + '\'' +
          ", title='" + title + '\'' +
          '}';
  }

}
