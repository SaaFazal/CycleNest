/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sahad
 */

@ApplicationPath("webresources")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(orchestrator.ItemsResource.class);
        s.add(orchestrator.RentalsResource.class);
        s.add(orchestrator.DistanceResource.class);
        s.add(orchestrator.JsonExceptionMapper.class);
        s.add(orchestrator.FallbackResource.class);
        s.add(orchestrator.DebugResource.class);
        return s;
    }
}


