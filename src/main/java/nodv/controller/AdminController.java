package nodv.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("")
    public String getAdmin(){
        return "Admin Resources !!!";
    }
}