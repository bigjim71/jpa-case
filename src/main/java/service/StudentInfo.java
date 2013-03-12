package service;

/**
 * todo
 */
public class StudentInfo {
    private long id;
    private String username;
    private int tokens; private String currentCourse;

    public StudentInfo(long id, String username, int tokens) {

        this.id = id;
        this.username = username;
        this.tokens = tokens;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getTokens() {
        return tokens;
    }

    public String getCurrentCourse() {
        return currentCourse;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", tokens=" + tokens +
                ", currentCourse='" + currentCourse + '\'' +
                '}';
    }
}
