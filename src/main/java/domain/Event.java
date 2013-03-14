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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @ManyToMany(mappedBy = "registeredEvents")
    private Set<Student> students = new HashSet<Student>();

    public abstract int getPriceInTokens();

    public LocalDate getStartDate() {
        return new LocalDate(this.startDate);
    }

    public Long getId() {
        return id;
    }

    public Event(LocalDate startDate) {
        assert startDate!=null;
        this.startDate = startDate.toDate();
    }

    protected Event() {
    }

    //would normally be final(!)
    //template method
    void _registerStudent(Student student) throws BookingException {
        this.canRegister(student);
        boolean studentWasAdded = students.add(student);
        assert studentWasAdded;
    }


    protected void canRegister(Student student) throws BookingException{
        // default no exceptions, student can register
    }

    public Set<Student> getStudents() {
        return Collections.unmodifiableSet(students);
    }

    public boolean hasRegistrations() {
        return !this.students.isEmpty();
    }
}
