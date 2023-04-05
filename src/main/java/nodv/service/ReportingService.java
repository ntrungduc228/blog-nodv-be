package nodv.service;

import nodv.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    @Autowired
    ReportingRepository reportingRepository;
}