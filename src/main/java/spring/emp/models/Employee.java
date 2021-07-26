package spring.emp.models;

import java.util.Date;

public class Employee {
    private long empID;
    private Project project;
    private Date dateFrom;
    private Date dateTo;

    public Employee() {
    }

    public Employee(long empID, Project project, Date dateFrom, Date dateTo) {
        this.empID = empID;
        this.project = project;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public long getEmpID() {
        return empID;

    }

    public Project getProject() {
        return project;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }
}
