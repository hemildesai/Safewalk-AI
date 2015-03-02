/**
 * Project 8
 * @author desai38
 * @author pahlawat
 */

/**
 * Implement a controller that updates the model and repaints the view.
 * 
 * @author dhelia
 * @author jtk
 */

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.*;
import javax.swing.SwingWorker;

public class Controller implements Observer {
    Model model;
    //View view;
    private Connector connector;
    private static final String KEY = "k065386";
    private static final String NICKNAME = "hemil10";
    private static final String HOST = "pc.cs.purdue.edu";
    private static final int PORT = 1337;
    Controller(Model model, String host, int port) {
        this.model = model;
        //this.view = view;
        
        // The Connector calls update(...) when messages arrive from the server...
        connector =  new Connector(host, port, String.format("connect %s", KEY), this);
        
        // Start thread to run doInBackground() method, which handles regular updates of the view
// execute();
    }
    
    /**
     * This method is run on a background Connector thread. It is called when a message arrives from server. Get lock
     * and update model.
     */
    public void update(Observable arg1, Object arg2) {
        String line = (String) arg2;
        String[] fields = line.split(" ");
        String message = fields[0];
        
        synchronized (model.lock) {
            // Use the incoming message type to call the appropriate handler...
            if (message.equals("location"))
                handleLocation(fields);
            else if (message.equals("volunteer"))
                handleVolunteer(fields);
            else if (message.equals("request"))
                handleRequest(fields);
            else if (message.equals("moving"))
                handleMoving(fields);
            else if (message.equals("walking"))
                handleWalking(fields);
            else if (message.equals("delete"))
                handleDelete(fields);
            else if (message.equals("reset"))
                handleReset(fields);
            else // Ignore other message types
                ignoring(fields);
        }
    }
    
    public void handleVolunteer(String[] fields) {
        String volunteerString = fields[1];
        String locationString = fields[2];
        int score = Integer.parseInt(fields[3]);
        Volunteer v = model.getVolunteerByName(volunteerString);
        Location loc = model.getLocationByName(locationString);
        Log.i("VOLUNTEER: volunteer=%s location=%s score=%d", volunteerString, locationString, score);
        
        //Volunteer volunteer = model.getVolunteerByName(volunteerString);
        //Location location = model.getLocationByName(locationString);
        if (loc == null) {
            Log.w("Haven't seen volunteer location %s yet", locationString);
            return;
        }
        if (volunteerString.equals(NICKNAME)) {
            AIClient ai = new AIClient(this.model);
            Request r = ai.nextRequest(v, loc);
            
            if (r != null) {
                connector.writeLine(String.format("walk %s", r.getName()));
                
            } else {
                
                Location next;
                do {
                    next = ai.getNextLocation(loc);
                    
                } while(next == null);
                
                connector.writeLine(String.format("move %s", next.getName()));
                
            }
            //current.addVolunteer(v);
        }
        
        if (v != null)
            model.removeVolunteer(v);
        new Volunteer(model, volunteerString, score, loc);
    }
    
    public void handleMoving(String[] fields) {
        String volunteerString = fields[1];
        String startString = fields[2];
        String destinationString = fields[3];
        int transitTime = Integer.parseInt(fields[4]);
        
        Log.i("MOVING: volunteer=%s start=%s destination=%s transitTime=%d", volunteerString,
              startString, destinationString, transitTime);
        
        Volunteer volunteer = model.getVolunteerByName(volunteerString);
        if (volunteer == null) {
            Log.w("Haven't seen moving volunteer %s yet", volunteerString);
            return;
        }
        
        Location start = model.getLocationByName(startString);
        if (start == null) {
            Log.w("Haven't see start location %s yet", startString);
            return;
        }
        
        Location destination = model.getLocationByName(destinationString);
        if (destination == null) {
            Log.w("Haven't seen destination location %s yet", destinationString);
            return;
        }
        
        if (!start.getName().equals(startString))
            Log.w("MOVING violation: %s not at %s but rather at %s", volunteerString, startString,
                  start.getName());
        
        volunteer.startMoving(destination, transitTime);
    }
    
    public void handleWalking(String[] fields) {
        String volunteerName = fields[1];
        String requesterName = fields[2];
        String startString = fields[3];
        String destinationString = fields[4];
        int transitTime = Integer.parseInt(fields[5]);
        
        Log.i("WALKING: volunteer=%s requester=%s start=%s destination=%s transitTime=%d",
              volunteerName, requesterName, startString, destinationString, transitTime);
        
        Volunteer volunteer = model.getVolunteerByName(volunteerName);
        if (volunteer == null) {
            Log.w("Haven't seen walking volunteer %s yet", volunteerName);
            return;
        }
        Request request = model.getRequestByName(requesterName);
        if (request == null) {
            Log.w("Haven't seen requester %s yet", requesterName);
            return;
        }
        
        Location start = model.getLocationByName(startString);
        if (start == null) {
            Log.w("Haven't seen start location %s yet", startString);
            return;
        }
        
        Location destination = model.getLocationByName(destinationString);
        if (destination == null) {
            Log.w("Haven't seen destination location %s yet", destinationString);
            return;
        }
        
        if (!start.getName().equals(startString))
            Log.w("WALKING violation: %s not at %s but rather at %s", volunteerName, startString,
                  start.getName());
        
        volunteer.startWalking(request, transitTime);
    }
    
    public void handleRequest(String[] fields) {
        String requesterName = fields[1];
        String startString = fields[2];
        String destinationString = fields[3];
        int value = Integer.parseInt(fields[4]);
        
        Log.i("REQUEST: requester=%s start=%s, destination=%s value=%d", requesterName, startString,
              destinationString, value);
        
        // Remove any old request with the same name...
        Request request = model.getRequestByName(requesterName);
        if (request != null)
            model.removeRequest(request);
        
        Location start = model.getLocationByName(startString);
        Location destination = model.getLocationByName(destinationString);
        
        if (start == null) {
            Log.w("Haven't see the request start location %s yet", startString);
            return;
        }
        
        if (destination == null) {
            Log.w("Haven't see the request destination location %s yet", destinationString);
            return;
        }
        
        new Request(model, requesterName, start, destination, value);
    }
    
    private void handleDelete(String[] fields) {
        Volunteer volunteer = model.getVolunteerByName(fields[1]);
        if (volunteer == null) {
            Log.w("Haven't see deleted volunteer %s yet", fields[1]);
            return;
        }
        model.removeVolunteer(volunteer);
    }
    
    private void handleLocation(String[] fields) {
        String building = fields[1];
        double x = Double.parseDouble(fields[2]);
        double y = Double.parseDouble(fields[3]);
        
        if (model.getLocationByName(building) == null) // only create if not already there (so can't update coordinates)
            new Location(model, building, x, y);
    }
    
    private void handleReset(String[] fields) {
        HashSet<Request> requestsClone = (HashSet<Request>) (model.getRequests().clone());
        for (Request request : requestsClone)
            model.removeRequest(request);
        HashSet<Volunteer> volunteersClone = (HashSet<Volunteer>) (model.getVolunteers().clone());
        for (Volunteer volunteer : volunteersClone)
            model.removeVolunteer(volunteer);
    }
    
    private void ignoring(String[] fields) {
        Log.w("IGNORING: %s", fields[0]);
    }
}