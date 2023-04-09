package nodv.controller;

import nodv.model.Notification;
import nodv.model.Reporting;
import nodv.security.TokenProvider;
import nodv.service.NotificationService;
import nodv.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/reporting")
public class ReportingController {
    @Autowired
    ReportingService reportingService;
    @Autowired
    TokenProvider tokenProvider;

    @PostMapping()
    public ResponseEntity<?> createReporting(HttpServletRequest request, @RequestBody Reporting reporting, String userIsReportedId) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Reporting newReporting = reportingService.createReporting(reporting, userId, userIsReportedId);


        // tao 1 thong bao toi tat ca cac admin co thuc hien real time
        // bo sung di

        return new ResponseEntity<>(newReporting, HttpStatus.OK);
    }
}