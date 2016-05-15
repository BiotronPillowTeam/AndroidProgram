package com.goldfiction.smartpillow.smartpillow;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class GraphActivity extends ActionBarActivity {
    static GraphView graph;
    static LineGraphSeries<DataPoint> series;
    public static LinkedList<Double> GDData=new LinkedList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button getData = (Button)findViewById(R.id.btnGetData);

        int datacount=40;
        double[] data={10,129,289,489,520,516,527,535,551,254,251,249,248,246,246,244,244,243,243,240,242,242,242,241,241,241,241,240,241,241,240,240,240,240,240,241,241,241,241,238};
        //double[] data={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        double[] diff=new double[datacount];//difference
        double[] wavg=new double[datacount];//weighted average
        int[] weight={1,9};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        DataPoint[] dps=new DataPoint[datacount];
        for(int x=0;x<datacount;x++){
            data[x]+=0.1;
            data[x]=Math.abs(data[x]);
            //get the percentage difference between each pair of data
            if(x<datacount-1) {
                diff[x] = Math.abs((data[x + 1] - data[x]) / data[x]);
            }
            else{
                diff[x]=0;
            }
            if (diff[x] > 0.05) {
                diff[x] = 0.05;
            }
            wavg[0] = 0.05;//always give first number of wavg 0.05
            if (x >= 1) {
                wavg[x] = (weight[0] * diff[x - 1] + weight[1] * wavg[x - 1]) / (weight[0] + weight[1]);
            }
            dps[x] = new DataPoint(x, wavg[x]);
        }

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(dps);
        graph.addSeries(series);

//        getData.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                String cmd="GD";
//                try {
//                    MainActivity.sendData(cmd);
//                } catch (IOException ex) {
//                }
//            }
//        });

        try{
        String cmd="GD";
        try {
            MainActivity.sendData(cmd,this);
        } catch(IOException ex) {
            MainActivity.beginListenForData(this);
        }}catch(Exception ex2){}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void redrawGraph(){
        int datacount=GDData.size();
        //double[] data={10,129,289,489,520,516,527,535,551,254,251,249,248,246,246,244,244,243,243,240,242,242,242,241,241,241,241,240,241,241,240,240,240,240,240,241,241,241,241,238};
        Double[] data=new Double[datacount];
        data=GDData.toArray(data);
        double[] diff=new double[datacount];//difference
        double[] wavg=new double[datacount];//weighted average
        int[] weight={1,9};
        DataPoint[] dps=new DataPoint[datacount];
        for(int x=0;x<datacount;x++){
            //get the percentage difference between each pair of data
            data[x]+=0.1;
            data[x]=Math.abs(data[x]);
            if(x<datacount-1) {
                diff[x] = Math.abs((data[x + 1] - data[x]) / data[x]);
            }
            else{
                diff[x]=0;
            }
            if (diff[x] > 0.05) {
                diff[x] = 0.05;
            }
            wavg[0] = 0.05;//always give first number of wavg 0.05
            if (x >= 1) {
                wavg[x] = (weight[0] * diff[x - 1] + weight[1] * wavg[x - 1]) / (weight[0] + weight[1]);
            }
            dps[x] = new DataPoint(x, wavg[x]);
        }

        series = new LineGraphSeries<DataPoint>(dps);
        graph.addSeries(series);
    }
}
