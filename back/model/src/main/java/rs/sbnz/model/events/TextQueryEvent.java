package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import rs.sbnz.model.Request;

@Role(Role.Type.EVENT)
@Expires("5m")
public class TextQueryEvent {
    private Request request;
    private String queryText;

    public TextQueryEvent() {
    }

    public TextQueryEvent(String queryText, Request request) {
        this.queryText = queryText.trim();
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getQueryText() {
        return this.queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }  
}
