package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Reporting;
import nodv.model.User;
import nodv.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportingService {
    @Autowired
    ReportingRepository reportingRepository;

    @Autowired
    UserService userService;

    public Reporting createReporting(Reporting reporting, String userId, String userIsReportedId){
        User user = userService.findById(userId);
        reporting.setUserId(userId);
        reporting.setUser(user);
        reporting.setUserIsReportedId(userIsReportedId);
        reporting.setIsResolved(false);
        return reportingRepository.save(reporting);
    }

    public List<Reporting> getAllReportings(){
        List<Reporting> reportings = reportingRepository.findAll();
        return reportings;
    }

    public Reporting updateReportingState(String reportingId){
        Optional<Reporting> reporting = reportingRepository.findById(reportingId);
        if(reporting.isPresent()){
            reporting.get().setIsResolved(!reporting.get().getIsResolved());
        }else {
            throw new NotFoundException("Reporting not found");
        }
        return reportingRepository.save(reporting.get());
    }
}