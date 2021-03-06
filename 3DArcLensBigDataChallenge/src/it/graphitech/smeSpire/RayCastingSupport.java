/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.Logging;

/**
 * Contains methods to resolve ray intersections with the terrain.
 * @author Patrick Murris
 * @version $Id: RayCastingSupport.java 1 2011-07-16 23:22:47Z dcollins $
 */

public class RayCastingSupport
{
    private static double defaultSampleLength = 100; // meters
    private static double defaultPrecision = 10;     // meters

    /**
     * Compute the intersection <code>Position</code> of the globe terrain with the ray starting 
     * at origin in the given direction. Uses default sample length and result precision.
     * @param globe the globe to intersect with.
     * @param origin origin of the ray.
     * @param direction direction of the ray.
     * @return the <code>Position</code> found or <code>null</code>.
     */
    public static Position intersectRayWithTerrain(Globe globe, Vec4 origin, Vec4 direction)
    {
        return intersectRayWithTerrain(globe, origin, direction, defaultSampleLength, defaultPrecision);
    }

    /**
     * Compute the intersection <code>Position</code> of the globe terrain with the ray starting
     * at origin in the given direction. Uses the given sample length and result precision.
     * @param globe the globe to intersect with.
     * @param origin origin of the ray.
     * @param direction direction of the ray.
     * @param sampleLength the sampling step length in meters.
     * @param precision the maximum sampling error in meters.
     * @return the <code>Position</code> found or <code>null</code>.
     */
    public static Position intersectRayWithTerrain(Globe globe, Vec4 origin, Vec4 direction, double sampleLength, double precision)
    //public static Vec4[] intersectRayWithTerrain(Globe globe, Vec4 origin, Vec4 direction, double sampleLength, double precision)
    {
        if (globe == null)
        {
            String msg = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (origin == null || direction == null)
        {
            String msg = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (sampleLength < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", sampleLength);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (precision < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", precision);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position pos = null;
        direction = direction.normalize3();

        // Check whether we intersect the globe at it's highest elevation
        Intersection inters[] = globe.intersect(new Line(origin, direction), globe.getMaxElevation());
        if (inters != null)
        {
            // Sort out intersection points and direction
            Vec4 p1 = inters[0].getIntersectionPoint();
            Vec4 p2 = null;
            if (p1.subtract3(origin).dot3(direction) < 0)
            {
            	System.out.println("wrong direction");
                p1 = null; // wrong direction
            }
            if (inters.length == 2)
            {
            //	System.out.println("trovati 2 intersezioni");
                p2 = inters[1].getIntersectionPoint();
                if (p2.subtract3(origin).dot3(direction) < 0)
                {System.out.println("wrong direction");
                    p2 = null; // wrong direction
                }
            }

            if (p1 == null && p2 == null)   // both points in wrong direction
            {
            	System.out.println("entrambi i punti in wrong direction");
            	return null;
            }
                

            if (p1 != null && p2 != null)
            {
                // Outside sphere move to closest point
                if (origin.distanceTo3(p1) > origin.distanceTo3(p2))
                {
                    // switch p1 and p2
                    Vec4 temp = p2;
                    p2 = p1;
                    p1 = temp;
                    
                  
                }
              //  System.out.println("punto pi� vicino: "+p1);
              //  System.out.println("punto pi� lontano: "+p2);
            }
            else
            {
                // single point in right direction: inside sphere
                p2 = p2 == null ? p1 : p2;
                p1 = origin;
                System.out.println("solo un punto: "+p2);
            }
           
            

            // Sample between p1 and p2
            Vec4 point = intersectSegmentWithTerrain(globe, p1, p2, sampleLength, precision);
            if (point != null)
                pos = globe.computePositionFromPoint(point);

        }
        return pos;
        
   
      
    }

    
    
    public static Vec4[] intersectRayWithTerrainReturns2Points(Globe globe, Vec4 origin, Vec4 direction, double sampleLength, double precision)
    {
    	
    	Vec4[] res = new Vec4[2];
    	
        if (globe == null)
        {
        	System.out.println("nullValue.GlobeIsNull");
            String msg = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (origin == null || direction == null)
        {
        	System.out.println("nullValue.Vec4IsNull");
            String msg = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (sampleLength < 0)
        {
        	System.out.println("generic.ArgumentOutOfRange");
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", sampleLength);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (precision < 0)
        {
        	System.out.println("generic.ArgumentOutOfRange");
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", precision);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        
        direction = direction.normalize3();

        // Check whether we intersect the globe at it's highest elevation
        Intersection inters[] = globe.intersect(new Line(origin, direction), globe.getMaxElevation());
        if (inters != null)
        {
            // Sort out intersection points and direction
            Vec4 p1 = inters[0].getIntersectionPoint();
            Vec4 p2 = null;
            if (p1.subtract3(origin).dot3(direction) < 0)
            {
            	//System.out.println("wrong direction");
                p1 = null; // wrong direction
            }
            if (inters.length == 2)
            {
            	//System.out.println("trovati 2 intersezioni");
                p2 = inters[1].getIntersectionPoint();
                if (p2.subtract3(origin).dot3(direction) < 0)
                {System.out.println("wrong direction");
                    p2 = null; // wrong direction
                }
            }
            if(inters.length == 1){
            	//System.out.println("trovata solo una intersezione");
            }

            if (p1 == null && p2 == null)   // both points in wrong direction
            {
            	System.out.println("entrambi i punti in wrong direction");
            	return null;
            }
                

            if (p1 != null && p2 != null)
            {
                // Outside sphere move to closest point
                if (origin.distanceTo3(p1) > origin.distanceTo3(p2))
                {
                    // switch p1 and p2
                    Vec4 temp = p2;
                    p2 = p1;
                    p1 = temp;
                    
                  
                }
              //  System.out.println("punto pi� vicino: "+p1);
              //  System.out.println("punto pi� lontano: "+p2);
            }
            else
            {
                // single point in right direction: inside sphere
                p2 = p2 == null ? p1 : p2;
                p1 = origin;
                //System.out.println("solo un punto: "+p2);
            }
           
            
/*
            // Sample between p1 and p2
            Vec4 point = intersectSegmentWithTerrain(globe, p1, p2, sampleLength, precision);
            if (point != null)
                pos = globe.computePositionFromPoint(point);

        }
        return pos;
        */
            res[0] = p1;
            res[1] = p2;
            
        }else{
        	
        }
       
        return res;
    }
    
    /**
     * Compute the intersection <code>Vec4</code> point of the globe terrain with a line segment
     * defined between two points. Uses the default sample length and result precision.
     * @param globe the globe to intersect with.
     * @param p1 segment start point.
     * @param p2 segment end point.
     * @return the <code>Vec4</code> point found or <code>null</code>.
     */
    public static Vec4 intersectSegmentWithTerrain(Globe globe, Vec4 p1, Vec4 p2)
    {
        return intersectSegmentWithTerrain(globe, p1, p2, defaultSampleLength, defaultPrecision);
    }

    /**
     * Compute the intersection <code>Vec4</code> point of the globe terrain with the a segment
     * defined between two points. Uses the given sample length and result precision.
     * @param globe the globe to intersect with.
     * @param p1 segment start point.
     * @param p2 segment end point.
     * @param sampleLength the sampling step length in meters.
     * @param precision the maximum sampling error in meters.
     * @return the <code>Vec4</code> point found or <code>null</code>.
     */
    public static Vec4 intersectSegmentWithTerrain(Globe globe, Vec4 p1, Vec4 p2,
                                                double sampleLength, double precision)
    {
        if (globe == null)
        {
            String msg = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (p1 == null || p2 == null)
        {
            String msg = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (sampleLength < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", sampleLength);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (precision < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange", precision);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Vec4 point = null;
        // Sample between p1 and p2
        Line ray = new Line(p1, p2.subtract3(p1).normalize3());
        double rayLength = p1.distanceTo3(p2);
        double sampledDistance = 0;
        Vec4 sample = p1;
        Vec4 lastSample = null;
        while (sampledDistance <= rayLength)
        {
            Position samplePos = globe.computePositionFromPoint(sample);
            if (samplePos.getElevation() <= globe.getElevation(samplePos.getLatitude(), samplePos.getLongitude()))
            {
                // Below ground, intersection found
                point = sample;
                break;
            }
            if (sampledDistance >= rayLength)
                break;    // break after last sample
            // Keep sampling
            lastSample = sample;
            sampledDistance = Math.min(sampledDistance + sampleLength, rayLength);
            sample = ray.getPointAt(sampledDistance);
        }

        // Recurse for more precision if needed
        if (point != null && sampleLength > precision && lastSample != null)
            point = intersectSegmentWithTerrain(globe, lastSample, point, sampleLength / 10, precision);

        return point;
    }
}
