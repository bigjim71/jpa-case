package service;

import domain.*;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JPAUtil;
import util.Option;


/**
 * todo
 */
public class EnrollmentService {
  private static Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

  StudentRepository studentRepository = StudentRepository.getInstance();
  private static EnrollmentService instance = JPAUtil.getTransactionalProxy(EnrollmentService.class);

  public static EnrollmentService getInstance(){
    return instance;
  }


  protected EnrollmentService() {
  }

  public long registerStudent(String username, String password, String creditCardNumber, LocalDate expDate, String initialEmail) {
    if (username==null) throw new IllegalArgumentException("username is null");
    if (creditCardNumber==null) throw new IllegalArgumentException("creditCardNumber is null");
    if (expDate==null) throw new IllegalArgumentException("creditCardNumber is null");
    if (initialEmail==null) throw new IllegalArgumentException("creditCardNumber is null");


    Student newStudent = new Student(username, password, initialEmail);
    newStudent.useCard(creditCardNumber,expDate);
    studentRepository.insert(newStudent);
    return newStudent.getId();
  }


  public void registerStudentForEvent(long studentId, long eventId) throws BookingException {
    logger.info("Registering student {} on event {}", studentId, eventId);

    Option<Student> studentOption = studentRepository.getStudent(studentId);
    Student student = studentOption.assertAndGet("Unknown student " + studentId);

    Option<Event> eventOption = studentRepository.getEvent(eventId);

    Event event = eventOption.assertAndGet("Unknown event id");
    student.registerForEvent(event);
  }

  public long addNewCourseTitle(String code, Days days, String title) {
    if (code==null) throw new IllegalArgumentException("code is null");
    if (days==null) throw new IllegalArgumentException("days is null");
    if (title==null) throw new IllegalArgumentException("days is null");
    CourseTitle courseTitle = new CourseTitle(code, days, title);
    studentRepository.insert(courseTitle);
    return courseTitle.getId();
  }

  public void scheduleCourses(long courseTitleId, LocalDate... dates) {
    if (dates==null) throw new IllegalArgumentException("dates is null");
    Option<CourseTitle> courseTitleOption = studentRepository.getCourseTitle(courseTitleId);
    CourseTitle courseTitle = courseTitleOption.assertAndGet("Unknown course title" + courseTitleId);
    courseTitle.schedule(dates);
  }

  public long scheduleSeminar(String code, LocalDate startDate, String title, int costInTokens) {
    Seminar newSeminar = new Seminar(code, startDate, title, costInTokens);
    studentRepository.insert(newSeminar);
    return newSeminar.getId();
  }

  public Option<StudentInfo> findStudent(String username) {
    Option<Student> studentOption = studentRepository.findStudent(username);
    return studentOption.map(new Option.Mapper<Student, StudentInfo>() {
      @Override
      public StudentInfo map(Student student) {
        return new StudentInfo(student.getId(), student.getUsername(), student.getTokens());
      }
    });
  }

  public Option<Long> findCourse(long courseTitleId, LocalDate fromDate, LocalDate toDate)  {
    Option<Course> course = studentRepository.findCourse(courseTitleId, fromDate, toDate);

    Option<Long> longOption = course.map(new Option.Mapper<Course, Long>() {
      @Override
      public Long map(Course course) {
        return course.getId();
      }
    });

    return longOption;


  }


}
