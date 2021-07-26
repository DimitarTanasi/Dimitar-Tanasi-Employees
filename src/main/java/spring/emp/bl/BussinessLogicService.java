package spring.emp.bl;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import spring.emp.models.Employee;
import spring.emp.models.Project;
import spring.emp.util.DateFormatDeterminator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BussinessLogicService {

    public void calculateResult(Resource resource) throws Exception {

        List<Employee> employeeList = new ArrayList<>();
        File file = resource.getFile();
//2015-04-27
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {

            String[] dataLine = line.split(", ");
            long id = Long.parseLong(dataLine[0]);
            Project project = new Project(Long.parseLong(dataLine[1]));
            Date dateFrom = null;
            Date dateTo = null;
            try {
                String formatFrom = DateFormatDeterminator.determineDateFormat(dataLine[2]);
                dateFrom = new SimpleDateFormat(formatFrom).parse(dataLine[2]);
                String formatTo = DateFormatDeterminator.determineDateFormat(dataLine[3]);
                dateTo = new SimpleDateFormat(formatTo).parse(dataLine[3]);
            } catch (Exception e) {
                dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
            }

            Employee employee = new Employee(id, project, dateFrom, dateTo);

            employeeList.add(employee);

        }
        Map<Long, List<Employee>> allProjects = new HashMap<>();

        for (Employee e : employeeList) {
            if (!allProjects.containsKey(e.getProject().getId())) {
                allProjects.put(e.getProject().getId(), new ArrayList<>());
                allProjects.get(e.getProject().getId()).add(e);
            } else {
                allProjects.get(e.getProject().getId()).add(e);
            }
        }

        List<Employee> bestEmployees = new ArrayList<>();
        for (Map.Entry<Long, List<Employee>> kvp : allProjects.entrySet()) {
            List<Employee> empList = kvp.getValue();
            long best = Long.MIN_VALUE;

            for (int i = 0; i < empList.size(); i++) {
                Employee e1 = empList.get(i);
                for (int j = i + 1; j < empList.size(); j++) {
                    Employee e2 = empList.get(j);
                    boolean result = doOverlap(e1, e2);
                    if (!result) {
                        continue;
                    }
                    int index = overlapIndex(e1, e2);
                    long period = periodOfWorkingTogether(e1, e2, index);

                    if (period > best) {
                        best = period;
                        bestEmployees = new ArrayList<>();
                        bestEmployees.add(e1);
                        bestEmployees.add(e2);
                    }
                }
            }

        }
        Employee bestemployee1 = bestEmployees.get(0);
        Employee bestemployee2 = bestEmployees.get(1);
        int idx = overlapIndex(bestemployee1, bestemployee2);
        System.out.printf("Employees working together ID:%d and ID:%d%n", bestemployee1.getEmpID(), bestemployee2.getEmpID());
        System.out.printf("Days, working together over project%d: %d", bestemployee1.getProject().getId(), periodOfWorkingTogether(bestemployee1, bestemployee2, idx));

        System.out.println();
    }


    public static boolean doOverlap(Employee e1, Employee e2) {
        return e1.getDateFrom().before(e2.getDateTo()) && e2.getDateFrom().before(e1.getDateTo());
    }

    public static long calculatePeriodInDays(long diff) {
        return (diff / (1000 * 60 * 60 * 24));

    }

    public static int overlapIndex(Employee e1, Employee e2) {

        if (e1.getDateFrom().after(e2.getDateFrom()) || e1.getDateFrom().equals(e2.getDateFrom())
                && e1.getDateTo().before(e2.getDateTo()) || e1.getDateTo().equals(e2.getDateTo())) {
            return 3;
        } else if (e2.getDateFrom().after(e1.getDateFrom()) || e2.getDateFrom().equals(e1.getDateFrom())
                && e2.getDateTo().before(e1.getDateTo()) || e2.getDateTo().equals(e1.getDateTo())) {
            return 4;
        } else if (e1.getDateTo().after(e2.getDateTo())) {
            return 2;
        } else if (e1.getDateTo().before(e2.getDateTo())) {
            return 1;
        }
        return 0;
    }

    public static long periodOfWorkingTogether(Employee e1, Employee e2, int index) {
        long diff = 0;
        long period = 0;
        switch (index) {
            case 1:
                diff = e1.getDateTo().getTime() - e2.getDateFrom().getTime();
                period = calculatePeriodInDays(diff);
                break;
            case 2:
                diff = e2.getDateTo().getTime() - e1.getDateFrom().getTime();
                period = calculatePeriodInDays(diff);
                break;
            case 3:
                diff = e1.getDateTo().getTime() - e1.getDateFrom().getTime();
                period = calculatePeriodInDays(diff);
                break;
            case 4:
                diff = e2.getDateTo().getTime() - e2.getDateFrom().getTime();
                period = calculatePeriodInDays(diff);
                break;
        }
        return period;
    }

    public String readFileContent() throws IOException {
        return Files.readAllLines(Paths.get("upload-dir/data.txt"))
                .stream().filter(s -> !s.isEmpty()).collect(Collectors.joining(System.lineSeparator()));
    }

    public static Date parse(String date) throws Exception {

        try {

            List<DateGroup> parse = new Parser().parse(date);
            return parse.get(0).getDates().get(0);

        } catch (Exception e) {
            throw new Exception("unparseable date: " + date);
        }

    }
}
