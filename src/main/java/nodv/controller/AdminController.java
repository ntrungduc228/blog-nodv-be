package nodv.controller;

import nodv.model.Comment;
import nodv.model.Reporting;
import nodv.security.TokenProvider;
import nodv.service.ReportingService;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    ReportingService reportingService;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("")
    public String getAdmin(){
        return "Admin Resources !!!";
    }


    @GetMapping("/reporting")
    public ResponseEntity<?> getAllReporting(HttpServletRequest request) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        List<Reporting> reportings = reportingService.getAllReportings();
        return new ResponseEntity<>(reportings, HttpStatus.OK);
    }

    @PatchMapping("/reporting/{id}")
    public ResponseEntity<?> updateReportingStatus(@PathVariable String id, HttpServletRequest request) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Reporting reporting =  reportingService.updateReportingState(id);
        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }
}