package spring.emp.models;

public class Project {
    private long Id;

    public Project() {
    }

    public Project(long id) {
        Id = id;
    }

    public long getId() {
        return Id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Project)) {
            return false;
        }
       Project p = (Project) obj;
        return this.Id == p.Id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
