package nodv.controller;

import nodv.model.Notification;
import nodv.model.ReportType;
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
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app", "https://blog-nodv-admin.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/reporting")
public class ReportingController {
    @Autowired
    ReportingService reportingService;
    @Autowired
    TokenProvider tokenProvider;

    @PostMapping()
    public ResponseEntity<?> createReporting(HttpServletRequest request, @RequestBody Reporting reporting) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Reporting newReporting = reportingService.createReport(reporting, userId);


        // tao 1 thong bao toi tat ca cac admin co thuc hien real time
        // bo sung di

        return new ResponseEntity<>(newReporting, HttpStatus.OK);
    }
    @PostMapping("/report/{id}")
    public ResponseEntity<?> reportComment(HttpServletRequest request, @PathVariable String id, @RequestParam(value = "contentReport", defaultValue = "", required = false) String contentReport, @RequestParam(value = "type", defaultValue = "", required = false) ReportType type){
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Reporting reportComment = reportingService.createReportComment(userId, id,contentReport, type);
        return new ResponseEntity<>(reportComment, HttpStatus.OK);

    }
}