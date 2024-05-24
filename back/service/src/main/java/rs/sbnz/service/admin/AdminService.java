package rs.sbnz.service.admin;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Alarm;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.UnblockEvent;

@Component
public class AdminService {
    @Autowired private KieSession ksession;

    public List<BlockEvent> getBlockEvents() {
        List<BlockEvent> blocks = new ArrayList<BlockEvent>();
        QueryResults results = ksession.getQueryResults("getAllBlockEventsForIp"); 
        for ( QueryResultsRow row : results ) {
            BlockEvent block = (BlockEvent) row.get( "$block" );
            blocks.add(block);
        }
        return blocks;
    }

    public List<Alarm> getUnhandledAlarms() {
        List<Alarm> alarms = new ArrayList<Alarm>();
        QueryResults results = ksession.getQueryResults("getAllUnhandledAlarms"); 
        for ( QueryResultsRow row : results ) {
            Alarm alarm = (Alarm) row.get( "$alarm" );
            alarms.add(alarm);
        }
        return alarms;
    }

    public void unblockIP(String ip) {
        ksession.insert(new UnblockEvent(ip));
        ksession.fireAllRules();
    }

}
