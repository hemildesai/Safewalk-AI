/**
 * Project 8
 * @author desai38
 * @author pahlawat
 */

import java.util.*;

public class AIClient {

    private Model model;
    private boolean check = false;
    //private double max = 14;

    AIClient(Model model) {
        this.model = model;
    }

    public Location getNextLocation(Location current) {

        HashSet<Location> l = model.getLocations();

        double min = 1250;
        Location next = null;

        Iterator<Location> it = l.iterator();
        while (it.hasNext()) {
            Location temp = it.next();
            if (temp != current) {
                if (!temp.getRequests().isEmpty()) {

                    double[] fromc = current.getXY();
                    double[] toc = temp.getXY();
                    int value = getHighestValue(temp);
                    double d = Math.sqrt((fromc[0] - toc[0]) * (fromc[0] - toc[0])
                               + (fromc[1] - toc[1]) * (fromc[1] - toc[1]));
                    if (value > 200) {
                        if ( d < min) {
                            min = d;
                            next = temp;
                        }
                    }
                }
            }
        }
        if (next == null) {
            next = getLocation(current);
            check = true;
        } else {
            check = false;
        }

        return next;

    }

    public double distance(double[] fromc, double[] toc) {
        double d = Math.sqrt((fromc[0] - toc[0]) * (fromc[0] - toc[0]) + (fromc[1] - toc[1]) * (fromc[1] - toc[1]));
        return d;
    }

    public Location getLocation(Location current) {

        HashSet<Location> l = model.getLocations();

        double min = 10000;
        Location next = null;

        Iterator<Location> it = l.iterator();
        while (it.hasNext()) {
            Location temp = it.next();
            if (temp != current) {
                if (!temp.getRequests().isEmpty()) {
                    double[] fromc = current.getXY();
                    double[] toc = temp.getXY();

                    double d = Math.sqrt((fromc[0] - toc[0]) * (fromc[0] - toc[0])
                               + (fromc[1] - toc[1]) * (fromc[1] - toc[1]));
                    if (d < min) {
                        min = d;
                        next = temp;
                    }
                }
            }
        }

        return next;

    }

    public int getHighestValue(Location l) {
        int max = 0;
        HashSet<Request> r = l.getRequests();

        Iterator<Request> i = r.iterator();
        while (i.hasNext()) {
            Request n = i.next();
            int value = n.getValue();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public double getPriority(Request request) {
        Location from = request.getStart();
        Location to = request.getDestination();

        double[] fromc = from.getXY();
        double[] toc = to.getXY();

        int rValue = getHighestValue(to);
        double d = Math.sqrt((fromc[0] - toc[0]) * (fromc[0] - toc[0]) + (fromc[1] - toc[1]) * (fromc[1] - toc[1]));

        int value = request.getValue();

        double priority = (value * 100) / d;
        if (value > 200) {
            priority *= 10;
        }
        if (rValue > 200) {
            priority *= 5;
        }

        int size = to.getRequests().size();


        return priority;

    }

    public Request nextRequest(Volunteer volunteer, Location current) {

        Request next = null;
        double max = 14.5;


        if (current.getRequests().isEmpty()) {

        } else {
            HashSet<Request> r = current.getRequests();
            Iterator<Request> i = r.iterator();
            while (i.hasNext()) {
                Request n = i.next();
                double pr = getPriority(n);

                if (pr > max) {
                    max = pr;
                    next = n;
                }
            }

        }

        return next;
    }

}