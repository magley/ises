package rs.sbnz.service.request.dto;

public class IPBlockedDTO {
    public String ipBlockedReason;


    public IPBlockedDTO() {
        this.ipBlockedReason = "You have been blocked due to abuse. Try again later.";
    }

    public IPBlockedDTO(String ipBlockedReason) {
        this.ipBlockedReason = ipBlockedReason;
    }

    public String getIpBlockedReason() {
        return this.ipBlockedReason;
    }

    public void setIpBlockedReason(String ipBlockedReason) {
        this.ipBlockedReason = ipBlockedReason;
    }    
}
