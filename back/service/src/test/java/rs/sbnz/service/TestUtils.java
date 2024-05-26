package rs.sbnz.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import rs.sbnz.model.Alarm;

public class TestUtils {
    static <T> List<T> getFactsFrom(KieSession ksession, Class<T> clazz) {
        Collection<? extends Object> coll = ksession.getObjects(new ClassObjectFilter(clazz));
        List<T> li = new ArrayList<>();
        for (Object o : coll) {
            li.add((T)o);
        }
        return li;
    }
}
