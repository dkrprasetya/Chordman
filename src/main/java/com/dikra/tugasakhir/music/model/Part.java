package com.dikra.tugasakhir.music.model;

import org.dom4j.Node;

import java.util.List;

/**
 * Created by DIKRA on 4/8/2015.
 */
public class Part {
    private String id;
    private Measure[] measures;
    private MeasureAttributes[] measureAtts;

    public Part(){
    }

    public void initFromXML(Node node){
        id = node.valueOf("@id");

        List<Node> measure_nodes = node.selectNodes("measure");

        measures = new Measure[measure_nodes.size()];
        measureAtts = new MeasureAttributes[measures.length];

        for (int i = 0; i < measures.length; i++){
            Node mnode = measure_nodes.get(i);
            measures[i] = new Measure();
            measures[i].initFromXML(mnode);

            Node att_node = mnode.selectSingleNode("attributes");
            if (att_node != null){
                measureAtts[i] = new MeasureAttributes();
                measureAtts[i].initFromXML(att_node);
            } else {
                measureAtts[i] = measureAtts[i-1];
            }
        }

//        System.out.println("Part " + id + ", consisting " + getMeasuresLength() + " measure(s), has been constructed.");
    }

    /*** Returns id of the part ***/
    public String getId(){
        return id;
    }

    /*** Returns list of measures ***/
    public Measure[] getMeasures(){
        return measures;
    }

    /*** Returns number of measures in part ***/
    public int getMeasuresLength(){
        return measures.length;
    }

    /*** Returns measure with its number as id (1-based index) ***/
    public Measure getMeasureAtNumber(int id){
        return measures[id-1];
    }

    public MeasureAttributes[] getMeasureAttributes(){
        return measureAtts;
    }

    public MeasureAttributes getMeasureAttAt(int id){
        return measureAtts[id];
    }
}
