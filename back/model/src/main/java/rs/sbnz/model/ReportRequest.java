package rs.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class ReportRequest {
    private String reportName;
    private List<String> result;

    public ReportRequest(String reportName) {
        this.reportName = reportName;
        this.result = new ArrayList<String>();
    }

    public String getReportName() {
        return reportName;
    }
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    public List<String> getResult() {
        return result;
    }
    public void setResult(List<String> result) {
        this.result = result;
    }
    public void addResult(String result) {
        this.result.add(result);
    }
    
}
