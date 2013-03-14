package service;

import domain.*;
import org.joda.time.LocalDate;
import util.JPAUtil;
import util.None;
import util.Option;
import util.Some;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  public Option<Event> getEvent(long eventId) {
    return get(Event.class,eventId);

  }

  public Option<CourseTitle> getCourseTitle(long courseTitleId) {
    return get(CourseTitle.class,courseTitleId);
  }

  public Option<Student> findStudent(String username) {
    EntityManager em = JPAUtil.getEntityManager();
    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<Student> query = builder.createQuery(Student.class);
    Root<Student> root = query.from(Student.class);
    query.where(builder.equal(root.get(Student_.username), username));

    Set<Student> resultSet;
    {
      List<Student> resultList = em.createQuery(query).getResultList();

      resultSet = new HashSet<Student>(resultList);
    }

    assert resultSet.size() == 0 || resultSet.size() == 1;

    if (resultSet.isEmpty())
      return None.none();
    else
      return new Some<Student>(resultSet.iterator().next());


  }

  public Option<Course> findCourse(long courseTitleId, LocalDate fromDate, LocalDate toDate) {
    EntityManager em = JPAUtil.getEntityManager();
    TypedQuery<Course> query = em.createQuery("select c from Course c " +
          "where c.courseTitle.id=:titleId" +
          " and c.startDate between :from and :to and c.students.size<10",
          Course.class);

    query.setParameter("from", fromDate.toDate());
    query.setParameter("to", toDate.toDate());
    query.setParameter("titleId", courseTitleId);
    List<Course> resultList = query.getResultList();

    if (resultList.isEmpty())
      return None.none();
    else
      return new Some<Course>(resultList.get(0));


  }

  private  <T> Option<T> get(Class<T> clazz, long id) {
    EntityManager em = JPAUtil.getEntityManager();
    T objectOrNull = em.find(clazz, id);
    if (objectOrNull==null) return util.None.none ();
    return new Some(objectOrNull);
  }



}
