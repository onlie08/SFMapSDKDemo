package com.sfmap.map.demo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.LatLng;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.simplify.VWSimplifier;

public class LatLngUtils {
    public interface Locatable {
        LatLng getLocation();
        int getPosition();
        int setPosition(int position);
    }

    public interface GeometryCoverable {
        LatLng getLocation();
    }
    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 有一条轨迹对指定经纬度点进行排序的方法
     * @param locatables 经纬度点
     * @param track 轨迹
     * @param distanceLimit 吸附距离，超出该范围的会被排除掉
     * @return 排序好的经纬度点
     */
    public static List<Locatable> sortByTrack(List<Locatable> locatables, List<LatLng> track, float distanceLimit) {
        if(locatables == null || locatables.size() <= 1) {
            return locatables;
        }

        if(track == null || track.isEmpty()) {
            return locatables;
        }

        List<Locatable> withDistanceLimitList = new ArrayList<>();
        List<float[]> toSortIndexAndDistance = new ArrayList<>();
        for(Locatable locatable : locatables) {
            float[] indexAndDistance = new float[2];
            closeIndexOfTrackToPoint(track, locatable.getLocation(), indexAndDistance);
            if(indexAndDistance[1] <= distanceLimit) {
                withDistanceLimitList.add(locatable);
                toSortIndexAndDistance.add(indexAndDistance);
            }
        }

        Integer[] sortedIndex = new ArrayIndexComparator(toSortIndexAndDistance).getSortedIndex();
        List<Locatable> sortedList = new ArrayList<>(withDistanceLimitList.size());


        for(int index = 0; index < withDistanceLimitList.size(); index++) {
            sortedList.add(withDistanceLimitList.get(sortedIndex[index]));
        }

        return sortedList;

    }

    private static int closeIndexOfTrackToPoint(List<LatLng> track, LatLng point, float[] indexAndDistance) {
        float minDistance = Float.MAX_VALUE;
        int closeIndex = -1;
        int trackPointCount = track.size();

        for(int index = 0; index < trackPointCount; index++) {
            float distance = MapUtils.calculateLineDistance(point, track.get(index));
            if(distance < minDistance) {
                minDistance = distance;
                closeIndex = index;
            }
        }
        indexAndDistance[0] = closeIndex;
        indexAndDistance[1] = minDistance;
        return closeIndex;
    }

    private static class ArrayIndexComparator implements Comparator<Integer> {
        private final List<float[]> indexAndDistances;
        private final Integer[] indexes;
        ArrayIndexComparator(List<float[]> indexAndDistances) {
            this.indexAndDistances = indexAndDistances;
            indexes = new Integer[indexAndDistances.size()];
            for(int index = 0; index < indexAndDistances.size(); index++) {
                indexes[index] = index;
            }
        }

        Integer[] getSortedIndex() {
            Arrays.sort(indexes, this);
            return indexes;
        }


        @Override
        public int compare(Integer firstIndex, Integer secondIndex) {
            float[] firstIndexAndDistance = indexAndDistances.get(firstIndex);
            float[] secondIndexAndDistance = indexAndDistances.get(secondIndex);
            if(firstIndexAndDistance[0] == secondIndexAndDistance[0]) {
                return Float.compare(firstIndexAndDistance[1], secondIndexAndDistance[1]);
            } else {
                return Float.compare(firstIndexAndDistance[0], secondIndexAndDistance[0]);
            }
        }
    }

    public static Geometry simplifyAndValidatePolygon(List<LatLng> points, double distanceTolerance) {

        Coordinate[] coordinates = new Coordinate[points.size() + 1];
        int index = 0;
        for(LatLng latLng : points) {
            coordinates[index] = new Coordinate(latLng.longitude, latLng.latitude);
            index++;
        }
        coordinates[index] = coordinates[0];

        Polygon polygon = geometryFactory.createPolygon(coordinates);

        return validate(polygon, distanceTolerance);

    }

    /**
     * Get / create a valid version of the geometry given.
     * If the geometry is a polygon or multi polygon, self intersections /
     * inconsistencies are fixed. Otherwise the geometry is returned.
     *
     * @param geom
     * @param distanceTolerance
     * @return a geometry
     */
    public static Geometry validate(Geometry geom, double distanceTolerance){
        if(geom instanceof Polygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the polygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            addPolygon((Polygon)geom, polygonizer);
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else if(geom instanceof MultiPolygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the multipolygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            for(int n = geom.getNumGeometries(); n-- > 0;){
                addPolygon((Polygon)geom.getGeometryN(n), polygonizer);
            }
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else{
            return geom; // In my case, I only care about polygon / multipolygon geometries
        }
    }


    /**
     * Add all line strings from the polygon given to the polygonizer given
     *
     * @param polygon polygon from which to extract line strings
     * @param polygonizer polygonizer
     */
    static void addPolygon(Polygon polygon, Polygonizer polygonizer){
        addLineString(polygon.getExteriorRing(), polygonizer);
        for(int n = polygon.getNumInteriorRing(); n-- > 0;){
            addLineString(polygon.getInteriorRingN(n), polygonizer);
        }
    }

    /**
     * Add the linestring given to the polygonizer
     *
     * @param lineString line string
     * @param polygonizer polygonizer
     */
    static void addLineString(LineString lineString, Polygonizer polygonizer){

        if(lineString instanceof LinearRing){ // LinearRings are treated differently to line strings : we need a LineString NOT a LinearRing
            lineString = lineString.getFactory().createLineString(lineString.getCoordinateSequence());
        }

        // unioning the linestring with the point makes any self intersections explicit.
        Point point = lineString.getFactory().createPoint(lineString.getCoordinateN(0));
        Geometry toAdd = lineString.union(point);

        //Add result to polygonizer
        polygonizer.add(toAdd);
    }

    /**
     * Get a geometry from a collection of polygons.
     *
     * @param polygons collection
     * @param factory factory to generate MultiPolygon if required
     * @return null if there were no polygons, the polygon if there was only one, or a MultiPolygon containing all polygons otherwise
     */
    public static Geometry toPolygonGeometry(Collection<Polygon> polygons, GeometryFactory factory) {
        switch (polygons.size()) {
            case 0:
                return null; // No valid polygons!
            case 1:
                return polygons.iterator().next(); // single polygon - no need to wrap
            default:
                //polygons may still overlap! Need to sym difference them
                Iterator<Polygon> iter = polygons.iterator();
                Geometry ret = iter.next();
                while (iter.hasNext()) {
                    ret = ret.symDifference(iter.next());
                }
                return ret;
        }
    }

    public static boolean isCoveredBy(Geometry geometry, GeometryCoverable coverable) {
        if(geometry == null || coverable == null || coverable.getLocation() == null) {
            return false;
        }

        Coordinate coordinate = new Coordinate();
        coordinate.x = coverable.getLocation().longitude;
        coordinate.y = coverable.getLocation().latitude;
        Point point = geometryFactory.createPoint(coordinate);
        return geometry.covers(point);
    }


}
