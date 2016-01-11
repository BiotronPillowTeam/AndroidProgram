package com.goldfiction.smartpillow.smartpillow;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int datacount=40;
        double[] data={10,129,289,489,520,516,527,535,551,254,251,249,248,246,246,244,244,243,243,240,242,242,242,241,241,241,241,240,241,241,240,240,240,240,240,241,241,241,241,238};
        double[] diff=new double[datacount];//difference
        double[] wavg=new double[datacount];//weighted average
        int[] weight={1,9};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        DataPoint[] dps=new DataPoint[datacount];
        for(int x=0;x<datacount;x++){
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

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dps);
        graph.addSeries(series);
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
}
