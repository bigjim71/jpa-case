package service;

import domain.Course;
import domain.CourseTitle;
import domain.Student;
import util.JPAUtil;
import util.Option;
import util.Some;

import javax.persistence.EntityManager;

/**
 * todo
 */
public class StudentRepository {


  private static StudentRepository instance = new StudentRepository();

  public static StudentRepository getInstance() {
    return instance;
  }

  private StudentRepository() {
  }


  public void insert(Object object) {
    EntityManager entityManager = JPAUtil.getEntityManager();
    entityManager.persist(object);


  }

  public Option<Student> getStudent(long studentId) {
    return get(Student.class,studentId);
  }

  public Option<Course> getCourse(long eventId) {
    return get(Course.class,eventId);

  }

  public Option<CourseTitle> getCourseTitle(long courseTitleId) {
    return get(CourseTitle.class,courseTitleId);
  }

  private  <T> Option<T> get(Class<T> clazz, long id) {
    EntityManager em = JPAUtil.getEntityManager();
    T objectOrNull = em.find(clazz, id);
    if (objectOrNull==null) return util.None.none ();
    return new Some(objectOrNull);
  }



}
