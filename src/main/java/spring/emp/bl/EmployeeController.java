package spring.emp.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import spring.emp.storage.StorageService;

import java.io.IOException;
import java.text.ParseException;

@Controller
public class EmployeeController {
    BussinessLogicService bussinessLogicService;
    StorageService storageService;

    @Autowired
    public EmployeeController(BussinessLogicService bussinessLogicService,StorageService storageService) {
        this.bussinessLogicService = bussinessLogicService;
        this.storageService = storageService;
    }

    @GetMapping("/employees")
    public ModelAndView ress() throws IOException {
        Object fileContent = bussinessLogicService.readFileContent();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("employees");
        modelAndView.addObject("view","employees");
        modelAndView.addObject("employees", fileContent);
        return modelAndView;
    }
    @PostMapping("/employees")
    public String findBestEmployees() throws Exception {
        Resource file = storageService.loadAsResource("data.txt");
        bussinessLogicService.calculateResult(file);
        return "redirect:/";
    }
}
